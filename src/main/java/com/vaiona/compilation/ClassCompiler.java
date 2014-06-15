/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaiona.compilation;

import java.util.ArrayList;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

/**
 *
 * @author standard
 */
public class ClassCompiler {
    List<JavaFileObject> sources = new ArrayList<>();
    
    public ClassCompiler addSource(String className, String body){
        sources.add(new InMemorySourceFile(className, body));
        return this;
    }
    
    // think of having the compiler, file manager or the whole class as static to save some 
    // compiler/ file, etc loading time. needs profiling
    public JavaFileManager compile(){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        // check whether it is Java 8
        JavaFileManager fileManager = new InMemoryFileManager(
                compiler.getStandardFileManager(null, null, null));
        compiler.getTask(null, fileManager, null, null, null, sources)
            .call();
        
        return fileManager;
    }
}
