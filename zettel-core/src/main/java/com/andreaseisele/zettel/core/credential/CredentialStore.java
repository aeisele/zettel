package com.andreaseisele.zettel.core.credential;

import com.andreaseisele.zettel.core.credential.data.Credential;

import java.util.Optional;

public interface CredentialStore {

    <T extends Credential> Optional<T> get(String key, Class<T> type);

    void put(String key, Credential credential);

    void delete(String key);

}
