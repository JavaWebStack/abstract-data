package org.javawebstack.abstractdata.json;

import org.javawebstack.abstractdata.AbstractArray;
import org.javawebstack.abstractdata.AbstractElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class JsonDumper {

    private boolean pretty;
    private String indent = "    ";

    public JsonDumper setPretty(boolean pretty) {
        this.pretty = pretty;
        return this;
    }

    public JsonDumper setIndent(String indent) {
        this.indent = indent;
        return this;
    }

    public String dump(AbstractElement element) {
        return String.join("\n", dumpLines(element));
    }

    private List<String> dumpLines(AbstractElement element) {
        if(element == null || element.isNull())
            return Collections.singletonList("null");
        if(element.isBoolean())
            return Collections.singletonList(element.bool().toString());
        if(element.isNumber())
            return Collections.singletonList(element.number().toString());
        if(element.isString())
            return Collections.singletonList("\"" + escape(element.string()) + "\"");
        if(element.isObject()) {
            List<String> lines =new ArrayList<>();
            if(pretty) {
                lines.add("{");
                List<String> keys = new ArrayList<>(element.object().keys());
                for(int i=0; i<keys.size(); i++) {
                    String k = keys.get(i);
                    List<String> vLines = dumpLines(element.object().get(k));
                    lines.add(indent + "\"" + escape(k) + "\": " + vLines.get(0));
                    for(int j=1; j<vLines.size(); j++)
                        lines.add(indent + vLines.get(j));
                    if(i+1 < keys.size())
                        lines.set(lines.size()-1, lines.get(lines.size()-1) + ",");
                }
                lines.add("}");
            } else {
                lines.add("{" + element.object().keys().stream().map(k -> "\"" + escape(k) + "\":" + dumpLines(element.object().get(k)).get(0)).collect(Collectors.joining(",")) + "}");
            }
            return lines;
        }
        if(element.isArray()) {
            List<String> lines =new ArrayList<>();
            if(pretty) {
                lines.add("[");
                AbstractArray array = element.array();
                for(int i=0; i<array.size(); i++) {
                    List<String> vLines = dumpLines(array.get(i));
                    for(int j=0; j<vLines.size(); j++)
                        lines.add(indent + vLines.get(j));
                    if(i+1 < array.size())
                        lines.set(lines.size()-1, lines.get(lines.size()-1) + ",");
                }
                lines.add("]");
            } else {
                lines.add("[" + element.array().stream().map(e -> dumpLines(e).get(0)).collect(Collectors.joining(",")) + "]");
            }
            return lines;
        }
        return new ArrayList<>();
    }

    private static String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<s.length(); i++) {
            char ch = s.charAt(i);
            switch(ch) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                default:
                    if (ch <= '\u001F' || ch >= '\u007F' && ch <= '\u009F' || ch >= '\u2000' && ch <= '\u20FF') {
                        String hex = Integer.toHexString(ch);
                        sb.append("\\u");
                        for(int k=0; k < 4-hex.length(); k++)
                            sb.append('0');
                        sb.append(hex.toUpperCase(Locale.ROOT));
                    } else {
                        sb.append(ch);
                    }
                    break;
            }
        }
        return sb.toString();
    }

}
