package com.fighton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    public Thread thread = null;

    Statement statement;
    Connection connection;

    // Collection of Server threads
    static ConcurrentHashMap<String, ServerThread> clients = new ConcurrentHashMap<>();
    ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();

    // Setup server
    public Server(int port) {
        try {
            // Start server
            serverSocket = new ServerSocket(port);
            System.out.println("Starting server at port " + port + "...");

            // Connect to db
            Class.forName(Constants.JDBC_DRIVER);

            connection = DriverManager.getConnection(
                    Constants.DB_URL,
                    Constants.USER,
                    Constants.PASSWORD
            );

            statement = connection.createStatement();

            System.out.println("Database connected....");

            start();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Connect clients
    public void run() {
        while (thread != null) {
            try {
                System.out.println("Ready for clients....");
                addClient(serverSocket.accept());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    * TO DO----
    * handles login, registration and so on....
    * */
    public synchronized void handle(String id, Message msg) {
        switch (msg.type) {
            case Message.PLAY:
                play(id, msg);
                break;
            case Message.QUEE:
                addToQueue(id, msg);
                break;
            case Message.LOGIN:
                loginplayer(id, msg);
                break;
            case Message.BYE:
                logout(id);
                break;
            case Message.REGISTER:
                register(id, msg);
                break;
            case Message.RECONNECT:
                reconnect(id, msg);
                break;
        }
    }

    private void reconnect(String id, Message msg) {
        sendMessage(msg.recipient, msg);
    }

    private void play(String id, Message msg) {
        Message response = new Message(
                Message.PLAY,
                "server",
                msg.contents,
                msg.recipient
        );

        sendMessage(response.recipient, response);
    }

    private void sendMessage(String id, Message msg) {
        ServerThread st = clients.get(id);
        st.send(msg);
    }

    // adds player to queue
    private void addToQueue(String id, Message msg) {

        if (msg.contents.equals("quit")) {
            queue.remove(id);
            System.out.println(queue.size());
            Message response = new Message(Message.QUEE, "server", "removed", id);

            sendMessage(id, response);
        } else if (queue.size() >= 1 && !queue.contains(id) ) {
            String player2 = (String) queue.poll();
            Message p1Msg = new Message(Message.QUEE, "server", "p2#" + player2, id);
            Message p2Msg = new Message(Message.QUEE, "server", "p1#" + id, player2);

            sendMessage(id, p1Msg);
            sendMessage(player2, p2Msg);
        } else if (!queue.contains(id)) {
            queue.add(id);
            Message newMsg = new Message(Message.QUEE, "server", "added", id);
            sendMessage(id, newMsg);
        }
    }

    // registers player
    private void register(String id, Message msg) {
        String[] credentials = msg.contents.split("#");
        String q = "INSERT INTO users (username, password) VALUES ('" + credentials[0] + "','"
                + credentials[1] + "');";
        System.out.println(q);

        Message response = new Message(Message.REGISTER,
                "server", "Account Registered", id);

        try {
            statement.executeUpdate(q);
        } catch (SQLException e) {
            e.printStackTrace();
            response.contents = "Registration failed";
        }

        sendMessage(id, response);
    }

    // logs out player
    private void logout(String id) {
        ServerThread st = clients.get(id);

        Message response = new Message(Message.BYE,
                "server", "Logout Successful", id);
        queue.remove(id);

        st.send(response);
        st.close();
        clients.remove(id);
    }

    // logs in player
    private void loginplayer(String id, Message msg) {
        String[] credentials = msg.contents.split("#");
        String q = "SELECT * FROM users WHERE username='" + credentials[0]
                + "' AND password='" + credentials[1] + "';";

        Message response = new Message(Message.LOGIN,
                "server", "Login Successful", id);

        try {
            ResultSet rs = statement.executeQuery(q);

            rs.first();
            System.out.println(rs.getString("id"));

            for (String key: clients.keySet()) {
                System.out.println("-----" + key);
            }

            String newId = rs.getString("id");

            if (!rs.getString("username").equals(credentials[0])) {
                response.contents = "Login Failed";
            } else if (!clients.containsKey(newId)) {
                response.recipient = newId;
                ServerThread st = clients.get(id);
                clients.put(newId, st);
                clients.remove(id);
                st.user.setId(newId);
            } else if (clients.containsKey(newId)) {
                System.out.println("CONTAINS");
                ServerThread st = clients.get(newId);
                if (st.user != null && st.user.inGame && st.disconected) {
                    ServerThread curr = clients.get(id);
                    clients.remove(newId);
                    clients.put(newId, curr);
                    clients.remove(id);
                    curr.update(st);
                    // TO DO Reconnecting msg
                } else {
                    response.contents = "User already logged in.";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.contents = "Wrong username or password.";
        }

        sendMessage(response.recipient, response);
    }

    // Creates new thread for each accepted client
    private void addClient(Socket socket) {
        System.out.println("Client accepted: " + socket);

        // Create temp id, until login/register
        String temporaryId = UUID.randomUUID().toString();

        // Setup thread
        ServerThread cl = new ServerThread(this, socket, temporaryId);
         try {
             cl.open();
             cl.start();
             clients.put(temporaryId, cl);
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    // Start thread
    private void start() {
        if (thread == null) {
            thread = new Thread(this);
        }
        thread.start();
    }

    public static void main(String args[]) {
        Server server = new Server(8000);
    }
}
