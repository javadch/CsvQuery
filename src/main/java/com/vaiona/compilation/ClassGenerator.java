/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.compilation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.rythmengine.RythmEngine;

/**
 *
 * @author standard
 */
public class ClassGenerator {
    static RythmEngine engine = null;
    public ClassGenerator(){
        if(engine == null){
            Map<String, Object> conf = new HashMap<>();
            conf.put("engine.precompile_mode.enabled", true);
            conf.put("engine.load_precompiled.enabled", true);
            engine = new RythmEngine(conf);
        }
    }
    public String generate(String source, String sourceType, Map<String, Object> contextData) throws IOException{
        if(sourceType.toUpperCase().equals("FILE")){
            return generateFromFile(source, contextData);
        } else if(sourceType.toUpperCase().equals("RESOURCE")){
            return generateFromResource(source, contextData);
        }
        return null;
    }

    public String generateFromFile(String fileName, Object contextData) throws IOException{
        String template = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);
        return template;
    }
    
    public String generateFromResource(String resourceName, Map<String, Object> contextData) throws IOException{
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("resources/" + resourceName + ".jt");
        //InputStream stream = this.getClass().getResourceAsStream("resources/" + resourceName + ".jt");
        if (stream == null) return "";
        try(java.util.Scanner s = new java.util.Scanner(stream)){
            // its ia trick: The reason it works is because Scanner iterates over tokens in the stream, and in this case we separate tokens 
            // using "beginning of the input boundary" (\A) thus giving us only one token for the entire contents of the stream
            String template = s.useDelimiter("\\A").hasNext() ? s.next() : "";
            return(engine.render(template, contextData));
        }
        catch (Exception ex) { 
            return "";
        }
        finally {
            stream.close();
        }
    }
    
}
