package org.minimumcosmic.game;

import com.badlogic.gdx.Game;
import org.minimumcosmic.game.loader.B2dAssetManager;
import org.minimumcosmic.game.screens.GameScreen;
import org.minimumcosmic.game.screens.HangarScreen;
import org.minimumcosmic.game.screens.LoadingScreen;
import org.minimumcosmic.game.screens.MenuScreen;
import org.minimumcosmic.game.screens.ShopScreen;

public class MinimumCosmic extends Game {
	public B2dAssetManager AssetManager = new B2dAssetManager();

	private GameScreen gameScreen;
	private LoadingScreen loadingScreen;
	private MenuScreen menuScreen;
	private HangarScreen hangarScreen;
	private ShopScreen shopScreen;

	public final static int MENU = 0;
	public final static int HANGAR = 1;
	public final static int APPLICATION = 2;
	public final static int SHOP = 3;

	public final static int NUMBEROFMODULES = 4;

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
				if(hangarScreen == null) hangarScreen = new HangarScreen(this);
				this.setScreen(hangarScreen);
				break;
			case SHOP:
				if(shopScreen == null) shopScreen = new ShopScreen(this);
				this.setScreen(shopScreen);
				break;
		}
	}
	@Override
	public void dispose() {
		AssetManager.assetManager.dispose();
	}

}