package fourThirtya2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Backend {

	private static Connection conn;

	/*
	 * The main call for the application. Creates the SSH tunnel, the JDBC
	 * connection, and creates the UI for the database modifications.
	 */
	public static void main(String[] args) throws SQLException {
		TunneledConnection tunnel = new TunneledConnection();
		conn = tunnel.createTunnel();
		if (conn != null) {
			System.out.println("--> Connection Open");
			ModelView view = new ModelView();
			view.run();
		}
	}
	
	/*
	 * I designed this to grab ONLY the table names of the tables on the
	 * database. There should be minimal connection waste since I'm just
	 * pulling metadata. Returns a ResultSet of table names.
	 */
	static ResultSet getAllTables() throws SQLException{
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet temp = meta.getTables(null, null, null, new String[]{"TABLE"});
		return temp;
	}
	
	/*
	 * This pulls the actual table, returning a ResultSet of the table data.
	 */
	static ResultSet getTable(String name){
		Statement stmt;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM " + name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
}
