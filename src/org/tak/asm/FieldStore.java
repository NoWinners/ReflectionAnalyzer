package org.tak.asm;

import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;

/**
 * User: Tommy
 * 5/28/13
 */
public class FieldStore {
    private final int access;
    private final String owner;
    private final String name;
    private final String desc;

    public FieldStore(FieldNode fieldNode, String owner) {
        this.access = fieldNode.access;
        this.name = fieldNode.name;
        this.desc = fieldNode.desc;
        this.owner = owner;
    }

    public int getAccess() {
        return access;
    }

    public String getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
    public String toString() {
        return getOwner()+"."+getName();
    }
    public boolean isStatic() {
        return Modifier.isStatic(access);
    }
}
