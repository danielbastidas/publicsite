package com.smartmatic.graph.service;

/**
 * Represents the general rest response
 */
public class RestResponse {

    /**
     * Constructor
     * @param type the message type
     * @param message the message
     */
    public RestResponse(Type type, String message){
        this.type = type;
        this.message = message;
    }

    /**
     * Constructor
     * @param type the message type
     * @param data the data content
     */
    public RestResponse(Type type, Object data){
        this.type = type;
        this.data = data;
    }

    /**
     * Constructor
     * @param type the message type
     * @param data the data content
     * @param messages the message
     */
    public RestResponse(Type type, Object data, String message){
        this.type = type;
        this.data = data;
        this.message = message;
    }

    /**
     * Holds all possible response status
     * @author Gerardo Monasterios
     *
     */
    public enum Type {
        SUCCESS,
        INFO,
        ERROR,
        WARN;
    }

    /**
     * Holds the response type
     */
    private Type type;

    /**
     * Holds the response message
     */
    private String message;

    /**
     * Holds the data response
     */
    private Object data;

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the data
     */
    public Object getData() {
        return this.data;
    }

    /**
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }

}