package com.mygdx.narwahl.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.mygdx.narwahl.util.AbstractGameObject;
import com.mygdx.narwahl.util.BunnyHead;
import com.mygdx.narwahl.util.Feather;
import com.mygdx.narwahl.util.GoldCoin;

import com.mygdx.narwahl.util.Rock;

public class Level {
	public static final String TAG = Level.class.getName();
	
	public enum BLOCK_TYPE {
		GOAL(255, 0, 0),					// red
		EMPTY(0, 0, 0),						// black
		ROCK(0, 255, 0),					// green
		PLAYER_SPAWNPOINT(255, 255, 255),	// white
		ITEM_FEATHER(255, 0, 255),			// purple
		ITEM_GOLD_COIN(255, 255, 0);		// yellow
		private int color;
		
		private BLOCK_TYPE(int r, int g, int b) {
			color = r << 24 | g << 16 | b << 8 | 0xff;
		}
		
		public boolean sameColor(int color) {
			return color == this.color;
		}
		
		public int getColor() {
			return color;
		}
	}
	
	public BunnyHead bunnyHead;
	public Array<GoldCoin> goldCoins;
	public Array<Feather> feathers;
	
	// objects
	public Array<Rock> rocks;
	// decoration
	// goal & carrots

	public Level(String filename) {
		init(filename);
	}
	
	public void init(String filename) {
		// player character
		bunnyHead = null;
		// objects
		rocks = new Array<Rock>();
		goldCoins = new Array<GoldCoin>();
		feathers = new Array<Feather>();

		// load the image file that represents the level data
		Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
		// scan pixels from top-left to bottom right
		int lastPixel = -1;
		for(int pixelY = 0; pixelY < pixmap.getHeight(); ++pixelY)  {
			for(int pixelX = 0; pixelX < pixmap.getWidth(); ++pixelX) {
				AbstractGameObject obj = null;
				float offsetHeight = 0;
				// height grows from bottom to top
				float baseHeight = pixmap.getHeight() - pixelY;
				// get color of current pixel as 32-bit RGBA value
				int currentPixel = pixmap.getPixel(pixelX, pixelY);
				// find matching color value to identify block type at (x, y)
				// point and create the corresponding game object if there is 
				// a match 
				// empty space
				if(BLOCK_TYPE.EMPTY.sameColor(currentPixel)) {
					// do nothing
				}
				// rock
				else if(BLOCK_TYPE.ROCK.sameColor(currentPixel)) {
					if(lastPixel != currentPixel) {
						obj = new Rock();
						float heightIncreaseFactor = 0.5f;
						offsetHeight = -2.5f;
						obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor
								+ offsetHeight);
						rocks.add((Rock)obj);
					} else {
						rocks.get(rocks.size - 1).increaseLength(1);
					}
				}
				// player spawn point
				else if(BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel)) {
		               obj = new BunnyHead();
		               offsetHeight = 1f;
		               obj.position.set(pixelX + 1.75f, baseHeight * obj.dimension.y + offsetHeight);
		               bunnyHead = (BunnyHead) obj;               
		            }
				// feather
				else if(BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel)) {
					obj = new Feather();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y+offsetHeight);
					feathers.add((Feather)obj);
				}
				// gold coin
				else if(BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel)) {
					obj = new GoldCoin();
					offsetHeight = -1.5f;
					obj.position.set(pixelX, baseHeight*obj.dimension.y+offsetHeight);
					goldCoins.add((GoldCoin)obj);
				}
				// unknown object/pixel color
				else {
					int r = 0xff & (currentPixel >>> 24); // red channel
					int g = 0xff & (currentPixel >>> 16); // green channel
					int b = 0xff & (currentPixel >>> 8);  // blue channel
					int a = 0xff & currentPixel;		  // alpha channel
					Gdx.app.debug(TAG, "unknown object at x<" + pixelX + "> y<"
							+ pixelY + ">: r<" + r + "> g<" + g + "> b<" + b + "> a<" + a + ">");
				}
				lastPixel = currentPixel;
			}
		}
		
		// free memory
		pixmap.dispose();
		Gdx.app.debug(TAG, "level " + filename + " loaded");
	}
	
	public void update(double deltaTime) {
		bunnyHead.update( deltaTime);
		for(Rock rock : rocks)
	        rock.update( deltaTime);
	    for(GoldCoin goldCoin : goldCoins)
	        goldCoin.update( deltaTime);
	    for(Feather feather : feathers)
	        feather.update( deltaTime);

	}
	
	public void render(SpriteBatch batch) {
		
		// draw rocks
		for(int i = 0; i < rocks.size; ++i) {
			rocks.get(i).render(batch);
		}
		// Draw Gold Coins
        for (GoldCoin goldCoin : goldCoins)
        	goldCoin.render(batch);
        // Draw Feathers
        for (Feather feather : feathers)
        	feather.render(batch);
        // Draw Player Character
        bunnyHead.render(batch);
		// draw water-overlay
	}
}