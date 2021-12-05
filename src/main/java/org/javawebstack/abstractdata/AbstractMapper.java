package org.javawebstack.abstractdata;

import org.javawebstack.abstractdata.mapper.Mapper;

public class AbstractMapper {

    private final Mapper mapper = new Mapper();
    private NamingPolicy namingPolicy = NamingPolicy.NONE;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private boolean exposeRequired = false;

    public AbstractMapper setNamingPolicy(NamingPolicy namingPolicy) {
        this.namingPolicy = namingPolicy;
        mapper.namingPolicy(namingPolicy.getMapperPolicy());
        return this;
    }

    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

    public AbstractMapper setExposeRequired(boolean exposeRequired) {
        this.exposeRequired = exposeRequired;
        mapper.requireExpose(exposeRequired);
        return this;
    }

    public boolean isExposeRequired() {
        return exposeRequired;
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
