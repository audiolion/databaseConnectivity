package main;

import java.util.ArrayList;

public class Equipment {
	private Integer equipID = null;
	private String equipName = null;
	private String equipDesc = null;
	private Integer equipCap = null;
	private ConnectMySQLServer dbInstance;
	
	public Equipment(){
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	public Equipment(Integer equipID){
		this.equipID = equipID;
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	public Equipment(Integer equipID, String equipName, String equipDesc,
						Integer equipCap){
		this.equipID = equipID;
		this.equipName = equipName;
		this.equipDesc = equipDesc;
		this.equipCap = equipCap;
		dbInstance = ConnectMySQLServer.getInstance();
	}
	
	private void printTable(){
		String sql = "SELECT * from Equipment";
		
		try{
			dbInstance.printFormat(dbInstance.getData(sql), dbInstance.getMetaData(sql));
		}catch(DLException e){
		}
	}
	
	private ArrayList<String> getEquipID(){
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
	
	private ArrayList<String> getEquipmentName(){
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
	
	private ArrayList<String> getEquipmentDescription(){
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
	
	private ArrayList<String> getEquipmentCapacity(){
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
	
	private boolean addEquipID(String equipID){
		String sql = "INSERT INTO equipment (equipid) values (" + equipID + ")";
		boolean result = false;
		try {
			result = dbInstance.setData(sql);
		} catch (DLException e) {
		}
		return result;
	}
	
	private boolean addEquipmentName(String equipName){
		String sql = "INSERT INTO equipment (equipmentname) values (" + equipName + ")";
		boolean result = false;
		try {
			result = dbInstance.setData(sql);
		} catch (DLException e) {
		}
		return result;
	}
	
	private boolean addEquipmentDescription(String equipDesc){
		String sql = "INSERT INTO equipment (equipmentdescription) values (" + equipDesc + ")";
		boolean result = false;
		try {
			result = dbInstance.setData(sql);
		} catch (DLException e) {
		}
		return result;
	}
	
	private boolean addEquipmentCapacity(String equipCap){
		String sql = "INSERT INTO equipment (equipmentcapacity) values (" + equipCap + ")";
		boolean result = false;
		try {
			result = dbInstance.setData(sql);
		} catch (DLException e) {
		}
		
		return result;
	}
	
	public ArrayList<String> fetch(){
		String sql = "SELECT * FROM Equipment WHERE EquipID = ?";
		ArrayList<String> parameters = new ArrayList<String>();
		if(equipID == null){
			parameters.add("");
		}else{
			parameters.add(equipID+"");
		}
		ArrayList<ArrayList<String>> results = dbInstance.getData(sql, parameters);
		
		return results.get(0);
	}
	
	public boolean put(){
		String sql = "INSERT INTO Equipment (EquipID,EquipmentName,EquipmentDescription,EquipmentCapacity) VALUES (?,?,?,?)";
		ArrayList<String> parameters = new ArrayList<String>();
		if(equipID == null){
			parameters.add("");
		}else{
			parameters.add(equipID+"");
		}
		
		if(equipName == null){
			parameters.add("");
		}else{
			parameters.add(equipName);
		}
		
		if(equipDesc == null){
			parameters.add("");
		}else{
			parameters.add(equipDesc);
		}
		
		if(equipCap == null){
			parameters.add("0");
		}else{
			parameters.add(equipCap+"");
		}
		
		boolean result = dbInstance.setData(sql, parameters);
		
		return result;
	}
	
	public boolean post(){
		String sql = "UPDATE Equipment SET EquipmentName = ?,EquipmentDescription = ?,EquipmentCapacity = ? WHERE EquipID = ?";
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add(equipName);
		parameters.add(equipDesc);
		parameters.add(equipCap+"");
		parameters.add(equipID+"");
				
		boolean result = dbInstance.setData(sql, parameters);
		
		return result;
	}
	
	public boolean delete(){
		String sql = "DELETE FROM Equipment WHERE EquipID = ?";
		ArrayList<String> parameters = new ArrayList<String>();
		if(equipID == null){
			return false;
		}else{
			parameters.add(equipID+"");
		}
		boolean result = dbInstance.setData(sql, parameters);
		return result;
	}
	
	private void setEquipID(Integer value){
		this.equipID = value;
	}
	
	private void setEquipName(String value){
		this.equipName = value;
	}
	
	private void setEquipDesc(String value){
		this.equipDesc = value;
	}
	
	private void setEquipCap(Integer value){
		this.equipCap = value;
	}
	
	
	public static void main(String[] args){
		Equipment eq = new Equipment(3644);
		boolean result = false;
		try {
			result = eq.dbInstance.defaultConnect();
		} catch (DLException e) {
		}
		if(result){
			eq.printTable();
			ArrayList<String> res = eq.fetch();
			eq.setEquipName("Hi Ravi :)");
			eq.setEquipDesc(res.get(2));
			eq.setEquipCap(Integer.parseInt(res.get(3)));
			eq.post();
			System.out.println();
			res = eq.fetch();
			for(String value : res){
				System.out.print(value + " ");
			}
			
			System.out.println();
			
			eq.printTable();
		}
	}
}
