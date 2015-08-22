/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.csv.reader;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author standard
 */
public class DefaultLineParser implements LineParser{
    private int delimiter = ',';
    private int quoteMarker = '\"';
    private boolean trimTokens = true;

    @Override
    public String[] split(String line) { // needs more work!
        // mostly taken from http://agiletribe.wordpress.com/2012/11/23/the-only-class-you-need-for-csv-files/
        int index = 0;
        int ch = line.charAt(index);
        while (ch == '\r' || ch == '\n') {
            ch = line.charAt(++index);
        }
        if (ch<0) {
            return null;
        }
        List<String> tokens = new ArrayList<>();
        StringBuffer curToken = new StringBuffer();
        boolean inquotes = false;
        boolean started = false;
        while (ch>=0 && index < line.length()) {
            if (inquotes) {
                started=true;
                if (ch == quoteMarker) {
                    inquotes = false;
                    curToken.append((char)quoteMarker); // put the quotes to the extracted token
                }                
                else { // needs more work, quotes inside quotes!
                    curToken.append((char)ch);
                }
            }
            else {
                if (ch == quoteMarker) {
                    inquotes = true;
                    curToken.append((char)quoteMarker);
                    // needs more work
//                    if (started) {
//                        // if this is the second quote in a value, add a quote
//                        // this is for the double quote in the middle of a value
//                        curToken.append(quoteMarker);
//                    }
                }
                else if (ch == delimiter) {
                    tokens.add(curToken.toString());
                    curToken = new StringBuffer();
                    started = false;
                }
                else if (ch == '\r') {
                    //ignore CR characters
                }
                else if (ch == '\n') {
                    //end of a line, break out
                    break;
                }
                else {
                    curToken.append((char)ch);
                }
            }
            if(++index < line.length()) // avoid geting out of range
                ch = line.charAt(index);
        }
        tokens.add(this.trimTokens? curToken.toString().trim(): curToken.toString());
        String[] t = tokens.toArray(new String[tokens.size()]);
        return t;
    }

    @Override
    public String join(String... segments) {
        String joined = String.join(String.valueOf((char)delimiter), segments);
        return joined;
    }

    @Override
    public void setQuoteMarker(String value) {
        this.quoteMarker = value.charAt(0);
    }

    @Override
    public void setDilimiter(String value) {
        this.delimiter = value.charAt(0);
    }
    
    @Override
    public void setTrimTokens(boolean value) {
        this.trimTokens = value;
    }
    
}
