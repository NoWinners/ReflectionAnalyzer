package org.tak.util.asm;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

/**
 * User: Tommy
 * 6/13/13
 */
public class ASMUtil {
    public static ClassNode getClassNode(String className, List<ClassNode> classNodes) {
        for (ClassNode classNode : classNodes) {
            if (classNode.name.endsWith(className))
                return classNode;
        }
        return null;
    }
    public static MethodNode getMethodNode(String methodName, String desc, ClassNode classNode) {
        for (MethodNode methodNode : classNode.methods) {
            if (methodNode.name.equals(methodName) && methodNode.desc.equals(desc))
                return methodNode;
        }
        return null;
    }
    public static MethodNode getMethodNode(String className, String methodName, String desc, List<ClassNode> classNodes) {
        ClassNode classNode = getClassNode(className,classNodes);
        if (classNode!=null) {
            return getMethodNode(methodName,desc,classNode);
        }
        return null;
    }
    public static MethodNode getMethodNode(MethodInsnNode methodInsnNode, List<ClassNode> classNodes) {
        return getMethodNode(methodInsnNode.owner,methodInsnNode.name,methodInsnNode.desc,classNodes);
    }
}
