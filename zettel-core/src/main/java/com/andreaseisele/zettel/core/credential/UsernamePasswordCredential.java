package com.andreaseisele.zettel.core.credential;

public class UsernamePasswordCredential implements Credential {

    private char[] username;

    private char[] password;

    @Override
    public Type getType() {
        return Type.USERNAME_PASSWORD;
    }

    public char[] getUsername() {
        return username;
    }

    public void setUsername(char[] username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

}
