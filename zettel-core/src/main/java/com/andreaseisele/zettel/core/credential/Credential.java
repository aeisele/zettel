package com.andreaseisele.zettel.core.credential;

public interface Credential {

    Type getType();

    public enum Type {
        USERNAME_PASSWORD
    }

}
