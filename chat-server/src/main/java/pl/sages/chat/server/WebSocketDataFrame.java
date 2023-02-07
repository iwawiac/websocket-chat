package pl.sages.chat.server;
import java.nio.ByteBuffer;

public class WebSocketDataFrame {
    public static byte[] createDataFrame(String payload) {
        byte[] payloadBytes = payload.getBytes();
        // calculates length of the payload
        int length = payloadBytes.length;
        // minimum WebSocket message length: 1 byte for opcode and 1 byte for payload length
        int frameSize = 2;
        // checks payload length and allocates frameSize accordingly
        if (length < 126) {
            frameSize += length;
        } else if (length < 65536) {
            frameSize += 2 + length;
        } else {
            frameSize += 8 + length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(frameSize);
        // puts opcode into the buffer
        buffer.put((byte) (0x80 | 1));

        // puts appropriate WebSocket message length into the buffer
        if (length < 126) {
            buffer.put((byte) (length & 0x7F));
        } else if (length < 65536) {
            buffer.put((byte) 126);
            buffer.putShort((short) (length & 0xFFFF));
        } else {
            buffer.put((byte) 127);
            buffer.putLong(length & 0xFFFFFFFFFFFFFFFFL);
        }

        buffer.put(payloadBytes);
        return buffer.array();
    }
}