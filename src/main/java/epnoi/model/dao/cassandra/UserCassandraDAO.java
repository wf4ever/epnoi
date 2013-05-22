package epnoi.model.dao.cassandra;

import java.util.ArrayList;
import java.util.List;

import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import epnoi.model.ExternalResource;
import epnoi.model.Search;
import epnoi.model.User;

public class UserCassandraDAO extends CassandraDAO {

	public void delete(String URI) {
		super.deleteRow(URI, UserCassandraHelper.COLUMN_FAMILLY);
	}

	// --------------------------------------------------------------------------------

	public void create(User user) {
		super.createRow(user.getURI(), UserCassandraHelper.COLUMN_FAMILLY);

		if (user.getName() != null) {

			super.updateColumn(user.getURI(), UserCassandraHelper.NAME,
					user.getName(), UserCassandraHelper.COLUMN_FAMILLY);

		}

		if (user.getPassword() != null) {
			super.updateColumn(user.getURI(), UserCassandraHelper.PASSWORD,
					user.getPassword(), UserCassandraHelper.COLUMN_FAMILLY);

		}

		for (String searchURI : user.getSearchs()) {
			super.updateColumn(user.getURI(), searchURI,
					UserCassandraHelper.SEARCHS,
					UserCassandraHelper.COLUMN_FAMILLY);
		}

	}

	// --------------------------------------------------------------------------------

	public User read(String URI) {
		/*
		 * System.out.println(" --> " + URI); ColumnSliceIterator<String,
		 * String, String> columnsIteratorProof = super .getAllCollumns(URI,
		 * ExternalResourceCassandraHelper.COLUMN_FAMILLY);
		 * 
		 * while (columnsIteratorProof.hasNext()) { HColumn<String, String>
		 * column = columnsIteratorProof.next(); System.out.println("Column   "
		 * + column); }
		 */
		ColumnSliceIterator<String, String, String> columnsIterator = super
				.getAllCollumns(URI, UserCassandraHelper.COLUMN_FAMILLY);
		if (columnsIterator.hasNext()) {
			User user = new User();
			user.setURI(URI);
			while (columnsIterator.hasNext()) {

				HColumn<String, String> column = columnsIterator.next();
				System.out.println("--column " + column);
				if (UserCassandraHelper.NAME.equals(column.getName())) {
					user.setName(column.getValue());

				} else {
					if (UserCassandraHelper.PASSWORD.equals(column.getName())) {
						user.setPassword(column.getValue());
					} else {
						if (UserCassandraHelper.SEARCHS.equals(column
								.getValue())) {
							user.addSearch(column.getName());
						}
					}

				}
			}

			return user;
		}

		return null;
	}

	// --------------------------------------------------------------------------------

	public void update(User externalResource) {
		super.updateColumn(externalResource.getURI(), UserCassandraHelper.NAME,
				externalResource.getDescription(),
				UserCassandraHelper.COLUMN_FAMILLY);
	}

	// --------------------------------------------------------------------------------
	public boolean existsUserWithName(String name) {
		List result = (CassandraCQLClient
				.query("select * from User where NAME='" + name + "'"));
		return ((result != null) && (result.size() > 0));
	}

	// --------------------------------------------------------------------------------
	public User getUserWithName(String name) {
		List result = (CassandraCQLClient
				.query("select * from User where NAME='" + name + "'"));
		if ((result != null) && (result.size() > 0)) {
			Row row = (Row) result.get(0);
			User user = this.read((String) row.getKey());
			return user;
		}
		return null;
	}

	// --------------------------------------------------------------------------------
	public List<User> getUsers() {
		List<User> users = new ArrayList<User>();
		List<Row<String, String, String>> result = (CassandraCQLClient
				.query("select * from " + UserCassandraHelper.COLUMN_FAMILLY));

		if (result != null) {
			for (Row<String, String, String> row : result) {

				User user = this.read((String) row.getKey());
				users.add(user);

			}
		}
		return users;
	}

	//
	public static void main(String[] args) {
		System.out.println("Starting test");
		System.out
				.println("Initialization --------------------------------------------");
		UserCassandraDAO userCassandraDAO = new UserCassandraDAO();
		ExternalResourceCassandraDAO externalResourceCassandraDAO = new ExternalResourceCassandraDAO();
		userCassandraDAO.init();
		externalResourceCassandraDAO.init();

		System.out.println(" --------------------------------------------");

		ExternalResource externalResource = new ExternalResource();
		externalResource.setURI("http://externalresourceuri");
		externalResource.setDescription("description of external resource");
		if (userCassandraDAO.existsUserWithName("Rafita")) {
			System.out.println("Rafita existe!");
			User userToDelete = userCassandraDAO.getUserWithName("Rafita");
			userCassandraDAO.delete(userToDelete.getURI());

		}

		if (userCassandraDAO.existsUserWithName("RafitaELOtro")) {
			System.out.println("RafitaElOtro existe!");
			User userToDelete = userCassandraDAO
					.getUserWithName("RafitaELOtro");
			userCassandraDAO.delete(userToDelete.getURI());
		}

		User user = new User();
		user.setURI("http://useruri");
		user.setName("Rafita");
		user.setPassword("PasswordDeRafita");
		user.addSearch("searchA");
		user.addSearch("searchB");

		User userElOtro = new User();
		userElOtro.setURI("http://useruri2");
		userElOtro.setName("RafitaElOtro");
		userElOtro.setPassword("PasswordDeRafita");
		userElOtro.addSearch("searchC");
		userElOtro.addSearch("searchD");

		externalResourceCassandraDAO.create(externalResource);
		userCassandraDAO.create(user);
		userCassandraDAO.create(userElOtro);

		ExternalResource readedExternalResource = externalResourceCassandraDAO
				.read("http://externalresourceuri");
		System.out.println("readedExternalResource> " + readedExternalResource);
		externalResourceCassandraDAO.delete("http://externalresourceuri");

		User readUser = userCassandraDAO.read("http://useruri");
		System.out.println("readed user> " + readUser);

		// userCassandraDAO.delete("http://useruri");
		System.out.println("Exiting test");

	}
}
