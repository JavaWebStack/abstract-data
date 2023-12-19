package org.javawebstack.abstractdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AbstractPath {

    public static final AbstractPath ROOT = new AbstractPath(null, null);

    private final AbstractPath parent;
    private final String name;

    public AbstractPath(String name) {
        this(ROOT, name);
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name can not be null or empty");
    }

    private AbstractPath(AbstractPath parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public AbstractPath getParent() {
        return parent;
    }

    public AbstractPath subPath(String name) {
        return new AbstractPath(this, name);
    }

    public AbstractPath clone() {
        return new AbstractPath(
                this.parent != null ? this.parent.clone() : null,
                name
        );
    }

    public AbstractPath concat(AbstractPath path) {
        AbstractPath cloned = clone();
        for (String part : path.getParts())
            cloned = cloned.subPath(part);
        return cloned;
    }

    public List<String> getParts() {
        List<String> parts = parent != null ? parent.getParts() : new ArrayList<>();
        if (name != null)
            parts.add(name);
        return parts;
    }

    public String toString() {
        return String.join(".", getParts());
    }

    public static AbstractPath parse(String s) {
        s = s.trim();
        if (s.isEmpty())
            return ROOT;
        String[] spl = s.split("\\.");
        AbstractPath path = new AbstractPath(spl[0]);
        for (int i = 1; i < spl.length; i++) {
            String sub = spl[i];
            if (sub.isEmpty())
                throw new IllegalArgumentException("Invalid empty sub-path");
            path = path.subPath(spl[i]);
        }
        return path;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof AbstractPath))
            return false;
        AbstractPath other = (AbstractPath) obj;
        if (parent != null) {
            if (!parent.equals(other.parent))
                return false;
        } else {
            if (other.parent != null)
                return false;
        }
        if (name == null)
            return other.name == null;
        return name.equals(other.name);
    }

    public int hashCode() {
        return Objects.hash(parent, name);
    }

}
