package org.javawebstack.abstractdata.util;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;
import org.javawebstack.abstractdata.AbstractObject;

import java.util.ArrayList;
import java.util.List;

public class Helpers {

    public static String typeName(AbstractElement e) {
        if (e.isArray())
            return "array";
        if (e.isObject())
            return "object";
        if (e.isString())
            return "string";
        if (e.isNumber())
            return "number";
        if (e.isBoolean())
            return "boolean";
        return "null";
    }

    public static Class<?> guessGeneric(AbstractElement e) {
        if (e.isArray())
            return AbstractArray.class;
        if (e.isObject())
            return AbstractObject.class;
        if (e.isString())
            return String.class;
        if (e.isNumber())
            return Number.class;
        if (e.isBoolean())
            return Boolean.class;
        return null;
    }

    public static List<String> words(String s) {
        List<String> words = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '_' || s.charAt(i) == '-') {
                if (sb.length() > 0) {
                    words.add(sb.toString());
                    sb = new StringBuilder();
                }
                continue;
            }
            if (i > 0 && (Character.isLowerCase(s.charAt(i - 1)) && Character.isUpperCase(s.charAt(i)))) {
                words.add(sb.toString());
                sb = new StringBuilder();
            }
            sb.append(Character.toLowerCase(s.charAt(i)));
        }
        if (sb.length() > 0)
            words.add(sb.toString());
        return words;
    }

}
