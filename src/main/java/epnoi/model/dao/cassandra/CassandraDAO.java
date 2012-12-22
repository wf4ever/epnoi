package epnoi.model.dao.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ColumnSliceIterator;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.cassandra.service.template.ThriftColumnFamilyTemplate;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.SliceQuery;

public class CassandraDAO {
	public static final String CLUSTER = "RecommenderServiceCluster";
	public static final String KEYSPACE = "RecommenderServiceKeyspace";


	protected Cluster cluster;

	protected KeyspaceDefinition keyspaceDefinition;
	protected Keyspace keyspace;
	public static String USER_CF ="CF";

	protected Map<String, ColumnFamilyTemplate<String, String>> columnFamilyTemplates;
	protected List<ColumnFamilyDefinition> columnFamilyDefinitions;

	// protected ColumnFamilyTemplate<String, String> userColumnFamilyTemplate;

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	public void init() {
		// this.columnFamilyDefinitions = new
		// ArrayList<ColumnFamilyDefinition>();
		// this.columnFamilyTemplates = new HashMap<String,
		// ColumnFamilyTemplate<String, String>>();

		cluster = HFactory.getOrCreateCluster(CLUSTER, "localhost:9160");
		System.out.println("Cluster instantiated");

		List<String> columnFamillyNames = Arrays.asList(ExternalResourceCassandraHelper.COLUMN_FAMILLY);
		// ExternalResourceCassandraHelper.COLUMN_FAMILLY);

		ColumnFamilyDefinition columnFamilyDefinition;
		// ColumnFamilyTemplate<String, String> columnFamilyTemplate;
		this.columnFamilyDefinitions = new ArrayList<ColumnFamilyDefinition>();
		for (String columnFamilyName : columnFamillyNames) {
			System.out.println("cd " + columnFamilyName);
			columnFamilyDefinition = HFactory.createColumnFamilyDefinition(
					KEYSPACE, columnFamilyName, ComparatorType.UTF8TYPE);

			this.columnFamilyDefinitions.add(columnFamilyDefinition);

		}
		// System.out.println("Column family definition: "
		// + this.columnFamilyDefinition);

		keyspaceDefinition = cluster.describeKeyspace(KEYSPACE);// tries to
		// instantiate an
		// existing keyspace
		if (keyspaceDefinition == null) {
			// if the keyspace doesn't exist, it creates one
			keyspaceDefinition = HFactory.createKeyspaceDefinition(
					KEYSPACE, ThriftKsDef.DEF_STRATEGY_CLASS, 1,
					columnFamilyDefinitions);

			cluster.addKeyspace(keyspaceDefinition, true);
			System.out.println("Keyspace " + KEYSPACE + " created");
		} else {
			System.out.println("The keyspace was already initialized");
			/*
			 * try {
			 * 
			 * cluster.addColumnFamily(userColumnFamilyDefinition);
			 * cluster.addColumnFamily(externalResourceColumnFamilyDefinition);
			 * 
			 * 
			 * } catch (HectorException e) { e.printStackTrace(); }
			 */
		}
		keyspace = HFactory.createKeyspace(KEYSPACE, cluster);
		System.out.println("Keyspace " + KEYSPACE + " instantiated");
		this.columnFamilyTemplates = new HashMap<String, ColumnFamilyTemplate<String, String>>();
		ColumnFamilyTemplate<String, String> columnFamilyTemplate;

		for (String columnFamilyName : columnFamillyNames) {
			System.out.println("ct " + columnFamilyName);
			columnFamilyTemplate = new ThriftColumnFamilyTemplate<String, String>(
					keyspace, columnFamilyName, StringSerializer.get(),
					StringSerializer.get());

			this.columnFamilyTemplates.put(columnFamilyName,
					columnFamilyTemplate);
		}

		/*
		 * this.userColumnFamilyTemplate = new
		 * ThriftColumnFamilyTemplate<String, String>( keyspace, USER_CF,
		 * StringSerializer.get(), StringSerializer.get());
		 */
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	protected void createRow(String key, String columnFamilyName) {
		// System.out.println("----->" + userColumnFamilyTemplate.getClock());
		
		 System.out.println(">> "+key+"|"+ this.columnFamilyTemplates.get(key));
		 
		 /*
			 * System.out.println("k> " + key + "columnFN> " + columnFamilyName
			 * + " usercft> " + userColumnFamilyTemplate);
			 */
		ColumnFamilyUpdater<String, String> updater = this.columnFamilyTemplates
				.get(columnFamilyName).createUpdater(key);
		
	

		// ColumnFamilyUpdater<String, String> updater =
		// (this.columnFamilyTemplates
		// .get(columnFamilyName)).createUpdater(key);
		try {
			this.columnFamilyTemplates.get(columnFamilyName).update(updater);
			// columnFamilyTemplates.get(columnFamilyName).update(updater);
			System.out.println("value created");
		} catch (HectorException e) {
			System.out.println("Error during creation");
			System.out.println(e.getMessage());
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	protected void updateColumn(String key, String name, String value,
			String columnFamilyName) {
		ColumnFamilyUpdater<String, String> updater = columnFamilyTemplates
				.get(columnFamilyName).createUpdater(key);
		updater.setString(name, value);

		try {
			columnFamilyTemplates.get(columnFamilyName).update(updater);
			System.out.println("value inserted");
		} catch (HectorException e) {
			System.out.println("Error during insertion");
			System.out.println(e.getMessage());
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	protected ColumnFamilyResult<String, String> readRow(String key,
			String columnFamilyName) {
		ColumnFamilyResult<String, String> result = null;
		try {
			result = columnFamilyTemplates.get(columnFamilyName).queryColumns(
					key);
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
			columnFamilyTemplates.get(columnFamilyName).deleteRow(key);
		} catch (HectorException e) {
			System.out.println("Not possible to delete row with key " + key);
		}

		try {
			columnFamilyTemplates.get(columnFamilyName).deleteRow(key);
		} catch (HectorException e) {
			System.out.println("Not possible to delete row with key " + key);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	protected ColumnSliceIterator<String, String, String> getAllCollumns(
			String key, String columnFamilyKey) {
/*ESTABAS AQUI!*/
		SliceQuery<String, String, String> query = HFactory
				.createSliceQuery(keyspace, StringSerializer.get(),
						StringSerializer.get(), StringSerializer.get())
				.setKey(key).setColumnFamily("USER_CF");

		ColumnSliceIterator<String, String, String> iterator = new ColumnSliceIterator<String, String, String>(
				query, null, "\uFFFF", false);
		return iterator;

	}

	// ---------------------------------------------------------------------------------------------------------------------------------------------------

	public static void main(String[] args) {
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