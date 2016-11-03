package org.columbia.adb.qprober.classifier.query.web.bing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.Validate;
import org.columbia.adb.qprober.classifier.query.web.QueryWeb;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component(value = "QueryBing")
@Scope("prototype")
public class QueryBing implements QueryWeb {

	private String key;
	private String dbName;

	public QueryBing(final String key, final String site) {
		Validate.notEmpty(key, "Key passed is Empty");
		this.key = key;
		this.dbName = site;
	}

	@Override
	public Long getMatchCount(final String queryString, final String cachePath) throws Exception {

		String accountKeyAuth = Base64.encodeBase64String((key + ":" + key)
				.getBytes());
		final String query = URLEncoder.encode(queryString, "utf8");
		String urlString = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Composite?Query=%27site%3a"
				+ dbName + "%20%" + query + "%27&$top=10&$format=JSON";

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
			addToCache(queryString, webTotal, cachePath);
			return webTotal;
		}

	}

	private void addToCache(final String queryString, final Long webTotal, final String cachePath)
			throws IOException {

		String filePath = cachePath + "/" + dbName + "/";
		final File parent = new File(filePath);
		parent.mkdirs();
		try (BufferedWriter wr = new BufferedWriter(new FileWriter(new File(
				parent, queryString.trim())))) {
			wr.write(webTotal.toString());
		}

	}

	@Override
	public String getContent(final String urlString) throws IOException {
		
		String accountKeyEnc = DatatypeConverter
				.printBase64Binary((key + ":" + key).getBytes());

		URL url = new URL(urlString);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic "
				+ accountKeyEnc);

		InputStream inputStream = (InputStream) urlConnection.getContent();
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		String content = new String(contentRaw);

		// The content string is the xml/json output from Bing.
		return content;
	}

	@Override
	public List<String> getTopURL(final String query, final int topK) {
		List<String> result = new ArrayList<String>();
		String bingQuery = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=%27site%3a"
							+ dbName + "%20" + query.replace(" ", "+") + "%27&$top=" + topK + "&$format=Atom";
		try {
			String content = getContent(bingQuery);
			Pattern p = Pattern.compile("<d:Url m:type=\"Edm.String\">(.+?)</d:Url>");
			Matcher m = p.matcher(content);
			while (m.find())
				result.add(m.group(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
