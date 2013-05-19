package epnoi.model.dao.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.model.BasicColumnDefinition;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnIndexType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.SliceQuery;

public class CassandraDAO {
	public static final String CLUSTER = "epnoiCluster";
	public static final String KEYSPACE = "epnoiKeyspace";

	protected static Cluster cluster = null;

	protected static KeyspaceDefinition keyspaceDefinition = null;
	protected static Keyspace keyspace = null;

	protected static Map<String, ColumnFamilyTemplate<String, String>> columnFamilyTemplates = null;
	protected static List<ColumnFamilyDefinition> columnFamilyDefinitions = null;

	public void init() {

		CassandraDAO.cluster = HFactory.getOrCreateCluster(CLUSTER,
				"localhost:9160");
		System.out.println("Cluster instantiated");

		List<String> columnFamillyNames = Arrays.asList(
				ExternalResourceCassandraHelper.COLUMN_FAMILLY,
				UserCassandraHelper.COLUMN_FAMILLY);

		if (CassandraDAO.columnFamilyDefinitions == null) {
			System.out.println("Intializing columnFamilyDefinitions");
			ColumnFamilyDefinition columnFamilyDefinition = null;

			CassandraDAO.columnFamilyDefinitions = new ArrayList<ColumnFamilyDefinition>();
			for (String columnFamilyName : columnFamillyNames) {
				if (columnFamilyName.equals(UserCassandraHelper.COLUMN_FAMILLY)) {

					BasicColumnDefinition columnDefinition = new BasicColumnDefinition();
					columnDefinition.setName(StringSerializer.get()
							.toByteBuffer(UserCassandraHelper.NAME));
					columnDefinition.setIndexName("NAME_INDEX");
					columnDefinition.setIndexType(ColumnIndexType.KEYS);
					columnDefinition.setValidationClass(ComparatorType.UTF8TYPE
							.getClassName());

					// columnFamilyDefinition.addColumnDefinition(columnDefinition);
					List<ColumnDefinition> columnsDefinition = new ArrayList<ColumnDefinition>();
					columnsDefinition.add(columnDefinition);
					columnFamilyDefinition = HFactory
							.createColumnFamilyDefinition(KEYSPACE,
									columnFamilyName, ComparatorType.UTF8TYPE,
									columnsDefinition);
				} else {
					columnFamilyDefinition = HFactory
							.createColumnFamilyDefinition(KEYSPACE,
									columnFamilyName, ComparatorType.UTF8TYPE);
				}
				CassandraDAO.columnFamilyDefinitions
						.add(columnFamilyDefinition);

			}
		} else {
			System.out
					.println("columnFamilyDefinitions was already initialized");
		}

		if (CassandraDAO.keyspaceDefinition == null) {
			CassandraDAO.keyspaceDefinition = cluster
					.describeKeyspace(KEYSPACE);
		}

		if (CassandraDAO.keyspaceDefinition == null) {
			// if the keyspace doesn't exist, it creates one
			CassandraDAO.keyspaceDefinition = HFactory
					.createKeyspaceDefinition(KEYSPACE,
							ThriftKsDef.DEF_STRATEGY_CLASS, 1,
							columnFamilyDefinitions);

			cluster.addKeyspace(CassandraDAO.keyspaceDefinition, true);
			System.out.println("Keyspace " + KEYSPACE + " created");
		} else {
			System.out.println("The keyspace was already initialized");

		}
		if (CassandraDAO.keyspace == null) {
			CassandraDAO.keyspace = HFactory.createKeyspace(KEYSPACE,
					CassandraDAO.cluster);
			System.out.println("Keyspace " + KEYSPACE + " instantiated");
		}

		if (CassandraDAO.columnFamilyTemplates == null) {
			CassandraDAO.columnFamilyTemplates = new HashMap<String, ColumnFamilyTemplate<String, String>>();
			ColumnFamilyTemplate<String, String> columnFamilyTemplate;

			for (String columnFamilyName : columnFamillyNames) {
				System.out.println("ct " + columnFamilyName);
				columnFamilyTemplate = new ThriftColumnFamilyTemplate<String, String>(
						CassandraDAO.keyspace, columnFamilyName,
						StringSerializer.get(), StringSerializer.get());

				CassandraDAO.columnFamilyTemplates.put(columnFamilyName,
						columnFamilyTemplate);
			}
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	protected void createRow(String key, String columnFamilyName) {

		ColumnFamilyUpdater<String, String> updater = CassandraDAO.columnFamilyTemplates
				.get(columnFamilyName).createUpdater(key);

		try {
			CassandraDAO.columnFamilyTemplates.get(columnFamilyName).update(
					updater);

		} catch (HectorException e) {

			System.out.println(e.getMessage());
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	protected void updateColumn(String key, String name, String value,
			String columnFamilyName) {
		ColumnFamilyUpdater<String, String> updater = CassandraDAO.columnFamilyTemplates
				.get(columnFamilyName).createUpdater(key);
		updater.setString(name, value);

		try {
			CassandraDAO.columnFamilyTemplates.get(columnFamilyName).update(
					updater);

		} catch (HectorException e) {

			System.out.println(e.getMessage());
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	protected ColumnFamilyResult<String, String> readRow(String key,
			String columnFamilyName) {
		ColumnFamilyResult<String, String> result = null;
		try {
			result = CassandraDAO.columnFamilyTemplates.get(columnFamilyName)
					.queryColumns(key);
			System.out.println("User: " + result.getString("name") + " "
					+ result.getString("last"));

		} catch (HectorException e) {
			System.out.println("Not possible to read the column with key "
					+ key);
		}
		return result;
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	protected void deleteRow(String key, String columnFamilyName) {
		try {
			CassandraDAO.columnFamilyTemplates.get(columnFamilyName).deleteRow(
					key);
		} catch (HectorException e) {
			System.out.println("Not possible to delete row with key " + key);
		}
		/*
		 * Esto estaba antes, mirar si vale para algo try {
		 * columnFamilyTemplates.get(columnFamilyName).deleteRow(key); } catch
		 * (HectorException e) {
		 * System.out.println("Not possible to delete row with key " + key); }
		 */
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 
	 * @param key
	 * @param columnFamilyKey
	 * @return
	 */
	protected ColumnSliceIterator<String, String, String> getAllCollumns(
			String key, String columnFamilyKey) {

		SliceQuery<String, String, String> query = HFactory
				.createSliceQuery(keyspace, StringSerializer.get(),
						StringSerializer.get(), StringSerializer.get())
				.setKey(key).setColumnFamily(columnFamilyKey);

		ColumnSliceIterator<String, String, String> iterator = new ColumnSliceIterator<String, String, String>(
				query, null, "\uFFFF", false);
		return iterator;

	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	public static void main(String[] args) {
		String USER_CF = "USER_CF";

		CassandraDAO cassandraDAO = new CassandraDAO();
		cassandraDAO.init();
		cassandraDAO.updateColumn("http://whatever", "pepito", "grillo",
				USER_CF);
		cassandraDAO.updateColumn("http://whatever", "pepito", "grillo2",
				USER_CF);

		cassandraDAO.readRow("http://whatever", USER_CF);
		// cassandraDAOProof.delete("http://whatever");
		// cassandraDAOProof.read("http://whatever");
		cassandraDAO.getAllCollumns("http://whatever", USER_CF);

	}
}