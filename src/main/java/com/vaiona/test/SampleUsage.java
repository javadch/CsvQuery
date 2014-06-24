/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.test;

import com.vaiona.csv.reader.DataReader;
import com.vaiona.csv.reader.DataReaderBuilder;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author standard
 */
public class SampleUsage {
    private void read2() throws Exception {
        DataReaderBuilder builder = new DataReaderBuilder();
        
        try{
            DataReader<Object> reader = builder
                    .baseClassName("Test") // the name used for the dynamically generated classes
                    // The fields as they are in the file. keep the right order
                    // The data types supported are listed in com.vaiona.data.TypeSystem class
                    // it is possible to read this information from the data file, from a third file or from any other source
                    .addField("time", "Date")
                    .addField("longitude", "Real")
                    .addField("latitude", "Real")
                    .addField("elevation", "Real")
                    .addField("temperature", "Real")
                    .addField("soilNi", "Real")
                    .addField("id", "Integer")

                    // The attributes the user is interested in. Attributes are higher level data items computed from the fields using a formula.
                    // if there is no need to compute, just pass the field name.
                    // put SPACE between any two elements of the expressions. The expression parser is intentionally kept simple
                    // for performance reasons. It expects a properly formatted input and does NO input validation.
                    // NOTE: function calls in the expressions are not yet supported.
                    .dateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                    .addAttribute("Timestamp", "Date", "time")
                    .addAttribute("Longitude", "Real", "longitude")
                    .addAttribute("Latitude", "Real", "latitude")
                    .addAttribute("Elevation", "Real", "elevation / 0.30480", Arrays.asList("elevation")) // change the unit of the elvation from Meter to Foot
                    .addAttribute("Temperature", "Real", "1.8 * temperature + 32", Arrays.asList("temperature")) // change the unit of the temperature from Celcius to Fahernheit
                    .addAttribute("SN", "Real", "soilNi / 1000", Arrays.asList("soilNi"))// change the unit of the Nitrogen Body from micro to mili gram per volume unit.
                    .addAttribute("ID", "Integer", "id")

                    // the rows that their ATTRIBUTEs (not their FILEDs) match the where clause's predicate will appear in the result set
                    .where("Elevation >= 00 && Elevation <= 300 && Temperature >= 0 && Temperature <= 300")
                    // optioanl: skips the defined number of records. these are the recored that have passed the where clause.
                    .skip(2)
                    // optional: takes the defined number of records or reaches the end of the file. when number of items are taken, the rest of the file is ignored (NOT PROCESSED)
                    .take(2)
                    // declares how and in which order the result set should be ordered
                    .addSort("Elevation", "Asc")
                    .addSort("Temperature", "DESC") // DESC
                    //.addSort("Timestamp", "Asc")
                    .addSort("ID", "Asc")

                    // puts together the entity class () and the reader class, compiles them ans instantiates an object of the type reader and returns it.
                    // using the reader, its possible to run the query against a specified file and have the result set.
                    .build() 
                    ;
            if(reader != null){
                // when the reader is built, it can be used nutiple time having different CSV settings
                // as long as the query has not changed. means the reader can read/ query different files that share the same column info
                // but maybe different delimiter, etc.
                List<Object> result = reader
                        .columnDelimiter(",")
                        .quoteMarker("\"")
                        //.unitDelimiter("::")
                        .source("D:\\data\\data_10_time.csv")
                        //.lineParser(null) // its possible to pass a custom line parser. 
                        //.trimTokens(true)
                        .bypassFirstRow(true)
                        .read();
                //System.out.println("The result set contains " + result.stream().count() + " records.");
            }
        } catch (Throwable ex){
            throw new Exception(ex.getMessage(), ex);
        }
    }    
}
