package com.dcc.kantersw;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.TimeUtils;

public class VisSpeller extends ApplicationAdapter {

	private enum States {
		ESTABLISH_BUFFER_CONNECTION, TRAINING_TEXT, CUE, FLASHING_GRID, FEEDBACK_TEXT, FEEDBACK
	}

	private States state = States.ESTABLISH_BUFFER_CONNECTION;

	// GRAPHICS RELATED VARIABLES
	private SpriteBatch batch;
	private BitmapFont font;
	private Sprite[][] grid = new Sprite[6][5];
	private Sprite[][] gridGreen = new Sprite[6][5];
	private Sprite[][] gridRed = new Sprite[6][5];

	// GRID FLASH RELATED VARIABLES
	private final static boolean[] ISCOLUMNFLASH = { true, true, true, true,
		true, true, false, false, false, false, false };
	private final static int[] NUMBERFLASH = { 0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4 };
	private final static int[] FLASHINDICES = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
		10 };
	private ArrayList<Integer> currentFlashes = new ArrayList<Integer>();
	private long lastStateTime;

	private static final String TITLE = "Visual Speller";
	private float titleX;
	private float titleY;

	private FPSLogger fpslogger;

	@Override
	public void create() {
		// Grab the width and height of the screen for convenience.
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();

		// Create a SpriteBatch for high-level 2d rendering
		batch = new SpriteBatch();

		// Create sprites for each column and row of the grid

		// Determine column and row sizes for the currently used screen. Defined
		// relative to the size of a 1080p screen.

		float gridWidth = height * (900f / 1080f) * (1080f / 900f);
		float gridHeight = height * (900f / 1080f);
		float rowColumnSize = height * (180f / 1080f);

		// Grabbing the grid texture.
		Texture gridTexture = new Texture("grid.png");

		// Create grid sprites
		for (int x = 0; x < 6; x++) {
			for (int y = 0; y < 5; y++) {
				TextureRegion texture = new TextureRegion(gridTexture,
						0 + x * 180, 720 - y * 180, 180, 180);
				grid[x][y] = new Sprite(texture);
				grid[x][y].setSize(rowColumnSize, rowColumnSize);
				grid[x][y].setPosition(width / 2 - gridWidth / 2 + x
						* rowColumnSize, height / 2 - gridHeight / 2 + y
						* rowColumnSize);

				gridRed[x][y] = new Sprite(texture);
				gridRed[x][y].setSize(rowColumnSize, rowColumnSize);
				gridRed[x][y].setPosition(width / 2 - gridWidth / 2 + x
						* rowColumnSize, height / 2 - gridHeight / 2 + y
						* rowColumnSize);
				gridRed[x][y].setColor(Color.RED);

				gridGreen[x][y] = new Sprite(texture);
				gridGreen[x][y].setSize(rowColumnSize, rowColumnSize);
				gridGreen[x][y].setPosition(width / 2 - gridWidth / 2 + x
						* rowColumnSize, height / 2 - gridHeight / 2 + y
						* rowColumnSize);
				gridGreen[x][y].setColor(Color.GREEN);

			}
		}

		// Generate a BitmapFont based on a freetype Ubuntu font.
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
				Gdx.files.internal("Ubuntu-R.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 26;
		font = generator.generateFont(parameter);
		generator.dispose();

		// Determine title position
		TextBounds titleBounds = font.getBounds(TITLE);
		titleX = width / 2 - titleBounds.width / 2;
		titleY = height - titleBounds.height;

		// Set the current time as lastFlashTime
		lastStateTime = TimeUtils.millis();

		// Create a fps logger
		fpslogger = new FPSLogger();

	}

	@Override
	public void dispose() {
		batch.dispose();
		font.dispose();
	}

	@Override
	public void render() {
		// Logs the fps. Prints it once per second to the console.
		fpslogger.log();

		render_stimulus();

		// Exit if escape key is pressed or touch screen is touched
		if (Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isTouched()) {
			Gdx.app.exit();
		}
	}

	public void render_stimulus() {
		// Determine if this is a flashing frame
		long timeSinceLastFlash = TimeUtils.millis() - lastStateTime;

		// Add flashes if necessary
		if (currentFlashes.size() == 0) {
			for (int index : FLASHINDICES) {
				currentFlashes.add(index);
			}
			Collections.shuffle(currentFlashes);
		}

		boolean flash = timeSinceLastFlash > 80;
		// Clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Begin drawing
		batch.begin();

		{
			// Disable alpha blending for increased performance
			batch.disableBlending();

			if (flash) {
				// Draw part of the grid
				int index = currentFlashes.remove(0);
				// Determine if it is a column or row flash
				if (ISCOLUMNFLASH[index]) {
					for (int x = 0; x < 6; x++) {
						if (NUMBERFLASH[index] != x) {
							for (int y = 0; y < 5; y++) {
								gridGreen[x][y].draw(batch);
							}
						}
					}
				} else {
					for (int y = 0; y < 5; y++) {
						if (NUMBERFLASH[index] != y) {
							for (int x = 0; x < 6; x++) {
								gridRed[x][y].draw(batch);
							}
						}
					}
				}
			} else {
				// Draw the whole grid
				for (int x = 0; x < 6; x++) {
					for (int y = 0; y < 5; y++) {
						grid[x][y].draw(batch);
					}
				}
			}

			// Enable alpha blending again so text can be rendered properly
			batch.enableBlending();

			// Draw title
			font.draw(batch, TITLE, titleX, titleY);
		}

		// Stop drawing
		batch.end();

		// Determine if flash has passed
		if (timeSinceLastFlash >= 100) {
			lastStateTime = TimeUtils.millis();
		}

	}
}
