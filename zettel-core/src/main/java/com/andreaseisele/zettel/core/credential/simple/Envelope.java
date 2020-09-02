package com.andreaseisele.zettel.core.credential.simple;

public class Envelope {

    private byte[] salt;

    private byte[] iv;

    private byte[] payload;

    public Envelope() {

    }

    public Envelope(byte[] salt, byte[] iv, byte[] payload) {
        this.salt = salt;
        this.iv = iv;
        this.payload = payload;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }
}
