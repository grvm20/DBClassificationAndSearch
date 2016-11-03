package org.columbia.adb.qprober.contentsummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.columbia.adb.qprober.classifier.model.Category;
import org.columbia.adb.qprober.classifier.query.web.QueryWeb;
import org.columbia.adb.qprober.classifier.query.web.bing.QueryBing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "QProberSummaryGenerator")
@Scope("prototype")
public class QProberSummaryGenerator {

	@Autowired
	private ApplicationContext ctx;

	@Autowired
	private LynxRunner lynxRunner;

	@Autowired
	private ContentSummaryFileGenerator contentSummaryFileGenerator;

	private String dbName;
	private String accessKey;
	private QueryWeb queryWeb;

	public QProberSummaryGenerator(String dbName, String accessKey) {
		this.dbName = dbName;
		this.accessKey = accessKey;
	}

	@PostConstruct
	public void init() {
		queryWeb = (QueryBing) ctx.getBean("QueryBing", accessKey, dbName);
	}

	public void generateContentSummary(Category category) {

		//System.out.println("Extracting topic content summaries");
		Map<String, Integer> map = new HashMap<String, Integer>();
		while (category != null) {

			String categoryName = category.getName();

			if (category.getQueries() == null
					|| category.getQueries().size() == 0) {
				category = category.getParent();
				continue;
			}
			List<String> duplicates = new ArrayList<String>();
			int c=0;
			int totalcount=0;
			
			for(Map.Entry<String, List<String>> entry : category.getQueries().entrySet()){
				totalcount += entry.getValue().size();
			}
			
			for (Map.Entry<String, List<String>> entry : category.getQueries()
					.entrySet()) {
				
				for (String query : entry.getValue()) {
					c++;
					System.out.println(c+"/"+totalcount);
					List<String> top4Urls = queryWeb.getTopURL(query, 4);
					for (String url : top4Urls) {
						if (!duplicates.contains(url)) {
							duplicates.add(url);
							// check encoding of url here
							System.out.println("Getting Page"+url);
							System.out.println("");
							List<String> setOfWords = lynxRunner.runLynx(url);
							List<String> newdocSet = new ArrayList<>();
							if (!setOfWords.isEmpty()) {
								for (String s : setOfWords) {
									if (!newdocSet.contains(s)) {
										newdocSet.add(s);
										if (map.containsKey(s))
											map.put(s, map.get(s) + 1);

										else
											map.put(s, 1);
									}
								}

							}

						}
						/*System.out.println("Completed Test: db:" + dbName
								+ " category:" + categoryName + " query:"
								+ query);*/
						//System.out.println("URLs: " + top4Urls);

					}

				}
			}
			System.out.println("Completed Test " + dbName + " for "
					+ category.getName());
			contentSummaryFileGenerator.createOutputFile(categoryName, dbName,
					map);
			category = category.getParent();
		}
	}
}
