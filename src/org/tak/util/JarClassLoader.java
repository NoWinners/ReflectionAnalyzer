package org.tak.util;

import org.tak.enums.EnumExplorer;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * User: Tommy
 * 5/27/13
 */
public class JarClassLoader {

    private final ArrayList<String> jars = new ArrayList<>();
    private URLClassLoader urlClassLoader;


    public void addFile(String path) {
        jars.add(path);
    }

    public void init() throws MalformedURLException {
        URL[] urls = new URL[jars.size()];
        for (int i = 0; i < jars.size(); i++) {
            urls[i] = new URL("jar:file:" + jars.get(i) + "!/");
        }
        urlClassLoader = URLClassLoader.newInstance(urls);
    }

    public Class loadClass(String name) throws ClassNotFoundException {
        return urlClassLoader.loadClass(name);
    }
    public EnumExplorer enumExplorer(String name) throws ClassNotFoundException {
        Class klass = loadClass(name);
        return klass.isEnum() ? new EnumExplorer(klass) : null;
    }
}
