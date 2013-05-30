package org.tak.runtime;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.tak.runtime.servers.Server;
import org.tak.util.FieldStore;
import org.tak.util.JarClassLoader;
import org.tak.util.JarUtils;
import org.tak.util.MultiplierFinder;

import java.applet.Applet;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tommy
 * 5/28/13
 */
public class Game {
    private final Server                       server;
    private       HashMap<FieldStore, Integer> multipliers;
    private       Applet                       client;

    public Game(Server server) {
        this.server = server;
    }

    public void init() {
        JarClassLoader jarClassLoader = new JarClassLoader();
        jarClassLoader.addFile(server.getFilePath());
        if (server.hasMultipliers()) {
            List<FieldStore> fieldStores = new ArrayList<>();
            ClassNode[] classNodes = JarUtils.getClassNodes(server.getFilePath());
            for (ClassNode classNode : classNodes) {
                if (classNode.name.length() < 5) {
                    for (FieldNode fieldNode : classNode.fields) {
                        if (fieldNode.desc.equals("I") && fieldNode.name.length() < 5) {
                            fieldStores.add(new FieldStore(fieldNode,classNode.name));
                        }
                    }
                }
            }
            multipliers = new MultiplierFinder(fieldStores).getMultipliers(classNodes);
        }
        try {
            jarClassLoader.init();
            Class clientClass = jarClassLoader.loadClass(server.getMainClassName());
            client = (Applet) clientClass.newInstance();
            client.setStub(server);
            client.init();
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void dumpMultipliers(OutputStream outputStream) {
        dumpMultipliers(new PrintStream(outputStream));
    }
    public void dumpMultipliers(PrintStream printStream) {
        for (FieldStore fieldStore : multipliers.keySet()) {
            printStream.println(fieldStore.toString()+":"+multipliers.get(fieldStore));
        }
    }
    public int getMultiplier(String owner, String name) {
        for (Map.Entry<FieldStore,Integer> entry : multipliers.entrySet()) {
            if (entry.getKey().getName().equals(name) && entry.getKey().getOwner().equals(owner)) {
                return entry.getValue();
            }
        }
        return -1;
    }

    public Applet getClient() {
        return client;
    }
}
