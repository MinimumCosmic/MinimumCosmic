package org.minimumcosmic.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.minimumcosmic.game.MinimumCosmic;

public class MenuScreen implements Screen {

    private MinimumCosmic game;
    private Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;
    Sprite backSprite;

    public MenuScreen(MinimumCosmic game) {
        this.game = game;

        skin = game.AssetManager.assetManager.get("skin/uiskin.json");
        textureAtlas = game.AssetManager.assetManager.get("images/loading_screen.atlas");

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        stage.getRoot().getColor().a  = 0;
        stage.getRoot().addAction(Actions.fadeIn(0.5f));

        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        TextButton newGame = new TextButton("New Game", skin);
        TextButton hangar = new TextButton("Hangar", skin);
        TextButton exit = new TextButton("Exit", skin);
        TextButton shop = new TextButton("Shop", skin);

        table.bottom().defaults();
        table.add(newGame).fillX().uniformX().padBottom(Gdx.graphics.getHeight() * 0.025f)
                .width(Gdx.graphics.getWidth() * 0.4f).height(Gdx.graphics.getHeight() * 0.05f);
        table.row();
        table.add(hangar).fillX().uniformX().padBottom(Gdx.graphics.getHeight() * 0.025f)
                .width(Gdx.graphics.getWidth() * 0.4f).height(Gdx.graphics.getHeight() * 0.05f);
        table.row();
        table.add(shop).fillX().uniformX().padBottom(Gdx.graphics.getHeight() * 0.025f)
                .width(Gdx.graphics.getWidth() * 0.4f).height(Gdx.graphics.getHeight() * 0.05f);
        table.row();
        table.add(exit).fillX().uniformX().padBottom(Gdx.graphics.getHeight() * 0.1f)
                .width(Gdx.graphics.getWidth() * 0.4f).height(Gdx.graphics.getHeight() * 0.05f);

        // create button listeners
        exit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        newGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(MinimumCosmic.APPLICATION);
            }
        });

        hangar.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(MinimumCosmic.HANGAR);
            }
        });

        shop.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(MinimumCosmic.SHOP);
            }
        });
    }

    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        stage.getBatch().begin();
        backSprite.draw(stage.getBatch());
        stage.getBatch().end();

        stage.draw();


    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
