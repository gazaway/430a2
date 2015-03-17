import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
 
 
public class Tunnel {
	
	private Connection conn = null;
	private Session session = null;
	
	/*
	 * Creates an SSH session as well as port forwarding.
	 * This allows this program to update the database
	 * from any connection, anywhere.
	 */
	public Connection createTunnel(){
        String host="faure.cs.colostate.edu";
		int localPort=5656;
        int remotePort=5432;
        String user="gazawayj";
        String in="?1meanpassword";
        String dbpassword = "829326478";
        String url = "jdbc:postgresql://localhost:"+localPort+"/"+user;
        try{
            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            //Port forwarding
            JSch jsch = new JSch();
            session=jsch.getSession(user, host, 22);
            session.setPassword(in);
            session.setConfig(config);
            session.connect();
            session.setPortForwardingL(localPort, host, remotePort);
            Class.forName("org.postgresql.Driver").newInstance();
            conn = DriverManager.getConnection (url, user, dbpassword);
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("--> Connection Open");
        return conn;
	}
	
	/*
	 * Closes the SSH session and JDBC connection.
	 */
	public void close() throws SQLException{
		conn.close();
		session.disconnect();
		System.out.println("--> Connection Closed");
	}
}