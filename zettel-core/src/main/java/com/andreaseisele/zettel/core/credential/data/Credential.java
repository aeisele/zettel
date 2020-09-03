package com.andreaseisele.zettel.core.credential.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UsernamePasswordCredential.class, name="UsernamePassword")
})
public interface Credential {


}
