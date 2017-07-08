package org.minimumcosmic.game;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Created by Kostin on 02.07.2017.
 */
public class MyActor extends Image {
    public Entity entity;
    private final int id;
    private final int module;

    public MyActor(TextureAtlas.AtlasRegion atlasRegion, Entity entity, int id, int module) {
        super(atlasRegion);
        this.entity = entity;
        this.id = id;
        this.module = module;
    }

    public int getId() {
        return this.id;
    }

    public int getModule(){ return this.module;}
}
