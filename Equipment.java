package dbconnect;

import java.sql.PreparedStatement;
import java.util.ArrayList;

public class Equipment {
	private String equipID = "";
	private String equipName = "";
	private String equipDesc = "";
	private String equipCap = "";
	private ConnectMySQLServer dbInstance;
	
	public Equipment(){
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	public Equipment(String equipID){
		this.equipID = equipID;
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	public Equipment(String equipID, String equipName, String equipDesc,
						String equipCap){
		this.equipCap = equipCap;
		this.equipName = equipName;
		this.equipID = equipID;
		this.equipDesc = equipDesc;
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	public ArrayList<String> getEquipID(){
		String sql = "SELECT equipment.equipid FROM equipment";
		ArrayList<ArrayList<String>> result;
		ArrayList<String> equipID = new ArrayList<String>();
		try {
			result = dbInstance.getData(sql);
			equipID = result.get(0);
		} catch (DLException e) {
		}
		
		return equipID;
	}
	
	public ArrayList<String> getEquipmentName(){
		String sql = "SELECT equipment.equipmentname FROM equipment";
		ArrayList<ArrayList<String>> result;
		ArrayList<String> equipmentName = new ArrayList<String>();
		try {
			result = dbInstance.getData(sql);
			equipmentName = result.get(0);
		} catch (DLException e) {
		}
		
		return equipmentName;
	}
	
	public ArrayList<String> getEquipmentDescription(){
		String sql = "SELECT equipment.equipmentdescription FROM equipment";
		ArrayList<ArrayList<String>> result;
		ArrayList<String> equipDesc = new ArrayList<String>();
		try {
			result = dbInstance.getData(sql);
			equipDesc = result.get(0);
		} catch (DLException e) {
		}
		
		return equipDesc;
	}
	
	public ArrayList<String> getEquipmentCapacity(){
		String sql = "SELECT equipment.equipmentcapacity FROM equipment";
		ArrayList<ArrayList<String>> result;
		ArrayList<String> equipCap = new ArrayList<String>();
		try {
			result = dbInstance.getData(sql);
			equipCap = result.get(0);
		} catch (DLException e) {
		}
		
		return equipCap;
	}
	
	public boolean addEquipID(String equipID){
		String sql = "INSERT INTO equipment (equipid) values (" + equipID + ")";
		boolean result = false;
		try {
			result = dbInstance.setData(sql);
		} catch (DLException e) {
		}
		return result;
	}
	
	public boolean addEquipmentName(String equipName){
		String sql = "INSERT INTO equipment (equipmentname) values (" + equipName + ")";
		boolean result = false;
		try {
			result = dbInstance.setData(sql);
		} catch (DLException e) {
		}
		return result;
	}
	
	public boolean addEquipmentDescription(String equipDesc){
		String sql = "INSERT INTO equipment (equipmentdescription) values (" + equipDesc + ")";
		boolean result = false;
		try {
			result = dbInstance.setData(sql);
		} catch (DLException e) {
		}
		return result;
	}
	
	public boolean addEquipmentCapacity(String equipCap){
		String sql = "INSERT INTO equipment (equipmentcapacity) values (" + equipCap + ")";
		boolean result = false;
		try {
			result = dbInstance.setData(sql);
		} catch (DLException e) {
		}
		
		return result;
	}
	
	public ArrayList<String> fetch(){
		String sql = "SELECT * FROM equipment WHERE equipid = ?";
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(equipID);
		ArrayList<ArrayList<String>> results;
		ArrayList<String> equipID = new ArrayList<String>();
		results = dbInstance.getData(sql, parameters);
		equipID = results.get(0);
		
		return equipID;
	}
	
	public boolean put(String update, String fieldMatch){
		String sql = "UPDATE equipment SET EquipmentName = ? where equipID = ?";
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(update);
		parameters.add(fieldMatch);
		boolean result = false;
		result = dbInstance.setData(sql, parameters);
		
		return result;
	}
	
	public boolean post(String attribute, String post){
		String sql = "INSERT INTO Equipment (?) VALUES (?)";
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(attribute);
		parameters.add(post);
		boolean result = false;
		result = dbInstance.setData(sql, parameters);
		
		return result;
	}
	
	public boolean delete(String delete){
		String sql = "DELETE FROM Equipment WHERE equipid = ?";
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(delete);
		boolean result = false;
		result = dbInstance.setData(sql, parameters);
		return result;
	}
	
	public static void main(String[] args){
		Equipment eq = new Equipment("568");
		boolean result = false;
		try {
			result = eq.dbInstance.defaultConnect();
		} catch (DLException e) {
		}
		if(result){
			ArrayList<String> res = eq.fetch();
			for(String line : res){
				System.out.print(line+"\t");
			}
			eq.put("Clearly Class", "568");
			res = eq.fetch();
			System.out.print("\n");
			for(String line : res){
				System.out.print(line+"\t");
			}
		}
	}
}
