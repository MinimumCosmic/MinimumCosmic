package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.systems.RenderingSystem;


public class HangarScreen implements Screen {
    private MinimumCosmic game;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;
    private Entity rocket;
    private PooledEngine engine;
    private ObjectFactory objectFactory;
    private OrthographicCamera camera;

    public HangarScreen(MinimumCosmic game) {
        this.game = game;
        skin = game.AssetManager.assetManager.get("skin/uiskin.json");
        textureAtlas = game.AssetManager.assetManager.get("images/game_screen.atlas");
    }

    @Override
    public void show(){
        engine = new PooledEngine();

        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();

        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = new OrthographicCamera();
        spriteBatch.setProjectionMatrix(camera.combined);


        engine.addSystem(renderingSystem);

        rocket = objectFactory.createRocket(textureAtlas, camera,
                (ParticleEffect)game.AssetManager.assetManager.get("smoke.p"),
                "xml/rocket.xml");

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);

        stage.addActor(table);

        TextButton back = new TextButton("Back", skin);
        table.bottom().defaults();
        table.left().defaults();
        table.add(back).fillX().uniformX().width(Gdx.graphics.getWidth() * 0.15f).height(Gdx.graphics.getHeight() * 0.07f);


        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.changeScreen(MinimumCosmic.MENU);
            }
        });

        //Top buttons to switch module
        Table modules = new Table();
        TextButton headButton = new TextButton("Head modules", skin);
        //TextButton bodyButton = new TextButton();

        Table listTable = new Table();
        listTable.top();
        listTable.left();
        listTable.setBounds(0, Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);
        listTable.debug();

        Table rocketTable = new Table();
        rocketTable.setBounds(listTable.getWidth(), Gdx.graphics.getHeight() * 0.2f, Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.6f);
        rocketTable.debug();

        String []str = {"aaa", "bbb", "ccc"};

        ScrollPane scrollPane = new ScrollPane(new Label("Test", skin), skin);
        listTable.add(scrollPane);
    }




    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        //pe.update(Gdx.graphics.getDeltaTime());

        stage.getBatch().begin();
        //backSprite.draw(stage.getBatch());
        //pe.draw(stage.getBatch());
        stage.getBatch().end();

        /*if (pe.isComplete()) {
            pe.reset();
        }*/

        stage.draw();

        engine.update(delta);
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
        objectFactory.dispose();
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}