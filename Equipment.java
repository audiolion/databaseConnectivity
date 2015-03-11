import java.util.ArrayList;



public class Equipment {
	private ConnectMySQLServer dbInstance;
	private String[] colNames = {"EquipID", "EquipmentName",
									"EquipmentDescription",	
										"EquipmentCapacity"};
	public Equipment(){
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	public Equipment(String equipID){
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	public Equipment(String equipID, String equipName, String equipDesc,
						String equipCap){
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	public ArrayList<String> getEquipID(){
		
	}
	
}
