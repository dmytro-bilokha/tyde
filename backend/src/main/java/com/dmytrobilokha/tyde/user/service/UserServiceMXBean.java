package com.dmytrobilokha.tyde.user.service;

import com.dmytrobilokha.tyde.infra.exception.InternalApplicationException;

public interface UserServiceMXBean {

    void createUser(String login, String password) throws InternalApplicationException;

}
