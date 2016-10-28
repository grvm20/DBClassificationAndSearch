package org.columbia.adb.qprober.classifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.columbia.adb.qprober.cache.QueryCache;
import org.columbia.adb.qprober.classifier.model.DBClass;
import org.columbia.adb.qprober.classifier.query.QueryWeb;
import org.columbia.adb.qprober.classifier.query.bing.QueryBing;
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
	
	private DBClass root;
	private Double expectedSpecificity;
	private Long expectedThreshhold;
	private String host;
	private String accessKey;

	public DBClassifier(DBClass root, Double expectedSpecificity,
			Long expectedThreshhold, String host, String accessKey) {
		this.root = root;
		this.expectedSpecificity = expectedSpecificity;
		this.expectedThreshhold = expectedThreshhold;
		this.host = host;
		this.accessKey = accessKey;
	}

	public List<DBClass> classify() throws Exception {

		queryWeb = (QueryBing)ctx.getBean("QueryBing", accessKey, host, CACHE_FILE_DIRECTORY);
		
		List<DBClass> dbClasses = new ArrayList<>();
		classify(root, expectedSpecificity, expectedThreshhold, dbClasses);

		return dbClasses;
	}

	private void classify(DBClass dbClass, Double specificity, Long coverage,
			List<DBClass> classes) throws Exception {

		if (dbClass.getChildren() == null) {
			classes.add(dbClass);
		} else {
			List<DBClass> validChildren = queryDBClass(dbClass, specificity,
					coverage, dbClass.getName());
			if (validChildren.size() == 0) {

				classes.add(dbClass);
			} else {

				for (DBClass child : validChildren) {

					classify(child, specificity, coverage, classes);
				}
			}
		}
	}

	private List<DBClass> queryDBClass(DBClass dbClass,
			Double expectedSpecificity, Long expectedCoverage, String name)
			throws Exception {

		long total = 0L;

		for (Map.Entry<String, List<String>> entry : dbClass.getQueries()
				.entrySet()) {

			long classCoverage = 0L;

			for (String q : entry.getValue()) {

				Long matches;
				if (queryCache.isCached(CACHE_FILE_DIRECTORY + host, q.trim())) {
					matches = queryCache.queryWeb(CACHE_FILE_DIRECTORY + host,
							q.trim());
				} else {
					matches = queryWeb.getMatchCount(q);
				}

				classCoverage += matches;
				total += matches;
			}
			dbClass.getChildren().get(entry.getKey())
					.setCoverage(classCoverage);
		}

		List<DBClass> list = new ArrayList<>();
		for (Map.Entry<String, DBClass> entry : dbClass.getChildren()
				.entrySet()) {

			DBClass child = entry.getValue();
			Long coverage = child.getCoverage();

			if (coverage > expectedCoverage) {
				Double specificity = (double) (child.getParent()
						.getSpecificity() * coverage) / total;
				if (specificity > expectedSpecificity) {
					child.setSpecificity(specificity);
					list.add(child);
				}
			}
		}

		return list;
	}

}
