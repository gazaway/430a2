package fourThirtya2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class ModelView {
	
	/*
	 * This is just a method that clears the terminal ALMOST no matter
	 * which OS the user is on. Simple quality of life improvement
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
			System.out.println("_________________________________________________");
			System.out.println("What would you like to do?");
			System.out.println("1. Add a student to the database.");
			System.out.println("2. Add a book to the databse.");
			System.out.println("3. Remove a student from the database.");
			System.out.println("4. Remove a book from the database.");
			System.out.println("5. Issue a book to a student.");
			System.out.println("6. Return a book from a student.");
			System.out.println("7. Print the contents of a table.");
			System.out.println("0. Quit the program.");
			kbd = new Scanner(System.in);
			int select = kbd.nextInt();
			switch (select) {
			case 1: // EXECUTE ADD STUDENT METHOD
				clearConsole();
				addStudent();
				break;
			case 2: // EXECUTE ADD BOOK METHOD
				clearConsole();
				addBook();
				break;
			case 3: // EXECUTE REMOVE STUDENT METHOD
				clearConsole();
				removeStudent();
				break;
			case 4: // EXECUTE REMOVE BOOK METHOD
				clearConsole();
				removeBook();
				break;
			case 5: // EXECUTE ISSUE BOOK TO STUDENT METHOD
				clearConsole();
				issueBook();
				break;
			case 6: // EXECUTE TABLE PRINT METHOD
				returnBook();
				clearConsole();
				break;
			case 7: // EXECUTE TABLE PRINT METHOD
				printTable();
				clearConsole();
				break;
			case 0: // QUIT 
				run = false;
				clearConsole();
				TunneledConnection.close();
				break;
			default:
				System.out.println("Unrecognized input of " + select + " detected.");
				clearConsole();
				break;
			}
		}		
		kbd.close();
	}
	
	private void returnBook() {
		System.out.println("Please type the ID of the student.");
		Scanner kbd = new Scanner(System.in);
		int ID = -1;
		try {
			ID = Integer.parseInt(kbd.nextLine());
		} catch (NumberFormatException e) {
		    System.out.println(e);
		}
		System.out.println("Please type the ISBN of the book to return.");
		int iSBN = -1;
		try {
			iSBN = Integer.parseInt(kbd.nextLine());
		} catch (NumberFormatException e) {
		    System.out.println(e);
		}
		Backend.removeCheckoutRecord(ID, iSBN);
	}

	public void addStudent(){
		System.out.println("Please type the ID of the student.");
		Scanner kbd = new Scanner(System.in);
		int ID = -1;
		try {
			ID = Integer.parseInt(kbd.nextLine());
		} catch (NumberFormatException e) {
		    System.out.println(e);
		}
		System.out.println("Please type the first name of the student.");
		String fname = kbd.nextLine();
		System.out.println("Please type the last name of the student.");
		String lname = kbd.nextLine();
		System.out.println("Please type the degree of the student.");
		String degree = kbd.nextLine();
		Backend.addStudent(ID, fname, lname, degree);
	}
	
	public void addBook(){
		System.out.println("Please tpye the ISBN number of the book.");
		Scanner kbd = new Scanner(System.in);
		int ISBN = -1;
		try {
			ISBN = Integer.parseInt(kbd.nextLine());
		} catch (NumberFormatException e) {
		    System.out.println(e);
		}
		if (Backend.haveBook(ISBN)) {
				// JUST NEED TO INCREMENT THE COUNTER OF AVAILABLE BOOKS
				Backend.incrimentNumCount(ISBN);
		}
		else {
			System.out.println("Please type the book's year of publication.");
			int year = -1;
			try {
				year = Integer.parseInt(kbd.nextLine());
			} catch (NumberFormatException e) {
			    System.out.println(e);
			}
			System.out.println("Please type the name of the book.");
			String name = kbd.nextLine();
			Backend.addBook(ISBN, name, year);
		}
	}
	
	public boolean removeStudent(){
		System.out.println("Please type the ID of the student.");
		Scanner kbd = new Scanner(System.in);
		int ID = -1;
		try {
			ID = Integer.parseInt(kbd.nextLine());
		} catch (NumberFormatException e) {
		    System.out.println(e);
		}
		if (Backend.removeStudent(ID)){
			System.out.println("Removed student " + ID );
		}
		else {
			System.out.println("Error removing student.");
		}
		return false;
	}
	
	public void removeBook(){
		System.out.println("Please type the ISBN number of the book you'd like to remove.");
		Scanner kbd = new Scanner(System.in);
		int ID = kbd.nextInt();
		Backend.removeBook(ID);
	}
	
	public void issueBook(){
		Scanner kbd = new Scanner(System.in);
		System.out.println("Please type the student ID.");
		int ID = -1;
		try {
			ID = Integer.parseInt(kbd.nextLine());
		} catch (NumberFormatException e) {
		    System.out.println(e);
		}
		System.out.println("Please type the book ISBN number.");
		int ISBN = -1;
		try {
			ISBN = Integer.parseInt(kbd.nextLine());
		} catch (NumberFormatException e) {
		    System.out.println(e);
		}
		if (Backend.Available(ISBN)){
			System.out.println("Issuing " + ISBN + " to " + ID);
			Backend.createCheckoutRecord(ID, ISBN);
			Backend.decrimentBookCount(ISBN);
		}
	}
	
	/*
	 * Option 6. Gives the user the choice of which table to print
	 * then prints it based off of that selection.
	 */
	public void printTable(){
		int cntr = 0;
		ResultSet rs = null;
		try {
			rs = Backend.getAllTables();
			int size = 0;
			if (rs != null) {
				rs.beforeFirst();
				rs.last();
				size = rs.getRow();
				rs.beforeFirst();
				System.out.println("_________________________________________________");
				System.out.println("Here is a list of all available tables.");
				System.out.println("Type the corresponding number to print the table.");
				String[] tables = new String[size];
				while (rs.next()) {
					String temp = rs.getString("TABLE_NAME");
					System.out.println(cntr + ". " + temp);
					tables[cntr] = temp;
					cntr++;
				}
				Scanner kbd = new Scanner(System.in);
				int select = kbd.nextInt();
				String tableName = tables[select];
				rs = Backend.getTable(tableName);
				while (rs.next()) {
					System.out.println(rs.getInt(1) + "\t,\t" + rs.getString(2)+ "\t,\t" + rs.getInt(3)+ "\t,\t" + rs.getString(4));
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
