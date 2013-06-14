package org.tak.util.asm.deob;

import org.objectweb.asm.tree.ClassNode;

import java.util.List;

/**
 * User: Tommy
 * 6/13/13
 */
public interface Deobfuscator {
    public void run(List<ClassNode> classNodes);
}
