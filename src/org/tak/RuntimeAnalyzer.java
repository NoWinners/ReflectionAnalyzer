package org.tak;

import org.tak.runtime.gui.ReflectorGUI;
import org.tak.util.JarClassLoader;
import org.tak.util.ReflectionEngine;

import java.lang.reflect.Field;
import java.net.MalformedURLException;

/**
 * User: Tommy
 * 5/31/13
 */
public class RuntimeAnalyzer {
    //static field
    private final String className;
    private final String fieldName;
    //main Method
    private final String mainMethodClassLocation;
    private ReflectionEngine reflectionEngine;





    public RuntimeAnalyzer(String className, String fieldName, String mainMethodClassLocation, String jarLocation) {
        this.className = className;
        this.fieldName = fieldName;
        this.mainMethodClassLocation = mainMethodClassLocation;
        JarClassLoader jarClassLoader = new JarClassLoader();
        jarClassLoader.addFile(jarLocation);
        try {
            jarClassLoader.init();
            reflectionEngine = new ReflectionEngine(jarClassLoader.getUrlClassLoader());
            Class klass = jarClassLoader.loadClass(mainMethodClassLocation);
            jarClassLoader.loadClass(className);
            reflectionEngine.invokeMain(klass, new String[1]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public ReflectorGUI init() throws ClassNotFoundException, IllegalAccessException {
        Field field = reflectionEngine.getField(className, fieldName);
        Object object = field.get(null);
        return new ReflectorGUI(object,object.getClass().getSimpleName(), false,null);
    }

    public static void main(String[] args) {
        RuntimeAnalyzer runtimeAnalyzer  = new RuntimeAnalyzer("client", "X", "client", "/Users/Tommy/Downloads/arravclient.jar");
        ReflectorGUI reflectorGUI;
        try {
            reflectorGUI = runtimeAnalyzer.init();
        } catch (ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
            return;
        }
        reflectorGUI.setVisible(true);
    }

}
