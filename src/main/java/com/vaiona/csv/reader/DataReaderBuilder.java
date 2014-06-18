package com.vaiona.csv.reader;

import com.vaiona.compilation.ClassCompiler;
import com.vaiona.compilation.ClassGenerator;
import com.vaiona.compilation.ObjectCreator;
import com.vaiona.data.AttributeInfo;
import com.vaiona.data.FieldInfo;
import com.vaiona.data.TypeSystem;
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
    public DataReaderBuilder addSort(String attributeName, String direction){
        if(!ordering.containsKey(attributeName)){
            ordering.put(attributeName, direction);                    
        }
        return this;
    }
    
    Map<String, AttributeInfo> attributes = new LinkedHashMap<>();
    public DataReaderBuilder addAttribute(String attributeName, String dataTypeRef, String forwardMap){
        List<String> referredFields = new ArrayList<>();
        referredFields.add(forwardMap);
        String dataType = TypeSystem.getTypes().get(dataTypeRef).getName();
        return addAttribute(attributeName, dataTypeRef, dataType, forwardMap, referredFields);
    }
    
    public DataReaderBuilder addAttribute(String attributeName, String dataTypeRef, String forwardMap, List<String> referredFields){        
        String dataType = TypeSystem.getTypes().get(dataTypeRef).getName();
        return addAttribute(attributeName, dataTypeRef, dataType, forwardMap, referredFields);        
    }
    
    public DataReaderBuilder addAttribute(String attributeName, String dataTypeRef, String dataType, String forwardMap, List<String> referredFields){        
        if(!attributes.containsKey(attributeName)){
            AttributeInfo ad = new AttributeInfo();
            ad.name = attributeName;
            ad.dataType = dataType;
            ad.dataTypeRef = dataTypeRef;
            ad.forwardMap = forwardMap;
            ad.fields = referredFields;
            ad.index = attributes.size();
            attributes.put(attributeName, ad);
        }            
        return this;
    }
    
    Map<String, FieldInfo> fields = new LinkedHashMap<>();
    // it would be good to have an overload that takes the index also. it removes the need to register unused fields
    public DataReaderBuilder addField(String fieldName, String dataTypeRef){
        if(!fields.containsKey(fieldName)){
            FieldInfo fd = new FieldInfo();
            fd.name = fieldName;
            fd.dataTypeRef = dataTypeRef;
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

    String whereClause = "";
    String whereClauseTranslated = "";
    Map<String, AttributeInfo> referencedAttributes = new LinkedHashMap<>();
    Map<String, AttributeInfo> postAttributes = new LinkedHashMap<>();
    public DataReaderBuilder addWhere(String whereClause){ 
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
    
    String columnDlimiter = ",";
    public String getColumnDelimiter(){ return columnDlimiter;}
    public DataReaderBuilder columnDelimiter(String columnDelimiter){
        this.columnDlimiter = columnDelimiter;
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

    public DataReader<Object> build() throws IOException, ClassNotFoundException, NoSuchMethodException, 
            InstantiationException, IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException {
        if(baseClassName == null || baseClassName.isEmpty()){
            baseClassName = "C" + (new Date()).getTime();
        }
        attributes.entrySet().stream().map((entry) -> entry.getValue()).forEach((ad) -> {
            ad.forwardMapTranslated = translate(ad.forwardMap, ad.dataTypeRef);
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
        String entity = generator.generate("Entity", "Resource", entityContext);
        
        Map<String, Object> readerContext = new HashMap<>();
        readerContext.put("namespace", NAME_SPACE);
        readerContext.put("BaseClassName", baseClassName);
        readerContext.put("Where", whereClauseTranslated);
        readerContext.put("Ordering", orderItems);
        readerContext.put("skip", skip);
        readerContext.put("take", take);
        String reader = generator.generate("Reader", "Resource", readerContext);
        
        // compile source files into classes
        
        ClassCompiler compiler = new ClassCompiler();
        compiler.addSource(baseClassName + "Entity", entity);
        compiler.addSource(baseClassName + "Reader", reader);
        JavaFileManager fileManager = compiler.compile();
        
        // load the classes
        Class classObject = fileManager.getClassLoader(null).loadClass(NAME_SPACE + "." + baseClassName + "Reader");
        DataReader<Object> instance = (DataReader<Object>)ObjectCreator.load(classObject);    
        instance
                .columnDelimiter(this.columnDlimiter)
                .typeDelimiter(this.typeDlimiter)
                .unitDelimiter(this.unitDlimiter);
        return instance;

    }

    private String translate(String expression, String typeRef) {
        String translated = "";
        for (StringTokenizer stringTokenizer = new StringTokenizer(expression, " ");
                stringTokenizer.hasMoreTokens();) {
            String token = stringTokenizer.nextToken();
            if(fields.containsKey(token)){
                FieldInfo fd = fields.get(token);
                // need for a type check
                String temp = TypeSystem.getTypes().get(fd.dataTypeRef).getCastPattern().replace("$data$", "row[" + fd.index + "]");
                translated = translated + " " + temp;
            }
            else {
                translated = translated + " " + token;
            }            
        }
        // enclose the translated attribute in a data conversion based on the attributes type
        //translated = dataTypes.get(type).replace("$data$", translated);
        // consider Date!!!
        String innerType = TypeSystem.getTypes().get(typeRef).getName();
        if(innerType.equals("Date")){
            translated = "(" + translated + ")";
        }
        else { // cast the translated expression to the attribute type
            translated = "(" + innerType.toLowerCase() + ")(" + translated + ")";
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
}
