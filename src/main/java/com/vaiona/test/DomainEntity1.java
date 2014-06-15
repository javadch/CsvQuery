//package com.vaiona.test;
//
//public class DomainEntity1 {
//    public double Longitude;
//    public double Latitude;
//    public double Elevation; // elevation in foot, the field is in meter
//    public double Temperature; // in F, the field is in C
//    public double SN; // in milligram per unit, the field is in microgram per unit
//    public boolean isValid = true; //set it to false if the object is not valid for any reason
//    
//    private String[] rowTemp; // should be a weak reference
//    
//    public DomainEntity1(String[] row, String missingValue){
//        // the field elevation is in index 6
//        //Elevation
//        int position = 2; // th index should be checked during the template generation
//        if(position > 0 && position < row.length){  //in range 
//            String cell = row[position];
//            if(
//                cell == null || cell.isEmpty() // no data
//                || cell.toUpperCase().equals(missingValue.toUpperCase())
//              )    {
//                // set the value to some default/ missing indicators
//                // may its possible to use Optional as in Java 8
//                isValid = false;
//            } 
//            else {
//                Elevation = Double.parseDouble(cell) / 0.30480; //convert from M to F
//            }
//        }
//        else {
//            isValid = false;
//        }
//        //Temperature
//        position = 3; // th index should be checked during the template generation
//        if(position > 0 && position < row.length){  //in range 
//            String cell = row[position];
//            if(
//                cell == null || cell.isEmpty() // no data
//                || cell.toUpperCase().equals(missingValue.toUpperCase())
//              )    {
//                // set the value to some default/ missing indicators
//                // may its possible to use Optional as in Java 8
//                isValid = false;
//            } 
//            else {
//                Temperature = Double.parseDouble(cell)*1.80 + 32; //convert from C to F
//            }
//        }
//        else {
//            isValid = false;
//        }
//        
//        // do the same for other attributes
//        if(isValid){
//            rowTemp = row;
//        }
//       
//    }
//    
//    public DomainEntity1 populate(){
//        //populate all other attributes and release rowTemp
//        // make the object invalid if something went wrong
//        // apply data conversion checks
//        Longitude = Double.parseDouble(rowTemp[0]); // also apply conversion formulas, if any
//        Latitude = Double.parseDouble(rowTemp[1]);
//        SN = Double.parseDouble(rowTemp[5]) / 1000;
//        rowTemp = null;
//        return this;
//    }
//    
////    private Double map(Integer position){
////        position = 3; // th index should be checked during the template generation
////        if(position > 0 && position < row.length){  //in range 
////            String cell = row[position];
////            if(
////                cell == null || cell.isEmpty() // no data
////                || cell.toUpperCase().equals(missingValue.toUpperCase())
////              )    {
////                // set the value to some default/ missing indicators
////                // may its possible to use Optional as in Java 8
////                isValid = false;
////            } 
////            else {
////                elevation = Double.parseDouble(cell)*0.30480; //convert from foot to meter
////            }
////        }
////        else {
////            isValid = false;
////        }
////    }
////     public DomainEntity1(String row){
////        // the field elevation is in index 6
////        elevation = Double.parseDouble(row.split(",")[6])*0.30480;
////    }
//}
