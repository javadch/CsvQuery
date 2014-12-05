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
    private String[] row;
    
    // pre populate
    public TestEntityJoin(String[] row){
        try {
            LeftKey = (int) (Integer.parseInt(row[0]));
            RightKey = (int) (Integer.parseInt(row[9]));
        } catch (Exception ex) {
            isValid = false;
        }
        if (isValid) {
            this.row = row;
        }        
    }

    // right anf left population methods are used in outer joins
    // populate 1
    public TestEntityJoin populate() {
        Elevation = (double) (Double.parseDouble(row[3]));
        isValid = true;
        return this;
    }

    // populate 2
    public TestEntityJoin populateRight() {
        Latitude = (double) (Double.parseDouble(row[2]));
        isValid = true;
        return this;
    }

    // populate 3
    public TestEntityJoin midPopulate() {
        Longitude = (double) (Double.parseDouble(row[1]));
        isValid = true;
        return this;
    }
    
    // post populate
}
