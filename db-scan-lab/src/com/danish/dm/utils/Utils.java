package com.danish.dm.utils;

import java.util.ArrayList;
import java.util.List;

public class Utils
{

    public static void unSuccessfulExit(String message)
    {
        System.err.println(message);
        System.exit(Constants.UNSUCCESSFUL_EXIT_CODE);
    }

    public static <T extends Number> T parseNumber(String s, Class<T> clazz)
    {
        if (clazz.equals(Integer.class))
        {
            return (T) Integer.valueOf(s);
        }

        if (clazz.equals(Float.class))
        {
            return (T) Float.valueOf(s);
        }

        if (clazz.equals(Double.class))
        {
            return (T) Double.valueOf(s);
        }

        throw new IllegalArgumentException("Only Int, Float and Double are supported");
    }

    //
    public static <T> List<T> union(List<T> list1, List<T> list2) {

        ArrayList<T> s =  new ArrayList<T>(list1);
        s.addAll(difference(list2, list1));
        return s;
    }

    public static <T> List<T> intersection(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }

    public static <T> List<T> difference(List<T> list1, List<T> list2) {
        List<T> list = new ArrayList<T>();

        for (T t : list1) {
            if(!list2.contains(t)) {
                list.add(t);
            }
        }

        return list;
    }


}
