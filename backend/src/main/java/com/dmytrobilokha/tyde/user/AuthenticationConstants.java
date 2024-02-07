package com.dmytrobilokha.tyde.user;

public final class AuthenticationConstants {

    static final String REMEMBER_ME_COOKIE_NAME = "JREMEMBERMEID";
    static final int REMEMBER_ME_COOKIE_LIFE = 60 * 60 * 24 * 24; // 24 days.
    static final String LOGOUT_SERVLET_PATH = "/logout";
    static final String LOGIN_SERVLET_PATH = "/login";
    static final String LOGIN_PAGE_PATH = "/login.xhtml";
    static final String LOGIN_ERROR_PAGE_PATH = "/login-error.xhtml";
    static final String DEFAULT_AFTER_LOGIN_PATH = "/usertest";

    private AuthenticationConstants() {
        // No instance, constants class
    }

}
