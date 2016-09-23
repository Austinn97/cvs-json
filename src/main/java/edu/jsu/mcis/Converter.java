package edu.jsu.mcis;

import java.io.*;
import java.util.*;
import au.com.bytecode.opencsv.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class Converter {
    /*
        Consider a CSV file like the following:
        
        ID,Total,Assignment 1,Assignment 2,Exam 1
        111278,611,146,128,337
        111352,867,227,228,412
        111373,461,96,90,275
        111305,835,220,217,398
        111399,898,226,229,443
        111160,454,77,125,252
        111276,579,130,111,338
        111241,973,236,237,500
        
        The corresponding JSON file would be as follows (note the curly braces):
        
        {
            "colHeaders":["Total","Assignment 1","Assignment 2","Exam 1"],
            "rowHeaders":["111278","111352","111373","111305","111399","111160","111276","111241"],
            "data":[[611,146,128,337],
                    [867,227,228,412],
                    [461,96,90,275],
                    [835,220,217,398],
                    [898,226,229,443],
                    [454,77,125,252],
                    [579,130,111,338],
                    [973,236,237,500]
            ]
        }  
    */
	
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
		JSONObject obj = new JSONObject();
		JSONArray data = new JSONArray();
		JSONArray colHeaders = new JSONArray();
		JSONArray rowHeaders = new JSONArray();
		
		
		colHeaders.add("Total");
		colHeaders.add("Assignment 1");
		colHeaders.add("Assignment 2");
		colHeaders.add("Exam 1");
		obj.put("colHeaders", colHeaders);
		obj.put("rowHeaders", rowHeaders);
		obj.put("data", data);
		
		CSVParser parser = new CSVParser();
		BufferedReader read = new BufferedReader(new StringReader(csvString));
		
		try{
			String str = read.readLine();
			
			while((str = read.readLine()) != null){
				String[] parseStr = parser.parseLine(str);
				rowHeaders.add(parseStr[0]);
				JSONArray newRow = new JSONArray();
				newRow.add(new Long(parseStr[1]));
				newRow.add(new Long(parseStr[2]));
				newRow.add(new Long(parseStr[3]));
				newRow.add(new Long(parseStr[4]));
				data.add(newRow);
			}
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
		return obj.toString();
	}

    public static String jsonToCsv(String jsonString) {
		JSONObject obj = null;
		
		try{
			JSONParser parseStr = new JSONParser();
			obj = (JSONObject) parseStr.parse(jsonString);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		String csv = "\"ID\"," + Converter.<String>arrayCombine((JSONArray) obj.get("colHeaders")) + "\n";
		
		JSONArray data = (JSONArray) obj.get("data");
		JSONArray rowHead = (JSONArray) obj.get("rowHeaders");
		
		for (int i=0, size = rowHead.size(); i < size; i++){
			csv +=(
			"\""+ (String) rowHead.get(i) + "\"," +
			Converter.<Long>arrayCombine((JSONArray) data.get(i)) + "\n"
			);
		}
        return csv;
    }
	
	
	private static boolean sameJson(Object one, Object two){
		if (one instanceof JSONObject && two instanceof JSONObject){
			return sameJsonObj((JSONObject) one, (JSONObject) two);
		}
		else if (one instanceof JSONArray && two instanceof JSONArray){
			return sameJsonArray((JSONArray) one, (JSONArray) two);
		}
		else {
			return one.equals(two);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static <Combine> String arrayCombine(JSONArray a){
		String space = "";
		for(int i=0, size = a.size(); i<size; i++){
			space += "\"" + ((Combine) a.get(i)) + "\"";
			if (i < size - 1){
				space += ",";
			}
		}
		return space;
	}
	
	public static boolean sameJsonStr(String one, String two){
		try{
			return sameJson(new JSONParser().parse(one), new JSONParser().parse(two));
		}
		catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	private static boolean sameJsonArray(JSONArray one, JSONArray two){
		int oneSize = one.size();
		
		if(oneSize != two.size()){
			return false;
		}
		else {
			for (int i=0, size = oneSize; i < size; i++){
				if (sameJson(one.get(i), two.get(i))){
					return false;
				}
			}
			return true;
		}
	}
	
	private static boolean sameJsonObj(JSONObject one, JSONObject two){
		for (Object i : one.keySet()){
			String key = (String) i;
			
			if (!two.containsKey(key) || !sameJson(one.get(key), two.get(key))){
				return false;
			}
		}
		return true;
	}

}














