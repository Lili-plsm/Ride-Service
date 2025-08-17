package ru.site.security.model;

public class JwtRequest {
    private String login;
    private String password;

    JwtRequest() {}

    public String getLogin() { return login; }
    public String getPassword() { return password; }

    public void getLogin(String login) { this.login = login; }

    public void getPassword(String password) { this.password = password; }

    public void setLogin(String login) { this.login = login; }

    public void setPassword(String password) { this.password = password; }
}