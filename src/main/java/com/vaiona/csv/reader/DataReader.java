package com.vaiona.csv.reader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author standard
 */
public interface DataReader<T> {
    public DataReader<T> columnDelimiter(String value);
    public DataReader<T> quoteMarker(String value);
    public DataReader<T> typeDelimiter(String value);
    public DataReader<T> unitDelimiter(String value);
    public DataReader<T> missingValue(String value);
    public DataReader<T> source(String value);
    public DataReader<T> bypassFirstRow(Boolean value);
    public List<T> read() throws FileNotFoundException, IOException;
}
