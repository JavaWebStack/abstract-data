package org.javawebstack.abstractdata.xml;

public class XMLTextNode implements XMLNode {

    private String text;

    public XMLTextNode(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
