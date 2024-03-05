package com.dmytrobilokha.tyde.user;

import com.dmytrobilokha.tyde.user.service.UserService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;

@WebServlet(AuthenticationConstants.LOGOUT_SERVLET_PATH)
public class LogoutServlet extends HttpServlet {

    private UserService userService;

    public LogoutServlet() { }

    @Inject
    public LogoutServlet(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var rememberMeToken = Arrays.stream(req.getCookies())
                .filter(cookie -> AuthenticationConstants.REMEMBER_ME_COOKIE_NAME.equals(cookie.getName()))
                .findAny()
                .map(Cookie::getValue)
                .orElse(null);
        req.logout();
        // For some weird reason TomEE erases "remember me" cookie value early and passes null instead of its value to
        // RememberMyIdentityStore.removeLoginToken(). That is why I have to remove it here explicitly.
        if (rememberMeToken != null) {
            userService.removeToken(rememberMeToken);
        }
        var responseWriter = resp.getWriter();
        responseWriter.println("You have been logged out");
    }

}
