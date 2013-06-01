package org.tak.runtime.gui;

import org.tak.runtime.Game;
import org.tak.runtime.Multipliers;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: Tommy
 * 5/28/13
 */
public class ReflectorGUI extends JFrame {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private final Object      client;
    private final boolean     hasMultipliers;
    private final Multipliers multipliers;
    private       JTree       tree;
    private static final int MAX_DEPTH = 3;

    public ReflectorGUI(final Game game) {
        this(game.getClient(), game.getServer().getMainClassName(), game.getServer().hasMultipliers(), game.getMultipliers());

    }

    public ReflectorGUI(final Object object, final String name, final boolean hasMultipliers, final Multipliers multipliers) {
        this.client = object;
        this.hasMultipliers = hasMultipliers;
        this.multipliers = multipliers;
        setTitle("Runtime Reflection Analyzer");
        setSize(500, 500);
        final DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(name);
        try {
            exploreObject(treeNode, getClient(), 0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        tree = new JTree(treeNode);
        final JScrollPane jScrollPane = new JScrollPane(tree);
        add(jScrollPane);

        EXECUTOR_SERVICE.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                while (!isVisible()) {
                    try {
                        Thread.sleep(100);
                    } catch (Exception ignored) {}
                }
                while (isVisible()) {
                    treeNode.removeAllChildren();
                    try {
                        exploreObject(treeNode,getClient(),0);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return false;
                    }
                    tree = new JTree(treeNode);
                    tree.updateUI();
                    jScrollPane.updateUI();
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
        for (Field field : klass.getFields()) {
            if (!field.isAccessible())
                field.setAccessible(true);
            Object result = field.get(object);
            if (result instanceof Integer && hasMultipliers) {

                result = (Integer)result*multipliers.getMultiplier(klass.getName(), field.getName());
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
