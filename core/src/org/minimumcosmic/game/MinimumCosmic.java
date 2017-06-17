package org.minimumcosmic.game;

import com.badlogic.gdx.Game;

public class MinimumCosmic extends Game {
	@Override
	public void create() {
		this.setScreen(new GameScreen());
	}
}