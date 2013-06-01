package org.tak.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * User: Tommy
 * 5/31/13
 */
public class ReflectionEngine {
    private final ClassLoader classLoader;
    public ReflectionEngine(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    public Class getClass(String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Field getField(Class klass, String fieldName) {
        try {
            return klass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Field getField(String className, String fieldName) {
        return getField(getClass(className), fieldName);
    }
    public Method getMethod(String className, String methodName, Class<?>... parameters) {
        return getMethod(getClass(className),methodName,parameters);
    }
    public Method getMethod(Class klass, String methodName, Class... parameters) {
        try {
            return klass.getDeclaredMethod(methodName,parameters);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void invoke(String className, String methodName, Object invokee, Object[] parameters, Class[] methodParameters) {
        invoke(getClass(className),methodName,invokee,parameters,methodParameters);
    }
    public void invoke(Class klass, String methodName, Object invokee, Object[] parameters, Class[] methodParameters) {
        Method method = getMethod(klass,methodName, methodParameters);
        if (method!=null) {
            try {
                method.invoke(invokee,parameters);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
    public void invokeMain(String className, String[] args) {
        invokeMain(getClass(className),args);
    }
    public void invokeMain(Class klass, String[] args) {
        invoke(klass,"main",null,args, new Class[] {String[].class});
    }
}
