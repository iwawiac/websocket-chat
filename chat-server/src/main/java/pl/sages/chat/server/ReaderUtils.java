package pl.sages.chat.server;

import java.io.IOException;
import java.io.InputStream;

public class ReaderUtils {

    public static byte[] readResponse(InputStream in) throws IOException {
        int opcode = in.read();

        System.out.println("opcode: " + opcode);

        int messageLength = in.read() - 128;
        messageLength = switch (messageLength) {
            case 127 -> {
                byte[] lengthBytes = in.readNBytes(8);
                long length = 0;
                for (int i = 0; i < 8; i++) {
                    length |= ((long) lengthBytes[i] & 0xff) << ((7 - i) * 8);
                }
                yield (int) length;
            }

            case 126 -> {
                int length1 = in.read();
                int length2 = in.read();
                messageLength = (length1 << 8) | length2;
                yield messageLength;
            }
            default -> messageLength;
        };

        byte[] decodingKey = new byte[]{(byte) in.read(), (byte) in.read(), (byte) in.read(), (byte) in.read()};
        byte[] encodedMessage = in.readNBytes(messageLength);
        return DecoderUtils.decodeBytes(encodedMessage, decodingKey);
    }

}
