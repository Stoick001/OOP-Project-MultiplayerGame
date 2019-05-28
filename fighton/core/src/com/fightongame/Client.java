package com.fightongame;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    public String address = "localhost";
    public int port = 8000;
    public Socket socket;
    public DataInputStream in;
    public DataOutputStream out;
    public FightOn game;

    public Client(FightOn game) throws IOException {
        this.game = game;
        socket = new Socket(address, port);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        boolean keepRunning = true;

        while (keepRunning) {
            try {
                String data = in.readUTF();
                System.out.println("Message: " + data);

                Message msg = new Message(data);


                System.out.println(msg.type + " " + msg.contents);

                if (msg.type.equals(Message.PLAY)) {
                    GameScreen gs = (GameScreen) game.getScreen();
                    gs.handlePlayer2(msg.contents);
                    Message response = new Message(Message.DID, msg.sender, "", "server");
                    send(response);
                } else if (msg.type.equals(Message.QUEE)) {
                    if (msg.contents.equals("removed")) {
                        final Screen screen = game.getScreen();

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(new MainMenuScreen(game));
                                screen.dispose();
                            }
                        });
                    } else if (msg.contents.equals("added")) {
                        System.out.println("Waiting for opponent");
                        final Screen screen = game.getScreen();

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(new QueueScreen(game));
                                screen.dispose();
                            }
                        });
                    } else {
                        String[] dt = msg.contents.split("#");

                        game.oponentId = dt[1];

                        if (dt[0].equals("p1")) {
                            game.player = 2;
                        } else {
                            game.player = 1;
                        }

                        final Screen screen = game.getScreen();

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(new GameScreen(game));
                                screen.dispose();
                            }
                        });
                    }
                } else if (msg.type.equals(Message.UPDATE)) {
                    GameScreen screen = (GameScreen) game.getScreen();

                    String[] moves = msg.contents.split("#");

                    System.out.println(msg.contents);

                    for (String move: moves) {
                        screen.handlePlayer2(move);
                    }

                } else if (msg.type.equals(Message.LOGIN)) {
                    if (msg.contents.equals("Login Successful")) {
                        final Screen screen = game.getScreen();

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(new MainMenuScreen(game));
                                screen.dispose();
                            }
                        });
                    } else {
                        final SignUpScreen screen = (SignUpScreen) game.getScreen();
                        screen.addFailed(msg.contents);
                    }
                } else if (msg.type.equals(Message.REGISTER)) {
                    if (msg.contents.equals("Account Registered")) {
                        final Screen screen = game.getScreen();

                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                game.setScreen(new MainMenuScreen(game));
                                screen.dispose();
                            }
                        });
                    } else {
                        final SignUpScreen screen = (SignUpScreen) game.getScreen();
                        screen.addFailed(msg.contents);
                    }
                } else if (msg.type.equals(Message.BYE)) {
                    keepRunning = false;
                } else if (msg.type.equals(Message.RECONNECT)) {
                    if (msg.contents.equals("Request Reconnect")) {
                        GameScreen gs = (GameScreen) game.getScreen();
                        String cont = gs.getGameState();
                        Message response = new Message(Message.RECONNECT, "p", cont, game.oponentId);
                        send(response);
                    } else {
                        System.out.println(msg.contents);
                    }
                }

            } catch (EOFException e) {
                e.printStackTrace();
                keepRunning = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void send(Message msg) {
        try {
            out.writeUTF(msg.getMessageString());
            out.flush();

            System.out.println("Outgoing: " + msg.getMessageString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
