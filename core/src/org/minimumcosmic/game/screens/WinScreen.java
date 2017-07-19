package org.minimumcosmic.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import org.minimumcosmic.game.MinimumCosmic;

public class WinScreen implements Screen {

    private MinimumCosmic game;
    private Stage stage;
    private Skin skin;
    private Label text;
    private Label copy;

    public WinScreen(MinimumCosmic game) {
        this.game = game;
        stage = new Stage();

        stage.getRoot().getColor().a  = 0;
        stage.getRoot().addAction(Actions.fadeIn(0.5f));
        skin = game.AssetManager.assetManager.get("skin/uiskin.json");
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage);
        text = new Label("You've reached the end of the \n " +
                "first planet\n\nThank you for playing the\n " +
                "beta version\n\n", skin);
        copy = new Label("     Dron Patron && Hmuryi", skin);
        TextButton toMenu = new TextButton("TO MENU", skin);
        toMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(MinimumCosmic.MENU);
            }
        });
        toMenu.setPosition(Gdx.graphics.getWidth() / 2 - toMenu.getWidth() / 2, Gdx.graphics.getHeight() * 0.15f);

        text.setY(Gdx.graphics.getHeight() / 2f);
        text.setAlignment(Align.center);
        text.setFontScale(1f * Gdx.graphics.getHeight() / 800);

        copy.setY(Gdx.graphics.getHeight() * 0.25f);
        copy.setAlignment(Align.center);
        copy.setFontScale(0.75f * Gdx.graphics.getHeight() / 800);

        stage.addActor(text);
        stage.addActor(copy);
        stage.addActor(toMenu);
    }

    @Override
    public void render(float delta) {
        float red = 36f / 255f; float green = 34f / 255f; float blue = 56f / 255f;
        Gdx.gl.glClearColor(red, green, blue, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height,true);
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
        stage.dispose();
    }
}

