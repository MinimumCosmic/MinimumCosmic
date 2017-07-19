package org.minimumcosmic.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import org.minimumcosmic.game.screens.ShopScreen;

public class BoxActor extends Image {
    private int boxNumber;
    private int boxCost;
    public BoxActor(final TextureAtlas.AtlasRegion atlasRegion, final int boxNumber, final Skin skin, final Stage stage,
                    final Label researchPoint, final TextureAtlas rocketAtlas, final ParticleEffect effect1, final ParticleEffect effect2) {
        super(atlasRegion);
        setBoxCost(boxNumber);
        this.boxNumber = boxNumber;
        this.addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
                final Dialog dialog = new Dialog("", skin);
                if(Integer.parseInt(researchPoint.getText().toString()) >= boxCost){
                    researchPoint.setText("" + (Integer.parseInt(researchPoint.getText().toString()) - boxCost));
                    Table tmpDialogTable = new Table();
                    Label openCount = new Label("Tap to unlock! 5", skin);
                    tmpDialogTable.add(openCount);
                    tmpDialogTable.row();
                    org.minimumcosmic.game.actors.BoxItemDropActor boxImage = new org.minimumcosmic.game.actors.BoxItemDropActor(atlasRegion, 5, openCount,
                            dialog, skin, boxNumber, rocketAtlas, effect1, effect2);
                    tmpDialogTable.add(boxImage).width(Gdx.graphics.getHeight() * 0.25f)
                            .height(Gdx.graphics.getHeight() * 0.25f);
                    dialog.add(tmpDialogTable);
                }
                else {
                    dialog.add(new Label("Sorry, haven't \nenough money :(", skin));
                    dialog.addListener(new InputListener() {
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            dialog.hide();
                            return false;
                        }
                    });
                }
                dialog.show(stage);
                return false;
            }
        });
    }

    private void setBoxCost(final int boxNumber){
        switch (boxNumber){
            case ShopScreen.SIMPLEBOX:
                boxCost = ShopScreen.SIMPLEBOXCOST;
                break;
            case ShopScreen.MEDIUMBOX:
                boxCost = ShopScreen.MEDIUMBOXCOST;
                break;
            case ShopScreen.SUPERBOX:
                boxCost = ShopScreen.SUPERBOXCOST;
                break;
        }
    }

    public void getItem(){
        System.out.println("Item from box " + boxNumber);
    }
}
