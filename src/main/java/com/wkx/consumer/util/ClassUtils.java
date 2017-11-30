package com.wkx.consumer.util;

import java.util.*;

public class ClassUtils {

    private static Set<Class> baseClassSet;

    static {
        baseClassSet=new HashSet<>();
        baseClassSet.add(String.class);
        baseClassSet.add(byte.class);
        baseClassSet.add(short.class);
        baseClassSet.add(int.class);
        baseClassSet.add(long.class);
        baseClassSet.add(float.class);
        baseClassSet.add(double.class);
        baseClassSet.add(boolean.class);
        baseClassSet.add(Boolean.class);
        baseClassSet.add(Byte.class);
        baseClassSet.add(Number.class);
    }

    public static boolean checkIsBaseClass(Class cs){
        if(baseClassSet.contains(cs)) return true;
        try {
            cs.asSubclass(Number.class);
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
