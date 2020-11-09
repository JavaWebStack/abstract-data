package org.javawebstack.graph;

import com.google.gson.*;
import com.google.gson.annotations.Expose;

public class GraphMapper {

    private Gson gson;
    private NamingPolicy namingPolicy = NamingPolicy.NONE;
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    private boolean exposeRequired = false;

    public GraphMapper setNamingPolicy(NamingPolicy namingPolicy){
        this.namingPolicy = namingPolicy;
        gson = null;
        return this;
    }

    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

    public GraphMapper setExposeRequired(boolean exposeRequired) {
        this.exposeRequired = exposeRequired;
        gson = null;
        return this;
    }

    public boolean isExposeRequired() {
        return exposeRequired;
    }

    public GraphMapper setDateFormat(String dateFormat) {
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
        return builder.create();
    }

    public GraphElement toGraph(Object object){
        return GraphElement.fromJson(gson().toJsonTree(object));
    }

    public <T> T fromGraph(GraphElement element, Class<T> type){
        return gson().fromJson(element.toJson(), type);
    }



}
