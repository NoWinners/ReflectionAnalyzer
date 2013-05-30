package org.tak.runtime.gui;

import org.tak.runtime.Game;
import org.tak.runtime.RuntimeAnalyzer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * User: Tommy
 * 5/28/13
 */
public class ReflectorGUI extends JFrame {
    private final Object client;
    private final Game game;
    private JTree tree;
    private static final int MAX_DEPTH = 3;

    public ReflectorGUI(Game game) {
        this.client = game.getClient();
        this.game = game;
        setTitle("Runtime Reflection Analyzer");
        setSize(500,500);
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("client");
        try {
            exploreObject(treeNode,getClient(),0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        tree = new JTree(treeNode);
        JScrollPane jScrollPane = new JScrollPane(tree);
        add(jScrollPane);
        RuntimeAnalyzer.EXECUTOR_SERVICE.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                int i = 0;
                while (i < 300) {
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode("Client");
                    try {
                        exploreObject(treeNode,getClient(),0);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return false;
                    }
                    tree = new JTree(treeNode);
                    tree.updateUI();
                    i++;
                    try {
                        Thread.sleep(5000);
                    } catch (Exception ignored) {

                    }
                }
                return true;
            }
        });
    }
    public Object getClient() {
        return client;
    }
    public void exploreObject(DefaultMutableTreeNode treeNode, Object object, int currDepth) throws IllegalAccessException {
        if (object == null)
            return;
        Class klass = object.getClass();
        for (Field field : klass.getDeclaredFields()) {
            if (!field.isAccessible())
                field.setAccessible(true);
            Object result = field.get(object);
            if (result instanceof Integer) {
                result = (Integer)result*game.getMultiplier(klass.getName(),field.getName());
            }
            if (result == null) {
                result = "null";
            }
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(field.getName()+"["+field.getType().getName()+"]");
            for (String s : out(result)) {
                childNode.add(new DefaultMutableTreeNode(s));
            }
            if (isRSObject(result) && currDepth < MAX_DEPTH) {
                exploreObject(childNode,result,currDepth+1);
            }
            treeNode.add(childNode);
        }
    }
    public static boolean isRSObject(Object object) {
        return object != null && !object.getClass().getName().contains(".");
    }
    public static List<String> out(Object object) {
        ArrayList<String> out = new ArrayList<>();
        if (object instanceof Object[][]) {
            for (Object[] o : (Object[][]) object) {
                out.addAll(out(o));
            }
        } else if (object instanceof int[]) {
            out.add(Arrays.toString((int[]) object));
        } else if (object instanceof Object[]) {
            out.add(Arrays.toString((Object[]) object));
        } else if (object instanceof boolean[]) {
            out.add(Arrays.toString((boolean[]) object));
        } else {
            try {
                out.add(object.toString());
            } catch (Exception ignored) {

            }
        }
        return out;
    }
}
