package epnoi.model.dao.cassandra;

import java.util.Arrays;

import epnoi.model.Search;
import epnoi.model.User;

public class SimpleTestSetUp {
	public static void main(String[] args) {

		System.out.println("Starting Test SetUp");
		System.out
				.println("Initialization --------------------------------------------");
		UserCassandraDAO userCassandraDAO = new UserCassandraDAO();
		userCassandraDAO.init();

		System.out.println(" --------------------------------------------");

		if (userCassandraDAO.existsUserWithName("Rafa")) {
			System.out.println("Rafita existe!");
			User userToDelete = userCassandraDAO.getUserWithName("Rafa");
			userCassandraDAO.delete(userToDelete.getURI());

		}

		if (userCassandraDAO.existsUserWithName("Sara")) {
			System.out.println("Sara existe!");
			User userToDelete = userCassandraDAO
					.getUserWithName("Sara");
			userCassandraDAO.delete(userToDelete.getURI());
		}

		//Let's create the users
		User user = new User();
		user.setURI("http://userRafa");
		user.setName("Rafa");
		user.setPassword("PasswordDeRafa");
		user.addSearch("searchA");
		user.addSearch("searchB");
		userCassandraDAO.create(user);

		User userElOtro = new User();
		userElOtro.setURI("http://userSara");
		userElOtro.setName("Sara");
		userElOtro.setPassword("PasswordDeSara");
		userElOtro.addSearch("searchC");
		userElOtro.addSearch("searchD");
		userCassandraDAO.create(userElOtro);
		
		User readUser = userCassandraDAO.read("http://userRafa");
		System.out.println("readed user> " + readUser);

		User otherReadUser = userCassandraDAO.read("http://userSara");
		System.out.println("readed user> " + otherReadUser);
		System.out.println("Exiting test");
		//Let's create the searchs
		
		SearchCassandraDAO searchCassandraDAO = new SearchCassandraDAO();
		for (String label : Arrays.asList("A","B","C","D")){
			String searchURI= "http://search"+label;
			Search search = new Search();
			search.setURI(searchURI);
			search.setDescription("Search"+label +" Description");
			search.setTitle("Search"+label);
			for (String expressionLabel : Arrays.asList("A","B","C","D")){
				search.addExpression("expression"+expressionLabel);
			}
			searchCassandraDAO.create(search);
			
		}
	}
}
