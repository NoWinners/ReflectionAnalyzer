package org.tak.runtime;

import org.tak.util.FieldStore;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Tommy
 * 5/31/13
 */
public class Multipliers {
    private final HashMap<FieldStore, Integer> multipliers;

    public Multipliers(HashMap<FieldStore, Integer> multipliers) {
        this.multipliers = multipliers;
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
}
