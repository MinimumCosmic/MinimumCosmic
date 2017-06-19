package org.minimumcosmic.game;

import com.badlogic.gdx.Game;
import org.minimumcosmic.game.loader.B2dAssetManager;
import org.minimumcosmic.game.screens.GameScreen;
import org.minimumcosmic.game.screens.LoadingScreen;
import org.minimumcosmic.game.screens.MenuScreen;

public class MinimumCosmic extends Game {
	public B2dAssetManager AssetManager = new B2dAssetManager();

	private GameScreen gameScreen;
	private LoadingScreen loadingScreen;
	private MenuScreen menuScreen;

	public final static int MENU = 0;
	public final static int APPLICATION = 2;

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
				if (gameScreen == null) gameScreen = new GameScreen();
				this.setScreen(gameScreen);
				break;
		}
	}
	@Override
	public void dispose() {
		AssetManager.assetManager.dispose();
	}

}