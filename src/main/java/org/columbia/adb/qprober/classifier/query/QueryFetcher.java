package org.columbia.adb.qprober.classifier.query;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class QueryFetcher {

	public Map<String, List<String>> fetchQueries(String fileName)
			throws Exception {

		
		//InputStream in = getClass().getResourceAsStream("/" + fileName);
		
		try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
			Map<String, List<String>> queries = new HashMap<>();

			String line;

			while ((line = br.readLine()) != null) {

				String[] queryClass = line.split(" ", 2);
				String dbClass = queryClass[0];
				String query = queryClass[1];

				if (queries.containsKey(dbClass)) {
					queries.get(dbClass).add(query);
				} else {

					List<String> list = new ArrayList<>();
					list.add(query);
					queries.put(dbClass, list);
				}
			}
			return queries;
		}

	}

}
