package com.m5rian.github.myraWebsite;

import spark.Request;
import spark.Response;

public class LoginRedirection extends Exception {

    public LoginRedirection(Request req, Response res) {
        res.cookie("/", "fromPage", req.url(), Integer.MAX_VALUE, false, false); // Save current url
        res.redirect("/login"); // Redirect to login page
    }

}
