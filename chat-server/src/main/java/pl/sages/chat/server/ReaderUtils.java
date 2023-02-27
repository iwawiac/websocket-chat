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
        var decodedBinary = DecoderUtils.decodeBytes(encodedMessage, decodingKey);
        var decodedMessage = new String(decodedBinary);

        if (decodedMessage.contains("\u0003�Exiting")) {
            System.out.println("Client has disconnected!");
            System.exit(0);
        }

//        if (opcode == 130) {
//            System.out.println("file received");
//            File file = new File("server_file");
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            fileOutputStream.write(decodedBinary);
//            fileOutputStream.close();
//        }

        return decodedBinary;
    }

}
