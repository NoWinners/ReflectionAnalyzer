package org.tak.util;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * User: Tommy
 * 5/28/13
 */
public class JarUtils {
    public static ClassNode[] getClassNodes(String jarPath) {
        try {
            return getClassNodes(new JarFile(jarPath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static ClassNode[] getClassNodes(JarFile jarFile) throws IOException {
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        List<ClassNode> classNodes = new ArrayList<>();
        while (jarEntries.hasMoreElements()) {
            JarEntry current = jarEntries.nextElement();
            if (current.getName().contains(".class")) {
                byte[] buffer = new byte[1024];
                BufferedInputStream bufferedInputStream = new BufferedInputStream(jarFile.getInputStream(current));
                ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                int read = 0;
                while ((read = bufferedInputStream.read(buffer,0,1024))!=-1) {
                    byteArrayInputStream.write(buffer,0,read);
                }
                ClassReader reader = new ClassReader(byteArrayInputStream.toByteArray());
                ClassNode node = new ClassNode();
                reader.accept(node,0);
                classNodes.add(node);
            }
        }
        return classNodes.toArray(new ClassNode[classNodes.size()]);
    }
}
