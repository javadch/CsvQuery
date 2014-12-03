/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vaiona.csv.reader;

/**
 *
 * @author Javad Chamanara <chamanara@gmail.com>
 */
public class TestEntityJoin {
    public double Longitude;
    public double Latitude;
    public double Elevation;
    public int LeftKey;
    public int RightKey;
    public double Temperature;
    public double SN;
    public boolean isValid = true;
    private String[] leftRow;
    private String[] rightRow;
    
    public TestEntityJoin(String[] leftRow, String[] rightRow){
        try {
            LeftKey = (int) (Integer.parseInt(leftRow[0]));
            RightKey = (int) (Integer.parseInt(rightRow[0]));
        } catch (Exception ex) {
            isValid = false;
        }
        if (isValid) {
            this.leftRow = leftRow;
            this.rightRow = rightRow;
        }        
    }

    // right anf left population methods are used in outer joins
    public TestEntityJoin populateLeft() {
        Elevation = (double) (Double.parseDouble(leftRow[3]));
        isValid = true;
        return this;
    }

    public TestEntityJoin populateRight() {
        Latitude = (double) (Double.parseDouble(rightRow[2]));
        isValid = true;
        return this;
    }

    public TestEntityJoin populateForWhere() {
        Longitude = (double) (Double.parseDouble(leftRow[1]));
        isValid = true;
        return this;
    }
}
