package org.columbia.adb.qprober.classifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.columbia.adb.qprober.classifier.model.DBClass;
import org.columbia.adb.qprober.classifier.query.QueryFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBClassTreeBuilder {

	@Autowired
	private QueryFetcher queryFetcher;

	public DBClass buildTree() throws Exception {

		// Initializing Computer's Children
		Map<String, DBClass> computersChildren = new HashMap<>();
		DBClass hardware = new DBClass("Hardware", null, null);
		DBClass programming = new DBClass("Programming", null, null);
		computersChildren.put("Hardware", hardware);
		computersChildren.put("Programming", programming);

		// Initializing Computers
		String computerString = "Computers";
		DBClass computers = new DBClass(computerString, computersChildren,
				fetchQuery(computerString));
		hardware.setParent(computers);
		programming.setParent(computers);

		// Initializing Health's Children
		Map<String, DBClass> healthChildren = new HashMap<>();
		DBClass fitness = new DBClass("Fitness", null, null);
		DBClass disease = new DBClass("Diseases", null, null);
		healthChildren.put("Fitness", fitness);
		healthChildren.put("Diseases", disease);

		// Initializing Health
		String healthString = "Health";
		DBClass health = new DBClass(healthString, healthChildren,
				fetchQuery(healthString));
		fitness.setParent(health);
		disease.setParent(health);

		// Initializing sports children
		Map<String, DBClass> sportsChildren = new HashMap<>();
		DBClass basketball = new DBClass("Basketball", null, null);
		DBClass soccer = new DBClass("Soccer", null, null);
		sportsChildren.put("Basketball", basketball);
		sportsChildren.put("Soccer", soccer);

		// Initializing sports
		String sportsString = "Sports";
		DBClass sports = new DBClass(sportsString, sportsChildren,
				fetchQuery(sportsString));
		basketball.setParent(sports);
		soccer.setParent(sports);

		Map<String, DBClass> rootChildren = new HashMap<>();
		rootChildren.put("Computers", computers);
		rootChildren.put("Health", health);
		rootChildren.put("Sports", sports);

		// Initializing Root
		String rootString = "Root";
		DBClass root = new DBClass(rootString, rootChildren,
				fetchQuery(rootString));
		computers.setParent(root);
		health.setParent(root);
		sports.setParent(root);

		root.setSpecificity(1.0);
		return root;
	}

	private Map<String, List<String>> fetchQuery(String name) throws Exception {

		Map<String, List<String>> queries = queryFetcher
				.fetchQueries("src/main/resources/" + name.toLowerCase()
						+ ".txt");
		return queries;
	}

}
