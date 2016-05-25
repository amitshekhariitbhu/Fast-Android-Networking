/*
 *    Copyright (C) 2016 Amit Shekhar
 *    Copyright (C) 2011 The Android Open Source Project
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.networking;

/**
 * Created by amitshekhar on 29/03/16.
 */
public class ApiEndPoint {
    public static String BASE_URL = "http://localhost:3000/api";
    public static final String GET_JSON_ARRAY = "/getAllUsers/{pageNumber}";
    public static final String GET_JSON_OBJECT = "/getAnUser/{userId}";
    public static final String CHECK_FOR_HEADER = "/checkForHeader";
    public static final String POST_CREATE_AN_USER = "/createAnUser";
    public static String UPLOAD_IMAGE_URL = "http://localhost:3000/upload";


}
