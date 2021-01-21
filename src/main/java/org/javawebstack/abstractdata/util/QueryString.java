package org.javawebstack.abstractdata.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class QueryString {

    private final Map<String, String> map;

    public QueryString(String query){
        this(parseQuery(query));
    }

    public QueryString(){
        this(new HashMap<>());
    }

    public QueryString(Map<String, String> map){
        this.map = map;
    }

    public QueryString set(String key, Object value){
        if(value == null){
            map.remove(key);
            return this;
        }
        if(!(value instanceof String))
            value = value.toString();
        map.put(key, (String) value);
        return this;
    }

    public QueryString set(String[] key, Object value){
        return set(implodeKey(key), value);
    }

    public String get(String... key){
        return map.get(implodeKey(key));
    }

    public Map<String[], Object> toTree(){
        Map<String[], Object> tree = new HashMap<>();
        for(String k : map.keySet()){
            tree.put(explodeKey(k), map.get(k));
        }
        return tree;
    }

    private static void putTree(Map<String, Object> tree, String[] keys, Object value){
        if(keys.length == 0)
            return;
        if(keys.length == 1){
            tree.put(keys[0], value);
            return;
        }
        if(!tree.containsKey(keys[0]))
            tree.put(keys[0], new HashMap<>());
        putTree((Map<String, Object>) tree.get(keys[0]), Arrays.copyOfRange(keys, 1, keys.length), value);
    }

    public boolean has(String... key){
        return map.containsKey(implodeKey(key));
    }

    public int size(){
        return map.size();
    }

    public QueryString forEach(BiConsumer<String, String> biConsumer){
        map.forEach(biConsumer);
        return this;
    }

    public Map<String, String> getMap(){
        return map;
    }

    public String toString(){
        return map.entrySet().stream().map(e -> e.getKey() + "=" + urlEncode(e.getValue())).collect(Collectors.joining("&"));
    }

    private static Map<String, String> parseQuery(String query){
        Map<String, String> map = new HashMap<>();
        if(query != null){
            String[] parts = query.split("&");
            for(String part : parts){
                if(part.length() == 0)
                    continue;
                String[] spl = part.split("=");
                map.put(spl[0], spl.length > 1 ? urlDecode(spl[1]) : "");
            }
        }
        return map;
    }

    private static String urlEncode(String s){
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {}
        return s;
    }

    private static String urlDecode(String s){
        try {
            return URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {}
        return s;
    }

    private static String implodeKey(String[] keys){
        if(keys.length == 0)
            return "";
        StringBuilder sb = new StringBuilder(keys[0]);
        for(int i=1; i<keys.length; i++)
            sb.append('[').append(urlEncode(keys[i])).append(']');
        return sb.toString();
    }

    private static String[] explodeKey(String key){
        String[] keys = key.split("\\[");
        for(int i=1; i<keys.length; i++)
            keys[i] = urlDecode(keys[i].substring(0, keys[i].length()-1));
        return keys;
    }

}
