package org.javawebstack.abstractdata.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class XMLDumper {

    private boolean pretty;
    private boolean useSelfClosing = true;
    private String indent = "    ";

    public XMLDumper setPretty(boolean pretty) {
        this.pretty = pretty;
        return this;
    }

    public XMLDumper setIndent(String indent) {
        this.indent = indent;
        return this;
    }

    public XMLDumper setUseSelfClosing(boolean useSelfClosing) {
        this.useSelfClosing = useSelfClosing;
        return this;
    }

    public String dump(XMLNode node) {
        return String.join("\n", dumpLines(node));
    }

    private List<String> dumpLines(XMLNode node) {
        if (node instanceof XMLTextNode) {
            XMLTextNode textNode = (XMLTextNode) node;
            return Arrays.asList(escape(textNode.getText(), true));
        }
        XMLElement element = (XMLElement) node;
        List<String> lines = new ArrayList<>();
        boolean selfClosing = useSelfClosing && element.getChildNodes().size() == 0;
        String openingTag = renderOpeningTag(element.tagName(), element.getAttributes(), selfClosing);
        String closingTag = selfClosing ? "" : "</" + escape(element.tagName(), false) + ">";
        if (pretty) {
            if (element.getChildNodes().size() == 0) {
                lines.add(openingTag + closingTag);
            } else if (element.getChildNodes().size() == 1 && element.getChildNodes().get(0) instanceof XMLTextNode) {
                lines.add(openingTag + escape(((XMLTextNode) element.getChildNodes().get(0)).getText(), true) + closingTag);
            } else {
                lines.add(openingTag);
                for (XMLNode child : element.getChildNodes()) {
                    lines.addAll(dumpLines(child).stream().map(l -> indent + l).collect(Collectors.toList()));
                }
                lines.add(closingTag);
            }
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(openingTag);
            for (XMLNode child : element.getChildNodes())
                dumpLines(child).forEach(sb::append);
            sb.append(closingTag);
            lines.add(sb.toString());
        }
        return lines;
    }

    public String renderOpeningTag(String tagName, Map<String, String> attributes, boolean selfClosing) {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        sb.append(escape(tagName, false));
        for (String attrName : attributes.keySet()) {
            sb.append(" ");
            sb.append(escape(attrName, false));
            sb.append('=');
            sb.append('"');
            sb.append(escape(attributes.get(attrName), false));
            sb.append('"');
        }
        if (selfClosing)
            sb.append("/");
        sb.append('>');
        return sb.toString();
    }

    private String escape(String value, boolean text) {
        value = value.replace("<", "&lt;");
        value = value.replace("&", "&amp;");
        if (!text) {
            value = value.replace("\"", "&quot;");
            value = value.replace("'", "&apos;");
            value = value.replace(">", "&gt;");
        }
        return value;
    }

}
