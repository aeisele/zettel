package com.andreaseisele.zettel.core.credential.simple;

import com.andreaseisele.zettel.core.credential.CredentialStore;
import com.andreaseisele.zettel.core.credential.CredentialStoreException;
import com.andreaseisele.zettel.core.credential.data.Credential;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleCredentialStore implements CredentialStore {

    private static final Logger logger = LogManager.getLogger();

    private static final String SECRET_KEY_DERIVE_ALGO = "PBKDF2WithHmacSHA512";
    private static final String SECRET_KEY_ALGO = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5Padding";
    private static final int ITERATION_COUNT = 40_000;
    private static final int HASH_SIZE = 256;
    private static final int SALT_SIZE = 128;

    private final Map<String, Credential> credentials;

    private final char[] masterPassword;

    private SimpleCredentialStore(Map<String, Credential> credentials, char[] masterPassword) {
        this.credentials = credentials;
        this.masterPassword = masterPassword;
    }

    public static SimpleCredentialStore createNew(char[] masterPassword) {
        return new SimpleCredentialStore(new HashMap<>(), masterPassword);
    }

    public static SimpleCredentialStore loadFromFile(Path inputFile, char[] masterPassword) {

        ObjectMapper objectMapper = new ObjectMapper();
        MapType credentialMapType = registerMapType(objectMapper);

        try (Reader reader = Files.newBufferedReader(inputFile)) {
            Envelope envelope = objectMapper.readValue(reader, Envelope.class);
            byte[] salt = envelope.getSalt();
            byte[] iv = envelope.getIv();
            byte[] payload = envelope.getPayload();

            SecretKeySpec secretKey = deriveSecretKey(masterPassword, salt);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(payload);

            Map<String, Credential> credentials = objectMapper.readValue(decrypted, credentialMapType);

            return new SimpleCredentialStore(credentials, masterPassword);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | NoSuchPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException
                | BadPaddingException e) {
            String message = "error loading simple credential store from file " + inputFile + ": " + e.getMessage();
            logger.error(message);
            throw new CredentialStoreException(message, e);
        }
    }

    public void saveToFile(Path outputFile) {

        ObjectMapper objectMapper = new ObjectMapper();
        MapType credentialMapType = registerMapType(objectMapper);

        try (Writer writer = Files.newBufferedWriter(outputFile)) {

            byte[] unEncrypted = objectMapper.writerFor(credentialMapType).writeValueAsBytes(credentials);

            logger.debug("unencrypted: {}", () -> new String(unEncrypted));

            byte[] salt = generateSalt();
            SecretKeySpec secretKey = deriveSecretKey(masterPassword, salt);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            IvParameterSpec ivParameters = cipher.getParameters().getParameterSpec(IvParameterSpec.class);
            byte[] iv = ivParameters.getIV();
            byte[] encrypted = cipher.doFinal(unEncrypted);

            Envelope envelope = new Envelope(salt, iv, encrypted);

            objectMapper.writeValue(writer, envelope);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException
                | InvalidParameterSpecException | IllegalBlockSizeException | BadPaddingException | IOException e) {
            String message = "error saving simple credential store to file " + outputFile + ": " + e.getMessage();
            logger.error(e);
            throw new CredentialStoreException(message, e);
        }
    }

    private static byte[] generateSalt() {
        byte[] salt = new byte[SALT_SIZE];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    private static SecretKeySpec deriveSecretKey(char[] masterPassword, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(SECRET_KEY_DERIVE_ALGO);
        PBEKeySpec keySpec = new PBEKeySpec(masterPassword, salt, ITERATION_COUNT, HASH_SIZE);
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        return new SecretKeySpec(secretKey.getEncoded(), SECRET_KEY_ALGO);
    }

    private static MapType registerMapType(ObjectMapper objectMapper) {
        return objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Credential.class);
    }

    @Override
    public <T extends Credential> Optional<T> get(String key, Class<T> type) {
        return Optional.ofNullable(credentials.get(key))
                .filter(c -> type.isAssignableFrom(c.getClass()))
                .map(type::cast);
    }

    @Override
    public void put(String key, Credential credential) {
        credentials.put(key, credential);
    }

    @Override
    public void delete(String key) {
        credentials.remove(key);
    }

}
