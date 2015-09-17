package com.vaiona.csv.reader;

import com.vaiona.commons.data.DataReaderBase;

/**
 *
 * @author Javad Chamanara
 */
public interface DataReader<T, S1, S2> extends DataReaderBase<T, S1, S2> {
    DataReader<T, S1, S2> columnDelimiter(String value);
    DataReader<T, S1, S2> columnDelimiterRight(String value);
    DataReader<T, S1, S2> quoteMarker(String value);
    DataReader<T, S1, S2> typeDelimiter(String value);
    DataReader<T, S1, S2> unitDelimiter(String value);
    DataReader<T, S1, S2> missingValue(String value);
    DataReader<T, S1, S2> source(String value);
    DataReader<T, S1, S2> sourceRight(String value);
    DataReader<T, S1, S2> target(String value);
    DataReader<T, S1, S2> bypassFirstRow(Boolean value);
    DataReader<T, S1, S2> bypassFirstRowRight(Boolean value);
    //DataReader<T> lineParser(LineParser value);
    //DataReader<T> lineParserRight(LineParser value);
    DataReader<T, S1, S2> trimTokens(boolean value);
}
