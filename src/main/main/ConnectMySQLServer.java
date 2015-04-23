package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

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
	
	Properties prop = new Properties();
	String propFileName = "config.properties";
	
	private String propsURL = "";
	private String propsUSER = "";
	private String propsPASS = "";
	
	private Hashtable<String, PreparedStatement> preparedStatementsHash;
	private Savepoint save1 = null;
	
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
	 * @throws  
	 */
	private ConnectMySQLServer(){
		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(propFileName);
		if(inputStream != null){
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try {
				throw new FileNotFoundException("property file '" + propFileName
						+ "' not found in the classpath");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		propsURL = prop.getProperty("url");
		propsUSER = prop.getProperty("user");
		propsPASS = prop.getProperty("password");
		
		preparedStatementsHash = new Hashtable<String, PreparedStatement>();
	}
	
	/**
	 * Provides a default connection method for ConnectMySQLServer instance
	 * @return boolean status of connection success
	 * @throws DLException 
	 */
	public boolean defaultConnect() throws DLException{
		boolean result = getInstance().dbConnect(propsURL, propsUSER,
				propsPASS);
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
	 * @throws DLException 
	 */
	public boolean dbConnect(String dbConnectURL, String username,
			String password) throws DLException{
	      try{
	         Class.forName("com.mysql.jdbc.Driver");
	         conn = DriverManager.getConnection(dbConnectURL, username, 
	        		 password);
	      }catch(ClassNotFoundException | SQLException e){
	    	  throw new DLException(e);
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
	 * @throws DLException 
	 */
	public void dbConnectClose() throws DLException{
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DLException(e);
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
	 * @throws DLException 
	 */
	public ArrayList<ArrayList<String>> getData(String sql) throws DLException{
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
				throw new DLException();
			}else{
				throw new DLException(e);
			}
		}	
		
		return resultSet;
	}
	
	/**
	 * Method executes a SQL Query if a database connection exists and converts
	 * the result set into a 2D ArrayList that is returned.
	 * 
	 * @param sql - String representing a SQL Query
	 * @return 2D ArrayList, if empty, connection did not exist, or no results
	 * @throws DLException 
	 */
	public ArrayList<ArrayList<String>> getData(String sql, Boolean value) throws DLException{
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
			if(value){
				ArrayList<String> colNames = new ArrayList<String>();
				for(int i = 1; i <= columnCount; i++){
					String str = metaData.getColumnName(i);
					colNames.add(str);
				}
				resultSet.add(colNames);
			}
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
				throw new DLException();
			}else{
				throw new DLException(e);
			}
		}	
		
		return resultSet;
	}
	
	/**
	 * Uses the prepare method and executes the query, printing the result set and
	 * returning the result set as a 2-D ArrayList.
	 * 
	 * @param sql - the sql string to prepare
	 * @param parameters - the parameters to prepare it with
	 * @return resultSet - a 2-d table representation of the results
	 */
	public ArrayList<ArrayList<String>> getData(String sql, ArrayList<String> parameters){
		if(conn == null){
			return new ArrayList<ArrayList<String>>();
		}
		
		ResultSet rs = null;
		ResultSetMetaData metaData = null;
		int columnCount = 0;
		ArrayList<ArrayList<String>> resultSet = 
				new ArrayList<ArrayList<String>>();
		try{
			PreparedStatement stmt = prepare(sql, parameters);
			rs = stmt.executeQuery();
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
		}catch (DLException | SQLException e) {
			
		}
		
		return resultSet;		
	}
	
	/**
	 * Wrapper for the execute and prepare methods. Method calls prepare to make a prepared
	 * statement and execute to execute the statement, return the result of the attempt.
	 * 
	 * @param sql - the sql query to prepare
	 * @param parameters - the parameters to prepare it with
	 * @return boolean - the result of the query execution
	 */
	public boolean setData(String sql, ArrayList<String> parameters){
		if(conn == null){
			return false;
		}
		
		boolean result = false;
		try{
			PreparedStatement stmt = prepare(sql, parameters);
			result = stmt.execute();
		}catch(SQLException | DLException e){
		}
		
		return result;
	}
	
	/**
	 * Makes a PreparedStatement object if one does not already exist for the given Sql string.
	 * If the string already exists then it pulls it from this classes hashtable of prepared
	 * statements. Then the provided parameters are assigned to the PreparedStatement and the
	 * object is returned as well as being stored in this classes hashtable.
	 * 
	 * @param sql - the sql query to prepare
	 * @param parameters - the parameters to provide to the sql query
	 * @return stmt - the prepared statement object
	 * @throws DLException
	 */
	public PreparedStatement prepare(String sql, ArrayList<String> parameters) throws DLException{
		PreparedStatement stmt = preparedStatementsHash.get(sql);
		
		try {
			if(stmt == null){
				stmt = conn.prepareStatement(sql);
			}
			for(int i = 0; i < parameters.size(); i++){
				stmt.setString((i+1), parameters.get(i));
			}
			preparedStatementsHash.put(sql, stmt);
		} catch (SQLException e) {
			throw new DLException(e);
		}
				
		return stmt;
	}
	
	/**
	 * Wrapper for executeUpdate and prepareStatement methods, returns -1 if there was
	 * an exception
	 * 
	 * @param sql - the sql string to be prepared
	 * @param parameters - the parameters to insert
	 * @return result - the integer return from executeUpdate
	 */
	public int executeStmt(String sql, ArrayList<String> parameters){
		int result = -1;
		try{
			PreparedStatement stmt = prepare(sql, parameters);
			result = stmt.executeUpdate();
		}catch(DLException | SQLException e){
		}
		return result;
	}
	
	
	/**
	 * Grabs the raw result set from a SQL query. Methods using this method
	 * will need to perform error checking.
	 * 
	 * @param sql - SQL Query
	 * @return rs - the result set from the query, null if no connection to db
	 * @throws DLException 
	 */
	public ResultSet getResultSet(String sql) throws DLException{
		if(conn == null){
			return null;
		}
		
		Statement stmt = null;
		ResultSet rs = null;
		
		try{
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
		}catch(SQLException e){
			throw new DLException(e);
		}
		
		return rs;
	}
	
	/**
	 * Grabs the raw result set meta data from a SQL query. Methods using this
	 * method will need to perform error checking.
	 * 
	 * @param sql - SQL Query
	 * @return metaData - Meta data of the result set, null if no db connection
	 * @throws DLException 
	 */
	protected ResultSetMetaData getMetaData(String sql) throws DLException{
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
			throw new DLException(e);
		}
		
		return metaData;		
	}
	
	/**
	 * Method determines whether or not a SQL Query will return a result set
	 * 
	 * @param sql - a String representing a SQL Query
	 * @return - A boolean value, true indicates a result set was returned
	 * @throws DLException 
	 */
	public boolean setData(String sql) throws DLException{
		if(conn == null){
			return false;
		}
		
		Statement stmt = null;
		boolean result = false;
		try{
			stmt = conn.createStatement();
			result = stmt.execute(sql);
		}catch(SQLException e){
			throw new DLException(e);
		}
		
		return result;
	}
	
	public boolean startTrans(){
		if(conn == null)
			return false;
		
		try {
			conn.setAutoCommit(false);
			save1 = conn.setSavepoint();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	public boolean endTrans(){
		if(conn == null)
			return false;
		
		try{
			conn.commit();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean rollbackTrans(){
		if(conn == null)
			return false;
		
		try{
			conn.rollback(save1);
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		return true;
	}
	
	/**
	 * Provides a pretty printing to the console that is similar to the SQL
	 * table layout
	 * 
	 * @param resultSet - the ArrayList<ArrayList<String>> of result set data
	 * @param metaData - ResultSetMetaData to get field names
	 * @throws DLException 
	 */
	public void printFormat(ArrayList<ArrayList<String>> resultSet, 
			ResultSetMetaData metaData) throws DLException{
		int columnCount = 0;
		try {
			columnCount = metaData.getColumnCount();
			for(int i = 1; i <= columnCount; i++){
				System.out.format("%-20s\t", metaData.getColumnName(i));
			}
		} catch (SQLException e) {
			throw new DLException(e);
		}
		System.out.println();
		for(ArrayList<String> array : resultSet){
    		for(String item : array){
    			 System.out.format("%-20s\t", item);
    		}
    		System.out.println();
    	}
	}
}
