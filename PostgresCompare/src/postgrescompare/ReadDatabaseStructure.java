package postgrescompare;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.postgresql.jdbc.PgDatabaseMetaData;

/**
 * This program demonstrates how to get structural information of a database.
 *
 * @author www.codejava.net
 */
public class ReadDatabaseStructure {
	
	public static void main(String[] args) {
		String url = "jdbc:postgresql://localhost:5432/postgres";
		String username = "postgres";
		String password = "postgres";
		ReadDatabaseStructure rds = new ReadDatabaseStructure();
		rds.readDbStructure(url, username, password);
	}


	public StringBuffer readDbStructure(String url, String username, String password) {
		StringBuffer result = new StringBuffer();
		try (Connection conn = DriverManager.getConnection(url, username, password)) {

			PgDatabaseMetaData meta = (PgDatabaseMetaData) conn.getMetaData();

			String catalog = null, schemaPattern = null, tableNamePattern = null;
			String[] types = { "TABLE" };

			ResultSet rsTables = meta.getTables(catalog, schemaPattern, tableNamePattern, types);

			while (rsTables.next()) {
				String tableName = rsTables.getString(3);
				String tableSchema = rsTables.getString("TABLE_SCHEM");
				if (false && !tableName.equals("member_praxis")) {
					continue;
				}
				result.append("\r\n");
				result.append(tableSchema + "." + tableName).append("\r\n");

				String columnNamePattern = null;
				ResultSet rsColumns = meta.getColumns(catalog, schemaPattern, tableName, columnNamePattern);

				while (rsColumns.next()) {
					String columnName = rsColumns.getString("COLUMN_NAME");
					String columnType = rsColumns.getString("TYPE_NAME").toUpperCase();
					int columnSize = rsColumns.getInt("COLUMN_SIZE");
					result.append("\t" + columnName + "\t" + columnType + "(" + columnSize + ")");
					if (rsColumns.getInt("NULLABLE") == 0) {
						result.append(" NOT NULL");
					}
					result.append("\r\n");
//					result.append("\tNULLABLE" + rsColumns.getString("NULLABLE")).append("\r\n");
//					result.append("\tDECIMAL_DIGITS" + rsColumns.getString("DECIMAL_DIGITS")).append("\r\n");
//					result.append("\tNUM_PREC_RADIX" + rsColumns.getString("NUM_PREC_RADIX")).append("\r\n");
					// NUM_PREC_RADIX
				}

				ResultSet rsPK = meta.getPrimaryKeys(catalog, schemaPattern, tableName);
				StringBuffer pkIds = new StringBuffer();
				String pkName = new String();

				while (rsPK.next()) {

					pkName = rsPK.getString("PK_NAME");
					String primaryKeyColumn = rsPK.getString("COLUMN_NAME");
					pkIds.append(primaryKeyColumn).append(",");
				}
				pkIds.append(")");

				result.append("\tCONSTRAINT " + pkName + " PRIMARY KEY (");
				result.append(pkIds.toString().replaceAll(",\\)", "\\)")).append("\r\n");


				String sql = "SELECT con.contype FROM pg_catalog.pg_constraint con "
						+ "INNER JOIN pg_catalog.pg_class rel ON rel.oid = con.conrelid "
						+ "INNER JOIN pg_catalog.pg_namespace nsp ON nsp.oid = connamespace "
						+ "WHERE nsp.nspname = ?  " + "AND rel.relname = ? " + "AND con.conname = ? ";

				List<UniqueConstraint> ucs = getUniqueConstraints(meta, tableSchema, tableName);
				for (UniqueConstraint uc : ucs) {
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setString(1, tableSchema);
					ps.setString(2, tableName);
					ps.setString(3, uc.name);
					ResultSet rsInd = ps.executeQuery();
					rsInd.next();
					String type = rsInd.getString(1);

//					if (type.equals("p")) {
//						result.append("\tCONSTRAINT " + uc.name +" PRIMARY KEY (");
//					}
					if (type.equals("u")) {
						result.append("\tCONSTRAINT " + uc.name + " UNIQUE (");
						StringBuffer indexIds = new StringBuffer();

						for (String col : uc.columns) {
							indexIds.append(col).append(",");

						}
						indexIds.append(")");
						result.append(indexIds.toString().replaceAll(",\\)", "\\)")).append("\r\n");
					}

				}

				ResultSet rsFk = meta.getImportedKeys(catalog, schemaPattern, tableName);

				while (rsFk.next()) {
					//result.append("\tFOREIGN KEY  " + rsFk.getString("FKTABLE_SCHEM")).append(".");
					result.append("\tFOREIGN KEY  ");
					result.append(rsFk.getString("FKTABLE_NAME"));
					result.append(" (").append(rsFk.getString("FKCOLUMN_NAME")).append(") ").append("\n");
					
					
					result.append("\t\tREFERENCES " + rsFk.getString("PKTABLE_SCHEM")).append(".");
					result.append(rsFk.getString("PKTABLE_NAME"));
					result.append(" (").append(rsFk.getString("PKCOLUMN_NAME")).append(")").append("\n");
					if (rsFk.getInt("UPDATE_RULE") == PgDatabaseMetaData.importedKeyNoAction) {
						result.append("\t\t\tON UPDATE NO ACTION").append("\n");
					}
					else if (rsFk.getInt("UPDATE_RULE") == PgDatabaseMetaData.importedKeyCascade) {
						result.append("\t\t\tON UPDATE CASCADE").append("\n");
					}
					if (rsFk.getInt("DELETE_RULE") == PgDatabaseMetaData.importedKeyNoAction) {
						result.append("\t\t\tON DELETE NO ACTION").append("\n");
					}
					else if (rsFk.getInt("DELETE_RULE") == PgDatabaseMetaData.importedKeyCascade) {
						result.append("\t\t\tON DELETE CASCADE").append("\n");
					}
					
				}

				ResultSet rsFunctions = meta.getFunctions(catalog, tableSchema, tableName);
				while (rsFunctions.next()) {
					//result.append("" + rsFunctions.getString(""));
					System.out.println("function found");
				}
			}

			
			System.out.println(result.toString());

		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			result.append(ex.getMessage());
		}
		return result;
		
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