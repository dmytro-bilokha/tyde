package com.dmytrobilokha.tyde.user;

public final class AuthenticationConstants {

    public static final String REMEMBER_ME_COOKIE_NAME = "JREMEMBERMEID";
    public static final int REMEMBER_ME_COOKIE_LIFE = 60 * 60 * 24 * 24; // 24 days.
    public static final String LOGOUT_SERVLET_PATH = "/logout";
    public static final String DEFAULT_AFTER_LOGOUT_PATH = "/";

    private AuthenticationConstants() {
        // No instance, constants class
    }

}
