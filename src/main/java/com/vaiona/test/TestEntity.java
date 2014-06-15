//package com.vaiona.test;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class TestEntity {
//
//    public Date Timestamp;
//    public double Longitude;
//    public double Latitude;
//    public double Elevation;
//    public double Temperature;
//    public double SN;
//    public int ID;
//    public boolean isValid = true;
//    private String[] row;
//
//    public TestEntity(String[] row) {
//        try {
//            Elevation = (double) (Double.parseDouble(row[3]) / 0.30480);
//            Temperature = (double) (1.8 * Double.parseDouble(row[4]) + 32);
//        } catch (Exception ex) {
//            isValid = false;
//        }
//        if (isValid) {
//            this.row = row;
//        }
//    }
//
//    public TestEntity populate() {
//        try {
//            Timestamp = ((new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")).parse(row[0]));
//            Longitude = (double) (Double.parseDouble(row[1]));
//            Latitude = (double) (Double.parseDouble(row[2]));
//            SN = (double) (Double.parseDouble(row[5]) / 1000);
//            ID = (int) (Integer.parseInt(row[6]));
//        } catch (Exception ex) {
//            isValid = false;
//        }
//        row = null;
//        return this;
//    }
//}
