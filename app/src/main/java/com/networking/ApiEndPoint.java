package com.networking;

/**
 * Created by amitshekhar on 29/03/16.
 */
public class ApiEndPoint {
    public static final String BASE_URL = "http://localhost:3000/api";
    public static final String GET_JSON_ARRAY = "/getAllUsers/{pageNumber}";
    public static final String GET_JSON_OBJECT = "/getAnUser/{userId}";
    public static final String CHECK_FOR_HEADER = "/checkForHeader";
    public static final String POST_CREATE_AN_USER = "/createAnUser";
    public static final String UPLOAD_IMAGE_URL = "http://localhost:3000/upload";


}
