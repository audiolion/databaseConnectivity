import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect{

   public static void main(String[] args){
      try{
         Class.forName("oracle.jdbc.driver.OracleDriver");
       }catch(Exception e){
         e.printStackTrace();
       }
       
       Connection conn = null;
       try{
         conn = DriverManager.getConnection("jdbc:oracle:thin:@//homer/jobs", "endUser", "useStuff");
       }catch(Exception e){
         e.printStackTrace();
       }
         
       if(conn != null){
         System.out.println("Connection Success!");
       }else{
         System.out.println("Connection Failed.");
       }
   }
}
