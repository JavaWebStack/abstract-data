package org.javawebstack.abstractdata.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XMLElement implements XMLNode {

    private String tagName;
    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<XMLNode> childNodes = new ArrayList<>();

    public XMLElement(String tagName) {
        this.tagName = tagName;
    }

    public XMLElement(String tagName, String text) {
        this(text);
        text(text);
    }

    public String tagName() {
        return tagName;
    }

    public XMLElement tagName(String tagName) {
        this.tagName = tagName;
        return this;
    }

    public String text() {
        return childNodes.stream()
                .filter(n -> n instanceof XMLTextNode)
                .map(n -> ((XMLTextNode) n))
                .map(XMLTextNode::getText)
                .collect(Collectors.joining());
    }

    public XMLElement text(String text) {
        childNodes.clear();
        childNodes.add(new XMLTextNode(text));
        return this;
    }

    public XMLElement attr(String name, String value) {
        if(value == null) {
            attributes.remove(name);
            return this;
        }
        attributes.put(name, value);
        return this;
    }

    public XMLElement ns(String namespace) {
        attr("xmlns", namespace);
        return this;
    }

    public XMLElement ns(String alias, String namespace) {
        attr("xmlns:" + alias, namespace);
        return this;
    }

    public XMLElement child(XMLNode childNode) {
        childNodes.add(childNode);
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public List<XMLNode> getChildNodes() {
        return childNodes;
    }

}
