package com.renekon.shared.connection;

import com.renekon.shared.connection.buffer.ChatMessageBuffer;
import com.renekon.shared.connection.buffer.MessageBuffer;
import com.renekon.shared.message.NioMessage;
import com.renekon.shared.message.Message;
import com.renekon.shared.message.NioMessageFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NioSocketConnection extends Connection {
    private enum Mode {READ, WRITE}

    private static final int INITIAL_READ_BUFFER_CAPACITY = 128;
    private static final int RESIZE_FACTOR = 2;

    private final ConcurrentLinkedDeque<ByteBuffer> writeBuffers;
    private ByteBuffer readBuffer;
    private MessageBuffer messageBuffer;

    private final Selector selector;
    public final SocketChannel channel;

    private final ModeChangeRequestQueue modeChangeRequestQueue;

    private Mode mode;

    public NioSocketConnection(InetSocketAddress address) throws IOException {
        selector = Selector.open();
        channel = SocketChannel.open();
        channel.connect(address);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, this);
        modeChangeRequestQueue = new ModeChangeRequestQueue(selector);
        this.writeBuffers = new ConcurrentLinkedDeque<>();
        this.readBuffer = ByteBuffer.allocate(INITIAL_READ_BUFFER_CAPACITY);
        this.mode = Mode.READ;
        messageFactory = new NioMessageFactory();
        messageBuffer = new ChatMessageBuffer();
    }

    public NioSocketConnection(Selector selector, SocketChannel channel,
                               ModeChangeRequestQueue modeChangeRequestQueue) throws IOException {

        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, this);

        this.selector = selector;
        this.channel = channel;
        this.modeChangeRequestQueue = modeChangeRequestQueue;

        this.writeBuffers = new ConcurrentLinkedDeque<>();
        this.readBuffer = ByteBuffer.allocate(INITIAL_READ_BUFFER_CAPACITY);
        messageFactory = new NioMessageFactory();
        messageBuffer = new ChatMessageBuffer();
        this.mode = Mode.READ;
    }

    @Override
    public boolean canRead() {
        try {
            selector.select();
        } catch (IOException e) {
            return false;
        }

        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        if (iterator.hasNext()) {
            SelectionKey key = iterator.next();
            iterator.remove();
            return key.isValid() && key.isReadable();
        }
        return false;
    }

    @Override
    public void write(Message src) {
        byte[] bytes = ((NioMessage) src).getBytes();
        writeBuffers.add(ByteBuffer.wrap(bytes));
        if (this.mode == Mode.READ) {
            this.mode = Mode.WRITE;
            modeChangeRequestQueue.add(new ModeChangeRequest(this, SelectionKey.OP_WRITE));
            this.selector.wakeup();
        }
    }

    @Override
    public List<Message> readMessages() {
        readBuffer.flip();
        byte[] ret = new byte[readBuffer.limit()];
        readBuffer.get(ret);
        readBuffer.clear();
        messageBuffer.put(ret);
        byte[] messageData = messageBuffer.getNextMessage();
        List<Message> messages = new ArrayList<>();
        while (messageData != null) {
            Message message = ((NioMessageFactory) messageFactory).createFromBytes(messageData);
            messages.add(message);
            messageData = messageBuffer.getNextMessage();
        }
        return messages;
    }

    @Override
    public void writeToChannel() throws IOException {
        while (!writeBuffers.isEmpty()) {
            ByteBuffer head = writeBuffers.peek();
            channel.write(head);
            if (head.hasRemaining()) {
                break;
            }
            writeBuffers.remove();
        }
        if (writeBuffers.isEmpty()) {
            this.mode = Mode.READ;
            modeChangeRequestQueue.add(new ModeChangeRequest(this, SelectionKey.OP_READ));
        }
    }

    @Override
    public void readFromChannel() throws IOException {
        channel.read(readBuffer);
        if (readBuffer.position() == readBuffer.capacity()) {
            readBuffer.rewind();
            ByteBuffer newBuffer = ByteBuffer.allocate(RESIZE_FACTOR * readBuffer.capacity());
            newBuffer.put(readBuffer);
            readBuffer = newBuffer;
        }
        modeChangeRequestQueue.add(new ModeChangeRequest(this, SelectionKey.OP_WRITE));
        MessageBuffer messageBuffer = this.messageBuffer;

    }

    public boolean nothingToWrite() {
        return writeBuffers.isEmpty();
    }
}
