package com.vaiona.csv.reader;

import com.vaiona.commons.compilation.ClassCompiler;
import com.vaiona.commons.compilation.ClassGenerator;
import com.vaiona.commons.compilation.InMemorySourceFile;
import com.vaiona.commons.compilation.ObjectCreator;
import com.vaiona.commons.data.AttributeInfo;
import com.vaiona.commons.data.FieldInfo;
import com.vaiona.commons.types.TypeSystem;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import javax.tools.JavaFileManager;

public class DataReaderBuilder {
    public static String NAME_SPACE = "com.vaiona.csv.reader";
    // get parameters from the caller
    // create the templated classes
    // compile the class/es
    // instantiate the reader
    // return the reader

    public DataReaderBuilder dateFormat(String format) throws ParseException{
        if(TypeSystem.getTypes().containsKey("Date"))            
            TypeSystem.getTypes().get("Date").setCastPattern("(new SimpleDateFormat(\"" + format + "\")).parse($data$)");
        return this;
    }
    
    Map<String, String> ordering = new LinkedHashMap<>();

    public Map<String, String> getOrdering() {
        return ordering;
    }

    public void setOrdering(Map<String, String> ordering) {
        this.ordering = ordering;
    }
    
    public DataReaderBuilder addSort(String attributeName, String direction){
        if(!ordering.containsKey(attributeName)){
            ordering.put(attributeName, direction);                    
        }
        return this;
    }
    
    Map<String, AttributeInfo> attributes = new LinkedHashMap<>();

    public Map<String, AttributeInfo> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, AttributeInfo> attributes) {
        this.attributes = attributes;
    }

//    private DataReaderBuilder addAttribute(String attributeName, String dataTypeRef, String forwardMap){
//        List<String> referredFields = new ArrayList<>();
//        referredFields.add(forwardMap);
//        String dataType = TypeSystem.getTypes().get(dataTypeRef).getName();
//        return addAttribute(attributeName, dataTypeRef, dataType, forwardMap, referredFields);
//    }
    
//    private DataReaderBuilder addAttribute(String attributeName, String dataTypeRef, String forwardMap, List<String> referredFields){        
//        String dataType = TypeSystem.getTypes().get(dataTypeRef).getName();
//        return addAttribute(attributeName, dataTypeRef, dataType, forwardMap, referredFields);        
//    }
    
