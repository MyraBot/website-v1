package com.m5rian.github.myraWebsite.Pages;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.utilities.Reader;
import spark.Request;
import spark.Response;

public class TermsOfService {

    public static Object onGet(Request req, Response res) {
        try {
            return new Reader(req, res).read("termsOfService/terms-of-service"); // Read raw html file
        } catch (LoginRedirection redirection) {
            return null;
        }
    }

}
