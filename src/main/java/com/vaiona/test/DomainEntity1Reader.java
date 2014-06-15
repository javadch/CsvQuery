//package com.vaiona.test;
//
//import com.vaiona.csvmonster.DataReader;
//import com.vaiona.data.HeaderItem;
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.Comparator;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class DomainEntity1Reader implements DataReader<DomainEntity1> {
//    BufferedReader reader;
//    Map<String, HeaderItem> headers = new LinkedHashMap<>();
//
//    String  columnDelimiter     = ",";
//    String  quoteDelimiter       = "\"";
//    String  typeDelimiter       = ":";
//    String  unitDelimiter       = "::";
//    String  commentIndicator    = "#";
//    String  missingValue        = "NA";
//    String  source              = "";
//    boolean bypassFirstRow      = false;
//
//    public List<HeaderItem> getHeaderItems() {
//        return headers.values().stream().collect(Collectors.toList());
//    }
//
//
//    // for cases that you have no control on the generated data file. SO that you can not add/ change the headers there,
//    // or they will be overriden by data producers.
//    // external header files are also good if you like to enrich them by data types and units of measurement.
//    // its possible to have the external header file as well as the internal header line. in this case the internal header
//    // will be ignored.
//    
////    public DomainEntity1Reader(String headerFilename, boolean firstRowIsHeader, String delimiter) throws FileNotFoundException, IOException{
////        BufferedReader headerReader = new BufferedReader(new FileReader(headerFilename));
////        parseHeader(headerReader);
////
////        init(firstRowIsHeader, true, delimiter);
////    }
//
//    @Override
//    public List<DomainEntity1> read() throws FileNotFoundException, IOException{
//        reader = new BufferedReader(new FileReader(source));
//        
//        Comparator<DomainEntity1> sorter = new java.util.Comparator<DomainEntity1>() {
//                 @Override
//                 public int compare(DomainEntity1 l, DomainEntity1 r){
//                  if(Double.compare(l.Elevation, r.Elevation) != 0){
//                        return Double.compare(l.Elevation, r.Elevation);
//                    } else {
//                        return Double.compare(r.Temperature, l.Temperature);
//                    }
//               }
//              };
//        
//        if(this.bypassFirstRow){
//                reader.readLine();
//        }    
//        
//        List<DomainEntity1> result = (List<DomainEntity1>) reader.lines()
//            // take out commented inout lines
//            .filter(p -> !p.trim().startsWith(commentIndicator))
//            // use a line parser here, map the line to a String[]
//            .map(p -> p.split(columnDelimiter))
//            // pre populate domain objects and apply conversions
//            // just on those attributes used in the filter clase
//            .map(p -> new DomainEntity1(p, missingValue))
//            .filter(p -> p.isValid == true && 
//                    ( p.Elevation >= 00 && p.Elevation <= 1200)
//                    &&
//                    (p.Temperature >= 32 && p.Temperature <= 122)
//                    )
//            //.parallel()
//            //post population of those attributes not taking part in the filtering
//            // this help to populate non required attributes after fileting to save some cpu time.
//            // the domain entity keeps a reference to its associated row from the pre population up to here, and
//            // releases it after post population is done.
//            //.collect(Collectors.toList()).parallelStream() // its safe to collect the items here, as filtering is finished
//            .map(p->p.populate())
//            .filter(p->p.isValid) // remove items that are made invalid by the post population                
//            //.sorted(sorter)
//            //.skip(20)
//            //.limit(10)                
//            .collect(Collectors.toList())
////            .sort((DomainEntity1 l, DomainEntity1 r)->                         
////                      {
////                    if(Double.compare(l.Elevation, r.Elevation) != 0){
////                        return Double.compare(l.Elevation, r.Elevation);
////                    } else {
////                        return Double.compare(r.Temperature, l.Temperature);
////                    }
////                }     
////            )
//            //Comparator<Integer> normal = Integer::compare;
//            //Comparator<Integer> reversed = normal.reversed(); 
//            //Collections.sort(listOfIntegers, reversed);                
//            ;
//        // sort the objects first by elevation then by temperature descending
////        Collections.sort(result, (DomainEntity1 l, DomainEntity1 r)-> 
////                {
////                    if(Double.compare(l.Elevation, r.Elevation) != 0){
////                        return Double.compare(l.Elevation, r.Elevation);
////                    } else {
////                        return Double.compare(r.Temperature, l.Temperature);
////                    }
////                }
////            );
//        return result;
//    }
//
////    private void parseHeader(BufferedReader reader) throws IOException {
////        headers.clear();
////        String headerLine = reader.readLine();
////        firstRowIsRead = true;
////        Scanner scanner = new Scanner(headerLine);
////        scanner.useDelimiter(columnDelimiter);
////        // header items can be one these formats: Name/ Name:Type/ Name:Type::Unit
////        int indexCount = 0;
////        while(scanner.hasNext()){
////            HeaderItem item = new HeaderItem();
////            item.setIndex(indexCount);
////            String temp = scanner.next().trim().replace("\"", "");
////            if(temp.contains(typeDelimiter)){
////                if(temp.contains(unitDelimiter)){
////                    item.setName(temp.substring(0, temp.indexOf(typeDelimiter)));
////                    item.setDataType(temp.substring(
////                            temp.indexOf(typeDelimiter)+typeDelimiter.length(), temp.indexOf(unitDelimiter)));
////                    item.setUnit(temp.substring(
////                            temp.indexOf(unitDelimiter)+unitDelimiter.length(), temp.length()));
////                }
////                else{
////                    item.setName(temp.substring(0, temp.indexOf(typeDelimiter)));
////                    item.setDataType(temp.substring(
////                            temp.indexOf(typeDelimiter)+typeDelimiter.length(), temp.length()));
////                }
////            }
////            else{
////                item.setName(temp);
////            }
////            headers.put(item.getName(), item);
////            indexCount++;
////        }
////    }
////
////    private void init(String filename, boolean firstRowIsHeader, boolean bypassHeader, String delimiter) 
////            throws FileNotFoundException, IOException {
////        this.firstRowIsHeader = firstRowIsHeader;
////        this.columnDelimiter = delimiter;
////
////        reader = new BufferedReader(new FileReader(filename));
////        if(this.firstRowIsHeader){
////            if(bypassHeader)
////                reader.readLine();
////            else{
////                // not used
////                //parseHeader(reader);
////            }                
////        }
////    }
//
//    @Override
//    public DataReader<DomainEntity1> columnDelimiter(String value) {
//        columnDelimiter = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<DomainEntity1> quoteDelimiter(String value) {
//        quoteDelimiter = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<DomainEntity1> typeDelimiter(String value) {
//        typeDelimiter = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<DomainEntity1> unitDelimiter(String value) {
//        unitDelimiter = value;
//        return this;
//    }
//
//    @Override
//    public DataReader<DomainEntity1> missingValue(String value) {
//        missingValue = value;
//        return this;
//    }
//    
//    @Override
//    public DataReader<DomainEntity1> source(String value){
//        source = value;
//        return this;
//    }
//    
//    @Override
//    public DataReader<DomainEntity1> bypassFirstRow(Boolean value){
//         bypassFirstRow = value;
//        return this;
//    }
//}