//    private DataReaderBuilder addAttribute(String attributeName, String dataTypeRef, String dataType, String forwardMap, List<String> referredFields){        
//        if(!attributes.containsKey(attributeName)){
//            AttributeInfo ad = new AttributeInfo();
//            ad.name = attributeName;
//            ad.conceptualDataType = dataType;
//            ad.internalDataType = dataTypeRef;
//            ad.forwardMap = forwardMap;
//            ad.fields = referredFields;
//            ad.index = attributes.size();
//            attributes.put(attributeName, ad);
//        }            
//        return this;
//    }
    
    Map<String, FieldInfo> fields = new LinkedHashMap<>();
    Map<String, FieldInfo> rightFields = new LinkedHashMap<>();

    public Map<String, FieldInfo> getFields() {
        return fields;
    }
    public Map<String, FieldInfo> getLeftFields() {
        return fields;
    }

    public Map<String, FieldInfo> getRightFields() {
        return rightFields;
    }
    
    // it would be good to have an overload that takes the index also. it removes the need to register unused fields
    public DataReaderBuilder addField(String fieldName, String dataTypeRef){
        if(!fields.containsKey(fieldName)){
            FieldInfo fd = new FieldInfo();
            fd.name = fieldName;
            fd.internalDataType = dataTypeRef;
            fd.index = fields.size();
            fields.put(fieldName, fd);
        }                
        return this;
    }
    
    public DataReaderBuilder addFields(Map<String, FieldInfo> fields){
        this.fields.clear();
        this.fields.putAll(fields);
        return this;
    }
    public DataReaderBuilder addLeftFields(Map<String, FieldInfo> fields){
        return addFields(fields);
    }
    
    public DataReaderBuilder addRightFields(Map<String, FieldInfo> fields){
        this.rightFields.clear();
        this.rightFields.putAll(fields);
        return this;
    }

    String whereClause = "";
    String whereClauseTranslated = "";
    Map<String, AttributeInfo> referencedAttributes = new LinkedHashMap<>();
    Map<String, AttributeInfo> postAttributes = new LinkedHashMap<>();
    
    public DataReaderBuilder where(String whereClause){ 
        this.whereClause = whereClause;
        // extract used attributes and put them in the pre population list
        extractUsedAttributes(whereClause);
        return this;
    }
    
    String baseClassName = "";
    public DataReaderBuilder baseClassName(String baseClassName){
        this.baseClassName = baseClassName;
        return this;
    }
    
    String columnDelimiter = ",";
    public String getColumnDelimiter(){ return columnDelimiter;}
    public DataReaderBuilder columnDelimiter(String columnDelimiter){
        this.columnDelimiter = columnDelimiter;
        return this;
    }
    
    public String getLeftColumnDelimiter(){ return columnDelimiter;}
    public DataReaderBuilder leftColumnDelimiter(String columnDelimiter) {
        this.columnDelimiter = columnDelimiter;
        return this;
    }

    String rightColumnDelimiter = ",";
    public String getRightColumnDelimiter(){ return rightColumnDelimiter;}
    public DataReaderBuilder rightColumnDelimiter(String columnDelimiter) {
        this.rightColumnDelimiter = columnDelimiter;
        return this;
    }

    String typeDlimiter = ":";
    public String getTypeDelimiter(){ return typeDlimiter;}
    public DataReaderBuilder typeDlimiter(String typeDlimiter){
        this.typeDlimiter = typeDlimiter;
        return this;
    }
    
    String unitDlimiter = "::";
    public String getUnitDelimiter(){ return unitDlimiter;}
    public DataReaderBuilder unitDlimiter(String unitDlimiter){
        this.unitDlimiter = unitDlimiter;
        return this;
    }
    
    Integer skip = -1;
    public DataReaderBuilder skip(Integer value){
        skip = value;
        return this;
    } 
    
    Integer take = -1;
    public DataReaderBuilder take(Integer value){
        take = value;
        return this;
    } 
    
    public DataReader<Object> build(Class classObject) throws IOException, ClassNotFoundException, NoSuchMethodException, 
            InstantiationException, IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException {
       
        
        DataReader<Object> instance = (DataReader<Object>)ObjectCreator.load(classObject);    
        instance
                .columnDelimiter(this.columnDelimiter)
                .typeDelimiter(this.typeDlimiter)
                .unitDelimiter(this.unitDlimiter);
        return instance;

    }

    private String translate(String expression, String conceptualType, boolean rightSide) {
        String translated = "";
        for (StringTokenizer stringTokenizer = new StringTokenizer(expression, " ");
                stringTokenizer.hasMoreTokens();) {
            String token = stringTokenizer.nextToken();
            boolean found = false;
            if(!rightSide && fields.containsKey(token)){
                FieldInfo fd = fields.get(token);
                // need for a type check
                String temp = TypeSystem.getTypes().get(fd.conceptualDataType).getCastPattern().replace("$data$", "row[" + fd.index + "]");
                translated = translated + " " + temp;
                found = true;
            }
            if(rightSide && rightFields.containsKey(token)){
                FieldInfo fd = rightFields.get(token);
                // need for a type check
                // the righside attributes reffer to the right side fields.Tthe Entity is a product of a line of the left and the right container
                // The generated code, creates the product by concatenating the left and right string arrays and passes them as the cotr argument 
                // to the Entity. This is why the fied indexes for the right side attributes are shifted by the size of the left hand side field array.
                String temp = TypeSystem.getTypes().get(fd.conceptualDataType).getCastPattern().replace("$data$", "row[" + (fields.size() + fd.index) + "]");
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
        if(conceptualType.equals("Date") || conceptualType.equals("String")){
            translated = "(" + translated + ")";
        }
        else { // cast the translated expression to the attribute type
            translated = "(" + TypeSystem.getTypes().get(conceptualType).getRuntimeType().toLowerCase() + ")(" + translated + ")";
        }
        
        return translated;
    }
    
    private void extractUsedAttributes(String expression) {
        referencedAttributes.clear();
        for (StringTokenizer stringTokenizer = new StringTokenizer(expression, " ");
                stringTokenizer.hasMoreTokens();) {
            String token = stringTokenizer.nextToken();
            if (attributes.containsKey(token) && !referencedAttributes.containsKey(token)) {
                referencedAttributes.put(token, attributes.get(token));
            } else {
                // thw wehre clause is referring to an undefined attribute
            }  
            // translate the wehre clause
            if(attributes.containsKey(token)){
                whereClauseTranslated = whereClauseTranslated + " " + "p." + token;
            }
            else {
                whereClauseTranslated = whereClauseTranslated + " " + token;
            }                      
        }
        postAttributes.clear();
        attributes.entrySet().stream()
                .filter((entry) -> (!referencedAttributes.containsKey(entry.getKey())))
                .forEach((entry) -> {
                    postAttributes.put(entry.getKey(), entry.getValue());
                });
    }

    boolean writeResultsToFile = false;
    public void writeResultsToFile(boolean value) {
        writeResultsToFile = value;
    }

    public LinkedHashMap<String, InMemorySourceFile> createSources() throws IOException {
        // check if the statement has no adapter, throw an exception
        
        if(baseClassName == null || baseClassName.isEmpty()){
            baseClassName = "C" + (new Date()).getTime();
        }
        attributes.entrySet().stream().map((entry) -> entry.getValue()).forEach((ad) -> {
            if(ad.joinSide.equalsIgnoreCase("R"))
                ad.forwardMapTranslated = translate(ad.forwardMap, ad.conceptualDataType, true);
            else
                ad.forwardMapTranslated = translate(ad.forwardMap, ad.conceptualDataType, false);
        });
        
        // transform the ordering clauses to their bound equivalent, in each attribute names are linked to the attibutes objects
        Map<AttributeInfo, String> orderItems = new LinkedHashMap<>();        
        for (Map.Entry<String, String> entry : ordering.entrySet()) {
                if(attributes.containsKey(entry.getKey())){
                    orderItems.put(attributes.get(entry.getKey()), entry.getValue());
                }            
        }
        
        ClassGenerator generator = new ClassGenerator();
        
        Map<String, Object> entityContext = new HashMap<>();
        entityContext.put("namespace", NAME_SPACE);
        entityContext.put("BaseClassName", baseClassName);
        entityContext.put("Attributes", attributes.values().stream().collect(Collectors.toList()));        
        entityContext.put("Pre", referencedAttributes.values().stream().collect(Collectors.toList()));
        entityContext.put("Post", postAttributes.values().stream().collect(Collectors.toList()));
        String entity = generator.generate(this, "Entity", "Resource", entityContext);
        
        Map<String, Object> readerContext = new HashMap<>();
        readerContext.put("Attributes", attributes.values().stream().collect(Collectors.toList()));
        String header = String.join(columnDelimiter, attributes.values().stream().map(p-> p.name + ":" + p.internalDataType).collect(Collectors.toList()));
        readerContext.put("rowHeader", header);        
        String linePattern = String.join(",", attributes.values().stream().map(p-> "String.valueOf(entity." + p.name + ")").collect(Collectors.toList()));
        readerContext.put("linePattern", linePattern);        
        readerContext.put("namespace", NAME_SPACE);
        readerContext.put("BaseClassName", baseClassName);
        readerContext.put("Where", whereClauseTranslated);
        readerContext.put("Ordering", orderItems);
        readerContext.put("skip", skip);
        readerContext.put("take", take);
        readerContext.put("writeResultsToFile", writeResultsToFile);
        String reader = generator.generate(this, "Reader", "Resource", readerContext);
        
        LinkedHashMap<String, InMemorySourceFile> sources = new LinkedHashMap<>();
        InMemorySourceFile rf = new InMemorySourceFile(baseClassName + "Reader", reader);
        rf.setEntryPoint(true);
        rf.setFullName(NAME_SPACE + "." + baseClassName + "Reader");
        sources.put(rf.getFullName(), rf); // the reader must be added first
        
        InMemorySourceFile ef = new InMemorySourceFile(baseClassName + "Entity", entity);
        ef.setFullName(NAME_SPACE + "." + baseClassName + "Entity");
        sources.put(ef.getFullName(), ef); // the reader must be added first
        return sources;
    }

}
