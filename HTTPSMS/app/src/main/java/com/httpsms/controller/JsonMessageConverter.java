package com.httpsms.controller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yanzhenjie.andserver.annotation.Converter;
import com.yanzhenjie.andserver.framework.MessageConverter;
import com.yanzhenjie.andserver.framework.body.JsonBody;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.util.IOUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Converter
public class JsonMessageConverter implements MessageConverter {

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T convert(@NonNull InputStream stream, @Nullable MediaType mediaType, @NonNull Type type) {
        try {
            String jsonStr = IOUtils.toString(stream, StandardCharsets.UTF_8);
            if (jsonStr == null || jsonStr.trim().isEmpty()) {
                return null;
            }

            if (jsonStr.trim().startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonStr);
                return (T) convertJsonArrayToList(jsonArray);
            } else {
                JSONObject jsonObject = new JSONObject(jsonStr);
                return (T) convertJsonObjectToMap(jsonObject);
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    @Override
    public ResponseBody convert(@Nullable Object output, @Nullable MediaType mediaType) {
        if (output == null) return null;
        
        if (output instanceof Map) {
            return new JsonBody(new JSONObject((Map<?, ?>) output).toString());
        } else if (output instanceof List) {
            return new JsonBody(new JSONArray((List<?>) output).toString());
        }
        return new JsonBody(output.toString());
    }

    private Map<String, Object> convertJsonObjectToMap(JSONObject jsonObject) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                value = convertJsonObjectToMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = convertJsonArrayToList((JSONArray) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private List<Object> convertJsonArrayToList(JSONArray jsonArray) throws Exception {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            if (value instanceof JSONObject) {
                value = convertJsonObjectToMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = convertJsonArrayToList((JSONArray) value);
            }
            list.add(value);
        }
        return list;
    }
}
