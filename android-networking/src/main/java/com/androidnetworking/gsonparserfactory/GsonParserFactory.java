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

package com.androidnetworking.gsonparserfactory;

import com.androidnetworking.interfaces.Parser;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by amitshekhar on 31/07/16.
 */
public final class GsonParserFactory extends Parser.Factory {

    private final Gson gson;

    public GsonParserFactory() {
        this.gson = new Gson();
    }

    public GsonParserFactory(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Parser<ResponseBody, ?> responseBodyParser(Type type) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonResponseBodyParser<>(gson, adapter);
    }

    @Override
    public Parser<?, RequestBody> requestBodyParser(Type type) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new GsonRequestBodyParser<>(gson, adapter);
    }

    @Override
    public Object getObject(String string, Type type) {
        try {
            return gson.fromJson(string, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getString(Object object) {
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public HashMap<String, String> getStringMap(Object object) {
        try {
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            return gson.fromJson(gson.toJson(object), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
}
