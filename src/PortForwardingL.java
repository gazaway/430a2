import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
 
 
public class PortForwardingL {
 
    public static void main(String[] args) throws SQLException {
 
        int lport=5656;
        String rhost="faure.cs.colostate.edu";
        String host="faure.cs.colostate.edu";
        int rport=5432;
        String user="gazawayj";
        String password="?1meanpassword";
        String dbuserName = "gazawayj";
        String dbpassword = "829326478";
        String url = "jdbc:postgresql://localhost:"+lport+"/gazawayj";
        String driverName="org.postgresql.Driver";
        Connection conn = null;
        Session session= null;
        try{
            //Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
            java.util.Properties config = new java.util.Properties(); 
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            session=jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            int assinged_port=session.setPortForwardingL(lport, rhost, rport);
            System.out.println("localhost:"+assinged_port+" -> "+rhost+":"+rport);
            System.out.println("Port Forwarded");
             
            //mysql database connectivity
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection (url, dbuserName, dbpassword);
            System.out.println ("Database connection established");
            try {
				Statement stmt = conn.createStatement();
				int resultSet = stmt.executeUpdate("INSERT INTO test values('199', 'Tim');");
				System.out.println("INSERTED TO TABLE");
			} catch (SQLException e) {
				System.out.println(e);
			}
            System.out.println("DONE");
        }catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }finally{
            if(conn != null && !conn.isClosed()){
                System.out.println("Closing Database Connection");
                conn.close();
            }
            if(session !=null && session.isConnected()){
                System.out.println("Closing SSH Connection");
                session.disconnect();
            }
        }
    }
 
}