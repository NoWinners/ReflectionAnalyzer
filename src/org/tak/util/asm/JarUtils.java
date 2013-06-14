package org.tak.util.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/**
 * User: Tommy
 * 5/28/13
 */
public class JarUtils {
    public static List<ClassNode> getClassNodes(String jarPath) {
        try {
            return getClassNodes(new JarFile(jarPath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    public static List<ClassNode> getClassNodes(JarFile jarFile) throws IOException {
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
        return classNodes;
    }
    public static void dumpClasses(String jarName, List<ClassNode> classes) {
        System.out.println("Dumping ClassNodes to "+jarName.substring(jarName.lastIndexOf("/")+1));
        try {
            File file = new File(jarName);
            JarOutputStream out = new JarOutputStream(new FileOutputStream(file));
            for(ClassNode node : classes) {
                JarEntry entry = new JarEntry(node.name + ".class");
                out.putNextEntry(entry);
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                node.accept(writer);
                out.write(writer.toByteArray());
            }
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
