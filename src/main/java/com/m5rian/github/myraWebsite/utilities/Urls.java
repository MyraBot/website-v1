package com.m5rian.github.myraWebsite.utilities;

import spark.Request;

import javax.servlet.http.HttpServletRequest;

public class Urls {
    public static String getBase(Request req) {
        HttpServletRequest request = req.raw();
        return request.getRequestURL().substring(0, request.getRequestURL().length() - request.getRequestURI().length()) + request.getContextPath();
    }
}
