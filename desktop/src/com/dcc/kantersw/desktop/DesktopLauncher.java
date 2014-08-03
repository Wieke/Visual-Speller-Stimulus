package com.dcc.kantersw.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dcc.kantersw.VisSpeller;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Visual Speller";
		config.fullscreen = true;
		config.vSyncEnabled = true;
		config.width = 1920;
		config.height = 1080;
		new LwjglApplication(new VisSpeller(), config);
	}
}
