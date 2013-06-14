package org.tak.util.asm;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Ramus
 */
public class RIS {

    private static final Map<String, List<Integer>> patternCache = new LinkedHashMap<>();
    private static final Map<String, Integer> opcodeCache = new LinkedHashMap<>();

    private static List<Integer> compilePattern(final String pattern) {
        if (patternCache.containsKey(pattern)) {
            return patternCache.get(pattern);
        }
        final List<Integer> opcodes = new LinkedList<>();
        if (pattern.startsWith("(") && pattern.contains("|") && pattern.endsWith(")")) {
            final String trimmed = pattern.substring(1, pattern.length() - 1);
            final String[] parts = trimmed.split("\\|");
            for (final String insn : parts) {
                opcodes.add(getOpcode(insn.replaceAll("!", "")));
            }
        } else {
            opcodes.add(getOpcode(pattern.replaceAll("!", "")));
        }
        patternCache.put(pattern, opcodes);
        return opcodes;
    }

    private static int getOpcode(final String name) {
        if (opcodeCache.containsKey(name)) {
            return opcodeCache.get(name);
        }
        try {
            final Integer opcode = (Integer) Opcodes.class.getField(name.toUpperCase()).get(null);
            opcodeCache.put(name, opcode);
            return opcode;
        } catch (final IllegalAccessException | NoSuchFieldException e) {
            return -1;
        }
    }

    private final MethodNode mn;
    private final InsnList iList;
    private int index = 0;

    public RIS(final MethodNode mn) {
        this.mn = mn;
        iList = mn.instructions;
    }

    public AbstractInsnNode current() {
        if (index < 0 || index >= iList.size()) {
            return null;
        }
        return iList.get(index);
    }

    public AbstractInsnNode[] getArray() {
        return iList.toArray();
    }

    public InsnList getInsnList() {
        return iList;
    }

    public int getPosition() {
        return index;
    }

    public int getSize() {
        return iList.size();
    }

    public AbstractInsnNode next() {
        ++index;
        return current();
    }

    public <T> T next(final Class<T> insn) {
        for (++index; index < iList.size(); ++index) {
            final AbstractInsnNode cur = current();
            if (cur != null && insn.isAssignableFrom(cur.getClass())) {
                return insn.cast(cur);
            }
        }
        return null;
    }

    public AbstractInsnNode next(final int... opcodes) {
        for (++index; index < iList.size(); ++index) {
            final AbstractInsnNode cur = current();
            if (cur == null) {
                continue;
            }
            for (final int op : opcodes) {
                if (cur.getOpcode() == op) {
                    return cur;
                }
            }
        }
        return null;
    }

    public <T> T next(final Class<T> insn, final int... opcodes) {
        for (++index; index < iList.size(); ++index) {
            final AbstractInsnNode cur = current();
            if (cur == null || !insn.isAssignableFrom(cur.getClass())) {
                continue;
            }
            for (final int op : opcodes) {
                if (cur.getOpcode() == op) {
                    return insn.cast(cur);
                }
            }
        }
        return null;
    }

    public List<AbstractInsnNode[]> nextPattern(final String pattern) {
        final List<AbstractInsnNode[]> matches = new LinkedList<>();
        final AbstractInsnNode[] array = getArray();
        final String[] parts = pattern.split(" ");
        int nodeIdx = 0;
        int patternIdx = 0;
        outer:
        for (int idx = index; idx < array.length; ++idx) {
            final AbstractInsnNode ain = array[idx];
            final String part = parts[patternIdx];
            final List<Integer> codes = compilePattern(part);
            for (final int code : codes) {
                if (part.startsWith("!")) {
                    if (code == ain.getOpcode()) {
                        continue;
                    }
                } else if (code != ain.getOpcode()) {
                    continue;
                }
                if (patternIdx < parts.length - 1) {
                    patternIdx++;
                } else {
                    final AbstractInsnNode[] nodes = new AbstractInsnNode[parts.length];
                    System.arraycopy(getArray(), nodeIdx, nodes, 0, parts.length);
                    matches.add(nodes);
                    idx = nodeIdx++;
                    index = nodeIdx;
                    patternIdx = 0;
                }
                continue outer;
            }
            idx = nodeIdx++;
            patternIdx = 0;
        }
        return matches;
    }

    public AbstractInsnNode previous() {
        --index;
        return current();
    }

    public <T> T previous(final Class<T> insn) {
        for (--index; index > 0; --index) {
            final AbstractInsnNode cur = current();
            if (cur != null && insn.isAssignableFrom(cur.getClass())) {
                return insn.cast(cur);
            }
        }
        return null;
    }

    public <T> T previous(final Class<T> insn, final int... opcodes) {
        for (--index; index > 0; --index) {
            final AbstractInsnNode cur = current();
            if (cur == null || !insn.isAssignableFrom(cur.getClass())) {
                continue;
            }
            for (final int op : opcodes) {
                if (cur.getOpcode() == op) {
                    return insn.cast(cur);
                }
            }
        }
        return null;
    }

    public AbstractInsnNode previous(final int... opcodes) {
        for (--index; index > 0; --index) {
            final AbstractInsnNode cur = current();
            if (cur == null) {
                continue;
            }
            for (final int op : opcodes) {
                if (cur.getOpcode() == op) {
                    return cur;
                }
            }
        }
        return null;
    }

    public void setPosition(final int index) {
        this.index = index;
    }
}