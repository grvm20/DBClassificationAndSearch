package org.columbia.adb.qprober.classifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.columbia.adb.qprober.classifier.model.Category;
import org.columbia.adb.qprober.classifier.query.QueryFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DBCategoryTreeBuilder {

	@Autowired
	private QueryFetcher queryFetcher;

	public Category buildTree() throws Exception {

		// Initializing Computer's Children
		Map<String, Category> computersChildren = new HashMap<>();
		Category hardware = new Category("Hardware", null, null);
		Category programming = new Category("Programming", null, null);
		computersChildren.put("Hardware", hardware);
		computersChildren.put("Programming", programming);

		// Initializing Computers
		String computerString = "Computers";
		Category computers = new Category(computerString, computersChildren,
				fetchQuery(computerString));
		hardware.setParent(computers);
		programming.setParent(computers);

		// Initializing Health's Children
		Map<String, Category> healthChildren = new HashMap<>();
		Category fitness = new Category("Fitness", null, null);
		Category disease = new Category("Diseases", null, null);
		healthChildren.put("Fitness", fitness);
		healthChildren.put("Diseases", disease);

		// Initializing Health
		String healthString = "Health";
		Category health = new Category(healthString, healthChildren,
				fetchQuery(healthString));
		fitness.setParent(health);
		disease.setParent(health);

		// Initializing sports children
		Map<String, Category> sportsChildren = new HashMap<>();
		Category basketball = new Category("Basketball", null, null);
		Category soccer = new Category("Soccer", null, null);
		sportsChildren.put("Basketball", basketball);
		sportsChildren.put("Soccer", soccer);

		// Initializing sports
		String sportsString = "Sports";
		Category sports = new Category(sportsString, sportsChildren,
				fetchQuery(sportsString));
		basketball.setParent(sports);
		soccer.setParent(sports);

		Map<String, Category> rootChildren = new HashMap<>();
		rootChildren.put("Computers", computers);
		rootChildren.put("Health", health);
		rootChildren.put("Sports", sports);

		// Initializing Root
		String rootString = "Root";
		Category root = new Category(rootString, rootChildren,
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
