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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author standard
 */
public class TypeSystem {
    private static final Map<String, DataTypeInfo> types = new HashMap<>();
    
    public static Map<String, DataTypeInfo> getTypes(){
        return types;
    }
    
    static {
        types.put("Boolean",    new DataTypeInfo("Boolean", "Boolean.parseBoolean($data$)", "Boolean.compare($first$, $second$)"));
        types.put("Byte",       new DataTypeInfo("Byte", "Byte.parseByte($data$)", "Boolean.compare($first$, $second$)"));
        types.put("String",     new DataTypeInfo("String", "String.valueOf($data$)", "$first$.compareTo($second$)"));
        types.put("Integer",    new DataTypeInfo("Int", "Integer.parseInt($data$)", "Integer.compare($first$, $second$)"));
        types.put("Long",       new DataTypeInfo("Long", "Long.parseLong($data$)", "Long.compare($first$, $second$)"));
        types.put("Real",       new DataTypeInfo("Double", "Double.parseDouble($data$)", "Double.compare($first$, $second$)"));
        types.put("Date",       new DataTypeInfo("Date", "(new SimpleDateFormat(\"yyyy-MM-dd'T'HH:mm:ssX\")).parse($data$)", "$first$.compareTo($second$)"));             
    }
}
