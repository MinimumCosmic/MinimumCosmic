package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import org.minimumcosmic.game.BodyFactory;
import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.controller.KeyboardController;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.entity.systems.*;

public class GameScreen implements Screen {
    private KeyboardController controller;
    private SpriteBatch spriteBatch;
    private OrthographicCamera camera;
    private PooledEngine engine;
    private ObjectFactory objectFactory;
    private MinimumCosmic game;
    private Entity rocket;
    private Stage stage;
    private ProgressBar fuelMeter;
    private TextureAtlas textureAtlas;
    private Skin skin;
    private Label speedLabel;

    public GameScreen(MinimumCosmic game) {
        this.game = game;


        game.AssetManager.queueAddSounds();
        game.AssetManager.assetManager.finishLoading();

        textureAtlas = game.AssetManager.assetManager.get("images/game_screen.atlas");
        skin = game.AssetManager.assetManager.get("skin/uiskin.json");


    }

    @Override
    public void show() {
        stage = new Stage();
        controller = new KeyboardController();
        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();

        System.out.println("World's body count: " + objectFactory.world.getBodyCount());
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);
        camera = renderingSystem.getCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Add required systems to the engine
        engine.addSystem(new PhysicsSystem(BodyFactory.getInstance(objectFactory.world).world));
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsDebugSystem(BodyFactory.getInstance(objectFactory.world).world, camera));
        engine.addSystem(new CollisionSystem());
        engine.addSystem(new RocketSystem(controller));
        engine.addSystem(new CameraSystem());


        ParticleEffect pe = game.AssetManager.assetManager.get("smoke.p");
        rocket = objectFactory.createRocket(textureAtlas, camera, pe);
        objectFactory.createFloor(textureAtlas.findRegion("player"));


        Gdx.input.setInputProcessor(controller);

        fuelMeter = new ProgressBar(0 , 100, 0.25f, true, skin);
        fuelMeter.setHeight(Gdx.graphics.getHeight() / 2);
        fuelMeter.setPosition(Gdx.graphics.getWidth() * 0.95f, Gdx.graphics.getHeight() * 0.1f);
        stage.addActor(fuelMeter);
        speedLabel = new Label(100 + "m/s", skin);
        stage.addActor(speedLabel);

        System.out.println("World's body count: " + objectFactory.world.getBodyCount());
        System.out.println("World's body count in bodyFactory: " + BodyFactory.getInstance(objectFactory.world).world.getBodyCount());
    }
    @Override
    public void render(float delta) {
        float red = 36f / 255f; float green = 34f / 255f; float blue = 56f / 255f;
        Gdx.gl.glClearColor(red, green, blue, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fuelMeter.setValue(rocket.getComponent(RocketComponent.class).bodyModule.getComponent(BodyModuleComponent.class).fuel);
        speedLabel.setText(rocket.getComponent(B2dBodyComponent.class).body.getLinearVelocity().y + "m/s");

        stage.act();
        stage.draw();

        engine.update(delta);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.changeScreen(MinimumCosmic.MENU);
        }
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
        objectFactory.dispose();
        stage.dispose();
    }

    @Override
    public void dispose() {
    }
}
