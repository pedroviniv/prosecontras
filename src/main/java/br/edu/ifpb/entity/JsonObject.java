package br.edu.ifpb.entity;

/**
 * Created by kieckegard on 02/09/2016.
 */
public interface JsonObject<T> {

    String getJsonString();
    T fromJsonString(String json);

}
