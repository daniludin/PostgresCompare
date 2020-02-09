package postgrescompare;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This program demonstrates how to get structural information of a database.
 *
 * @author www.codejava.net
 */
public class ReadDatabaseStructure {
	private static String NL = "\n";

	public static void main(String[] args) {
		String url = "jdbc:postgresql://localhost:5432/postgres";
		String username = "postgres";
		String password = "postgres";
		ReadDatabaseStructure rds = new ReadDatabaseStructure();
		try {
			System.out.println(rds.readDbStructure(url, username, password));
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Error connecting to url:  " + url + "\t" + e.getMessage());
		}
		;
	}

	public StringBuffer readDbStructure(String url, String username, String password) throws SQLException {
		StringBuffer result = new StringBuffer();
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException();
		}

		try {

			DatabaseMetaData meta = (DatabaseMetaData) conn.getMetaData();

			String catalog = null;
			String schemaPattern = null;
			String tableNamePattern = null;
			String[] types = { "TABLE", "VIEW" };

			result.append(listGeneralMetadata(meta));

			ResultSet rsTables = meta.getTables(catalog, schemaPattern, tableNamePattern, types);
			while (rsTables.next()) {
				String tableName = rsTables.getString(3);
				String tableSchema = rsTables.getString("TABLE_SCHEM");
				String tableType = rsTables.getString("TABLE_TYPE");
				if (false && !tableName.equals("member_praxis")) {
					continue;
				}
				result.append(NL);
				result.append(tableSchema + "." + tableName).append(NL);
				result.append(listAttributes(meta, catalog, schemaPattern, tableName));
				if (tableType.equals("TABLE")) {
					result.append(listPrimaryKeys(meta, catalog, tableSchema, tableName));					
				}
				result.append(listUniqueConstraints(meta, conn, catalog, tableSchema, tableName));
				result.append(listForeignKeys(meta, catalog, schemaPattern, tableName));

				ResultSet rsFunctions = meta.getFunctions(catalog, tableSchema, tableName);
				while (rsFunctions.next()) {
					// result.append("" + rsFunctions.getString(""));
					System.out.println("function found");
				}
				rsFunctions.close();
			}

			System.out.println(result.toString());
			rsTables.close();
			// conn.close();

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			result.append(ex.getMessage());
		}
		return result;

	}

	public String listPrimaryKeys(DatabaseMetaData meta, String catalog, String schemaPattern, String tableName)
			throws SQLException {
		StringBuffer result = new StringBuffer();
		ResultSet rsPK = meta.getPrimaryKeys(catalog, schemaPattern, tableName);
		StringBuffer pkIds = new StringBuffer();
		String pkName = new String();

		while (rsPK.next()) {

			pkName = rsPK.getString("PK_NAME");
			String primaryKeyColumn = rsPK.getString("COLUMN_NAME");
			pkIds.append(primaryKeyColumn).append(",");
		}

		pkIds.append(")");
		rsPK.close();
		result.append("\tCONSTRAINT " + pkName + " PRIMARY KEY (");
		result.append(pkIds.toString().replaceAll(",\\)", "\\)")).append(NL);
		return result.toString();
	}

	public String listAttributes(DatabaseMetaData meta, String catalog, String schemaPattern, String tableName)
			throws SQLException {
		StringBuffer result = new StringBuffer();
		ResultSet rsColumns = meta.getColumns(catalog, schemaPattern, tableName, null);

		while (rsColumns.next()) {
			String columnName = rsColumns.getString("COLUMN_NAME");
			String columnType = rsColumns.getString("TYPE_NAME").toUpperCase();
			int columnSize = rsColumns.getInt("COLUMN_SIZE");
			result.append("\t" + columnName + "\t" + columnType + "(" + columnSize + ")");
			if (rsColumns.getInt("NULLABLE") == 0) {
				result.append(" NOT NULL");
			}
			result.append(NL);
		}
		rsColumns.close();
		return result.toString();
	}

	public String listUniqueConstraints(DatabaseMetaData meta, Connection conn, String catalog, String tableSchema,
			String tableName) throws SQLException {
		StringBuffer result = new StringBuffer();
		String sql = "SELECT con.contype FROM pg_catalog.pg_constraint con "
				+ "INNER JOIN pg_catalog.pg_class rel ON rel.oid = con.conrelid "
				+ "INNER JOIN pg_catalog.pg_namespace nsp ON nsp.oid = connamespace " + "WHERE nsp.nspname = ?  "
				+ "AND rel.relname = ? " + "AND con.conname = ? ";

		List<UniqueConstraint> ucs = getUniqueConstraints(meta, tableSchema, tableName);
		for (UniqueConstraint uc : ucs) {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, tableSchema);
			ps.setString(2, tableName);
			ps.setString(3, uc.name);
			ResultSet rsInd = ps.executeQuery();
			if (rsInd.next()) {

				String type = rsInd.getString(1);
				if (type.equals("u")) {
					result.append("\tCONSTRAINT " + uc.name + " UNIQUE (");
					StringBuffer indexIds = new StringBuffer();

					for (String col : uc.columns) {
						indexIds.append(col).append(",");

					}
					indexIds.append(")");
					result.append(indexIds.toString().replaceAll(",\\)", "\\)")).append(NL);
				}
			} else {
				System.err.println("error: no entr in catalog for this Unique Constraint");
			}
			ps.close();
			rsInd.close();
		}
		return result.toString();
	}

	public String listForeignKeys(DatabaseMetaData meta, String catalog, String schemaPattern, String tableName)
			throws SQLException {
		StringBuffer result = new StringBuffer();
		ResultSet rsFk = meta.getImportedKeys(catalog, schemaPattern, tableName);

		while (rsFk.next()) {
			// result.append("\tFOREIGN KEY " +
			// rsFk.getString("FKTABLE_SCHEM")).append(".");
			result.append("\tFOREIGN KEY  ");
			result.append(rsFk.getString("FKTABLE_NAME"));
			result.append(" (").append(rsFk.getString("FKCOLUMN_NAME")).append(") ").append(NL);

			result.append("\t\tREFERENCES " + rsFk.getString("PKTABLE_SCHEM")).append(".");
			result.append(rsFk.getString("PKTABLE_NAME"));
			result.append(" (").append(rsFk.getString("PKCOLUMN_NAME")).append(")").append(NL);
			if (rsFk.getInt("UPDATE_RULE") == DatabaseMetaData.importedKeyNoAction) {
				result.append("\t\t\tON UPDATE NO ACTION").append(NL);
			} else if (rsFk.getInt("UPDATE_RULE") == DatabaseMetaData.importedKeyCascade) {
				result.append("\t\t\tON UPDATE CASCADE").append(NL);
			}
			if (rsFk.getInt("DELETE_RULE") == DatabaseMetaData.importedKeyNoAction) {
				result.append("\t\t\tON DELETE NO ACTION").append(NL);
			} else if (rsFk.getInt("DELETE_RULE") == DatabaseMetaData.importedKeyCascade) {
				result.append("\t\t\tON DELETE CASCADE").append(NL);
			}

		}
		rsFk.close();
		return result.toString();
	}

	public static List<UniqueConstraint> getUniqueConstraints(DatabaseMetaData dm, String schema, String table)
			throws SQLException {
		Map<String, UniqueConstraint> constraints = new HashMap<>();

		ResultSet rs = dm.getIndexInfo(null, schema, table, false, false);
		while (rs.next()) {
			String indexName = rs.getString("index_name");
			String columnName = rs.getString("column_name");

			UniqueConstraint constraint = new UniqueConstraint();
			constraint.table = table;
			constraint.name = indexName;
			constraint.columns.add(columnName);

			constraints.compute(indexName, (key, value) -> {
				if (value == null) {
					return constraint;
				}
				value.columns.add(columnName);
				return value;
			});
		}

		return new ArrayList<>(constraints.values());
	}

	public static String listGeneralMetadata(DatabaseMetaData meta) throws SQLException {
		StringBuffer result = new StringBuffer();
		result.append("Database Product Name: " + meta.getDatabaseProductName()).append(NL);
		result.append("Database Product Version: " + meta.getDatabaseProductVersion()).append(NL);
		result.append("Logged User: " + meta.getUserName()).append(NL);
		result.append("JDBC Driver: " + meta.getDriverName()).append(NL);
		result.append("Driver Version: " + meta.getDriverVersion()).append(NL);
		result.append(NL);
		return result.toString();
	}

	public static Connection createConnection() {

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();
		}

		System.out.println("PostgreSQL JDBC Driver Registered!");

		Connection connection = null;
		try {
			connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
					"postgres");
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}
		return connection;
	}

}