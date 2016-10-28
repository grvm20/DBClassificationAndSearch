package org.columbia.adb.qprober.classifier.query.bing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.columbia.adb.qprober.classifier.query.QueryWeb;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "QueryBing")
@Scope("prototype")
public class QueryBing implements QueryWeb {

	private String key;
	private String site;
	private String cachePath;

	public QueryBing(final String key, final String site, final String cachePath) {
		Validate.notEmpty(key, "Key passed is Empty");
		this.key = key;
		this.site = site;
		this.cachePath = cachePath;
	}

	@Override
	public Long getMatchCount(String queryString) throws Exception {

		String accountKeyAuth = Base64.encodeBase64String((key + ":" + key)
				.getBytes());
		final String query = URLEncoder.encode(queryString, "utf8");
		String urlString = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=%27site%3a"
				+ site + "%20%" + query + "%27&$top=10&$format=JSON";

		System.out.println("URL: " + urlString);

		URL url = new URL(urlString);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(0);
		connection.setRequestProperty("Authorization", "Basic "
				+ accountKeyAuth);

		if (connection.getResponseCode() != 200) {
			System.err
					.printf("Got HTTP error %d", connection.getResponseCode());
			throw new Exception(
					"Bing threw exception while processing query. Response Code : "
							+ connection.getResponseCode());
		}

		try (final BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream()))) {
			String inputLine;
			final StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			JSONObject result = new JSONObject(response.toString());
			JSONObject d = result.getJSONObject("d");
			JSONObject webResult = d.getJSONArray("results").getJSONObject(0);

			Long webTotal = Long.parseLong(webResult.getString("WebTotal"));
			addToCache(queryString, webTotal);
			return webTotal;
		}

	}

	private void addToCache(String queryString, Long webTotal)
			throws IOException {

		String filePath = cachePath + "/" + site + "/";
		final File parent = new File(filePath);
		parent.mkdirs();
		try (BufferedWriter wr = new BufferedWriter(new FileWriter(new File(
				parent, queryString.trim())))) {
			wr.write(webTotal.toString());
		}

	}

}
