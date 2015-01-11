package com.vaiona.csv.reader;

import com.vaiona.csv.reader.*;
import com.vaiona.commons.data.AttributeInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestReader implements DataReader<TestEntity, TestEntity, TestEntity> {

    BufferedReader reader;
    BufferedWriter writer;
//Map<String, FieldInfo> headers = new LinkedHashMap<>();
    String columnDelimiter = ",";
    String quoteMarker = "\"";
    String typeDelimiter = ":";
    String unitDelimiter = "::";
    String commentIndicator = "#";
    String missingValue = "NA";
    String source = "";
    String target = "";
    boolean bypassFirstRow = false;
    boolean trimTokens = true;
    LineParser lineParser = new DefaultLineParser();

    public List<TestEntity> read(List<TestEntity> source1, List<TestEntity> source2) throws FileNotFoundException, IOException {
        reader = new BufferedReader(new FileReader(source));

        if (this.bypassFirstRow) {
            reader.readLine();
        }
        lineParser.setQuoteMarker(quoteMarker);
        lineParser.setDilimiter(columnDelimiter);
        lineParser.setTrimTokens(trimTokens);
        List<TestEntity> result
                = reader.lines()
                .filter(p -> !p.trim().startsWith(commentIndicator))
                .map(p -> lineParser.split(p))
                .map(p -> new TestEntity(p))
                .filter(p -> (p.isValid == true) && ((p.Description.matches("-?\\d+(\\.\\d+)?"))))
                .skip(1)
                .limit(9)
                .map(p -> p.populate())
                .filter(p -> p.isValid)
                .collect(Collectors.toList());
        return result;
    }

    private void writeToFile(TestEntity entity) {
        try {
            if (writer == null) {
                writer = new BufferedWriter(new FileWriter(target));
                writer.write("Longitude:Real,Latitude:Real,Elevation:Real,ID:Integer,Temperature:Real,SN:Real,Description:String" + "\n");
            }
            String line = lineParser.join(String.valueOf(entity.Longitude), String.valueOf(entity.Latitude), String.valueOf(entity.Elevation), String.valueOf(entity.ID), String.valueOf(entity.Temperature), String.valueOf(entity.SN), String.valueOf(entity.Description));
            writer.write(line + "\n");
        } catch (IOException ex) {
            Logger.getLogger(TestReader.class.getName()).log(Level.SEVERE, null, ex); // change it with an AdpaterExcetion
        }
    }

    @Override
    public DataReader columnDelimiter(String value) {
        columnDelimiter = value;
        return this;
    }

    @Override
    public DataReader quoteMarker(String value) {
        quoteMarker = value;
        return this;
    }

    @Override
    public DataReader trimTokens(boolean value) {
        trimTokens = value;
        return this;
    }

    @Override
    public DataReader typeDelimiter(String value) {
        typeDelimiter = value;
        return this;
    }

    @Override
    public DataReader unitDelimiter(String value) {
        unitDelimiter = value;
        return this;
    }

    @Override
    public DataReader missingValue(String value) {
        missingValue = value;
        return this;
    }

    @Override
    public DataReader source(String value) {
        source = value;
        return this;
    }

    @Override
    public DataReader target(String value) {
        target = value;
        return this;
    }

    @Override
    public DataReader bypassFirstRow(Boolean value) {
        bypassFirstRow = value;
        return this;
    }

    @Override
    public DataReader bypassFirstRowRight(Boolean value) {
        return this;
    }

    @Override
    public DataReader sourceRight(String value) {
        return this;
    }

    @Override
    public DataReader columnDelimiterRight(String value) {
        return this;
    }
    
}
