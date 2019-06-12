package com.fighton;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerThread extends Thread{
    User user = null;
    boolean disconected;

    private Server server;
    Socket socket;

    DataOutputStream out;
    DataInputStream in;

    ConcurrentLinkedQueue moves;
    ConcurrentLinkedQueue notPlayed;

    StringBuilder stringBuilder;
    String oponent;

    public ServerThread(Server server, Socket socket, String temporaryId) {
        super();
        disconected = false;
        this.server = server;
        this.socket = socket;
        this.user = new User(temporaryId);

        moves = new ConcurrentLinkedQueue();
        notPlayed = new ConcurrentLinkedQueue();
        stringBuilder = new StringBuilder();
    }

    public void open() throws IOException {
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        boolean running = true;
        while (running) {
            try {
                String data = in.readUTF();
                System.out.println(data);
                Message msg = new Message(data);
//                if (msg.type.equals(Message.PLAY)) {
//                    moves.add(msg.contents);
//                }
                server.handle(user.getId(), msg);
            } catch (Exception e) {
                if (user.inGame) {
                    disconected = true;
                } else {
                    Server.clients.remove(user.getId());
                    close();
                    running = false;
                }
            }
        }
    }

    public void close() {
        try {
            if (socket != null)    socket.close();
            if (out != null)  out.close();
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(Message msg) {
        if (msg != null) {
            try {
                if (msg.type.equals(Message.DID)) {
                    notPlayed.poll();
                } else {
                    if (msg.type.equals(Message.PLAY)) {
                        moves.add(msg.contents);
                        notPlayed.add(msg.contents);
                        if (notPlayed.size() >= 5) {
                            stringBuilder = new StringBuilder();
                            msg.type = Message.UPDATE;
                            for (Object item: notPlayed) {
                                notPlayed.poll();
                                stringBuilder.append((String)item);
                                stringBuilder.append("#");
                            }
                            msg.contents = stringBuilder.toString();
                        }
                    } else if (msg.type.equals(Message.QUEE) && (msg.contents.contains("p2") || msg.contents.contains("p1"))) {
                        oponent = msg.contents;
                        System.out.println(oponent);
                        user.inGame = true;
                    }
                    System.out.println(msg.getMessageString());
                    out.writeUTF(msg.getMessageString());
                    out.flush();

                    if (msg.type.equals(Message.END)) {
                        moves = new ConcurrentLinkedQueue();
                        notPlayed = new ConcurrentLinkedQueue();
                        oponent = "";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(ServerThread st) {
        oponent = st.oponent;
        user.setId(st.user.getId());
        System.out.println("---------Reeee-----");
        Message response = new Message(Message.QUEE, "server", oponent, user.getId());
        send(response);
        st.close();
        response.type = Message.RECONNECT;
        response.recipient = (oponent.split("#"))[1];
        response.contents = "Request Reconnect";
        server.handle(user.getId(), response);
    }
}
