package com.vaiona.csv.reader;

import com.vaiona.commons.compilation.ObjectCreator;
import com.vaiona.commons.data.AttributeInfo;
import com.vaiona.commons.data.DataReaderBuilderBase;
import com.vaiona.commons.data.FieldInfo;
import com.vaiona.commons.types.TypeSystem;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import xqt.model.functions.AggregationCallInfo;

public class DataReaderBuilder extends DataReaderBuilderBase{

    String columnDelimiter = ",";
    String rightColumnDelimiter = ",";
    String typeDlimiter = ":";
    String unitDlimiter = "::";
    String containerName = "";
    String rightContainerName = "";

    
    public String getContainerName(){ return containerName;}
    public DataReaderBuilder containerName(String value){
        this.containerName = value;
        return this;
    }

    public String getRightContainerName(){ return rightContainerName;}
    public DataReaderBuilder rightContainerName(String value){
        this.rightContainerName = value;
        return this;
    }

    public String getColumnDelimiter(){ return columnDelimiter;}
    public DataReaderBuilder columnDelimiter(String value){
        this.columnDelimiter = value;
        return this;
    }
    
    public String getLeftColumnDelimiter(){ return columnDelimiter;}
    public DataReaderBuilder leftColumnDelimiter(String value) {
        this.columnDelimiter = value;
        return this;
    }

    public String getRightColumnDelimiter(){ return rightColumnDelimiter;}
    public DataReaderBuilder rightColumnDelimiter(String value) {
        this.rightColumnDelimiter = value;
        return this;
    }

    public String getTypeDelimiter(){ return typeDlimiter;}
    public DataReaderBuilder typeDlimiter(String value){
        this.typeDlimiter = value;
        return this;
    }
    
    public String getUnitDelimiter(){ return unitDlimiter;}
    public DataReaderBuilder unitDlimiter(String value){
        this.unitDlimiter = value;
        return this;
    }

    List<AggregationCallInfo> aggregationCallInfo = new ArrayList<>();
    public DataReaderBuilder addAggregates(List<AggregationCallInfo> value) {
        aggregationCallInfo = value;
        return this;
    }

    
    public DataReader build(Class classObject) throws IOException, ClassNotFoundException, NoSuchMethodException, 
            InstantiationException, IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException {
       try{        
            DataReader<Object, Object, Object> instance = (DataReader<Object, Object, Object>)ObjectCreator.createInstance(classObject);    
            instance
                    .columnDelimiter(this.columnDelimiter)
                    .columnDelimiterRight(this.rightColumnDelimiter)
                    .typeDelimiter(this.typeDlimiter)
                    .unitDelimiter(this.unitDlimiter);
            return instance;
       }catch(Exception ex){
           // better to escalate the exception
           return null;
       }
    }

    @Override
    protected String translate(AttributeInfo attribute, boolean rightSide) {
        String translated = "";
        for (StringTokenizer stringTokenizer = new StringTokenizer(attribute.forwardMap, " "); stringTokenizer.hasMoreTokens();) {
            String token = stringTokenizer.nextToken();
            boolean found = false;
            String properCaseToken = token;
            if(!namesCaseSensitive)
                properCaseToken = token.toLowerCase();
            if(!rightSide && fields.containsKey(properCaseToken)){
                FieldInfo fd = fields.get(properCaseToken);
                // need for a type check
                // the follwoing statement, sets a default format for the date, if the field is of type Date
                String temp = TypeSystem.getTypes().get(fd.conceptualDataType).getCastPattern().replace("$data$", "row[" + fd.index + "]");
                if(fd.conceptualDataType.equalsIgnoreCase(TypeSystem.TypeName.Date)
                    || (attribute.conceptualDataType.equalsIgnoreCase(TypeSystem.TypeName.Date))){
                    // check whether the field has date format, if yes, apply it
                    if(fd.unit!= null && !fd.unit.isEmpty() && !fd.unit.equalsIgnoreCase(TypeSystem.TypeName.Unknown)){
                        temp = TypeSystem.getTypes().get(fd.conceptualDataType).makeDateCastPattern(fd.unit).replace("$data$", "row[" + fd.index + "]");
                    // check wether the attribute has date format, if yes, apply it
                    } else if(attribute.unit!= null && !attribute.unit.isEmpty()  && !attribute.unit.equalsIgnoreCase(TypeSystem.TypeName.Unknown)){
                        temp = TypeSystem.getTypes().get(fd.conceptualDataType).makeDateCastPattern(attribute.unit).replace("$data$", "row[" + fd.index + "]");                        
                    }
                }
                translated = translated + " " + temp;
                found = true;
            }
            if(rightSide && rightFields.containsKey(properCaseToken)){
                FieldInfo fd = rightFields.get(properCaseToken);
                // need for a type check
                // the righside attributes reffer to the right side fields.Tthe Entity is a product of a line of the left and the right container
                // The generated code, creates the product by concatenating the left and right string arrays and passes them as the cotr argument 
                // to the Entity. This is why the fied indexes for the right side attributes are shifted by the size of the left hand side field array.
                String temp = TypeSystem.getTypes().get(fd.conceptualDataType).getCastPattern().replace("$data$", "row[" + (fields.size() + fd.index) + "]");
                if(fd.conceptualDataType.equalsIgnoreCase(TypeSystem.TypeName.Date)
                    || (attribute.conceptualDataType.equalsIgnoreCase(TypeSystem.TypeName.Date))){
                    // check whether the field has date format, if yes, apply it
                    if(fd.unit!= null && !fd.unit.isEmpty() && !fd.unit.equalsIgnoreCase(TypeSystem.TypeName.Unknown)){
                        temp = TypeSystem.getTypes().get(fd.conceptualDataType).makeDateCastPattern(fd.unit).replace("$data$", "row[" + (fields.size() + fd.index) + "]");
                    // check wether the attribute has date format, if yes, apply it
                    } else if(attribute.unit!= null && !attribute.unit.isEmpty()  && !attribute.unit.equalsIgnoreCase(TypeSystem.TypeName.Unknown)){
                        temp = TypeSystem.getTypes().get(fd.conceptualDataType).makeDateCastPattern(attribute.unit).replace("$data$", "row[" + (fields.size() + fd.index) + "]");                        
                    }
                }
                translated = translated + " " + temp;
                found = true;
            }
            if(!found) {
                translated = translated + " " + token;
            }            
        }
        // enclose the translated attribute in a data conversion based on the attributes type
        //translated = dataTypes.get(type).replace("$data$", translated);
        // consider Date!!!
        if(attribute.conceptualDataType.equals("Date") || attribute.conceptualDataType.equals("String")){
            translated = "(" + translated + ")";
        }
        else { // cast the translated expression to the attribute type
            translated = "(" + TypeSystem.getTypes().get(attribute.conceptualDataType).getRuntimeType().toLowerCase() + ")(" + translated + ")";
        }
        
        return translated;
    }
    
