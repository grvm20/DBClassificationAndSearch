package org.columbia.adb.qprober.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.columbia.adb.qprober.classifier.DBCategoryTreeBuilder;
import org.columbia.adb.qprober.classifier.DBClassifier;
import org.columbia.adb.qprober.classifier.model.Category;
import org.columbia.adb.qprober.contentsummary.QProberSummaryGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class QProberProcessor {

	private Double specificityThreshhold;
	private Long coverageThreshhold;
	private String dbName;
	private String accessKey;

	@Autowired
	private DBCategoryTreeBuilder dbCategoryTreeBuilder;

	@Autowired
	private ApplicationContext ctx;

	private QProberSummaryGenerator qProberSummaryGenerator;

	public QProberProcessor(Double specificityThreshhold,
			Long coverageThreshhold, String host, String accessKey) {
		this.specificityThreshhold = specificityThreshhold;
		this.coverageThreshhold = coverageThreshhold;
		this.dbName = host;
		this.accessKey = accessKey;
	}

	public void startProcessing() throws Exception {

		Category root = dbCategoryTreeBuilder.buildTree();
		DBClassifier dbClassifier = (DBClassifier) ctx.getBean("DBClassifier",
				root, specificityThreshhold, coverageThreshhold, dbName,
				accessKey);
		List<Category> categories = dbClassifier.classify();
		printClassification(categories);

		qProberSummaryGenerator = (QProberSummaryGenerator) ctx.getBean(
				"QProberSummaryGenerator", dbName, accessKey);

		for (Category dbClass : categories) {
			qProberSummaryGenerator.generateContentSummary(dbClass);
		}

	}

	private void printClassification(List<Category> categories) {
		
		System.out.println();
		System.out.println();
		System.out.println("Classification:");
		for(Category category : categories){
			List<String> categoryHeirarchyList = new ArrayList<>();
			
			while(category != null){
				categoryHeirarchyList.add(category.getName());
				category = category.getParent();
			}
			Collections.reverse(categoryHeirarchyList);
			System.out.println(StringUtils.join(categoryHeirarchyList, "/"));
		}
		
	}

}
