package org.columbia.adb.qprober.classifier.model;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBClass {

	private Map<String, DBClass> children;
	private String name;
	private Map<String, List<String>> queries;
	private Long coverage;
	private Double specificity;
	private DBClass parent;
	

	public DBClass (String name, Map<String, DBClass> children, Map<String, List<String>> queries){
		this.name = name;
		this.children = children;
		this.queries = queries;
	}
	public String toString(){
		return name;
	}
	
}
