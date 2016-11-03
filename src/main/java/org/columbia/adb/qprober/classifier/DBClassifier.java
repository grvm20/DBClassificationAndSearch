package org.columbia.adb.qprober.classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.columbia.adb.qprober.cache.QueryCache;
import org.columbia.adb.qprober.classifier.model.Category;
import org.columbia.adb.qprober.classifier.query.web.QueryWeb;
import org.columbia.adb.qprober.classifier.query.web.bing.QueryBing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DBClassifier {

	private static final String CACHE_FILE_DIRECTORY = "src/main/resources/cache/matches/";

	private QueryWeb queryWeb;

	@Autowired
	private QueryCache queryCache;

	@Autowired
	private ApplicationContext ctx;

	private Category root;
	private Double expectedSpecificity;
	private Long expectedThreshhold;
	private String dbName;
	private String accessKey;

	public DBClassifier(Category root, Double expectedSpecificity,
			Long expectedThreshhold, String dbName, String accessKey) {
		this.root = root;
		this.expectedSpecificity = expectedSpecificity;
		this.expectedThreshhold = expectedThreshhold;
		this.dbName = dbName;
		this.accessKey = accessKey;
	}

	public List<Category> classify() throws Exception {

		queryWeb = (QueryBing) ctx.getBean("QueryBing", accessKey, dbName);

		System.out.println("Classifying...");
		List<Category> categories = new ArrayList<>();
		classify(root, expectedSpecificity, expectedThreshhold, categories);

		return categories;
	}

	private void classify(Category category, Double specificity, Long coverage,
			List<Category> categories) throws Exception {

		if (category.getChildren() == null) {
			categories.add(category);
		} else {
			List<Category> validChildren = getValidChildrenCategories(category, specificity,
					coverage, category.getName());
			if (validChildren.size() == 0) {

				categories.add(category);
			} else {

				for (Category child : validChildren) {

					classify(child, specificity, coverage, categories);
				}
			}
		}
	}

	private List<Category> getValidChildrenCategories(Category category,
			Double expectedSpecificity, Long expectedCoverage, String categoryName)
			throws Exception {

		long total = 0L;

		for (Map.Entry<String, List<String>> entry : category.getQueries()
				.entrySet()) {

			long categoryCoverage = 0L;

			for (String q : entry.getValue()) {

				Long matches;
				if (queryCache.isCached(CACHE_FILE_DIRECTORY + dbName, q.trim())) {
					matches = queryCache.getMatchCount(CACHE_FILE_DIRECTORY
							+ dbName, q.trim());
				} else {
					matches = queryWeb.getMatchCount(q, CACHE_FILE_DIRECTORY);
				}

				categoryCoverage += matches;
				total += matches;
			}
			category.getChildren().get(entry.getKey())
					.setCoverage(categoryCoverage);
		}

		List<Category> list = new ArrayList<>();
		for (Map.Entry<String, Category> entry : category.getChildren()
				.entrySet()) {

			Category child = entry.getValue();
			Long coverage = child.getCoverage();
			
			
			Double specificity = (double) (child.getParent()
					.getSpecificity() * coverage) / total;
			
			System.out.println("Specificity for category: " + child.getName() + " is " + specificity);
			System.out.println("Coverage for category: " + child.getName() + " is " + coverage);
			
			if (coverage > expectedCoverage && specificity > expectedSpecificity) {
				child.setSpecificity(specificity);
				list.add(child);
			}
			
		}

		return list;
	}

}
