package com.m5rian.github.myraWebsite.Pages;

import com.m5rian.github.myraWebsite.utilities.Reader;
import spark.Request;
import spark.Response;

public class Support {

    public static Object onGet(Request req, Response res) {
        return Reader.readFile("/pages/support.html");
    }

}
