package org.javawebstack.abstractdata;

import org.javawebstack.abstractdata.mapper.Mapper;

@Deprecated
public class AbstractMapper {

    private final Mapper mapper = new Mapper();
    private NamingPolicy namingPolicy = NamingPolicy.NONE;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";

    public AbstractMapper setNamingPolicy(NamingPolicy namingPolicy) {
        this.namingPolicy = namingPolicy;
        mapper.namingPolicy(namingPolicy.getMapperPolicy());
        return this;
    }

    public boolean shouldOmitNull() {
        return mapper.shouldOmitNull();
    }

    public AbstractMapper setOmitNull(boolean omitNull) {
        this.mapper.omitNull(omitNull);
        return this;
    }

    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

    public AbstractMapper setExposeRequired(boolean exposeRequired) {
        mapper.requireExpose(exposeRequired);
        return this;
    }

    public boolean isExposeRequired() {
        return mapper.isExposeRequired();
    }

    public AbstractMapper setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        mapper.dateFormat(dateFormat);
        return this;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public AbstractElement toAbstract(Object object) {
        return mapper.map(object);
    }

    public <T> T fromAbstract(AbstractElement element, Class<T> type) {
        if (element == null)
            return null;
        return mapper.map(element, type);
    }


}
