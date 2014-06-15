/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.data;

/**
 *
 * @author standard
 */
public class DataTypeInfo {
    String name;
    String lowerCaseName;
    String castPattern;
    String comparePattern;
    
    public DataTypeInfo(String name, String castPattern, String comparePattern){
        this.name = name;
        this.lowerCaseName = name.toLowerCase();
        this.castPattern = castPattern;
        this.comparePattern = comparePattern;
    }

    public String getName() {
        return name;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public String getCastPattern() {
        return castPattern;
    }

    public String getComparePattern() {
        return comparePattern;
    }

    public void setCastPattern(String castPattern) {
        this.castPattern = castPattern;
    }

    public void setComparePattern(String comparePattern) {
        this.comparePattern = comparePattern;
    }
    
    
}
