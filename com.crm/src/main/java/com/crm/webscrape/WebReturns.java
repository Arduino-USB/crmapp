package com.crm.webscrape;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class WebReturns {

    private Map<String,String> stringVals = new HashMap<>();
    private Map<String,List<String>> listVals = new HashMap<>();
    private Map<String,Map<String,String>> mapVals = new HashMap<>();

    public WebReturns() {

    }

    public void storeString(String name, String value) {
        stringVals.put(name, value);
    }
    
    public void storeList(String name, List<String> value) {        
        listVals.put(name, value);
    }

    public void storeMap(String name, Map<String,String> value) {
        mapVals.put(name, value);
    }


    public Map<String,String> getStrings() {
        return stringVals;
    }

    
    public Map<String,List<String>> getLists() {
        return listVals;
    }

    
    public Map<String,Map<String,String>> getMaps() {
        return mapVals;
    }


	@Override
	public String toString() {
		return  "Strings:\n\t" + stringVals + "\n" +
				"Lists:\n\t" + listVals + "\n" +
				"Maps:\n\t" + mapVals + "\n";
	}


}
