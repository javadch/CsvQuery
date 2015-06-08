/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.csv.reader;

import com.vaiona.commons.data.FieldInfo;
import com.vaiona.commons.logging.LoggerHelper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 *
 * @author jfd
 */
public class HeaderBuilder {
    // pother types of header builders should also be available. like the one that inferrs the field type from its usage in the attributes
    public LinkedHashMap<String, FieldInfo> buildFromDataFile(String fileName, String delimiter, String typeDelimiter, String unitDelimiter) throws IOException {
        LoggerHelper.logDebug(MessageFormat.format("The CSV adapter is extracting the fields from file: {0} ", fileName));        
        LinkedHashMap<String, FieldInfo> fields;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            fields = this.buildFromDataFile(reader, delimiter, typeDelimiter, unitDelimiter);
        }
        return (fields);
    }
    
    public LinkedHashMap<String, FieldInfo> buildFromDataFile(BufferedReader reader, String delimiter, String typeDelimiter, String unitDelimiter) throws IOException {
        LinkedHashMap<String, FieldInfo> headers = new LinkedHashMap<>();
        String headerLine = reader.readLine();
        Scanner scanner = new Scanner(headerLine);
        scanner.useDelimiter(delimiter);
        // header items can be one these formats: Name/ Name:Type/ Name:Type::Unit
        int indexCount = 0;
        while(scanner.hasNext()){
            FieldInfo field = new FieldInfo();
            field.index = indexCount;
            String temp = scanner.next().trim().replace("\"", "");
            if(temp.contains(typeDelimiter)){
                if(temp.contains(unitDelimiter)){
                    field.name = temp.substring(0, temp.indexOf(typeDelimiter));
                    field.internalDataType = temp.substring(
                            temp.indexOf(typeDelimiter)+typeDelimiter.length(), temp.indexOf(unitDelimiter));  
                    field.unit = temp.substring(
                            temp.indexOf(unitDelimiter)+unitDelimiter.length(), temp.length());
                }
                else{
                    field.name = temp.substring(0, temp.indexOf(typeDelimiter));
                    field.internalDataType = temp.substring(
                            temp.indexOf(typeDelimiter)+typeDelimiter.length(), temp.length());                    
                }
            }
            else{
                field.name = temp;
            }
            headers.put(field.name, field);
            indexCount++;
        }
        return (headers);
    }
    
}
