package org.tak.util.asm.deob.flow;

import org.objectweb.asm.tree.MethodNode;

/**
 * User: Tommy
 * 6/13/13
 */
public class MethodStore {
    private final MethodNode methodNode;
    private final String ownerName;

    public MethodStore(MethodNode methodNode, String ownerName) {
        this.methodNode = methodNode;
        this.ownerName = ownerName;
    }

    public MethodNode getMethodNode() {
        return methodNode;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodStore)) return false;

        MethodStore that = (MethodStore) o;

        if (methodNode != null ? !methodNode.equals(that.methodNode) : that.methodNode != null) return false;
        if (ownerName != null ? !ownerName.equals(that.ownerName) : that.ownerName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = methodNode != null ? methodNode.hashCode() : 0;
        result = 31 * result + (ownerName != null ? ownerName.hashCode() : 0);
        return result;
    }
    public String toString() {
        return getOwnerName()+"."+getMethodNode().name;
    }
}
