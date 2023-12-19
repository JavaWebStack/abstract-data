package org.javawebstack.abstractdata.xml;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*
This is a temporary implementation using Java's built-in xml implementation
 */
public class LegacyXMLParser {

    @Deprecated
    protected static XMLElement parse(String s) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
            return convert(document.getDocumentElement());
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static XMLElement convert(Element element) {
        String tag = element.getTagName();
        XMLElement e = new XMLElement(tag);
        NamedNodeMap attrNodeMap = element.getAttributes();
        for (int i = 0; i < attrNodeMap.getLength(); i++) {
            String name = attrNodeMap.item(i).getNodeName();
            String value = ((Attr) attrNodeMap.item(i)).getValue();
            e.attr(name, value);
        }
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            switch (node.getNodeType()) {
                case Node.ATTRIBUTE_NODE: {
                    Attr attr = (Attr) node;
                    e.attr(attr.getName(), attr.getValue());
                    break;
                }
                case Node.ELEMENT_NODE: {
                    e.child(convert((Element) node));
                    break;
                }
                case Node.CDATA_SECTION_NODE:
                case Node.TEXT_NODE: {
                    e.child(new XMLTextNode(((Text) node).getData()));
                    break;
                }
            }
        }
        return e;
    }

}
