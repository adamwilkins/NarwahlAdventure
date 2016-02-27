package com.mygdx.narwahl.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.narwahl.NarwahlMain;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.badlogic.gdx.tools.texturepacker.*;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher {
	private static boolean rebuildAtlas = true;
    private static boolean drawDebugOutline = false;
	
	public static void main (String[] arg) {
		 if (rebuildAtlas) {
             Settings settings = new Settings();
             settings.maxWidth = 1024;
             settings.maxHeight = 1024;
             settings.duplicatePadding = false;
             settings.debug = drawDebugOutline;
             TexturePacker.process(settings, "assets-raw/images", "../desktop/assets-raw/atlas", "narwahl");
		 }
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new NarwahlMain(), config);
		 
	}
}