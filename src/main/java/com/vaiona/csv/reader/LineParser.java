/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.csv.reader;

/**
 *
 * @author standard
 */
public interface LineParser {
    String[] split(String line);
    String join(String... segments); //at least one segment should be provided
    void setQuoteMarker(String value);
    void setDilimiter(String value);
    void setTrimTokens(boolean value);
}
