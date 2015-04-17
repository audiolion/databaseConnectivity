package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Class is set up to connect to a MSSQLServer database and provides methods
 * to perform CRUD operations on the database it connects to. Dependent on the
 * sqljdbc4.jar driver being in the classpath.
 * 
 * @author Ryan Castner rrc9704@rit.edu
 *
 */
public class ConnectMSSQLServer {
	
	//Connection attribute
	private Connection conn = null;
	
	/**
	 * Attempts to connect to a MS SQL Server database with the provided
	 * parameter information. Utilizes the sqljdbc4.jar driver. Returns
	 * a boolean result indicating connection success.
	 * 
	 * @param dbConnectURL - db url of the form jdbc:subprotocol:subname
	 * @param username - username for login credentials
	 * @param password - password for login credentials
	 * @return boolean - true if connection was successful, false if not
	 */
	public boolean dbConnect(String dbConnectURL, String username, String password){
	      try{
	         Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
	         conn = DriverManager.getConnection(dbConnectURL, username, password);
	      }catch(ClassNotFoundException ex){
	    	  new DLException(ex);
	      }catch(SQLException e){
	    	  new DLException(e);
	      }
	      if(conn != null){
	    	  return true;
	      }else{
	    	  return false;
	      }
	}
	
	/**
	 * Closes the current database connection if it is open and sets the
	 * attribute to null.
	 */
	public void dbConnectClose(){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				new DLException(e);
			}
		}
		
		conn = null;
	}
	
	/**
	 * Method executes a SQL Query if a database connection exists and converts
	 * the result set into a 2D ArrayList that is returned.
	 * 
	 * @param sql - String representing a SQL Query
	 * @return 2D ArrayList, if empty, connection did not exist, or no results
	 */
	public ArrayList<ArrayList<String>> getData(String sql){
		if(conn == null){
			return new ArrayList<ArrayList<String>>();
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData metaData = null;
		int columnCount = 0;
		ArrayList<ArrayList<String>> resultSet = new ArrayList<ArrayList<String>>();

		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			metaData = rs.getMetaData();
			columnCount = metaData.getColumnCount();
			while(rs.next()){
				ArrayList<String> list = new ArrayList<String>();
				for(int i = 1; i < columnCount; i++){
					String str = rs.getString(i);
					list.add(str);
				}
				resultSet.add(list);
			}
		}catch (SQLException e){
			if(resultSet.isEmpty()){
				new DLException(e);
			}else{
				new DLException(e, resultSet);
			}
		}	
		return resultSet;
	}
	
	/**
	 * Method determines whether or not a SQL Query will return a result set
	 * 
	 * @param sql - a String representing a SQL Query
	 * @return - A boolean value, true indicates a result set was returned
	 */
	public boolean setData(String sql){
		if(conn == null){
			return false;
		}
		
		Statement stmt = null;
		boolean result = false;
		try{
			stmt = conn.createStatement();
			result = stmt.execute(sql);
		}catch(SQLException e){
			new DLException(e);
		}
		
		return result;
	}
	
	/**
	 * Sets up the default connection string to RIT's database. If args are
	 * provided then it will use them instead.
	 * @param args - command line arguments
	 */
    public static void main(String[] args){
    	String url = "jdbc:sqlserver://theodore.it.rit.edu";
    	String username = "330User";
    	String password = "330Password";
    	if(args.length == 3){
    		url = args[0];
    		username = args[1];
    		password = args[2];
    	}
    	ConnectMSSQLServer connServer = new ConnectMSSQLServer();
    	connServer.dbConnect(url, username, password);
    	String sql = "SELECT * from Equipment";
    	ArrayList<ArrayList<String>> data = connServer.getData(sql);
    	for(ArrayList<String> array : data){
    		for(String item : array){
    			System.out.print("\t"+item);
    		}
    		System.out.println();
    	}
    	connServer.dbConnectClose();
    }
}
