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
    String sourceOfData = "container";

    public String getSourceOfData(){ return columnDelimiter;}
    public DataReaderBuilder sourceOfData(String value){
        this.sourceOfData = value;
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
        for (StringTokenizer stringTokenizer = new StringTokenizer(attribute.forwardMap, " ");
                stringTokenizer.hasMoreTokens();) {
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
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
//    public LinkedHashMap<String, InMemorySourceFile> createSources()  {
//        // check if the statement has no adapter, throw an exception
//        
//        if(baseClassName == null || baseClassName.isEmpty()){
//            baseClassName = "C" + (new Date()).getTime();
//        }
//        attributes.entrySet().stream().map((entry) -> entry.getValue()).forEach((ad) -> {
//            if(ad.joinSide.equalsIgnoreCase("R"))
//                ad.forwardMapTranslated = translate(ad.forwardMap, ad.conceptualDataType, true);
//            else
//                ad.forwardMapTranslated = translate(ad.forwardMap, ad.conceptualDataType, false);
//        });
//        
//        // transform the ordering clauses to their bound equivalent, in each attribute names are linked to the attibutes objects
//        Map<AttributeInfo, String> orderItems = new LinkedHashMap<>();        
//        for (Map.Entry<String, String> entry : ordering.entrySet()) {
//                if(attributes.containsKey(entry.getKey())){
//                    orderItems.put(attributes.get(entry.getKey()), entry.getValue());
//                }            
//        }
//        
//        ClassGenerator generator = new ClassGenerator();
//        
//        Map<String, Object> entityContext = new HashMap<>();
//        entityContext.put("namespace", NAME_SPACE);
//        entityContext.put("BaseClassName", baseClassName);
//        entityContext.put("Attributes", attributes.values().stream().collect(Collectors.toList()));        
//        
//        Map<String, Object> readerContext = new HashMap<>();
//        readerContext.put("Attributes", attributes.values().stream().collect(Collectors.toList()));
//        // the output row header, when the reader, pushes the resultset to another file
//        String header = String.join(columnDelimiter, attributes.values().stream().map(p-> p.name + ":" + p.internalDataType).collect(Collectors.toList()));
//        readerContext.put("rowHeader", header);        
//        String linePattern = String.join(",", attributes.values().stream().map(p-> "String.valueOf(entity." + p.name + ")").collect(Collectors.toList()));
//        readerContext.put("linePattern", linePattern);        
//        readerContext.put("namespace", NAME_SPACE);
//        readerContext.put("BaseClassName", baseClassName);
//        readerContext.put("Where", whereClauseTranslated);
//        readerContext.put("Ordering", orderItems);
//        readerContext.put("skip", skip);
//        readerContext.put("take", take);
//        readerContext.put("writeResultsToFile", writeResultsToFile);
//        String entity = "";
//        String reader = "";
//        
//        if(this.joinType.equalsIgnoreCase("")){ // Single Source
//            // Pre list contains the attributes referenced from the where clause
//            entityContext.put("Pre", referencedAttributes.values().stream().collect(Collectors.toList()));
//            postAttributes = attributes.entrySet().stream()
//                .filter((entry) -> (!referencedAttributes.containsKey(entry.getKey())))
//                .collect(Collectors.toMap(p->p.getKey(), p->p.getValue()));
//            // Single container does not the Mid attributes
//            // Post list contains all the other attributes except those in the Pre
//            entityContext.put("Post", postAttributes.values().stream().collect(Collectors.toList()));
//            // Post_Left and Post_Right should be emtpy in single container cases.
//            List<AttributeInfo> leftOuterItems = postAttributes.values().stream()
//                    .filter(p-> p.joinSide.equalsIgnoreCase("L"))
//                    .collect(Collectors.toList());
//            leftOuterItems.addAll(orderItems.keySet().stream().filter(p-> !leftOuterItems.contains(p)).collect(Collectors.toList()));
//            entityContext.put("Post_Left", leftOuterItems);
//            
//            List<AttributeInfo> rightOuterItems = postAttributes.values().stream()
//                    .filter(p-> p.joinSide.equalsIgnoreCase("R"))
//                    .collect(Collectors.toList());
//            rightOuterItems.addAll(orderItems.keySet().stream().filter(p-> !rightOuterItems.contains(p)).collect(Collectors.toList()));
//            entityContext.put("Post_Right", rightOuterItems);
//            
//            entity = generator.generate(this, "Entity", "Resource", entityContext);
//            reader = generator.generate(this, "Reader", "Resource", readerContext);
//        } else {
//            // set pre to join keys, mid: where clause keys
//            entityContext.put("joinType", this.joinType);
//            joinKeyAttributes.put(leftJoinKey, attributes.get(leftJoinKey));
//            joinKeyAttributes.put(rightJoinKey, attributes.get(rightJoinKey));
//            // Pre list contains the attribtes used as join keys
//            entityContext.put("Pre", joinKeyAttributes.values().stream().collect(Collectors.toList()));
//            // Mid contains the attrbutes referenced from the where clause
//            entityContext.put("Mid", referencedAttributes.values().stream().collect(Collectors.toList()));
//            postAttributes = attributes.entrySet().stream()
//                .filter((entry) -> (!referencedAttributes.containsKey(entry.getKey())))
//                .filter((entry) -> (!joinKeyAttributes.containsKey(entry.getKey())))
//                .collect(Collectors.toMap(p->p.getKey(), p->p.getValue()));
//            // Post list contains all the attributes except those used as join key or in the where clause
//            entityContext.put("Post", postAttributes.values().stream().collect(Collectors.toList()));
//            // In case of outer join, if ordering (check also for frouping) is present, the ordering attributes should be unioned
//            // with the post population attributes, so that the sort method on the data reader should hev proper values populaed into the entity
//            List<AttributeInfo> leftOuterItems = postAttributes.values().stream()
//                    .filter(p-> p.joinSide.equalsIgnoreCase("L")).collect(Collectors.toList());
//            leftOuterItems.addAll(orderItems.keySet().stream().filter(p-> !leftOuterItems.contains(p)).collect(Collectors.toList()));
//            entityContext.put("Post_Left", leftOuterItems);
//            
//            List<AttributeInfo> rightOuterItems = postAttributes.values().stream()
//                    .filter(p-> p.joinSide.equalsIgnoreCase("R")).collect(Collectors.toList());
//            rightOuterItems.addAll(orderItems.keySet().stream().filter(p-> !rightOuterItems.contains(p)).collect(Collectors.toList()));
//            entityContext.put("Post_Right", rightOuterItems);            
//
//            readerContext.put("joinType", this.joinType);
//            readerContext.put("joinOperator", this.joinOperator);            
//            readerContext.put("leftJoinKey", this.leftJoinKey);
//            readerContext.put("rightJoinKey", this.rightJoinKey);
//            // Mid is passed to the reader in order to prevent calling midPopulate when not neccessary; the case when there is no WHERE clause.
//            readerContext.put("Mid", referencedAttributes.values().stream().collect(Collectors.toList()));
//            
//            entity = generator.generate(this, "Entity", "Resource", entityContext);
//            reader = generator.generate(this, "JoinReader", "Resource", readerContext);
//        }
//        LinkedHashMap<String, InMemorySourceFile> sources = new LinkedHashMap<>();
//        InMemorySourceFile rf = new InMemorySourceFile(baseClassName + "Reader", reader);
//        rf.setEntryPoint(true);
//        rf.setFullName(NAME_SPACE + "." + baseClassName + "Reader");
//        sources.put(rf.getFullName(), rf); // the reader must be added first
//        
//        InMemorySourceFile ef = new InMemorySourceFile(baseClassName + "Entity", entity);
//        ef.setFullName(NAME_SPACE + "." + baseClassName + "Entity");
//        sources.put(ef.getFullName(), ef); // the reader must be added first
//        return sources;
//    }


}
