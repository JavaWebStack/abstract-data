package org.javawebstack.abstractdata.xml;

import org.javawebstack.abstractdata.AbstractElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class XmlDumper {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    private boolean header;
    private boolean pretty;
    private String indent = "    ";

    public XmlDumper setPretty(boolean pretty) {
        this.pretty = pretty;
        return this;
    }

    public XmlDumper setHeader(boolean header) {
        this.header = header;
        return this;
    }

    public XmlDumper setIndent(String indent) {
        this.indent = indent;
        return this;
    }

    public String dump(AbstractElement element) {
        StringBuilder sb = new StringBuilder();
        if (header)
            sb.append(HEADER).append("\n");
        sb.append(String.join("\n", dumpLines(element)));
        return sb.toString();
    }

    private List<String> dumpLines(AbstractElement element) {
        if(element == null || element.isNull())
            throw new RuntimeException("Null is not supported!");
        if(element.isBoolean())
            return Collections.singletonList(element.bool().toString());
        if(element.isNumber())
            return Collections.singletonList(element.number().toString());
        if(element.isString())
            return Collections.singletonList(escape(element.string()));
        if (element.isObject()) {
            List<String> lines = new ArrayList<>();
            if (element.object().size() == 0)
                return lines;
            if (pretty) {
                List<String> keys = new ArrayList<>(element.object().keys());
                for(int i=0; i<keys.size(); i++) {
                    String k = keys.get(i);
                    List<String> vLines = dumpLines(element.object().get(k));
                    if (element.object().get(k).isObject() || element.object().get(k).isArray()) {
                        lines.add("<" + escape(k) + ">");
                        for (String vLine : vLines)
                            lines.add(indent + vLine);
                        lines.add("</" + escape(k) + ">");
                    } else {
                        lines.add("<"+escape(k)+">"+vLines.get(0)+"</"+escape(k)+">");
                    }
                    if(i+1 < keys.size())
                        lines.set(lines.size()-1, lines.get(lines.size()-1));
                }
            } else {
                lines.add(element.object().keys().stream().map(k -> "<"+escape(k)+">"+dumpLines(element.object().get(k)).get(0)+"</"+escape(k)+">").collect(Collectors.joining()));
            }
            return lines;
        }
        if (element.isArray()) {
            List<String> lines = new ArrayList<>();
            List<String> dumped = element.array().stream().map(k -> "<value>"+dumpLines(k).get(0)+"</value>").collect(Collectors.toList());
            if (pretty) {
                lines.addAll(dumped);
            } else {
                lines.add(String.join("", dumped));
            }
            return lines;
        }
        return new ArrayList<>();
    }

    private String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<s.length(); i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(ch);
                    break;
            }
        }
        return sb.toString();
    }
}
