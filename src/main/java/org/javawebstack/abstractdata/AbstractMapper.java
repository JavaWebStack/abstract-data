package org.javawebstack.abstractdata;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.javawebstack.abstractdata.mapper.Mapper;
import org.javawebstack.abstractdata.util.GsonAbstractDataAdapter;

public class AbstractMapper {

    // This allows
    public static boolean enableExperimentalMapper = false;

    private Gson gson;
    private final Mapper mapper = new Mapper();
    private NamingPolicy namingPolicy = NamingPolicy.NONE;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private boolean exposeRequired = false;

    public AbstractMapper setNamingPolicy(NamingPolicy namingPolicy) {
        this.namingPolicy = namingPolicy;
        gson = null;
        mapper.namingPolicy(namingPolicy.getMapperPolicy());
        return this;
    }

    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

    public AbstractMapper setExposeRequired(boolean exposeRequired) {
        this.exposeRequired = exposeRequired;
        gson = null;
        mapper.requireExpose(exposeRequired);
        return this;
    }

    public boolean isExposeRequired() {
        return exposeRequired;
    }

    public AbstractMapper setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        gson = null;
        mapper.dateFormat(dateFormat);
        return this;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    private Gson gson() {
        if (gson != null)
            return gson;
        GsonBuilder builder = new GsonBuilder()
                .registerTypeAdapter(AbstractElement.class, new GsonAbstractDataAdapter<>())
                .registerTypeAdapter(AbstractObject.class, new GsonAbstractDataAdapter<>())
                .registerTypeAdapter(AbstractArray.class, new GsonAbstractDataAdapter<>())
                .registerTypeAdapter(AbstractPrimitive.class, new GsonAbstractDataAdapter<>())
                .registerTypeAdapter(AbstractNull.class, new GsonAbstractDataAdapter<>())
                .setFieldNamingPolicy(namingPolicy.getGsonPolicy())
                .disableHtmlEscaping();
        if (dateFormat != null)
            builder.setDateFormat(dateFormat);
        if (exposeRequired) {
            builder.excludeFieldsWithoutExposeAnnotation();
        } else {
            builder.setExclusionStrategies(new ExclusionStrategy() {
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return fieldAttributes.getAnnotation(Expose.class) != null && !fieldAttributes.getAnnotation(Expose.class).serialize();
                }

                public boolean shouldSkipClass(Class<?> aClass) {
                    return false;
                }
            });
        }
        gson = builder.create();
        return gson;
    }

    public AbstractElement toAbstract(Object object) {
        if(enableExperimentalMapper)
            return mapper.map(object);
        return AbstractElement.fromJson(gson().toJsonTree(object));
    }

    public <T> T fromAbstract(AbstractElement element, Class<T> type) {
        if (element == null)
            return null;
        if(enableExperimentalMapper)
            return mapper.map(element, type);
        return gson().fromJson(element.toJson(), type);
    }


}
