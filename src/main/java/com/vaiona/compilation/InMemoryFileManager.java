package com.vaiona.compilation;

import java.io.IOException;
import java.security.SecureClassLoader;
import java.util.HashMap;
import java.util.Map;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class InMemoryFileManager extends ForwardingJavaFileManager {
    
    /**
    * Instance of JavaClassObject that will store the
    * compiled bytecode of our class
    * Added support for multiple class object storage. Javad
    */
    private Map<String, InMemoryClassObject> classObjects = new HashMap<>();

    /**
    * Will initialize the manager with the specified
    * standard java file manager
    *
    * @param standardManger
    */
    public InMemoryFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
    }

    /**
    * Will be used by us to get the class loader for our
    * compiled class. It creates an anonymous class
    * extending the SecureClassLoader which uses the
    * byte code created by the compiler and stored in
    * the JavaClassObject, and returns the Class for it
    */
    @Override
    public java.lang.ClassLoader getClassLoader(Location location) {
        return new SecureClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                byte[] b = classObjects.get(name).getBytes();
                return super.defineClass(name, b, 0, b.length);
            }
        };
    }

    /**
    * Gives the compiler an instance of the JavaClassObject
    * so that the compiler can write the byte code into it.
    */
    @Override
    public JavaFileObject getJavaFileForOutput(
            Location location, String className, Kind kind, FileObject sibling) throws IOException {
        if(!classObjects.containsKey(className)){
            InMemoryClassObject classObject = new InMemoryClassObject(className, kind);
            classObjects.put(className, classObject);
        }
        return classObjects.get(className);
    }
}