    @Override
    protected void buildSharedSegments(){
        super.buildSharedSegments();
        String header = String.join(columnDelimiter, resultEntityAttributes.values().stream().map(p-> p.name + ":" + p.internalDataType).collect(Collectors.toList()));
        readerContext.put("rowHeader", header);        
        String linePattern = String.join(",", resultEntityAttributes.values().stream().map(p-> "String.valueOf(entity." + p.name + ")").collect(Collectors.toList()));
        readerContext.put("linePattern", linePattern);     
        readerContext.put("sourceOfData", sourceOfData);     
        readerContext.put("LeftClassName", this.leftClassName); // used as both left and right sides' type.
        readerContext.put("RightClassName", this.leftClassName); // in the single container it is not used by the reader, but shold be provided for compilation purposes.
        readerContext.put("AggregationCallInfos", this.aggregationCallInfo);
        // do not move these items to the base class
        recordContext.put("Attributes", rowEntityAttributes.values().stream().collect(Collectors.toList()));           
    }
    
    @Override
    protected void buildSingleSourceSegments(){
        super.buildSingleSourceSegments();
        if(sourceOfData.equalsIgnoreCase("container")){
            String otherCalssNames = (namespace + "." + baseClassName + "Entity");
            readerContext.put("LeftClassName", "Object"); // used as both left and right sides' type.
            readerContext.put("RightClassName", "Object"); // in the single container it is not used by the reader, but shold be provided for compilation purposes.
            readerContext.put("TargetRowType", otherCalssNames);
            readerContext.put("ContainerName", this.containerName);            
        } else if (sourceOfData.equalsIgnoreCase("variable")){
            readerContext.put("LeftClassName", this.leftClassName); // used as both left and right sides' type.
            readerContext.put("RightClassName", this.leftClassName); // in the single container it is not used by the reader, but shold be provided for compilation purposes.
            readerContext.put("TargetRowType", this.leftClassName);            
        }
        // it is a test case for the aggregate reader, delete it from here. it is safe
//        AggregationCallInfo callInfo = ((List<AggregationCallInfo>)readerContext.get("AggregationCallInfos")).get(0);
//        FunctionImplementation xm = callInfo
//                .getFunction().getFunctionSpecification().getImplementations().stream()
//                .filter(p->p.getDialect().equalsIgnoreCase("default"))
//                .findFirst().get();        
//        xm = null;
//        for(FunctionImplementation fm : callInfo.getFunction().getFunctionSpecification().getImplementations()){
//            if(fm.getDialect().equalsIgnoreCase("default")){
//                xm = fm;
//                break;
//            }
//        }
    }

    @Override
    protected void buildJoinedSourceSegments(){
        super.buildJoinedSourceSegments();
        readerContext.put("LeftClassName", "Object"); 
        readerContext.put("RightClassName", "Object");
        readerContext.put("TargetRowType", (namespace + "." + baseClassName + "Entity"));
        readerContext.put("ContainerName", this.containerName);            
        readerContext.put("RightContainerName", this.rightContainerName);            
        readerContext.put("LeftFieldsNo", this.fields.size());                    
        readerContext.put("RightFieldsNo", this.rightFields.size());                    
    }

}
