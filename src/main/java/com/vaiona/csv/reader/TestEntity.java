package com.vaiona.csv.reader;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestEntity {

    public double Longitude;
    public double Latitude;
    public double Elevation;
    public int ID;
    public double Temperature;
    public double SN;
    public String Description;
    public boolean isValid = true;
    private String[] row;

    public TestEntity(String[] row) {
 // if there is no where clause, usually there is no need for a pre population, hence, the Pre is empty
        try {
            Description = (String.valueOf(row[6]));
        } catch (Exception ex) {
            isValid = false;
        }
        if (isValid) {
            this.row = row;
        }
    }

    public TestEntity populate() {
        try {
            Longitude = (double) (Double.parseDouble(row[0]));
            Latitude = (double) (Double.parseDouble(row[1]));
            Elevation = (double) (((Double.parseDouble(row[2])) / (0.3048)));
            ID = (int) (Integer.parseInt(row[5]));
            Temperature = (double) (((((1.8) * (Double.parseDouble(row[3])))) + (32)));
            SN = (double) (((Double.parseDouble(row[4])) / (1000)));
        } catch (Exception ex) {
            isValid = false;
        }
        row = null;
        return this;
    }
}
