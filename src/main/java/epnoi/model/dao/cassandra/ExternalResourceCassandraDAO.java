package epnoi.model.dao.cassandra;

import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.hector.api.beans.HColumn;
import epnoi.model.ExternalResource;

public class ExternalResourceCassandraDAO extends CassandraDAO {

	public void delete(String URI) {
		super.deleteRow(URI, ExternalResourceCassandraHelper.COLUMN_FAMILLY);
	}

	// --------------------------------------------------------------------------------

	public void create(ExternalResource externalResource) {
		super.createRow(externalResource.getURI(), ExternalResourceCassandraHelper.COLUMN_FAMILLY);
		if (externalResource.getDescription()!=null){
		super.updateColumn(externalResource.getURI(),
				ExternalResourceCassandraHelper.DESCRIPTION,
				externalResource.getDescription(),
				ExternalResourceCassandraHelper.COLUMN_FAMILLY);
		}
		
	}

	// --------------------------------------------------------------------------------

	public ExternalResource read(String URI) {

		ColumnSliceIterator<String, String, String> columnsIterator = super
				.getAllCollumns(URI,
						ExternalResourceCassandraHelper.COLUMN_FAMILLY);
		if (columnsIterator.hasNext()) {
			ExternalResource externalResource = new ExternalResource();
			externalResource.setURI(URI);
			while (columnsIterator.hasNext()) {
				HColumn<String, String> column = columnsIterator.next();
				if (ExternalResourceCassandraHelper.DESCRIPTION.equals(column
						.getName())) {
					externalResource.setDescription(column.getValue());

				}
			}

			return externalResource;
		}

		return null;
	}

	// --------------------------------------------------------------------------------

	public void update(ExternalResource externalResource) {
		super.updateColumn(externalResource.getURI(),
				ExternalResourceCassandraHelper.DESCRIPTION,
				externalResource.getDescription(),
				ExternalResourceCassandraHelper.COLUMN_FAMILLY);
	}

	// --------------------------------------------------------------------------------

	public static void main(String[] args) {
		ExternalResourceCassandraDAO externalResourceCassandraDAO = new ExternalResourceCassandraDAO();
		externalResourceCassandraDAO.init();
		System.out.println("Starting test");
		
		ExternalResource externalResource = new ExternalResource();
		externalResource.setURI("http://uriproof");
		externalResource.setDescription("description proof");

		ExternalResource externalResource2 = new ExternalResource();
		externalResource2.setURI("http://uriproof2");
		externalResource2.setDescription("description proof2");

		externalResourceCassandraDAO.create(externalResource);

		externalResourceCassandraDAO.update(externalResource2);
		externalResourceCassandraDAO.delete("http://uriproof2");
		// externalResourceCassandraDAO.delete("http://uriproof");

		//externalResourceCassandraDAO.delete("http://uriproof");

		ExternalResource readedExternalResource = externalResourceCassandraDAO
				.read("http://uriproof");
		System.out
				.println("readedExternalResource.> " + readedExternalResource);
		System.out.println("Exiting test");

	}
}
