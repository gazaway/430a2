package fourThirtya2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class View {
	
	private Connection conn;
	
	public View(Connection _conn){
		conn = _conn;
	}
	

	/*
	 * This is just a simple method that clears the terminal no matter
	 * which OS the user is on. Simple Quality of Life improvement
	 */
	public void clearConsole(){
	    try {
	        final String os = System.getProperty("os.name");
	        //windows machine
	        if (os.contains("Windows")){
	            Runtime.getRuntime().exec("cls");
	        }
	        //linux or mac machine
	        else{
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (Exception e){    }
	}

	/*
	 * A very simple text-based UI
	 */
	public void run(){
		boolean run = true;
		Scanner kbd = null;
		while (run) {
			System.out.println("What would you like to do?");
			System.out.println("1. Add a student to the database.");
			System.out.println("2. Add a book to the databse.");
			System.out.println("3. Remove a student from the database.");
			System.out.println("4. Remove a book from the database.");
			System.out.println("5. Issue a book to a student.");
			System.out.println("6. Print the contents of a table.");
			System.out.println("0. Quit the program.");
			kbd = new Scanner(System.in);
			int select = kbd.nextInt();
			switch (select) {
			case 1: // EXECUTE ADD STUDENT METHOD
				clearConsole();
				break;
			case 2: // EXECUTE ADD BOOK METHOD
				clearConsole();
				break;
			case 3: // EXECUTE REMOVE STUDENT METHOD
				clearConsole();
				break;
			case 4: // EXECUTE REMOVE BOOK METHOD
				clearConsole();
				break;
			case 5: // EXECUTE ISSUE BOOK TO STUDENT METHOD
				clearConsole();
				break;
			case 6: // EXECUTE TABLE PRINT METHOD
				printTable();
				clearConsole();
				break;
			case 0: run = false;
				clearConsole();
				Tunnel.close();
				break;
			default:
				System.out.println("Unrecognized input of " + select + " detected.");
				clearConsole();
				break;
			}
		}		
		kbd.close();
	}
	
	public void addStudent(){
		
	}
	
	public void addBook(){
		
	}
	
	public boolean removeStudent(){
		return false;
	}
	
	public boolean removeBook(){
		return false;
	}
	
	public void issueBook(){
		
	}
	
	public void printTable(){
		int cntr = 0;
		ResultSet rs = null;
		try {
			rs = Backend.getAllTables();
			System.out.println("_________________________________________________");
			System.out.println("Here is a list of all available tables.");
			System.out.println("Type the corresponding number to print the table.");
			while (rs.next()){
				System.out.println(cntr + ". " + rs.getString("TABLE_NAME"));
				cntr++;
			}
			Scanner kbd = new Scanner(System.in);
			int select = kbd.nextInt();
			cntr = 0;
			while (cntr < select){
				rs.next();
				cntr++;
			}
			String tableName = rs.getString("TABLE_NAME");
			rs = Backend.getTable(tableName);
			while (rs.next()){
				System.out.println(rs);//.getString("test_id") + "   ,     " + rs.getString("test_name"));
			}
		} catch (Exception e) {	}
		//TODO finish this. Figure out why the tables aren't printing.
	}
}
