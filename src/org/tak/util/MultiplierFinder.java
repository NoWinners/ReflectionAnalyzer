package org.tak.util;

import org.objectweb.asm.tree.*;
import org.tak.runtime.Multipliers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Tommy
 * 5/28/13
 */
public class MultiplierFinder {
    private final List<FieldStore> fieldStores;
    private static final String[] REGEXES = new String[]{
            "LDC GETFIELD IMUL",
            "LDC GETSTATIC IMUL",
            "GETFIELD LDC IMUL",
            "GETSTATIC LDC IMUL",
    };

    public MultiplierFinder(List<FieldStore> fieldStores) {
        this.fieldStores = fieldStores;
    }

    public Multipliers getMultipliers(ClassNode[] classNodes) {
        final HashMap<FieldStore, Integer> multipliers = new HashMap<>();
        final HashMap<FieldStore, HashMap<Integer, Integer>> hashMap = new HashMap<>();
        for (ClassNode classNode : classNodes) {
            getMultipliers(classNode, REGEXES, hashMap);
        }
        for (Map.Entry<FieldStore, HashMap<Integer, Integer>> entry : hashMap.entrySet()) {
            int mult = -1;
            int best = 0;
            for (Map.Entry<Integer, Integer> integerEntry : entry.getValue().entrySet()) {
                if (integerEntry.getValue() > best) {
                    mult = integerEntry.getKey();
                    best = integerEntry.getValue();
                }
            }
            multipliers.put(entry.getKey(), mult);
        }
        return new Multipliers(multipliers);
    }

    private void getMultipliers(ClassNode classNode, final String[] regexes, HashMap<FieldStore, HashMap<Integer, Integer>> hashMap) {
        for (MethodNode methodNode : classNode.methods) {
            RIS insnFinder = new RIS(methodNode);
            for (String regex : regexes) {
                List<AbstractInsnNode[]> list = insnFinder.nextPattern(regex);
                for (AbstractInsnNode[] possible : list) {
                    FieldInsnNode fieldInsnNode = null;
                    LdcInsnNode ldcInsnNode = null;
                    for (AbstractInsnNode abstractInsnNode : possible) {
                        if (abstractInsnNode instanceof LdcInsnNode) {
                            ldcInsnNode = (LdcInsnNode) abstractInsnNode;
                        } else if (abstractInsnNode instanceof FieldInsnNode) {
                            fieldInsnNode = (FieldInsnNode) abstractInsnNode;
                        }
                        if (fieldInsnNode != null && ldcInsnNode != null) {
                            if (fieldInsnNode.desc.equals("I")) {
                                for (int i = fieldStores.size() - 1; i >= 0; i--) {
                                    FieldStore field = fieldStores.get(i);
                                    if (field.getName().endsWith(fieldInsnNode.name) && field.getOwner().equals(fieldInsnNode.owner) && ldcInsnNode.cst instanceof Integer) {
                                        Integer mult = (Integer) ldcInsnNode.cst;
                                        if (hashMap.containsKey(field)) {
                                            HashMap<Integer, Integer> mults = hashMap.get(field);
                                            if (mults.containsKey(mult)) {
                                                mults.put(mult, mults.remove(mult) + 1);
                                            } else {
                                                mults.put(mult, 1);
                                            }
                                        } else {
                                            HashMap<Integer, Integer> mults = new HashMap<>();
                                            mults.put(mult, 1);
                                            hashMap.put(field, mults);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
