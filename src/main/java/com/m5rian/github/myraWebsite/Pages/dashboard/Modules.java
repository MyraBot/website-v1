package com.m5rian.github.myraWebsite.Pages.dashboard;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.utilities.Reader;
import spark.Request;
import spark.Response;


public class Modules {

    public static Object onGet(Request req, Response res) {
        try {
            final String read = new Reader(req, res)
                    .isOnDashboard()
                    .read("/dashboard/modules/modules");

            return read;
        } catch (LoginRedirection loginRedirection) {
            return null;
        }
    }

}
