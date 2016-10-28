package org.columbia.adb.qprober.processor;

import java.util.List;

import org.columbia.adb.qprober.classifier.DBClassTreeBuilder;
import org.columbia.adb.qprober.classifier.DBClassifier;
import org.columbia.adb.qprober.classifier.model.DBClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class QProberProcessor {

	private Double specificityThreshhold;
	private Long coverageThreshhold;
	private String host;
	private String accessKey;

	@Autowired
	private DBClassTreeBuilder dbClassTreeBuilder;
	
	@Autowired
	private ApplicationContext ctx;

	public QProberProcessor(Double specificityThreshhold,
			Long coverageThreshhold, String host, String accessKey) {
		this.specificityThreshhold = specificityThreshhold;
		this.coverageThreshhold = coverageThreshhold;
		this.host = host;
		this.accessKey = accessKey;
	}

	public void startProcessing() throws Exception {

		DBClass root = dbClassTreeBuilder.buildTree();
		DBClassifier dbClassifier = (DBClassifier) ctx.getBean("DBClassifier",
				root, specificityThreshhold, coverageThreshhold, host, accessKey);
		List<DBClass> classes = dbClassifier.classify();
		System.out.println(classes);

	}

}
