package epnoi.model.dao.cassandra;

import java.util.ArrayList;
import java.util.List;

import scala.Array;

import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.Row;
import epnoi.model.ExternalResource;
import epnoi.model.Search;
import epnoi.model.User;

public class SearchCassandraDAO extends CassandraDAO {

	public void delete(String URI) {
		super.deleteRow(URI, SearchCassandraHelper.COLUMN_FAMILLY);
	}

	// --------------------------------------------------------------------------------

	public void create(Search search) {
		super.createRow(search.getURI(), SearchCassandraHelper.COLUMN_FAMILLY);

		if (search.getTitle() != null) {

			super.updateColumn(search.getURI(), SearchCassandraHelper.TITLE,
					search.getTitle(), SearchCassandraHelper.COLUMN_FAMILLY);

		}

		if (search.getDescription() != null) {
			super.updateColumn(search.getURI(),
					SearchCassandraHelper.DESCRIPTION, search.getDescription(),
					SearchCassandraHelper.COLUMN_FAMILLY);

		}

		for (String expression : search.getExpressions()) {
			super.updateColumn(search.getURI(), expression,
					SearchCassandraHelper.EXPRESSIONS,
					SearchCassandraHelper.COLUMN_FAMILLY);
		}

	}

	// --------------------------------------------------------------------------------

	public Search read(String URI) {
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
				.getAllCollumns(URI, SearchCassandraHelper.COLUMN_FAMILLY);
		if (columnsIterator.hasNext()) {
			Search search = new Search();
			search.setURI(URI);
			while (columnsIterator.hasNext()) {

				HColumn<String, String> column = columnsIterator.next();
				System.out.println("--column " + column);
				if (SearchCassandraHelper.TITLE.equals(column.getName())) {
					search.setTitle(column.getValue());

				} else {
					if (SearchCassandraHelper.DESCRIPTION.equals(column
							.getName())) {
						search.setDescription(column.getValue());
					} else {
						if (SearchCassandraHelper.EXPRESSIONS.equals(column
								.getValue())) {
							search.addExpression(column.getName());
						}
					}

				}
			}

			return search;
		}

		return null;
	}

	// --------------------------------------------------------------------------------

	public void update(Search search) {
		super.updateColumn(search.getURI(), SearchCassandraHelper.DESCRIPTION,
				search.getDescription(), UserCassandraHelper.COLUMN_FAMILLY);
	}

	// --------------------------------------------------------------------------------
	/*
	 * public boolean existsUserWithName(String name) { List result =
	 * (CassandraCQLClient .query("select * from User where NAME='" + name +
	 * "'")); return ((result != null) && (result.size() > 0)); }
	 * 
	 * //
	 * ------------------------------------------------------------------------
	 * -------- public User getUserWithName(String name) { List result =
	 * (CassandraCQLClient .query("select * from User where NAME='" + name +
	 * "'")); if ((result != null) && (result.size() > 0)) { Row row = (Row)
	 * result.get(0); User user = this.read((String) row.getKey()); return user;
	 * } return null; }
	 */

	public List getSearchs() {
		List<Search> searchs = new ArrayList<Search>();
		List<Row<String, String, String>> result = (CassandraCQLClient
				.query("select * from User"));
		for (Row row : result) {

			Search user = this.read((String) row.getKey());

		}
		return searchs;
	}
}
