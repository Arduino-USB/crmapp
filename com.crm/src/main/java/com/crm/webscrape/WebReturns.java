package com.crm.webscrape;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Embeddable
public class WebReturns {

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb") // Uses PostgreSQL's native JSONB type
    private Map<String, String> stringVals = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, List<String>> listVals = new HashMap<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Map<String, String>> mapVals = new HashMap<>();

    public WebReturns() {
    }

    public void storeString(String name, String value) {
        stringVals.put(name, value);
    }
    
    public void storeList(String name, List<String> value) {        
        listVals.put(name, value);
    }

    public void storeMap(String name, Map<String, String> value) {
        mapVals.put(name, value);
    }

    public Map<String, String> getStrings() {
        return stringVals;
    }
    
    public Map<String, List<String>> getLists() {
        return listVals;
    }
    
    public Map<String, Map<String, String>> getMaps() {
        return mapVals;
    }

    @Override
    public String toString() {
        return "Strings:\n\t" + stringVals + "\n" +
               "Lists:\n\t" + listVals + "\n" +
               "Maps:\n\t" + mapVals + "\n";
    }
}
