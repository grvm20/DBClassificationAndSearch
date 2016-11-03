package org.columbia.adb.qprober;

import org.columbia.adb.qprober.processor.QProberProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Hello world!
 *
 */
public class QProberRunner {
	public static void main(String[] args) throws Exception {

		ApplicationContext ctx = new AnnotationConfigApplicationContext(
				"org.columbia.adb.qprober");
		String bingAccessKey = args[0];
		Double specificityThreshold = Double.parseDouble(args[1]);
		Long coverageThreshhold = Long.parseLong(args[2]);
		String db = args[3];

		QProberProcessor qProberProcessor = (QProberProcessor) ctx.getBean(
				"QProberProcessor", specificityThreshold, coverageThreshhold,
				db, bingAccessKey);
		qProberProcessor.startProcessing();

	}
}
