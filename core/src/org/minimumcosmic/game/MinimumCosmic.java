package org.minimumcosmic.game;

import com.badlogic.gdx.Game;
import org.minimumcosmic.game.loader.B2dAssetManager;
import org.minimumcosmic.game.screens.*;

public class MinimumCosmic extends Game {
    public B2dAssetManager AssetManager = new B2dAssetManager();

    private GameScreen gameScreen;
    private LoadingScreen loadingScreen;
    private MenuScreen menuScreen;
    private HangarScreen hangarScreen;
    private ShopScreen shopScreen;
    private WinScreen winScreen;
    private LoseScreen loseScreen;

    public final static int MENU = 0;
    public final static int HANGAR = 1;
    public final static int APPLICATION = 2;
    public final static int SHOP = 3;
    public final static int WIN = 4;
    public final static int LOSE = 5;

    public final static long NUMBEROFMODULES = 4;

    @Override
    public void create() {
        loadingScreen = new LoadingScreen(this);
        this.setScreen(loadingScreen);
    }

    public void changeScreen(int screen) {
        switch (screen) {
            case MENU:
                if (menuScreen == null) menuScreen = new MenuScreen(this);
                this.setScreen(menuScreen);
                break;
            case APPLICATION:
                if (gameScreen == null) gameScreen = new GameScreen(this);
                this.setScreen(gameScreen);
                break;
            case HANGAR:
                if (hangarScreen == null) hangarScreen = new HangarScreen(this);
                this.setScreen(hangarScreen);
                break;
            case SHOP:
                if (shopScreen == null) shopScreen = new ShopScreen(this);
                this.setScreen(shopScreen);
                break;
            case WIN:
                if (winScreen == null) winScreen = new WinScreen(this);
                this.setScreen(winScreen);
                break;
            case LOSE:
                if (loseScreen == null) loseScreen = new LoseScreen(this);
                this.setScreen(loseScreen);
                break;
        }
    }

    @Override
    public void dispose() {
        AssetManager.assetManager.dispose();
    }

}