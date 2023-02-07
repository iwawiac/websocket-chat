package pl.sages.chat.server;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;

public class EncoderUtils {


    public static String encodeHashAndBase64(Matcher match) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-1")
                .digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                        .getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

}
