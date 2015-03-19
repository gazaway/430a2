package fourThirtya2;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Backend {

	private static Connection conn;
	
	/*
	 * This is my attempt to always close the connection on exit. This
	 * uses a shutdown hook to accomplish 
	 */
	static class OnClose extends Thread {
		public void run(){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * The main call for the application. Creates the SSH tunnel, the JDBC
	 * connection, and creates the UI for the database modifications.
	 */
	public static void main(String[] args) throws SQLException {
		TunneledConnection tunnel = new TunneledConnection();
		conn = tunnel.createTunnel();
		if (conn != null) {
			Runtime.getRuntime().addShutdownHook(new OnClose() );
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
			stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			rs = stmt.executeQuery("SELECT * FROM " + name);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	//TELLS ME IF I HAVE THE BOOK ALREADY
	public static boolean haveBook(int ISBN){
		PreparedStatement stmt;
		ResultSet rs;
		try {
			stmt = conn.prepareStatement("SELECT * FROM books WHERE isbn_num=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, ISBN);
			rs = stmt.executeQuery();
			int size = -1;
			try {
				rs.beforeFirst();
				rs.last();
				size = rs.getRow();
				rs.beforeFirst();
			} catch (SQLException e) {
				System.out.println(e);
			}
			//MEANS DATABASE DOESN'T HAVE THE BOOK
			if (size >0){
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e){
			System.out.println(e);
			return false;
		}
		
	}

	public static void addStudent(int iD, String fname, String lname, String degree) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO students (studentid, first_name, last_name, degree) VALUES" + "(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, iD);
			stmt.setString(2, fname);
			stmt.setString(3, lname);
			stmt.setString(4, degree);
			stmt.executeUpdate();
			System.out.println("Table insert successful.");
		} catch (Exception e){
			System.out.println(e);
		}
		
	}

	public static boolean removeStudent(int iD) {
		//check if student had any books.
		PreparedStatement stmt;
		ResultSet rs;
		try {
			//returns a list of bookIDs associated with the student
			stmt = conn.prepareStatement("SELECT bookissued FROM books2students WHERE studentid = ?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, iD);
			int size = -1;
			rs = stmt.executeQuery();
			if (rs != null) {
				rs.beforeFirst();
				rs.last();
				size = rs.getRow();
				rs.beforeFirst();
				int[] books = new int[size];
				int i = 0;
				while (rs.next()) {
					int temp = rs.getInt(1);
					books[i] = temp;
					i++;
				}
				//now have all the bookIDs in an array, just need to remove all associations. Delete relation from students2books and incriment on books.
				stmt = conn.prepareStatement("DELETE FROM books2students WHERE studentid=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				for (int j = 0; j < books.length; j++){
					incrimentNumCount(books[j]);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		try {
			stmt = conn.prepareStatement("DELETE FROM students WHERE studentid=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, iD);
			stmt.executeUpdate();
			System.out.println("Table deletion successful.");
			return true;
		} catch (Exception e){
			System.out.println(e);
			return false;
		}
	}

	public static void incrimentNumCount(int ISBN) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("UPDATE books SET numcopiesavail = numcopiesavail + 1 WHERE isbn_num=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, ISBN);
			stmt.executeUpdate();
		} catch (Exception e){
			System.out.println(e);
		}
		System.out.println("Bookcount has been incrimented.");	
	}

	public static void addBook(int ISBN, String name, int year) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("INSERT INTO books (isbn_num, name, yearofpublication, numcopiesavail) VALUES" + "(?, ?, ?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, ISBN);
			stmt.setString(2, name);
			stmt.setInt(3, year);
			stmt.setInt(4, 1);
			stmt.executeUpdate();
			System.out.println(name + " has been added to the database.");
		} catch (Exception e){
			System.out.println("Error adding the book.");
		}
	}

	public static void decrimentBookCount(int iD) {
		PreparedStatement stmt;
		ResultSet rs;
		try {
			stmt = conn.prepareStatement("SELECT numcopiesavail FROM books WHERE isbn_num=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, iD);
			rs = stmt.executeQuery();
			if (rs.next()) {
				int numHave = rs.getInt(1);
				if (numHave > 0) {
					try {
						stmt = conn.prepareStatement("UPDATE books SET numcopiesavail = numcopiesavail -1 WHERE isbn_num=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
						stmt.setInt(1, iD);
						stmt.executeUpdate();
						System.out.println("Lowered count of books for " + iD);
					} catch (Exception e) {
						System.out.println(e + "204");
					}
				} else {
					// have to remove the book
					System.out.println("No aditional copies of " + iD + " available");
				}
			}
		} catch (Exception e) {
			System.out.println(e + "212");
		}
	}

	static void removeBook(int iD) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("DELETE FROM books WHERE isbn_num=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, iD);
			stmt.execute();
			System.out.println("Book ID: " + iD + " has been removed from the system.");
		} catch (Exception e){
			System.out.println(e + "224");
		}
	}

	public static boolean Available(int iSBN) {
		PreparedStatement stmt;
		ResultSet rs;
		int num = -1;
		try {
			stmt = conn.prepareStatement("SELECT * FROM books WHERE isbn_num=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, iSBN);
			rs = stmt.executeQuery();
			rs.beforeFirst();
			while (rs.next())
			num = rs.getInt(4);
			System.out.println("Num copies available for " + iSBN + " equals " + num);
		} catch (Exception e){
			System.out.println(e + "239");
		}
		if (num >=1){
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void removeCheckoutRecord(int iD, int iSBN){
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("DELETE FROM books2students WHERE studentid=? AND bookissued=?", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1, iD);
			stmt.setString(2, iSBN+"");
			System.out.println("DELETE FROM books2students WHERE studentid= " + iD + " AND bookissued= " + iSBN);
			stmt.executeUpdate();
			System.out.println(iSBN + " removed from student " + iD);
		} catch (Exception e){
			System.out.println(e + "258");
		}
		incrimentNumCount(iSBN);
	}

	public static void createCheckoutRecord(int iD, int iSBN) {
		PreparedStatement stmt;
		//First block creates the relation
		if (Available (iSBN)){
			try {
				stmt = conn.prepareStatement("INSERT INTO books2students (studentid, bookissued, issuedate) VALUES" + "(?, ?, ?)", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				stmt.setInt(1, iD);
				stmt.setInt(2, iSBN);
				stmt.setInt(3, Calendar.DATE);
				Calendar cal = new GregorianCalendar();
				cal.add(Calendar.DATE, 30);
				//stmt.setInt(4,  cal.getTime());
				stmt.executeUpdate();
				System.out.println("ADDED TO STU/BOOK");
			} catch (Exception e){
				System.out.println(e + "275");
			}	
		}
		else {
			System.out.println("Unable to offer that book for checkout.");
		}
	}
}
