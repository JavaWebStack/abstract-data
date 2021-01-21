package org.javawebstack.abstractdata;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

public class AbstractMapper {

    private Gson gson;
    private NamingPolicy namingPolicy = NamingPolicy.NONE;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private boolean exposeRequired = false;

    public AbstractMapper setNamingPolicy(NamingPolicy namingPolicy){
        this.namingPolicy = namingPolicy;
        gson = null;
        return this;
    }

    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

    public AbstractMapper setExposeRequired(boolean exposeRequired) {
        this.exposeRequired = exposeRequired;
        gson = null;
        return this;
    }

    public boolean isExposeRequired() {
        return exposeRequired;
    }

    public AbstractMapper setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        gson = null;
        return this;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    private Gson gson(){
        if(gson != null)
            return gson;
        GsonBuilder builder = new GsonBuilder()
                .setFieldNamingPolicy(namingPolicy.getGsonPolicy());
        if(dateFormat != null)
            builder.setDateFormat(dateFormat);
        if(exposeRequired){
            builder.excludeFieldsWithoutExposeAnnotation();
        }else{
            builder.setExclusionStrategies(new ExclusionStrategy() {
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    return fieldAttributes.getAnnotation(Expose.class) != null && !fieldAttributes.getAnnotation(Expose.class).serialize();
                }
                public boolean shouldSkipClass(Class<?> aClass) { return false;  }
            });
        }
        gson = builder.create();
        return gson;
    }

    public AbstractElement toAbstract(Object object){
        return AbstractElement.fromJson(gson().toJsonTree(object));
    }

    public <T> T fromAbstract(AbstractElement element, Class<T> type){
        if(element == null)
            return null;
        return gson().fromJson(element.toJson(), type);
    }



}
