package com.dmytrobilokha.tyde.user.persistence;

import java.time.LocalDateTime;

public record AuthenticationToken(long id, long userId, String login, String passwordHash, LocalDateTime validTo) { }
