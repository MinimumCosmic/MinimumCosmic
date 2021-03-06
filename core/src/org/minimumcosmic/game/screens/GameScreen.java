package org.minimumcosmic.game.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import org.minimumcosmic.game.BodyFactory;
import org.minimumcosmic.game.MinimumCosmic;
import org.minimumcosmic.game.ObjectFactory;
import org.minimumcosmic.game.entity.components.TransformComponent;
import org.minimumcosmic.game.entity.systems.*;
import org.minimumcosmic.game.exception.XmlLoadException;
import org.minimumcosmic.game.loader.SettingsLoader;
import org.minimumcosmic.game.saver.SettingsSaver;
import org.minimumcosmic.game.controller.KeyboardController;
import org.minimumcosmic.game.entity.components.B2dBodyComponent;
import org.minimumcosmic.game.entity.components.PickupComponent;
import org.minimumcosmic.game.entity.components.RocketComponent;
import org.minimumcosmic.game.entity.components.modules.BodyModuleComponent;
import org.minimumcosmic.game.parallax.Parallax;

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
    private Label fpsLabel;
    private Label bodyCountLabel;
    private Label pickUpLabel;
    private Label healthLabel;
    private Parallax parallaxBackground;
    private Parallax parallaxForeground;
    private ParticleEffect smokeParticles;
    private ParticleEffect explosionParticles;
    private float countDown = .75f;


    public GameScreen(MinimumCosmic game) {
        this.game = game;

        game.AssetManager.queueAddSounds();
        game.AssetManager.assetManager.finishLoading();

        textureAtlas = game.AssetManager.assetManager.get("images/rocket.atlas");
        skin = game.AssetManager.assetManager.get("skin/uiskin.json");
        smokeParticles = game.AssetManager.assetManager.get("smoke.p");
        smokeParticles.scaleEffect(0.075f);

        explosionParticles = game.AssetManager.assetManager.get("explosion.p");

        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void show() {
        explosionParticles.allowCompletion();
        stage = new Stage();
        controller = new KeyboardController();
        engine = new PooledEngine();
        objectFactory = new ObjectFactory(engine);
        spriteBatch = new SpriteBatch();
        parallaxBackground = new Parallax();
        parallaxForeground = new Parallax();
        RenderingSystem renderingSystem = new RenderingSystem(spriteBatch);

        camera = renderingSystem.getCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        // Add required systems to the engine
        engine.addSystem(new PhysicsSystem(BodyFactory.getInstance(objectFactory.world).world));
        engine.addSystem(renderingSystem);
        engine.addSystem(new PhysicsDebugSystem(BodyFactory.getInstance(objectFactory.world).world, camera));
        engine.addSystem(new CollisionSystem(objectFactory, engine));
        engine.addSystem(new RocketSystem(controller));
        engine.addSystem(new CameraSystem());
        engine.addSystem(new BoundsSystem());
        engine.addSystem(new EnemySystem());


        //rocket = objectFactory.createRocket(textureAtlas, camera, "xml/rocket.xml", smokeParticles);
        objectFactory.createFloor(textureAtlas.findRegion("player"));
        objectFactory.generateWorld(new TextureAtlas("images/items.atlas"));
        objectFactory.generateParallaxBackground(new TextureAtlas("images/parallaxbnd.atlas"), parallaxBackground);
        objectFactory.generateParallaxForeground(new TextureAtlas("images/parallaxbnd.atlas"), parallaxForeground);

        /*Gdx.input.setInputProcessor(controller);

        fuelMeter = new ProgressBar(0,
                rocket.getComponent(RocketComponent.class).bodyModule.getComponent(BodyModuleComponent.class).fuel,
                0.25f, true, skin);
        fuelMeter.setHeight(Gdx.graphics.getHeight() / 2);
        fuelMeter.setPosition(Gdx.graphics.getWidth() * 0.95f, Gdx.graphics.getHeight() * 0.1f);
        stage.addActor(fuelMeter);
        speedLabel = new Label(100 + "m/s", skin);
        fpsLabel = new Label(60 + " fps", skin);
        bodyCountLabel = new Label(0 + " bodies", skin);
        pickUpLabel = new Label(0 + " money", skin);
        healthLabel = new Label(10000 + " health", skin);
        fpsLabel.setY(Gdx.graphics.getHeight() * 0.95f);
        bodyCountLabel.setY(Gdx.graphics.getHeight() * 0.9f);
        pickUpLabel.setY(Gdx.graphics.getHeight() * 0.85f);
        healthLabel.setY(Gdx.graphics.getHeight() * 0.8f);
        stage.addActor(speedLabel);
        stage.addActor(fpsLabel);
        stage.addActor(bodyCountLabel);
        stage.addActor(pickUpLabel);
        stage.addActor(healthLabel);*/

        try{
            rocket = objectFactory.createRocket(textureAtlas, camera,
                smokeParticles, "xml/rocket.xml");

            objectFactory.createFloor(textureAtlas.findRegion("player"));
            objectFactory.generateWorld(new TextureAtlas("images/items.atlas"));
            objectFactory.generateParallaxBackground(new TextureAtlas("images/parallaxbnd.atlas"), parallaxBackground);
            objectFactory.generateParallaxForeground(new TextureAtlas("images/parallaxbnd.atlas"), parallaxForeground);

            Gdx.input.setInputProcessor(controller);

            fuelMeter = new ProgressBar(0,
                    rocket.getComponent(RocketComponent.class).bodyModule.getComponent(BodyModuleComponent.class).fuel,
                    0.25f, true, skin);
            fuelMeter.setHeight(Gdx.graphics.getHeight() / 2);
            fuelMeter.setPosition(Gdx.graphics.getWidth() * 0.95f, Gdx.graphics.getHeight() * 0.1f);
            stage.addActor(fuelMeter);
            speedLabel = new Label(100 + "m/s", skin);
            fpsLabel = new Label(60 + " fps", skin);
            bodyCountLabel = new Label(0 + " bodies", skin);
            pickUpLabel = new Label(0 + " money", skin);
            healthLabel = new Label(10000 + " health", skin);
            fpsLabel.setY(Gdx.graphics.getHeight() * 0.95f);
            bodyCountLabel.setY(Gdx.graphics.getHeight() * 0.9f);
            pickUpLabel.setY(Gdx.graphics.getHeight() * 0.85f);
            healthLabel.setY(Gdx.graphics.getHeight() * 0.8f);
            stage.addActor(speedLabel);
            stage.addActor(fpsLabel);
            stage.addActor(bodyCountLabel);
            stage.addActor(pickUpLabel);
            stage.addActor(healthLabel);
        }catch (XmlLoadException e){
            objectFactory.dispose();
            Gdx.input.setInputProcessor(stage);
            Dialog dialog = new Dialog("", skin);
            dialog.add(e.getException()).center();
            dialog.addListener(new InputListener(){
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                    game.changeScreen(MinimumCosmic.MENU);
                    return false;
                }
            });
            dialog.show(stage);
        }
    }

    @Override
    public void render(float delta) {
        float red = 36f / 255f;
        float green = 34f / 255f;
        float blue = 56f / 255f;
        Gdx.gl.glClearColor(red, green, blue, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fuelMeter.setValue(rocket.getComponent(RocketComponent.class).bodyModule.getComponent(BodyModuleComponent.class).fuel);
        speedLabel.setText(rocket.getComponent(B2dBodyComponent.class).body.getLinearVelocity().len2() + "m/s");
        fpsLabel.setText(Gdx.graphics.getFramesPerSecond() + " fps");
        bodyCountLabel.setText(objectFactory.getBodyCount() + " bodies");
        pickUpLabel.setText(rocket.getComponent(PickupComponent.class).count + " money");
        healthLabel.setText(rocket.getComponent(RocketComponent.class).health + " health");

        stage.act();

        spriteBatch.begin();
        parallaxBackground.draw(camera, spriteBatch);
        spriteBatch.end();

        engine.update(delta);

        spriteBatch.begin();
        parallaxForeground.draw(camera, spriteBatch);
        explosionParticles.draw(spriteBatch, delta);
        spriteBatch.end();

        stage.draw();

        switch (rocket.getComponent(RocketComponent.class).state) {
            case 1:
                game.changeScreen(MinimumCosmic.WIN);
                break;
            case -1:
                explosionParticles.setPosition(rocket.getComponent(TransformComponent.class).position.x,
                        rocket.getComponent(TransformComponent.class).position.y);

                explosionParticles.start();
                countDown -= delta;
                if (countDown < 0.0f) {
                    game.changeScreen(MinimumCosmic.LOSE);
                }
                break;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            int currentPoint;
            try{
                currentPoint = SettingsLoader.loadResearchPoint();
                SettingsSaver.saveResearchPoint(rocket.getComponent(PickupComponent.class).count
                        + currentPoint);
                game.changeScreen(MinimumCosmic.MENU);
            }catch (XmlLoadException e){
                objectFactory.dispose();
                Gdx.input.setInputProcessor(stage);
                Dialog dialog = new Dialog("", skin);
                dialog.add(e.getException()).center();
                dialog.addListener(new InputListener(){
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                        game.changeScreen(MinimumCosmic.MENU);
                        return false;
                    }
                });
                dialog.show(stage);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            this.dispose();
            this.show();
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
        objectFactory.dispose();
        stage.dispose();
    }
}
