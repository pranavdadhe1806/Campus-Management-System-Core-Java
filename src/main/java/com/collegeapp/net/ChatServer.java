package com.collegeapp.net;

import com.collegeapp.util.LoggerUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer implements Runnable {

    public static final int DEFAULT_PORT = 9090;

    private final int port;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private volatile boolean running;
    private ServerSocket serverSocket;

    public ChatServer() {
        this(DEFAULT_PORT);
    }

    public ChatServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        running = true;
        try (ServerSocket socket = new ServerSocket(port)) {
            serverSocket = socket;
            LoggerUtil.info("Chat server started on port " + port);
            while (running) {
                Socket clientSocket = socket.accept();
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                new Thread(handler, "chat-client-" + clientSocket.getPort()).start();
            }
        } catch (IOException e) {
            if (running) {
                LoggerUtil.error("Chat server failed", e);
            }
        } finally {
            running = false;
            clients.clear();
        }
    }

    public void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                LoggerUtil.warn("Failed to close chat server socket: " + e.getMessage());
            }
        }
    }

    private void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.send(message);
            }
        }
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;
        new ChatServer(port).run();
    }

    private final class ClientHandler implements Runnable {

        private final Socket socket;
        private PrintWriter out;
        private String displayName = "anonymous";

        private ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (Socket closeable = socket;
                    BufferedReader in = new BufferedReader(new InputStreamReader(closeable.getInputStream()));
                    PrintWriter writer = new PrintWriter(closeable.getOutputStream(), true)) {
                out = writer;
                writer.println("CONNECTED " + port);
                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("/name ")) {
                        displayName = line.substring(6).trim();
                        writer.println("NAME_OK " + displayName);
                    } else {
                        broadcast(displayName + ": " + line, this);
                    }
                }
            } catch (IOException e) {
                LoggerUtil.warn("Chat client disconnected: " + e.getMessage());
            } finally {
                clients.remove(this);
            }
        }

        private void send(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }
}
