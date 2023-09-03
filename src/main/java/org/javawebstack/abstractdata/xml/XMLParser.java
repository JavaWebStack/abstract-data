package org.javawebstack.abstractdata.xml;

public class XMLParser {

    public XMLElement parse(String source) {
        return LegacyXMLParser.parse(source);
    }

}
