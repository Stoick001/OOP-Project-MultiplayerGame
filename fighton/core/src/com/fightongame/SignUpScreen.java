package com.fightongame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class SignUpScreen implements Screen {
    final FightOn game;
    private Skin skin;
    private Stage stage;


    public SignUpScreen(final FightOn game) {
        this.game = game;

        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());

        // Buttons
        final TextButton loginButton = new TextButton("Login", skin, "default");
        loginButton.setSize(200, 50);
        loginButton.setPosition(200, 150);

        final TextButton signUpButton = new TextButton("SignUp", skin, "default");
        signUpButton.setSize(200, 50);
        signUpButton.setPosition(420, 150);


        // Username
        final Label usernameLabel = new Label("Username:", skin, "default");
        usernameLabel.setSize(120, 50);
        usernameLabel.setPosition(200, 290);

        final TextField username = new TextField("", skin, "default");
        username.setSize(300, 50);
        username.setPosition(320, 290);


        // Password
        final Label passwordLabel = new Label("Password:", skin, "default");
        passwordLabel.setSize(120, 50);
        passwordLabel.setPosition(200, 220);

        final TextField password = new TextField("", skin, "default");
        password.setSize(300, 50);
        password.setPosition(320, 220);
        password.setPasswordMode(true);
        password.setPasswordCharacter('*');

        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String usernameText = username.getText();
                String passwordText = password.getText();


                if (usernameText.length() == 0 || passwordText.length() == 0) {
                    addFailed("Please fill out the fields");
                } else {
                    Message msg = new Message(Message.LOGIN, "login", usernameText + "#" + passwordText, "server");

                    game.client.send(msg);
                }
            }
        });

        signUpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String usernameText = username.getText();
                String passwordText = password.getText();

                Message msg = new Message(Message.REGISTER, "register", usernameText + "#" + passwordText, "server");

                game.client.send(msg);
            }
        });

        stage.addActor(usernameLabel);
        stage.addActor(username);

        stage.addActor(passwordLabel);
        stage.addActor(password);

        stage.addActor(signUpButton);
        stage.addActor(loginButton);

        Gdx.input.setInputProcessor(stage);
    }

    public void addFailed(String text) {
        for (Actor actor: stage.getActors()) {
            if (actor.getName() != null && actor.getName().equals("Failed")) {
                actor.remove();
            }
        }

        final Label failedLabel = new Label(text, skin, "default");
        failedLabel.setName("Failed");
        failedLabel.setSize(120, 50);
        failedLabel.setPosition(300, 90);

        stage.addActor(failedLabel);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.5f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
