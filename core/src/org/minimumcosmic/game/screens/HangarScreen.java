package org.minimumcosmic.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.minimumcosmic.game.MinimumCosmic;


public class HangarScreen implements Screen {
    private MinimumCosmic game;
    private Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;
    Sprite backSprite;
    ParticleEffect pe;

    public HangarScreen(MinimumCosmic game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());

        skin = game.AssetManager.assetManager.get("skin/uiskin.json");
        textureAtlas = game.AssetManager.assetManager.get("images/loading_screen.atlas");

        backSprite = textureAtlas.createSprite("background");
        backSprite.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


    }

    @Override
    public void show(){
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

        pe = new ParticleEffect();
        pe = game.AssetManager.assetManager.get("smoke.p");
        pe.getEmitters().first().setPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        pe.scaleEffect(0.25f);
        pe.start();
    }

    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        pe.update(Gdx.graphics.getDeltaTime());

        stage.getBatch().begin();
        backSprite.draw(stage.getBatch());
        pe.draw(stage.getBatch());
        stage.getBatch().end();

        if (pe.isComplete()) {
            pe.reset();
        }

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

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
