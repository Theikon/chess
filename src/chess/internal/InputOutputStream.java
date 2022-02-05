package chess.internal;

import java.io.InputStream;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class InputOutputStream extends InputStream {

    private final BlockingDeque<Byte> queue;

    public InputOutputStream() {
        this.queue = new LinkedBlockingDeque<Byte>();
    }

    public void insert(final byte[] buffer) {
        for (final byte b : buffer) {
            this.queue.add(b);
        }
    }

    public void insert(final byte[] buffer,
                                   final int offset,
                                   final int length) {
        for (int i = 0; i < length; ++i) {
            final byte b = buffer[offset + i];
            this.queue.add(b);
        }
    }

    @Override
    public synchronized int available() {
        return this.queue.size();
    }

    @Override
    public int read() {
        try {
            return (int) this.queue.take();
        } catch (final InterruptedException ex) {
            return -1;
        }
    }

    @Override
    public int read(final byte[] buffer,
                    final int offset,
                    final int length) {
        if (buffer.length == 0 || length == 0) {
            return 0;
        } else if (offset < 0 || length < 0 || buffer.length - offset < length) {
            throw new IllegalArgumentException();
        }
        final int available = this.available();
        if (available == 0) {
            buffer[0] = (byte) this.read();
            return 1;
        } else {
            final int minimum = Math.min(available, length);
            for (int i = 0; i < minimum; ++i) {
                buffer[i] = (byte) this.read();
            }
            return minimum;
        }
    }

    @Override
    public int read(final byte[] buffer) {
        if (buffer.length == 0) {
            return 0;
        }
        final int available = this.available();
        if (available == 0) {
            buffer[0] = (byte) this.read();
            return 1;
        } else {
            final int minimum = Math.min(available, buffer.length);
            for (int i = 0; i < minimum; ++i) {
                buffer[i] = (byte) this.read();
            }
            return minimum;
        }
    }
}
