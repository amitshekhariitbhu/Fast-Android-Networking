/*
 *
 *  *    Copyright (C) 2016 Amit Shekhar
 *  *    Copyright (C) 2011 Android Open Source Project
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.jacksonandroidnetworking;

import com.androidnetworking.interfaces.Parser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by amitshekhar on 15/09/16.
 */
public final class JacksonParserFactory extends Parser.Factory {

    private final ObjectMapper mapper;

    public JacksonParserFactory() {
        this.mapper = new ObjectMapper();
    }

    public JacksonParserFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Parser<ResponseBody, ?> responseBodyParser(Type type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectReader reader = mapper.readerFor(javaType);
        return new JacksonResponseBodyParser<>(reader);
    }

    @Override
    public Parser<?, RequestBody> requestBodyParser(Type type) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectWriter writer = mapper.writerFor(javaType);
        return new JacksonRequestBodyParser<>(writer);
    }

    @Override
    public Object getObject(String string, Type type) {
        try {
            JavaType javaType = mapper.getTypeFactory().constructType(type);
            ObjectReader objectReader = mapper.readerFor(javaType);
            return objectReader.readValue(string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getString(Object object) {
        try {
            ObjectWriter objectWriter = mapper.writerFor(object.getClass());
            return objectWriter.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public HashMap<String, String> getStringMap(Object object) {
        try {
            TypeReference<HashMap<String, String>> typeRef
                    = new TypeReference<HashMap<String, String>>() {
            };
            ObjectWriter objectWriter = mapper.writerFor(object.getClass());
            return mapper.readValue(objectWriter.writeValueAsString(object), typeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
