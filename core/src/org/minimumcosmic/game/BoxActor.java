package org.minimumcosmic.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class BoxActor extends Image {
    private int boxNumber;
    public BoxActor(TextureAtlas.AtlasRegion atlasRegion, int boxNumber) {
        super(atlasRegion);
        this.boxNumber = boxNumber;
    }

    public void getItem(){
        System.out.println("Item from box " + boxNumber);
    }
}
