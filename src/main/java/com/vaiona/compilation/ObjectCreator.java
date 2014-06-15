package com.vaiona.compilation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author standard
 */
public class ObjectCreator {
    // does not work with my own class compiler. the classes are not registered with the default class loader!
    public static Object load(String fullClassName, Object[] ctorArgs) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException{        
        // ClassLoader cl2 = ClassLoader.getSystemClassLoader();
        // also using the fileManager.getClassLoader(null).loadClass(name) should return the class!
        Class cls = Class.forName(fullClassName);// cl2.loadClass(packageName + "." + className); // the exception should not happen
        Constructor<?> c = cls.getConstructor(String.class, boolean.class, String.class);
        // c = cls.getDeclaredConstructor(
        // String.class.getClass(), boolean.class.getClass(), String.class.getClass());
        c.setAccessible(true);
        Object instance = c.newInstance(ctorArgs); // pass parameters
       return instance;
    }

    public static Object load(Class classObject) throws NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {        

        Constructor<?> c = classObject.getConstructor(); // parameterless ctor
        c.setAccessible(true);
        Object instance = c.newInstance();
       return instance;
    }
}
