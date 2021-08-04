package com.m5rian.github.myraWebsite.discord;

import com.m5rian.github.myraWebsite.LoginRedirection;
import com.m5rian.github.myraWebsite.utilities.Utils;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.io.IOException;

public class OAuth2 {
    private final static String TOKEN_URL = "https://discord.com/api/v8/oauth2/token";

    /**
     * Make a post request to get an access token.
     * The access token will have the following scopes:
     * - identify guilds
     *
     * @param code The current code.
     * @param req  A {@link Request}.
     * @param res  A {@link Response}.
     * @return Returns an access token.
     */
    public static String requestToken(String code, Request req, Response res) {
        System.out.println("Requesting new access token");
        final RequestBody body = new FormBody.Builder()
                .add("client_id", "718444709445632122")
                .add("client_secret", "M4jvh71GpUtJPsIm1ICNSh_j9XVFE15y")
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("redirect_uri", req.url())
                .add("scope", "identify%20guilds")
                .build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(TOKEN_URL)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        String accessToken = null;
        try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
            JSONObject json = new JSONObject(response.body().string()); // Create JSONObject

            // Error
            if (json.has("error_description")) {
                // Invalid code
                if (json.getString("error").equals("invalid_request")) {
                    System.out.println("Invalid code in request");
                    res.removeCookie("session");
                    res.removeCookie("code");

                    res.cookie("/", "fromPage", req.url(), Integer.MAX_VALUE, false, false); // Save current url
                    res.redirect("/login"); // Login
                    return null;
                }

                System.out.println(json);
                return null;
            }

            accessToken = json.getString("access_token"); // Get access token
            res.cookie("/", "oAuth2Token", accessToken, Integer.MAX_VALUE, false, false); // Save access token

            String refreshToken = json.getString("refresh_token"); // Get refresh token
            res.cookie("/", "refreshToken", refreshToken, Integer.MAX_VALUE, false, false); // Save refresh token
        } catch (IOException e) {
            e.printStackTrace();
        }

        return accessToken;
    }

    public static void refreshToken(Request req, Response res) throws LoginRedirection {
        // A refresh token doesn't exist
        //if (req.cookie("refreshToken") == null) {
        // This would end up in an infite loop because when we have no refresh token which is required here we end up here evert time after logging in
        //    throw new LoginRedirection(req, res);
        //}

        final String refreshToken = req.cookie("refreshToken");
        System.out.println("Refresh token: " + refreshToken);

        final RequestBody body = new FormBody.Builder()
                .add("client_id", "718444709445632122")
                .add("client_secret", "M4jvh71GpUtJPsIm1ICNSh_j9XVFE15y")
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .build();

        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(TOKEN_URL)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(body)
                .build();

        try (okhttp3.Response response = Utils.HTTP_CLIENT.newCall(request).execute()) {
            final String string = response.body().string();

            // Refresh token expired
            // I'm not sure if this error is only thrown when the refresh token expires.
            if (string.equals(Utils.ERROR_INVALID_GRANT)) {
                throw new LoginRedirection(req, res); // Redirect to login page
            }

            System.out.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get an active oAuth2 token.
     *
     * @param req
     * @param res
     * @return Returns a token as a {@link String}
     */
    public static String getToken(Request req, Response res) throws LoginRedirection {
        // There is no token yet
        if (req.cookie("oAuth2Token") == null) {
            // No active session
            if (req.cookie("session") == null || req.cookie("code") == null) {
                System.out.println("Redirecting to login page");
                throw new LoginRedirection(req, res); // Redirect
            }
            // Active session
            else {
                System.out.println("Requesting access token from code");
                final String code = req.cookie("code"); // Get code from oAuth2 process
                return requestToken(code, req, res); // Return requested token
            }

        }
        // There is an active cookie
        else return req.cookie("oAuth2Token");
    }

}