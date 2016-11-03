package org.columbia.adb.qprober.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.springframework.stereotype.Component;


@Component
public class QueryCache {

	public boolean isCached(String filePath, String query){
		File file = new File(filePath + "/" + query);
		return file.exists();
	}
	
	public Long getMatchCount(String filePath, String query) throws Exception{
		
		try (BufferedReader br = new BufferedReader(new FileReader(filePath + "/" + query))) {
            return Long.parseLong(br.readLine());
        }		
	}
	
}