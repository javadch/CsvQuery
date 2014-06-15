//package com.vaiona.test;
//
//import com.vaiona.csvmonster.DataReader;
//import com.vaiona.data.HeaderItem;
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Comparator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class TestReader implements DataReader<TestEntity> {
//
//    BufferedReader reader;
//    Map<String, HeaderItem> headers = new LinkedHashMap<>();
//    String columnDelimiter = ",";
//    String quoteDelimiter = "\"";
//    String typeDelimiter = ":";
//    String unitDelimiter = "::";
//    String commentIndicator = "#";
//    String missingValue = "NA";
//    String source = "";
//    boolean bypassFirstRow = false;
//
//    public List<TestEntity> read() throws FileNotFoundException, IOException {
//        reader = new BufferedReader(new FileReader(source));
//
//        Comparator<TestEntity> sorter = new Comparator<TestEntity>() {
//            @Override
//            public int compare(TestEntity left, TestEntity right) {
//                if (Double.compare(left.Temperature, right.Temperature) != 0) {
//                    return Double.compare(right.Temperature, left.Temperature);
//                } else if (Double.compare(left.Elevation, right.Elevation) != 0) {
//                    return Double.compare(left.Elevation, right.Elevation);
//                } else {
//                    return Double.compare(left.Latitude, right.Latitude); //left.Timestamp.compareTo(right.Timestamp) 
//                }
//                
//            }
//        };       
//        
//        if (this.bypassFirstRow) {
//            reader.readLine();
//        }
//        List<TestEntity> result = reader.lines()
//                .filter(p -> !p.trim().startsWith(commentIndicator))
//                .map(p -> p.split(columnDelimiter))
//                .map(p -> new TestEntity(p))
//                .filter(p -> (p.isValid == true)
//                        && (p.Elevation >= 00 && p.Elevation <= 323 && p.Temperature >= 49 && p.Temperature <= 100)
//                )
//                .map(p -> p.populate())
//                .filter(p -> p.isValid)
//                .sorted(sorter)
//                .skip(10)
//                .limit(10)
//                .collect(Collectors.toList());
//        return result;
//    }
//
//    @Override
//    public DataReader<TestEntity> columnDelimiter(String value) {
//        columnDelimiter = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<TestEntity> quoteDelimiter(String value) {
//        quoteDelimiter = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<TestEntity> typeDelimiter(String value) {
//        typeDelimiter = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<TestEntity> unitDelimiter(String value) {
//        unitDelimiter = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<TestEntity> missingValue(String value) {
//        missingValue = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<TestEntity> source(String value) {
//        source = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<TestEntity> bypassFirstRow(Boolean value) {
//        bypassFirstRow = value;
//        return this;
//    }
//}
