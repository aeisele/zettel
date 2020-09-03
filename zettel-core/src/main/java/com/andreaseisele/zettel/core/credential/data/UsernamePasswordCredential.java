package com.andreaseisele.zettel.core.credential.data;

public class UsernamePasswordCredential implements Credential {

    private char[] username;

    private char[] password;

    public UsernamePasswordCredential() {
    }

    public UsernamePasswordCredential(char[] username, char[] password) {
        this.username = username;
        this.password = password;
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
