package org.tak.runtime;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.tak.runtime.servers.Server;
import org.tak.asm.FieldStore;
import org.tak.util.JarClassLoader;
import org.tak.util.JarUtils;
import org.tak.asm.MultiplierFinder;
import org.tak.asm.deob.flow.FlowGraph;

import java.applet.Applet;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Tommy
 * 5/28/13
 */
public class Game {
    private final Server server;
    private Multipliers multipliers;
    private Applet client;

    public Game(Server server) {
        this.server = server;
    }

    public void init() {
        JarClassLoader jarClassLoader = new JarClassLoader();
        jarClassLoader.addFile(server.getFilePath());
        if (server.hasMultipliers()) {
            List<FieldStore> fieldStores = new ArrayList<>();
            List<ClassNode> classNodes = JarUtils.getClassNodes(server.getFilePath());
            FlowGraph flowGraph = new FlowGraph();
            flowGraph.run(classNodes);
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

    public Applet getClient() {
        return client;
    }

    public Server getServer() {
        return server;
    }

    public Multipliers getMultipliers() {
        return multipliers;
    }
}
