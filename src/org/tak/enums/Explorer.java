package org.tak.enums;

import org.tak.util.JarClassLoader;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Tommy
 * 5/27/13
 */
public class Explorer {
    private       boolean  rsbotDependency;
    private       String[] otherDependencies;
    public static boolean  DEBUG;
    private final JarClassLoader jarClassLoader = new JarClassLoader();

    public Explorer(String file) {
        jarClassLoader.addFile(file);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvocationTargetException {
        args = new String[]{"/Users/Tommy/keyloggers/uFighter-ob.jar","true", "false"};
        if (args.length < 3) {
            System.out.println("Needs at least three args: file, rsbotDependency, DEBUG, otherDependencies (optional)");
            System.exit(0);
        }
        Explorer explorer = new Explorer(args[0]);
        explorer.setRsbotDependency(Boolean.parseBoolean(args[1]));
        DEBUG = Boolean.parseBoolean(args[2]);
        String[] otherDependencies = new String[args.length-3];
        for (int i = 3; i < args.length; i++) {
            otherDependencies[i-3] = args[i];
        }
        explorer.setOtherDependencies(otherDependencies);
        explorer.init();
        explorer.exploreEnum("v").byMethod();
    }
    public void init() throws IOException, ClassNotFoundException {
        loadDependencies();
        jarClassLoader.init();
    }
    public EnumExplorer exploreEnum(String enumName) throws ClassNotFoundException {
        return jarClassLoader.enumExplorer(enumName);
    }

    public void loadDependencies() throws IOException, ClassNotFoundException {
        if (rsbotDependency) {
            loadRSBot();
        }
        for (String path : otherDependencies) {
            jarClassLoader.addFile(path);
        }
    }
    public void loadRSBot() throws IOException, ClassNotFoundException {
        jarClassLoader.addFile("/Users/Tommy/Downloads/RSBot_inject.jar");
    }

    public boolean isRSBotDependent() {
        return rsbotDependency;
    }

    public void setRsbotDependency(boolean rsbotDependency) {
        this.rsbotDependency = rsbotDependency;
    }

    public String[] getOtherDependencies() {
        return otherDependencies;
    }

    public void setOtherDependencies(String[] otherDependencies) {
        this.otherDependencies = otherDependencies;
    }
}
