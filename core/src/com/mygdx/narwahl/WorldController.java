package com.mygdx.narwahl;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.narwahl.util.BunnyHead;
import com.mygdx.narwahl.util.BunnyHead.JUMP_STATE;
import com.mygdx.narwahl.util.Feather;
import com.mygdx.narwahl.util.GoldCoin;
import com.mygdx.narwahl.util.Level;
import com.mygdx.narwahl.util.Rock;
import com.mygdx.narwahl.CameraHelper;
import com.mygdx.narwahl.util.Constants;

public class WorldController extends InputAdapter implements Disposable {

	private static final String TAG = WorldController.class.getName();

	CameraHelper cameraHelper;

	public Level level;
	public int lives;
	public int score;
	public float livesVisual;
	public float scoreVisual;



	// Rectangles for collision detection
	private final Rectangle r1 = new Rectangle();
	private final Rectangle r2 = new Rectangle();

	public WorldController() {
		init();
	}

	private void init() {
		cameraHelper = new CameraHelper();
		livesVisual = lives;
		initLevel();
	}

	private void initLevel() {
		score = 0;
		scoreVisual = 0;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.bunnyHead);
	}



	public void update(float deltaTime) {
		handleDebugInput(deltaTime);

		
		handleInputGame(deltaTime);

		level.update(deltaTime);
		testCollisions();
		bottomTest();
		cameraHelper.update(deltaTime);


	}
	
	private void bottomTest(){
		if (level.bunnyHead.position.y < -1){
			init();
		}
	}
	
	private void handleInputGame(float deltaTime) {
		if (cameraHelper.hasTarget(level.bunnyHead)) {
			// Player Movement
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
			} else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
			} else {
				// Execute auto-forward movement on non-desktop platform
				if (Gdx.app.getType() != ApplicationType.Desktop) {
					level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
				}
			}
			// Bunny Jump
			level.bunnyHead.setJumping(false);
			}
		}


	private void handleDebugInput(float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop) {
			return;
		}

		if (!cameraHelper.hasTarget(level.bunnyHead)) {
			// Camera controls (move)
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
				camMoveSpeed *= camMoveSpeedAccelerationFactor;
			}
			if (Gdx.input.isKeyPressed(Keys.LEFT)) {
				moveCamera(-camMoveSpeed, 0);
			}
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
				moveCamera(camMoveSpeed, 0);
			}
			if (Gdx.input.isKeyPressed(Keys.UP)) {
				moveCamera(0, camMoveSpeed);
			}
			if (Gdx.input.isKeyPressed(Keys.DOWN)) {
				moveCamera(0, -camMoveSpeed);
			}
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) {
				cameraHelper.setPosition(0, 0);
			}
		}

		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		}
		if (Gdx.input.isKeyPressed(Keys.COMMA)) {
			cameraHelper.addZoom(camZoomSpeed);
		}
		if (Gdx.input.isKeyPressed(Keys.PERIOD)) {
			cameraHelper.addZoom(-camZoomSpeed);
		}
		if (Gdx.input.isKeyPressed(Keys.SLASH)) {
			cameraHelper.setZoom(1);
		}
	}

	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	@Override
	public boolean keyUp(int keyCode) {
		// Reset game world
		if(keyCode == Keys.R) {
			init();
			Gdx.app.debug(TAG, "Game world reset");
		}
		// Toggle camera follow
        else if (keyCode == Keys.ENTER) {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null
                    : level.bunnyHead);
            Gdx.app.debug(TAG,
                    "Camera follow enabled: " + cameraHelper.hasTarget());
        }

		return false;
	}

	private void onCollisionBunnyHeadWithRock(Rock rock) {
		BunnyHead bunnyHead = level.bunnyHead;
		float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
		if (heightDifference > 0.25f) {
			boolean hitLeftEdge = bunnyHead.position.x > rock.position.x + rock.bounds.width / 2.0f;
			if (hitLeftEdge) {
				bunnyHead.position.x = rock.position.x + rock.bounds.width;
			} else {
				bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
			}
			return;
		}
		switch (bunnyHead.jumpState) {
		case GROUNDED:
			break;
		case FALLING:
		case JUMP_FALLING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
			bunnyHead.jumpState = JUMP_STATE.GROUNDED;
			if (bunnyHead.position.y < 2)
				init();
			else 
				break;
		case JUMP_RISING:
			bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
			break;
		}
	}

	private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin) {
		goldcoin.collected = true;
		score += goldcoin.getScore();
		Gdx.app.log(TAG, "Gold coin collected");
	}

	private void onCollisionBunnyWithFeather(Feather feather) {
		feather.collected = true;
		score += feather.getScore();
		level.bunnyHead.setFeatherPowerup(true);
		Gdx.app.log(TAG, "Feather collected");
	}


	private void testCollisions() {
		r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);
		// Test collision: Bunny Head <-> Rocks
		for (Rock rock : level.rocks) {
			r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
			if (!r1.overlaps(r2)) {
				continue;
			}
			onCollisionBunnyHeadWithRock(rock);
			// IMPORTANT: must do all collisions for valid
			// edge testing on rocks.
		}
		// Test collision: Bunny Head <-> Gold Coins
		for (GoldCoin goldcoin : level.goldCoins) {
			if (goldcoin.collected) {
				continue;
			}
			r2.set(goldcoin.position.x, goldcoin.position.y, goldcoin.bounds.width, goldcoin.bounds.height);
			if (!r1.overlaps(r2)) {
				continue;
			}
			onCollisionBunnyWithGoldCoin(goldcoin);
			break;
		}
		// Test collision: Bunny Head <-> Feathers
		for (Feather feather : level.feathers) {
			if (feather.collected) {
				continue;
			}
			r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height);
			if (!r1.overlaps(r2)) {
				continue;
			}
			onCollisionBunnyWithFeather(feather);
			break;
		}
		// Test collision: Bunny Head <-> Goal
		}


	

	

	@Override
	public void dispose() {
		
	}

}