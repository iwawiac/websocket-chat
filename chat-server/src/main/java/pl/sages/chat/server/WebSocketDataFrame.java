package pl.sages.chat.server;
import java.nio.ByteBuffer;

public class WebSocketDataFrame {
    public static byte[] createDataFrame(byte [] payloadBytes, boolean isBinary) {
        int length = payloadBytes.length;
        // minimum WebSocket message length: 1 byte for opcode and 1 byte for payload length
        int frameSize = 2;
        // allocates frameSize accordingly
        if (length < 126) {
            frameSize += length;
        } else if (length < 65536) {
            frameSize += 2 + length;
        } else {
            frameSize += 8 + length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(frameSize);

        // puts opcode and fin bits into the buffer
        if(!isBinary) {
            buffer.put((byte) (0x80 | 1));
        } else {
            buffer.put((byte) (0x80 | 2));
        }

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