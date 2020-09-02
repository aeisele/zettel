package com.andreaseisele.zettel.core.credential.simple;

import com.andreaseisele.zettel.core.credential.Credential;
import com.andreaseisele.zettel.core.credential.CredentialStore;
import com.andreaseisele.zettel.core.credential.CredentialStoreException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimpleCredentialStore implements CredentialStore {

    private static final Logger logger = LogManager.getLogger();

    private static final String SECRET_KEY_DERIVE_ALGO = "PBKDF2WithHmacSHA512";
    private static final String SECRET_KEY_ALGO = "AES";
    private static final String CIPHER = "AES/CBC/PKCS5Padding";
    private static final int ITERATION_COUNT = 40_000;
    private static final int HASH_SIZE = 512;
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
        Base64.Decoder base64Decoder = Base64.getDecoder();

        ObjectMapper objectMapper = new ObjectMapper();
        MapType credentialMapType = objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Credential.class);

        try {
            Envelope envelope = objectMapper.readValue(inputFile.toFile(), Envelope.class);
            byte[] salt = base64Decoder.decode(envelope.getSalt());
            byte[] iv = base64Decoder.decode(envelope.getIv());
            byte[] payload = base64Decoder.decode(envelope.getPayload());

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
        Base64.Encoder base64Encoder = Base64.getEncoder();

        ObjectMapper objectMapper = new ObjectMapper();

        try {

            byte[] unEncrypted = objectMapper.writeValueAsBytes(credentials);

            byte[] salt = generateSalt();
            SecretKeySpec secretKey = deriveSecretKey(masterPassword, salt);

            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            IvParameterSpec ivParameters = cipher.getParameters().getParameterSpec(IvParameterSpec.class);
            byte[] iv = base64Encoder.encode(ivParameters.getIV());
            byte[] encrypted = cipher.doFinal(unEncrypted);

            byte[] payload = base64Encoder.encode(encrypted);
            byte[] saltEncoded = base64Encoder.encode(salt);

            Envelope envelope = new Envelope(saltEncoded, iv, payload);
            objectMapper.writeValue(outputFile.toFile(), envelope);

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

    @Override
    public Optional<Credential> get(String key) {
        return Optional.ofNullable(credentials.get(key));
    }

    @Override
    public void put(String key, Credential credential) {
        credentials.put(key, credential);
    }

}
