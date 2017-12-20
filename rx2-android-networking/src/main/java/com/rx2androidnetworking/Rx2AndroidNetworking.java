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

package com.rx2androidnetworking;

/**
 * Created by amitshekhar on 30/01/17.
 */

/**
 * Rx2AndroidNetworking entry point.
 * You must initialize this class before use. The simplest way is to just do
 * {#code AndroidNetworking.initialize(context)}.
 */
public class Rx2AndroidNetworking {

    /**
     * private constructor to prevent instantiation of this class
     */
    private Rx2AndroidNetworking() {
    }

    /**
     * Method to make GET request
     *
     * @param url The url on which request is to be made
     * @return The GetRequestBuilder
     */
    public static Rx2ANRequest.GetRequestBuilder get(String url) {
        return new Rx2ANRequest.GetRequestBuilder(url);
    }

    /**
     * Method to make HEAD request
     *
     * @param url The url on which request is to be made
     * @return The HeadRequestBuilder
     */
    public static Rx2ANRequest.HeadRequestBuilder head(String url) {
        return new Rx2ANRequest.HeadRequestBuilder(url);
    }

    /**
     * Method to make OPTIONS request
     *
     * @param url The url on which request is to be made
     * @return The OptionsRequestBuilder
     */
    public static Rx2ANRequest.OptionsRequestBuilder options(String url) {
        return new Rx2ANRequest.OptionsRequestBuilder(url);
    }

    /**
     * Method to make POST request
     *
     * @param url The url on which request is to be made
     * @return The PostRequestBuilder
     */
    public static Rx2ANRequest.PostRequestBuilder post(String url) {
        return new Rx2ANRequest.PostRequestBuilder(url);
    }

    /**
     * Method to make PUT request
     *
     * @param url The url on which request is to be made
     * @return The PutRequestBuilder
     */
    public static Rx2ANRequest.PutRequestBuilder put(String url) {
        return new Rx2ANRequest.PutRequestBuilder(url);
    }

    /**
     * Method to make DELETE request
     *
     * @param url The url on which request is to be made
     * @return The DeleteRequestBuilder
     */
    public static Rx2ANRequest.DeleteRequestBuilder delete(String url) {
        return new Rx2ANRequest.DeleteRequestBuilder(url);
    }

    /**
     * Method to make PATCH request
     *
     * @param url The url on which request is to be made
     * @return The PatchRequestBuilder
     */
    public static Rx2ANRequest.PatchRequestBuilder patch(String url) {
        return new Rx2ANRequest.PatchRequestBuilder(url);
    }

    /**
     * Method to make download request
     *
     * @param url      The url on which request is to be made
     * @param dirPath  The directory path on which file is to be saved
     * @param fileName The file name with which file is to be saved
     * @return The DownloadBuilder
     */
    public static Rx2ANRequest.DownloadBuilder download(String url, String dirPath, String fileName) {
        return new Rx2ANRequest.DownloadBuilder(url, dirPath, fileName);
    }

    /**
     * Method to make upload request
     *
     * @param url The url on which request is to be made
     * @return The MultiPartBuilder
     */
    public static Rx2ANRequest.MultiPartBuilder upload(String url) {
        return new Rx2ANRequest.MultiPartBuilder(url);
    }

    /**
     * Method to make Dynamic request
     *
     * @param url    The url on which request is to be made
     * @param method The HTTP METHOD for the request
     * @return The DynamicRequestBuilder
     */
    public static Rx2ANRequest.DynamicRequestBuilder request(String url, int method) {
        return new Rx2ANRequest.DynamicRequestBuilder(url, method);
    }
}
