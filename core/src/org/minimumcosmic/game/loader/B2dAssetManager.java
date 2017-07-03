package org.minimumcosmic.game.loader;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class B2dAssetManager {
    public final AssetManager assetManager = new AssetManager();

    public final String gameImages = "images/rocket.atlas";

    public final String smokeEffect = "smoke.p";

    public final String loadingImages = "images/loading_screen.atlas";

    public final String boxImages = "boxes2/box.atlas";

    public final String skin = "skin/uiskin.json";

    public void queueAddImages() {
        assetManager.load(gameImages, TextureAtlas.class);
    }

    public void queueAddLoadingImages() {
        assetManager.load(loadingImages, TextureAtlas.class);
    }

    public void queueAddLoadingBoxImages(){ assetManager.load(boxImages, TextureAtlas.class);}

    public void queueAddFonts() {

    }

    public void queueAddParticleEffects() {
        assetManager.load(smokeEffect, ParticleEffect.class);
    }

    public void queueAddSkin() {
        SkinLoader.SkinParameter params = new SkinLoader.SkinParameter("skin/uiskin.atlas");
        assetManager.load(skin, Skin.class, params);
    }

    public void queueAddMusic() {
    }

    public void queueAddSounds() {
    }
}
