package com.vaiona.csv.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author standard
 */
public interface DataReader<T> {
    DataReader<T> columnDelimiter(String value);
    DataReader<T> columnDelimiterRight(String value);
    DataReader<T> quoteMarker(String value);
    DataReader<T> typeDelimiter(String value);
    DataReader<T> unitDelimiter(String value);
    DataReader<T> missingValue(String value);
    DataReader<T> source(String value);
    DataReader<T> sourceRight(String value);
    DataReader<T> target(String value);
    DataReader<T> bypassFirstRow(Boolean value);
    DataReader<T> bypassFirstRowRight(Boolean value);
    //DataReader<T> lineParser(LineParser value);
    //DataReader<T> lineParserRight(LineParser value);
    DataReader<T> trimTokens(boolean value);
    List<T> read() throws FileNotFoundException, IOException;
}
