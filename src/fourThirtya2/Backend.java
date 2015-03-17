package fourThirtya2;

import java.sql.*;

public class Backend {

	private static Connection conn;

	/*
	 * The main call for the application. Creates the SSH tunnel, the JDBC
	 * connection, and creates the UI for the database modifications.
	 */
	public static void main(String[] args) throws SQLException {
		Tunnel tunnel = new Tunnel();
		conn = tunnel.createTunnel();
		if (conn != null) {
			System.out.println("--> Connection Open");
			View view = new View(conn);
			view.run();
		}
	}
	
	static ResultSet getAllTables() throws SQLException{
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet temp = meta.getTables(null, null, null, new String[]{"TABLE"});
		return temp;
	}
	
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
