package pl.sages.chat.server;

public class DecoderUtils {

    public static byte[] decodeBytes(byte[] encodedMessage, byte[] decodingKey) {
        byte[] decoded = new byte[encodedMessage.length];
        for (int i = 0; i < encodedMessage.length; i++) {
            decoded[i] = (byte) (encodedMessage[i] ^ decodingKey[i & 0x3]);
        }
        return decoded;
    }
}
