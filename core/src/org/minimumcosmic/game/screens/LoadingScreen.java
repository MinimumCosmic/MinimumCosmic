package org.minimumcosmic.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import org.minimumcosmic.game.MinimumCosmic;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

public class LoadingScreen implements Screen {

    private MinimumCosmic game;
    private TextureAtlas textureAtlas;
    private TweenManager tweenManager;
    private SpriteBatch batch;

    public final int IMAGE = 0;        // loading images
    public final int FONT = 1;        // loading fonts
    public final int PARTY = 2;        // loading particle effects
    public final int SOUND = 3;        // loading sounds
    public final int MUSIC = 4;        // loading music


    private int currentLoadingStage = 0;

    // timer for exiting loading screen
    private float countDown = 2.0f;

    private Stage stage;
    private Table table;
    private Sprite backSprite;
    private Sprite logoSprite;
    private Skin skin;
    ProgressBar progressBar;

    public LoadingScreen(MinimumCosmic game) {
        this.game = game;
        stage = new Stage();

        loadAssets();

    }

    private void loadAssets() {
        // Load LoadingScreen images and wait until it's finished
        game.AssetManager.queueAddLoadingImages();
        game.AssetManager.assetManager.finishLoading();

        game.AssetManager.queueAddSkin();
        game.AssetManager.assetManager.finishLoading();

        skin = game.AssetManager.assetManager.get("skin/uiskin.json");

        textureAtlas = game.AssetManager.assetManager.get("images/loading_screen.atlas");

        logoSprite = textureAtlas.createSprite("logo");
        logoSprite.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        logoSprite.setScale(0.5f);

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


    }

    @Override
    public void show() {
        tweenManager = new TweenManager();
        batch = new SpriteBatch();
        Tween.registerAccessor(Sprite.class, new SpriteAccesor());

       progressBar = new ProgressBar(0 , 100, 20, false, skin);
       progressBar.setAnimateDuration(countDown);
       progressBar.setPosition(Gdx.graphics.getWidth() / 2 - progressBar.getWidth() / 2, Gdx.graphics.getHeight() * 0.1f);
       stage.addActor(progressBar);

        Tween.set(logoSprite, SpriteAccesor.ALPHA).target(0).start(tweenManager);
        Tween.to(logoSprite, SpriteAccesor.ALPHA, 2).target(1).start(tweenManager);
        Tween.to(logoSprite, SpriteAccesor.ALPHA, 2).target(0).delay(3).start(tweenManager);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        tweenManager.update(delta);

        if (game.AssetManager.assetManager.update()) { // Load some, will return true if done loading
            currentLoadingStage += 1;

            progressBar.setValue(currentLoadingStage * 20.0f);

            switch (currentLoadingStage) {
                case FONT:
                    System.out.println("Loading fonts....");
                    game.AssetManager.queueAddFonts();
                    break;
                case PARTY:
                    System.out.println("Loading Particle Effects....");
                    game.AssetManager.queueAddParticleEffects();
                    break;
                case SOUND:
                    System.out.println("Loading Sounds....");
                    game.AssetManager.queueAddSounds();
                    break;
                case MUSIC:
                    System.out.println("Loading fonts....");
                    game.AssetManager.queueAddMusic();
                    break;
                case 5:
                    System.out.println("Finished");
                    break;
            }

            if (currentLoadingStage > 5) {
                countDown -= delta;
                currentLoadingStage = 5;
                if (countDown < 0) {
                    game.changeScreen(MinimumCosmic.MENU);
                }
            }
        }

        batch.begin();
        logoSprite.draw(batch);
        batch.end();
       /* stage.act();

        stage.getBatch().begin();
        backSprite.draw(stage.getBatch());
        logoSprite.draw(stage.getBatch());
        progressBar.draw(stage.getBatch(), 1.0f);
        stage.getBatch().end();

        stage.draw();*/
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

