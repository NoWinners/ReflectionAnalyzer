package org.tak.enums;

import org.tak.Explorer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: Tommy
 * 5/27/13
 */
public class EnumExplorer {
    private Class klass;

    public EnumExplorer(Class klass) {
        this.klass = klass;
    }

    public void byMethod() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<Method> methods = new ArrayList<>();
        for (Method method : klass.getDeclaredMethods()) {
            if (!Modifier.isStatic(method.getModifiers())) {
                if (method.getParameterTypes().length == 0) {
                    methods.add(method);
                }
            }
        }
        for (Field field : klass.getDeclaredFields()) {
            if (field.getType().equals(klass) && Modifier.isStatic(field.getModifiers())) {
                System.out.println(field.getName());
                Object instance = field.get(null);
                for (Method method : methods) {
                    method.setAccessible(true);
                    Object object = method.invoke(instance);
                    System.out.print(method.getName()+"(): ");
                    if (method.getReturnType().getName().contains("[")) {
                        print(object);
                    } else if (object != null) {
                        if (Explorer.DEBUG) {
                            if (object.getClass().getName().contains(".")) {
                                System.out.println(object);
                            } else {
                                System.out.println(object + ":" + object.getClass());
                            }
                        } else {
                            if (object.getClass().getName().equals("org.powerbot.game.api.wrappers.Tile")) {
                                System.out.println(tileToString(object));
                            } else {
                                System.out.println(object.toString());
                            }
                        }
                    } else {
                        System.out.println("null");
                    }
                }
            }
        }
    }
    public void byField() throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<Field> instanceFields = new ArrayList<>();
        for (Field field : klass.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                if (!field.isAccessible())
                    field.setAccessible(true);
                instanceFields.add(field);
            }
        }
        for (Field field : klass.getDeclaredFields()) {
            if (field.getType().equals(klass) && Modifier.isStatic(field.getModifiers())) {
                System.out.println(field.getName());
                Object instance = field.get(null);
                for (Field instanceField : instanceFields) {
                    Object object = instanceField.get(instance);
                    if (instanceField.getType().getName().contains("[")) {
                        print(object);
                    } else if (object != null) {
                        if (Explorer.DEBUG) {
                            if (object.getClass().getName().contains(".")) {
                                System.out.println(object);
                            } else {
                                System.out.println(object + ":" + object.getClass());
                            }
                        } else {
                            if (object.getClass().getName().equals("org.powerbot.game.api.wrappers.Tile")) {
                                System.out.println(tileToString(object));
                            } else {
                                System.out.println(object.toString());
                            }
                        }
                    }

                }
            }
        }
    }
    private static String tileToString(Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class klass = object.getClass();
        Method getX = klass.getDeclaredMethod("getX");
        Method getY = klass.getDeclaredMethod("getY");
        Method getZ = klass.getDeclaredMethod("getPlane");
        return "("+getX.invoke(object)+","+getY.invoke(object)+","+getZ.invoke(object)+")";
    }

    private static void print(Object objects) {
        if (objects instanceof Object[][]) {
            for (Object[] o : (Object[][]) objects) {
                print(o);
            }
        } else if (objects instanceof Object[]) {
            System.out.println(Arrays.toString((Object[]) objects));
        } else if (objects instanceof int[]) {
            System.out.println(Arrays.toString((int[]) objects));
        }  else {
            throw new IllegalArgumentException("Unknown type given to print(Object)");
        }
    }
}
