package org.columbia.adb.qprober.classifier.query.web;

import java.io.IOException;
import java.util.List;

public interface QueryWeb {

	/**
	 * Returns match count for given query
	 * @param queryString
	 * @return
	 * @throws Exception
	 */
	public Long getMatchCount(final String queryString, final String cachePath) throws Exception;
	
	public String getContent(final String url) throws IOException;
	
	public List<String> getTopURL(final String query, final int topK);

}
