package org.columbia.adb.qprober.contentsummary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ContentSummaryFileGenerator {

	public void createOutputFile(String categoryName, String dbName,
			Map<String, Integer> map) {
		String fileName = categoryName + "-" + dbName + ".txt";

		File f = new File(fileName);
		if (f.exists()) {
			Map<String, Integer> existingMap = getExistingData(fileName);
			map = concatenate(existingMap, map);
		}

		System.out.println("Creating File: " + fileName);
		String[] keys = map.keySet().toArray(new String[map.keySet().size()]);
		Arrays.sort(keys);

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(fileName), "utf-8"))) {

			for (String s : keys) {
				String new1 = s + "#" + (float)map.get(s) + "#\n";
				writer.write(new1);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Map<String, Integer> concatenate(Map<String, Integer> existingMap,
			Map<String, Integer> map) {

		for (Map.Entry<String, Integer> entry : existingMap.entrySet()) {

			String key = entry.getKey();
			if (map.containsKey(key)) {
				map.put(key, map.get(key) + entry.getValue());
			} else {
				map.put(key, entry.getValue());
			}
		}

		return map;
	}

	private Map<String, Integer> getExistingData(String fileName) {
		Map<String, Integer> map = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(
				new File(fileName)))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split("#");
				map.put(values[0], Integer.parseInt(values[1]));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
