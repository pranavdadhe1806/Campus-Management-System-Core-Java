package com.collegeapp.net;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.function.Consumer;

public class ChatClient implements Closeable {

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private Thread listenerThread;

    public ChatClient(String host, int port, String displayName) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        if (displayName != null && !displayName.isBlank()) {
            out.println("/name " + displayName.trim());
        }
    }

    public void startListening(Consumer<String> messageConsumer) {
        Objects.requireNonNull(messageConsumer, "messageConsumer");
        listenerThread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    messageConsumer.accept(line);
                }
            } catch (IOException e) {
                messageConsumer.accept("Disconnected: " + e.getMessage());
            }
        }, "chat-client-listener");
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void send(String message) {
        if (message != null && !message.isBlank()) {
            out.println(message);
        }
    }

    @Override
    public void close() throws IOException {
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
        socket.close();
    }
}
