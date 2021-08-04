package com.m5rian.github.myraWebsite.Pages;

import com.m5rian.github.myraWebsite.discord.OAuth2;
import com.m5rian.github.myraWebsite.utilities.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.Random;

public class Login {
    private final static Logger logger = LoggerFactory.getLogger(Login.class);
    private final static String baseAuthUrl = "https://discord.com/oauth2/authorize?"
            + "client_id=718444709445632122"
            + "&redirect_uri={redirectUrl}"
            + "&response_type=code"
            + "&scope=identify%20guilds"
            + "&state={session}";

    public static Object login(Request req, Response res) {
        logger.info(String.valueOf(req.cookies()));
        // Get cookies
        final String sessionCookie = req.cookie("session"); // Get session
        final String codeCookie = req.cookie("code"); // Get code

        // Get query parameters
        final String sessionParam = req.queryParams("state"); // Get session from query
        final String codeParam = req.queryParams("code"); // Get code from query

        // Error (Mostly when user denies access)
        if (req.queryParams("error") != null) {
            res.removeCookie("fromPage"); // Remove page cookie
            res.removeCookie("session"); // Remove session

            res.redirect("/"); // Redirect user to home page

            return null;
        }

        // No active session
        if (sessionParam == null || sessionCookie == null) {
            logger.info("session is null");
            redirect(req, res); // Redirect to discord
        }
        // Wrong session
        else if (!sessionCookie.equals(sessionParam)) {
            logger.info("wrong session");
            redirect(req, res); // Redirect to discord
        }

        // User has a correct session session
        else {
            logger.info("User has a correct session");
            res.cookie("/", "code", codeParam, Integer.MAX_VALUE, true, false); // Add code as cookie

            // No access token saved yet
            if (req.cookie("oAuth2Token") == null) {
                logger.info("Requesting new oAuth2 token");
                OAuth2.requestToken(codeParam, req, res); // Request access token
            }

            final String fromPage = req.cookie("fromPage"); // Get url of the page the user was before redirecting
            res.removeCookie("fromPage"); // Remove cookie
            logger.info("USER WILL BE REDIRECTED TO " + fromPage);
            res.redirect(fromPage); // Redirect user to the page he was  before
        }

        return Reader.readFile("/pages/login/login.html"); // Return html file
    }

    /**
     * Redirects you to discord, to authorize yourself.
     */
    private static void redirect(Request req, Response res) {
        final String redirectUrl = req.url(); // Set current url as redirect url

        res.removeCookie("session"); // Remove old cookie
        res.removeCookie("code");
        res.removeCookie("oAuth2Token");
        res.removeCookie("refreshToken");
        res.removeCookie("id");
        res.removeCookie("userAsTag");
        res.removeCookie("userAvatar");



        final String session = generateCode(); // Create new session
        res.cookie("/", "session", session, Integer.MAX_VALUE, false, false); // Add session cookie
        logger.info("Created new session");

        final String authUrl = baseAuthUrl
                .replace("{redirectUrl}", redirectUrl)
                .replace("{session}", session);

        res.redirect(authUrl); // Redirect to discord
    }

    private static String generateCode() {
        final int length = 15; // Code length
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // Available characters

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // Append random character
            result.append(characters.toCharArray()[new Random().nextInt(characters.length())]);
        }

        return result.toString();
    }

}
