package com.m5rian.github.myraWebsite.utilities.json;

import java.util.HashMap;
import java.util.Map;

public class JsonObjectBuilder {
    private final Map<String, String> stringParameters = new HashMap<>();
    private final Map<String, Integer> intParameters = new HashMap<>();

    public JsonObjectBuilder() {

    }

    public JsonObjectBuilder add(String key, String value) {
        stringParameters.put(key, value);
        return this;
    }

    public JsonObjectBuilder addInt(String key, Integer value) {
        intParameters.put(key, value);
        return this;
    }

    public String build() {
        StringBuilder jsonObject = new StringBuilder();
        jsonObject.append("{");
        stringParameters.forEach((key, value) -> jsonObject.append("\"").append(key).append("\":\"").append(value).append("\","));
        intParameters.forEach((key, value) -> jsonObject.append("\"" + key + "\":" + value + "\""));
        return jsonObject.substring(0, jsonObject.toString().length() - 1) + "}";

    }

}
