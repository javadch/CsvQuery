/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.data;

/**
 *
 * @author standard
 */
public class FieldInfo {
    public static final String UNKOWN_TYPE = "String";
    
    public static final String UNKOWN_UNIT = "Unknown";
    public String unit = UNKOWN_UNIT; // unit of measurement
    
    //private String missingValue = null;
    //private String format = null;
    
    public String name = "";
    public String dataTypeRef = UNKOWN_TYPE;
    public int index = 0;
}
