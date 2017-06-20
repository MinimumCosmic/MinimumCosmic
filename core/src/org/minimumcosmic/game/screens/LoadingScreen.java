package org.minimumcosmic.game.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import org.minimumcosmic.game.MinimumCosmic;

public class LoadingScreen implements Screen {

    private MinimumCosmic game;
    private TextureAtlas textureAtlas;

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
        game.AssetManager.queueAddImages();
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
        logoSprite.setCenter(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.75f);

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


    }

    @Override
    public void show() {
       progressBar = new ProgressBar(0 , 100, 20, false, skin);
       progressBar.setWidth(Gdx.graphics.getWidth() / 2);
       progressBar.setAnimateDuration(countDown);
       progressBar.setPosition(Gdx.graphics.getWidth() / 2 - progressBar.getWidth() / 2, Gdx.graphics.getHeight() * 0.1f);
       stage.addActor(progressBar);

       table = new Table();
       table.setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() * 0.2f);
       stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.AssetManager.assetManager.update()) { // Load some, will return true if done loading
            currentLoadingStage += 1;

            progressBar.setValue(currentLoadingStage * 20.0f);


            switch (currentLoadingStage) {
                case FONT:
                    table.row();
                    table.add(new Label("Loading fonts....", skin)).align(Align.left);
                    game.AssetManager.queueAddFonts();
                    break;
                case PARTY:
                    table.row();
                    table.add(new Label("Loading Particle Effects....", skin)).align(Align.left);
                    game.AssetManager.queueAddParticleEffects();
                    break;
                case SOUND:
                    table.row();
                    table.add(new Label("Loading Sounds....", skin)).align(Align.left);
                    game.AssetManager.queueAddSounds();
                    break;
                case MUSIC:
                    table.row();
                    table.add(new Label("Loading music....", skin)).align(Align.left);
                    game.AssetManager.queueAddMusic();
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

        stage.act();

        stage.getBatch().begin();
        backSprite.draw(stage.getBatch());
        logoSprite.draw(stage.getBatch());
        progressBar.draw(stage.getBatch(), 1.0f);
        stage.getBatch().end();

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
