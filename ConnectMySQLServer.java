import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Class is set up to connect to a MySQL Server database and provides methods
 * to perform CRUD operations on the database it connects to. Dependent on the
 * mysql-connector-java-5.0.8-bin.jar driver being in the classpath.
 * 
 * @author Ryan Castner rrc9704@rit.edu
 *
 */

public class ConnectMySQLServer {
		
	//Connection attribute
	private Connection conn = null;
	
	private static final String DEFAULT_URL = "jdbc:mysql://localhost/travel2";
	private static final String DEFAULT_USER = "root";
	private static final String DEFAULT_PASS = "111883";
	
	/**
	 * Ensures singleton access to database instance
	 */
	private static class SingletonWrapper{
		private static ConnectMySQLServer INSTANCE = new ConnectMySQLServer();
	}
	
	/**
	 * Provides access to singleton instance of db class
	 * @return ConnectMySQLServer.INSTANCE
	 */
	public static ConnectMySQLServer getInstance(){
		return SingletonWrapper.INSTANCE;
	}
	
	/**
	 * Ensures there is no public default constructor
	 */
	private ConnectMySQLServer(){
	}
	
	/**
	 * Provides a default connection method for ConnectMySQLServer instance
	 * @return boolean status of connection success
	 */
	public boolean defaultConnect(){
		boolean result = getInstance().dbConnect(DEFAULT_USER, DEFAULT_USER,
				DEFAULT_PASS);
		return result;
	}
	
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
	public boolean dbConnect(String dbConnectURL, String username,
			String password){
	      try{
	         Class.forName("com.mysql.jdbc.Driver");
	         conn = DriverManager.getConnection(dbConnectURL, username, 
	        		 password);
	      }catch(ClassNotFoundException ex){
	    	  ex.printStackTrace();
	      }catch(SQLException e){
	    	  e.printStackTrace();
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
				e.printStackTrace();
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
		ArrayList<ArrayList<String>> resultSet = 
				new ArrayList<ArrayList<String>>();

		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			metaData = rs.getMetaData();
			columnCount = metaData.getColumnCount();
			while(rs.next()){
				ArrayList<String> list = new ArrayList<String>();
				for(int i = 1; i <= columnCount; i++){
					String str = rs.getString(i);
					list.add(str);
				}
				resultSet.add(list);
			}
		}catch (SQLException e){
			if(resultSet.isEmpty()){
				e.printStackTrace();
			}else{
				e.printStackTrace();
			}
		}	
		this.printFormat(resultSet, metaData);
		return resultSet;
	}
	
	/**
	 * Grabs the raw result set from a SQL query. Methods using this method
	 * will need to perform error checking.
	 * 
	 * @param sql - SQL Query
	 * @return rs - the result set from the query, null if no connection to db
	 */
	public ResultSet getResultSet(String sql){
		if(conn == null){
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/**
	 * Grabs the raw result set meta data from a SQL query. Methods using this
	 * method will need to perform error checking.
	 * 
	 * @param sql - SQL Query
	 * @return metaData - Meta data of the result set, null if no db connection
	 */
	public ResultSetMetaData getMetaData(String sql){
		if(conn == null){
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		ResultSetMetaData metaData = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			metaData = rs.getMetaData();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return metaData;		
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
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Provides a pretty printing to the console that is similar to the SQL
	 * table layout
	 * 
	 * @param resultSet - the ArrayList<ArrayList<String>> of result set data
	 * @param metaData - ResultSetMetaData to get field names
	 */
	public void printFormat(ArrayList<ArrayList<String>> resultSet, 
			ResultSetMetaData metaData){
		int columnCount = 0;
		try {
			columnCount = metaData.getColumnCount();
			for(int i = 1; i <= columnCount; i++){
				System.out.format("%-20s\t", metaData.getColumnName(i));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		for(ArrayList<String> array : resultSet){
    		for(String item : array){
    			 System.out.format("%-20s\t", item);
    		}
    		System.out.println();
    	}
	}
	
	/**
	 * Sets up the default connection string to RIT's database. If args are
	 * provided then it will use them instead.
	 * @param args - command line arguments
	 */
    public static void main(String[] args){
    	String url = "";
    	String username = "";
    	String password = "";
    	if(args.length == 3){
    		url = args[0];
    		username = args[1];
    		password = args[2];
    	}
    	ConnectMySQLServer connServer = new ConnectMySQLServer();
    	
    	if(args.length == 0){
    		connServer.dbConnect(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASS);
    	}else{
    		connServer.dbConnect(url, username, password);
    	}
    	
    	String sql = "SELECT * from trip";
    	ArrayList<ArrayList<String>> data = connServer.getData(sql);
    	
    	ResultSetMetaData metaData = connServer.getMetaData(sql);
    	int columnCount;
		try {
			columnCount = metaData.getColumnCount();
			System.out.println("\n" + columnCount + " Fields Retrieved");
			System.out.format("%-12s\t%12s\n", "Field", "Type");
			for(int i = 1; i <= columnCount; i++){
	    		System.out.format("%-20s\t%-15s\n", metaData.getColumnName(i), metaData.getColumnTypeName(i));
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	
    	connServer.dbConnectClose();
    }
}
