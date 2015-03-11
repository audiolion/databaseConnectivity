import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Provides a custom exception in the data layer that gracefully
 * handles errors by omitting a generic message to the user and 
 * writing information to a log file. Typically this log file would
 * be written to a secure server, to the database, or to a locked file
 * that only the program can access, but for the purposes of this exercise
 * it was simply written to the default directory.
 * 
 * 
 * @author Ryan Castner rrc9704@rit.edu
 *
 */
public class DLException extends Exception{
	
	//Default message and attributes
	private static final String DEFAULT_MESSAGE = "Unable to complete operation.";
	private List<ArrayList<String>> list = new ArrayList<ArrayList<String>>();
	private Exception e;
	
	/**
	 * Default constructor, outputs default message
	 */
	public DLException(){
		super(DEFAULT_MESSAGE);
	}
	
	/**
	 * Takes an exception as a parameter and writes its stack trace to a
	 * log file, as well as displaying the default message to the user.
	 * 
	 * @param e - The exception
	 */
	public DLException(Exception e){
		super(DEFAULT_MESSAGE);
		this.e = e;
		this.writeLog();
	}
	
	/**
	 * Takes an exception and a 2D array of string error data. Writes all data
	 * to a log file and displays the default message to the user.
	 * 
	 * @param e - The exception
	 * @param list - The 2D array of string arrays of error data
	 */
	public DLException(Exception e, List<ArrayList<String>> list){
		super(DEFAULT_MESSAGE);
		this.e = e;
		this.list = list;
		this.writeLog();
	}
	
	/**
	 * Private method that writes available information to a log file. The data
	 * is appended to the error log, time stamping each new call to this method
	 */
	private void writeLog(){
		
		// uses a buffered writer to write to a file
		BufferedWriter writer = null;
		try{
			// create a time stamp using system time and date
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(
					Calendar.getInstance().getTime());
			// create a log file to write to
			File logFile = new File("errorlog.txt");
			
			// due to varying implementations of new line and errors with
			// BufferedWriter class we get the systems line separator value
			// and use that instead of "\n"
			String newline = System.getProperty("line.separator");
			
			// instantiate writer, append is set to true
			writer = new BufferedWriter(new FileWriter(logFile, true));
			
			// start new log
			writer.write(newline + "START OF NEW LOG AT " + timeStamp + newline);
			
			// write type of exception that occurred e.g. SQLException
			writer.write("Exception Type: " + e.toString() + newline);
			
			// Convert stack trace to a string and write it
			StringWriter s = new StringWriter();
			e.printStackTrace(new PrintWriter(s));
			String strackTrace = s.toString();
			writer.write(strackTrace);
			
			// if this exception was passed a 2d list of data, add it to the
			// logfile
			if(!list.isEmpty()){
				for(ArrayList<String> array : list){
					for(String line : array){
						writer.write(line + newline);
					}
					writer.write(newline  + "----------------" + newline);
				}
			}
		}catch(Exception e){
			System.out.println("log write failed");
		}finally{
			try{
				writer.close();
			}catch(Exception e){
			}
		}		
	}
}
