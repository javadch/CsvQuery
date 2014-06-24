
package com.vaiona.csv.reader;
import java.text.SimpleDateFormat;
import java.util.Date;
public class TestEntity {
  public double Elevation;
  public double Temperature;
  public String Description;
  public double Latitude;
  public int ID;
  public double SN;
  public double Longitude;
 public boolean isValid = true;
private String[] row;
public TestEntity (String[] row){
try {
 Temperature = (double)( (( (( 1.8 ) * ( Double.parseDouble(row[3]) )) ) + ( 32 )));
 } catch (Exception ex) {
isValid = false;
}
if(isValid){
this.row = row;
}
}
public TestEntity populate(){
try {
 Elevation = (double)( (( Double.parseDouble(row[2]) ) / ( 0.3048 )));
 Description = ( String.valueOf(row[6]));
 Latitude = (double)( Double.parseDouble(row[1]));
 ID = (int)( Integer.parseInt(row[5]));
 SN = (double)( (( Double.parseDouble(row[4]) ) / ( 1000 )));
 Longitude = (double)( Double.parseDouble(row[0]));
 } catch (Exception ex) {
isValid = false;
}
row = null;
return this;
}
}
