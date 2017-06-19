package org.minimumcosmic.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.minimumcosmic.game.MinimumCosmic;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "MINIMUM COSMIC";
		config.width = 480;
		config.height = 800;
		new LwjglApplication(new MinimumCosmic(), config);
	}
}
