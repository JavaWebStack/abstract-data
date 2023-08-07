package org.javawebstack.abstractdata.xml;

public interface XMLNode {

    default String toXML() {
        return new XMLDumper().dump(this);
    }

    default String toXML(boolean pretty) {
        return new XMLDumper().setPretty(pretty).dump(this);
    }

}
