package com.vaiona.csv.reader;

import com.vaiona.csv.reader.*;
import com.vaiona.commons.data.AttributeInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class TestReaderJoin implements DataReader<TestEntityJoin> {

    BufferedReader leftContainer, rightContainer;
    BufferedWriter writer;
//Map<String, FieldInfo> headers = new LinkedHashMap<>();
    String columnDelimiter = ",";
    String quoteMarker = "\"";
    String typeDelimiter = ":";
    String unitDelimiter = "::";
    String commentIndicator = "#";
    String missingValue = "NA";
    String leftSource = "";
    String rightSource = "";
    String target = "";
    boolean bypassFirstRow = false;
    boolean trimTokens = true;
    LineParser lineParser = new DefaultLineParser();

    public List<TestEntityJoin> read() throws FileNotFoundException, IOException {
        leftSource = "D:\\Projects\\PhD\\Data\\leftJoin1.csv";
        rightSource = "D:\\Projects\\PhD\\Data\\rightJoin1.csv";
        List<TestEntityJoin> resultset = new ArrayList<>();
        if (this.bypassFirstRow) {
            leftContainer.readLine();
        }
        lineParser.setQuoteMarker(quoteMarker);
        lineParser.setDilimiter(columnDelimiter);
        lineParser.setTrimTokens(trimTokens);

        long skipped =0, taken =0;
        long skip =1, take = 2;
        leftContainer = new BufferedReader(new FileReader(leftSource));
        FileInputStream rightInputStream = new FileInputStream(rightSource);
        for(String[] left: leftContainer.lines()
                .filter(p -> !p.trim().startsWith(commentIndicator))
                .map(p -> lineParser.split(p)).collect(Collectors.toList())                 
            ){
            if(taken >= take) break;
            try{
                rightInputStream.getChannel().position(0);
                rightContainer = new BufferedReader(new InputStreamReader(rightInputStream));
                for(String[] right: rightContainer.lines()
                        .filter(p -> !p.trim().startsWith(commentIndicator))
                        .map(p -> lineParser.split(p)).collect(Collectors.toList()) 
                    ){
                    TestEntityJoin row = new TestEntityJoin(left, right);
                    if(!row.isValid) continue; // filter rows with wrong keys
                    if(row.LeftKey != row.RightKey) continue; // INNER JOIN key match
                    row.populateForWhere(); // populate attributes required by the where clause.
                    if(!row.isValid || row.Longitude < 10) continue; // check population validity and appy the Where clause.
                    // if no sorting is requested, try applying limits here, otherwise do it when the join is finished
                    if(skipped++ < skip) continue;
                    row.populateLeft(); // populate the ramining attributes.
                    row.populateRight();
                    resultset.add(row);
                    if(taken++ >= take) break;
                }
            } catch (IOException ex){
                // throw proper exception
            }
        }

        List<TestEntityJoin> result
            = resultset.stream()
                .skip(1)
                .limit(9)
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
            Logger.getLogger(TestReaderJoin.class.getName()).log(Level.SEVERE, null, ex); // change it with an AdpaterExcetion
        }
    }

    @Override
    public DataReader<TestEntityJoin> columnDelimiter(String value) {
        columnDelimiter = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> quoteMarker(String value) {
        quoteMarker = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> trimTokens(boolean value) {
        trimTokens = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> typeDelimiter(String value) {
        typeDelimiter = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> unitDelimiter(String value) {
        unitDelimiter = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> missingValue(String value) {
        missingValue = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> source(String value) {
        leftSource = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> target(String value) {
        target = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> bypassFirstRow(Boolean value) {
        bypassFirstRow = value;
        return this;
    }

    @Override
    public DataReader<TestEntityJoin> lineParser(LineParser value) {
        lineParser = value;
        return this;
    }

}
