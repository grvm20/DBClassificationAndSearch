package org.columbia.adb.qprober.classifier.query;

public interface QueryWeb {

	/**
	 * Returns match count for given query
	 * @param queryString
	 * @return
	 * @throws Exception
	 */
	public Long getMatchCount(String queryString) throws Exception;

}
