package com.vaiona.csv.reader;

import com.vaiona.csv.reader.*;
import com.vaiona.data.*;
import com.vaiona.data.AttributeInfo;
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

public class TestReader implements DataReader<TestEntity> {

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

    public List<TestEntity> read() throws FileNotFoundException, IOException {
        reader = new BufferedReader(new FileReader(source));

        if (this.bypassFirstRow) {
            reader.readLine();
        }
        lineParser.setQuoteMarker(quoteMarker);
        lineParser.setDilimiter(columnDelimiter);
        lineParser.setTrimTokens(trimTokens);

        reader.lines()
                .filter(p -> !p.trim().startsWith(commentIndicator))
                .map(p -> lineParser.split(p))
                .map(p -> new TestEntity(p))
                .filter(p -> (p.isValid == true) && (((p.Temperature) > (0))))
                .map(p -> p.populate())
                .filter(p -> p.isValid)
                .skip(1)
                .limit(9)
                .peek(p -> writeToFile(p))
                .count() // it is just to make the stream to be consumed
                ;
         if (writer != null){
            try {
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(TestReader.class.getName()).log(Level.SEVERE, null, ex);
            }        
         }
        return null;
    }

    private void writeToFile(TestEntity entity) {
        try {
            if (writer == null) {
                writer = new BufferedWriter(new FileWriter(target));
                writer.write("Elevation:Real,Temperature:Real,Description:String,Latitude:Real,ID:Integer,SN:Real,Longitude:Real" + "\n");
            }
            String line = lineParser.join(String.valueOf(entity.Elevation), String.valueOf(entity.Temperature), String.valueOf(entity.Description)
                    , String.valueOf(entity.Latitude), String.valueOf(entity.ID), String.valueOf(entity.SN), String.valueOf(entity.Longitude));
            System.out.println(line);
            writer.write(line + "\n");
        } catch (IOException ex) {
            Logger.getLogger(TestReader.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    @Override
    public DataReader<TestEntity> columnDelimiter(String value) {
        columnDelimiter = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> quoteMarker(String value) {
        quoteMarker = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> trimTokens(boolean value) {
        trimTokens = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> typeDelimiter(String value) {
        typeDelimiter = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> unitDelimiter(String value) {
        unitDelimiter = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> missingValue(String value) {
        missingValue = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> source(String value) {
        source = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> target(String value) {
        target = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> bypassFirstRow(Boolean value) {
        bypassFirstRow = value;
        return this;
    }

    @Override
    public DataReader<TestEntity> lineParser(LineParser value) {
        lineParser = value;
        return this;
    }
}
