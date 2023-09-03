package org.javawebstack.abstractdata.xml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class XMLElement implements XMLNode {

    private String tagName;
    private final Map<String, String> attributes = new LinkedHashMap<>();
    private final List<XMLNode> childNodes = new ArrayList<>();

    public XMLElement(String tagName) {
        if(tagName == null)
            throw new IllegalArgumentException("tagName can not be null");
        this.tagName = tagName;
    }

    public XMLElement(String tagName, String text) {
        this(tagName);
        if(text == null)
            throw new IllegalArgumentException("text can not be null");
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

    public String attr(String name) {
        return attributes.get(name);
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

    public XMLElement onlyIf(boolean condition, Consumer<XMLElement> fn) {
        if(condition)
            fn.accept(this);
        return this;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public List<XMLNode> getChildNodes() {
        return childNodes;
    }

    public static XMLElement from(String xmlString) {
        return new XMLParser().parse(xmlString);
    }

}
