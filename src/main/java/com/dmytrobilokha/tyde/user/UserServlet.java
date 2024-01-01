package com.dmytrobilokha.tyde.user;

import jakarta.annotation.security.DeclareRoles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/usertest")
@DeclareRoles({"foo", "bar", "baz"})
public class UserServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var responseWriter = resp.getWriter();
        var userPrincipal = req.getUserPrincipal();
        String userName = userPrincipal == null ? null : userPrincipal.getName();
        responseWriter.println("Current user: " + userName);
        responseWriter.println("User has role 'foo': " + req.isUserInRole("foo"));
        responseWriter.println("User has role 'bar': " + req.isUserInRole("bar"));
        responseWriter.println("User has role 'baz': " + req.isUserInRole("baz"));
    }

}
