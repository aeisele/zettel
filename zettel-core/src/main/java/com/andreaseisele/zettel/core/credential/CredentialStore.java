package com.andreaseisele.zettel.core.credential;

import java.util.Optional;

public interface CredentialStore {

    Optional<Credential> get(String key);

    void put(String key, Credential credential);

}
