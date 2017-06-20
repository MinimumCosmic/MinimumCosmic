package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.minimumcosmic.game.B2dContactListener;
import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.controller.KeyboardController;
import org.minimumcosmic.game.entity.systems.*;

public class GameScreen implements Screen {
    private KeyboardController controller;
    //private World world;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private PooledEngine engine;
    private ObjectFactory objectFactory;
    private MinimumCosmic game;

    private TextureAtlas textureAtlas;

    public GameScreen(MinimumCosmic game) {
        this.game = game;
        game.AssetManager.queueAddSounds();
        game.AssetManager.assetManager.finishLoading();

        textureAtlas = game.AssetManager.assetManager.get("images/game_screen.atlas");

        controller = new KeyboardController();
        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();

        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = renderingSystem.getCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Add required systems to the engine
        engine.addSystem(new PhysicsSystem(objectFactory.world));
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsDebugSystem(objectFactory.world, camera));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));

        objectFactory.createPlayer(textureAtlas.findRegion("player"), camera);
        objectFactory.createFloor(textureAtlas.findRegion("player"));

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(delta);
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
