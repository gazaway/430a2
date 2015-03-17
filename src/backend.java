import java.sql.*;
import java.util.Properties;


public class backend {
	
	private static Connection conn;
	
	public static boolean createTunnel(String url){
		 Properties props = new Properties();
		 props.setProperty("user", "gazawayj");
		 props.setProperty("password", "829326478");
		 try {
			conn = DriverManager.getConnection(url, props);
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("shit");
			return false;
		}
		 return true;
	}

	public static void main(String[] args) {
		String url = "jdbc:postgresql://faure.cs.colostate.edu:5432/gazawayj";
		if (createTunnel(url)){
			 try {
				Statement stmt = conn.createStatement();
				ResultSet resultSet = stmt.executeQuery("INSERT INTO test values('123', 'Jim');");
				System.out.println("INSERTED TO TABLE");
			} catch (SQLException e) {
			}

		 }
		 

	}

}
