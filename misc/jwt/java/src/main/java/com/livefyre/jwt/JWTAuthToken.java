package com.livefyre.jwt;

import java.security.InvalidKeyException;
import java.security.SignatureException;

import com.google.gson.JsonObject;

import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;

public class JWTAuthToken {
    private JsonToken mToken;

    public JWTAuthToken(String networkName, String networkSecret,
            String userId, String displayName, double expires)
            throws InvalidKeyException {
        HmacSHA256Signer signer = new HmacSHA256Signer(null, null,
                networkSecret.getBytes());
        mToken = new JsonToken(signer);
        JsonObject tokenJSON = mToken.getPayloadAsJsonObject();
        tokenJSON.addProperty("domain", networkName);
        tokenJSON.addProperty("user_id", userId);
        tokenJSON.addProperty("display_name", displayName);
        tokenJSON.addProperty("expires", expires);
    }

    public String toString() {
        try {
            return mToken.serializeAndSign();
        } catch (SignatureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "error";
        }
    }
}
