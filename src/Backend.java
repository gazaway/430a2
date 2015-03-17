import java.sql.*;

public class Backend {
	
	private static Connection conn;
	
		/*
		 * The call for the application. Creates the SSH tunnel,
		 * the JDBC connection, and creates the UI for the database
		 * modifications.
		 */
		public static void main(String[] args) throws SQLException {
			Tunnel tunnel = new Tunnel();
			conn = tunnel.createTunnel();
			if (conn != null){
				View view = new View();
				view.run();
			}
			tunnel.close();
		}
}
