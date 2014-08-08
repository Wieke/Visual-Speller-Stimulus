package com.dcc.kantersw;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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

	private SpriteBatch batch;
	private BitmapFont font;

	private Sprite[] columns = new Sprite[6];
	private Sprite[] rows = new Sprite[5];

	private static final String TITLE = "Visual Speller";
	private float titleX;
	private float titleY;

	private final static boolean[] ISCOLUMNFLASH = { true, true, true, true,
			true, true, false, false, false, false, false };
	private final static int[] NUMBERFLASH = { 0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4 };
	private final static int[] FLASHINDICES = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
			10 };

	private ArrayList<Integer> currentFlashes = new ArrayList<Integer>();

	private long lastFlashTime;

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

		for (int i = 0; i < 6; i++) {
			// Get a subregion from the entire grid texture
			TextureRegion texture = new TextureRegion(gridTexture, 0 + i * 180,
					0, 180, 900);
			// Create a sprite using the subregion
			columns[i] = new Sprite(texture);
			// Change the size so it fits the screen properly
			columns[i].setSize(rowColumnSize, gridHeight);
			// Set the position of this column
			columns[i].setPosition(width / 2 - gridWidth / 2 + i
					* rowColumnSize, height / 2 - gridHeight / 2);
		}

		for (int i = 0; i < 5; i++) {
			TextureRegion texture = new TextureRegion(gridTexture, 0,
					720 - i * 180, 1080, 180);
			rows[i] = new Sprite(texture);
			rows[i].setSize(gridWidth, rowColumnSize);
			rows[i].setPosition(width / 2 - gridWidth / 2, height / 2
					- gridHeight / 2 + i * rowColumnSize);
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
		lastFlashTime = TimeUtils.millis();

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

		// Add flashes if necessary
		if (currentFlashes.size() == 0) {
			for (int index : FLASHINDICES) {
				currentFlashes.add(index);
			}
			Collections.shuffle(currentFlashes);
		}

		// Determine if this is a flashing frame
		long timeSinceLastFlash = TimeUtils.millis() - lastFlashTime;

		boolean flash = timeSinceLastFlash > 80;

		// Determine if flash has passed
		if (timeSinceLastFlash >= 100) {
			lastFlashTime = TimeUtils.millis();
		}

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
					for (int i = 0; i < 6; i++) {
						// Draw the column if it is't
						// flashing.
						if (i != NUMBERFLASH[index]) {
							columns[i].draw(batch);
						}
					}
				} else {
					for (int i = 0; i < 5; i++) {
						if (i != NUMBERFLASH[index]) {
							rows[i].draw(batch);
						}
					}
				}
			} else {
				// Draw the whole grid
				for (Sprite s : rows) {
					s.draw(batch);
				}
			}

			// Enable alpha blending again so text can be rendered properly
			batch.enableBlending();

			// Draw title
			font.draw(batch, TITLE, titleX, titleY);
		}

		// Stop drawing
		batch.end();

		// Exit if escape key is pressed or touch screen is touched
		if (Gdx.input.isKeyPressed(Keys.ESCAPE) || Gdx.input.isTouched()) {
			Gdx.app.exit();
		}
	}
}
