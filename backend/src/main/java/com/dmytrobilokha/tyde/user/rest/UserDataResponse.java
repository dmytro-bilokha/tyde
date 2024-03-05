package com.dmytrobilokha.tyde.user.rest;

public class UserDataResponse {

    private final String login;

    public UserDataResponse(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return "UserDataResponse{"
                + "login='" + login + '\''
                + '}';
    }

}
