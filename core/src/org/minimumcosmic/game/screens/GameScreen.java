package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import org.minimumcosmic.game.B2dContactListener;
import org.minimumcosmic.game.controller.KeyboardController;
import org.minimumcosmic.game.entity.systems.*;

public class GameScreen implements Screen {
    private KeyboardController controller;
    private World world;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private PooledEngine engine;


    @Override
    public void show() {
        controller = new KeyboardController();
        world = new World(new Vector2(0, -10f), false);
        world.setContactListener(new B2dContactListener());

        spriteBatch = new SpriteBatch();
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = renderingSystem.getCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        engine = new PooledEngine();

        // Add required systems to the engine
        engine.addSystem(new PhysicsSystem(world));
        engine.addSystem(new PhysicsDebugSystem(world, camera));
        engine.addSystem(renderingSystem);
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new PlayerControlSystem(controller));

        // Create some entities
        /*createPlayer();
        createPlatform(2, 2);
        createPlatform(3, 4);
        createPlatform(5, 2);
        createPlatform(6, 4);

        createFloor();*/

        Gdx.input.setInputProcessor(controller);
    }




    // create a coloured TextureRegion
    private TextureRegion createTexture(Color color, boolean circle, int w, int h) {
        Pixmap pmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pmap.setColor(color);
        if (circle) {
            pmap.fillCircle(15, 15, 15);
        } else {
            pmap.fill();
        }
        TextureRegion texr = new TextureRegion(new Texture(pmap));
        pmap.dispose();
        return texr;

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
