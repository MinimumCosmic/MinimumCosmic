package org.minimumcosmic.game.actors;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import org.minimumcosmic.game.screens.ShopScreen;

import java.util.Random;

/**
 * Created by Kostin on 10.07.2017.
 */

public class BoxItemDropActor extends Image {
    private int numberOfTouch;
    private int boxNumber;
    private TextureAtlas rocketAtlas;
    private SpriteBatch spriteBatch;
    public BoxItemDropActor(final TextureAtlas.AtlasRegion atlasRegion, final int touch, final Label openCount,
                            final Dialog dialog, final Skin skin, int boxNumber, TextureAtlas rocketAtlas,
                            final ParticleEffect effect1, final ParticleEffect effect2){
        super(atlasRegion);
        this.numberOfTouch = touch;
        this.boxNumber = boxNumber;
        this.rocketAtlas = rocketAtlas;
        this.addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                //if (effect.isComplete())
                //{
                numberOfTouch = numberOfTouch - 1;
                effect1.reset();
                effect1.start();
                //}
                openCount.setText("Tap to unlock! " + numberOfTouch);

                if(numberOfTouch == 0){
                    dialog.clear();
                    effect2.start();
                    getRandomModule(skin, dialog);
                    //dialog.add(new Label("Your prise!", skin));

                }
                return false;
            }
        });
    }

    private void getRandomModule(Skin skin, final Dialog dialog){
        Random rnd = new Random(System.currentTimeMillis());
        Image itemDrop = null;
        final int id, moduleRarity, module;

        if(boxNumber == ShopScreen.SIMPLEBOX) {
            module = 1;
            id = rnd.nextInt(4);
        }
        else if(boxNumber == ShopScreen.MEDIUMBOX) {
            moduleRarity = rnd.nextInt(100) + 1;
            if (moduleRarity <= 60) {
                module = 1;
                id = rnd.nextInt(4);
            } else {
                module = 2;
                id = rnd.nextInt(4);
            }
        }
        else {
            moduleRarity = rnd.nextInt(100) + 1;
            if (moduleRarity <= 70) {
                module = moduleRarity % 2 + 2;
                id = rnd.nextInt(4);
            } else {
                module = 1;
                id = rnd.nextInt(4);
            }
        }
        switch(id){
            case ShopScreen.HEAD:
                itemDrop = new Image(rocketAtlas.findRegion("head_" + module));
                break;
            case ShopScreen.BODY:
                itemDrop = new Image(rocketAtlas.findRegion("body_" + module));
                break;
            case ShopScreen.FINS:
                itemDrop = new Image(rocketAtlas.findRegion("fins_" + module));
                break;
            case ShopScreen.ENGINE:
                itemDrop = new Image(rocketAtlas.findRegion("engine_" + module));
                break;
        }
        Table tmpTable = new Table();
        itemDrop.addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                ShopScreen.addModule(module, id);
                dialog.hide();
                return false;
            }
        });
        tmpTable.add(new Label("Your prize!", skin));
        tmpTable.row();
        tmpTable.add(itemDrop);
        dialog.add(tmpTable);
    }

    public int getNumberOfTouch(){
        return numberOfTouch;
    }
}
