package com.dmytrobilokha.tyde.user;

import java.time.LocalDateTime;

public record AuthenticationToken(long id, long userId, String login, String passwordHash, LocalDateTime validTo) { }
