package org.tak.asm.deob.flow;

import org.objectweb.asm.tree.*;
import org.tak.util.ASMUtil;
import org.tak.util.JarUtils;
import org.tak.asm.deob.Deobfuscator;

import java.util.*;

/**
 * User: Tommy
 * 6/13/13
 */
public class FlowGraph implements Deobfuscator {
    private final Set<MethodStore> visited = new HashSet<>();

    public FlowGraph() {

    }

    public static void main(String[] args) {
        FlowGraph flowGraph = new FlowGraph();
        List<ClassNode> classNodes = JarUtils.getClassNodes("/Users/Tommy/RSClients/r763.jar");
        long start = System.currentTimeMillis();
        flowGraph.init(classNodes);
        flowGraph.remove(classNodes);
        System.out.println("time taken: "+(System.currentTimeMillis()-start));
        JarUtils.dumpClasses("/Users/Tommy/out.jar",classNodes);
    }
    public void remove(List<ClassNode> classNodes) {
        int count = 0;
        for (ClassNode classNode : classNodes) {
            if (classNode.name.contains("/"))
                continue;
            for (int i = classNode.methods.size()-1; i >= 0; i--) {
                MethodNode methodNode = classNode.methods.get(i);
                if (methodNode.name.equals("<init>") || methodNode.name.equals("<clinit>"))
                    continue;
                final MethodStore methodStore = new MethodStore(methodNode,classNode.name);
                if (!visited.contains(methodStore) && methodNode.name.length() <= 4) {
                    System.out.println(methodStore);
                    count++;
                    classNode.methods.remove(methodNode);
                }
            }
        }
        System.out.println(count+" methods removed!");
    }
    public void init(List<ClassNode> classNodes) {
        List<MethodStore> methodStores = new ArrayList<>();
        for (ClassNode classNode : classNodes) {
            for (MethodNode methodNode : classNode.methods) {
                methodStores.add(new MethodStore(methodNode,classNode.name));
            }
        }
        init(methodStores,classNodes);
    }

    public void init(List<MethodStore> methods, List<ClassNode> classNodes) {
        for (MethodStore methodStore : methods) {
            explore(methodStore,classNodes);
        }
    }
    public void explore(MethodStore methodStore, List<ClassNode> classNodes) {
        InsnList insnList = methodStore.getMethodNode().instructions;
        Iterator<AbstractInsnNode> insnNodeIterator = insnList.iterator();
        while (insnNodeIterator.hasNext()) {
            AbstractInsnNode curr = insnNodeIterator.next();
            if (curr instanceof MethodInsnNode) {
                MethodInsnNode methodInsnNode = (MethodInsnNode) curr;
                MethodNode methodInvoke = ASMUtil.getMethodNode(methodInsnNode, classNodes);
                if (methodInvoke!=null) {
                    visited.add(new MethodStore(methodInvoke, methodInsnNode.owner));
                }
            }
        }
    }

    @Override
    public void run(List<ClassNode> classNodes) {
        init(classNodes);
        remove(classNodes);
        //JarUtils.dumpClasses("/Users/Tommy/out.jar",classNodes);
        visited.clear();
    }
}
