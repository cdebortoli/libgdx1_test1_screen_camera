package com.me.test1screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.me.test1screen.test1Screen;

public class gameScreen extends abstractScreen {

	// Camera & shapes
	private OrthographicCamera cam;
    ShapeRenderer debugRenderer = new ShapeRenderer();
    float rotationSpeed = 0.5f;

    // Size values
    static final int VIEWPORT_WIDTH_UNITS  = 10;
    static final int VIEWPORT_HEIGHT_UNITS = 10;

    static final int RECT_WIDTH_UNITS  = 2000;
    static final int RECT_HEIGHT_UNITS = 2000;
    
    // Used for images
    private TextureRegion[] squareTextures = new TextureRegion[5];
    private SpriteBatch spriteBatch;
    private SpriteCache spriteCache;
    private int _spriteCacheIndex;  
    private int[][] colorIndexArray;
    
    public gameScreen(test1Screen game) {
		super(game);
		
        spriteBatch = new SpriteBatch();
        spriteCache = new SpriteCache(RECT_WIDTH_UNITS*RECT_HEIGHT_UNITS, false);
        
        // Create the random quadrillage
        colorIndexArray = new int[RECT_WIDTH_UNITS][RECT_HEIGHT_UNITS];
		int i_color = 0;
        for (int i = -RECT_WIDTH_UNITS/2; i < RECT_WIDTH_UNITS/2; i++) {
			int j_color = 0;
        	for (int j = -RECT_HEIGHT_UNITS/2; j < RECT_HEIGHT_UNITS/2; j++) {
				int random = (int)(Math.random() * (5-1)) + 1;
				colorIndexArray[i_color][j_color] = random;
				j_color++;
        	}
        	i_color++;
		}
		
		// Load images
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/textures/textures.pack"));
        for (int i = 1; i <= 5; i++) {
			squareTextures[i-1] = atlas.findRegion("square"+String.valueOf(i));
		}
        
        // Create the sprite cache
        spriteCache.beginCache();
		int sprite_i_color = 0;
		for (int i = -RECT_WIDTH_UNITS/2; i < RECT_WIDTH_UNITS/2; i++) {
			int sprite_j_color = 0;
			for (int j = -RECT_HEIGHT_UNITS/2; j < RECT_HEIGHT_UNITS/2; j++) {
				spriteCache.add(squareTextures[colorIndexArray[sprite_i_color][sprite_j_color]], i, j, 1, 1);
				sprite_j_color++;
			}
			sprite_i_color++;
		}
		_spriteCacheIndex = spriteCache.endCache();
	}
    
	@Override
	public void render(float delta) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

		// Handle input of the camera
		handleInput();

		// Set camera
		debugRenderer.setProjectionMatrix(cam.combined);
		spriteBatch.setProjectionMatrix(cam.combined);
	    spriteCache.setProjectionMatrix(cam.combined);
		
		// Sprite batch
//		spriteBatch.begin();
//			int i_color = 0;
//			for (int i = -RECT_WIDTH_UNITS/2; i < RECT_WIDTH_UNITS/2; i++) {
//				int j_color = 0;
//				for (int j = -RECT_HEIGHT_UNITS/2; j < RECT_HEIGHT_UNITS/2; j++) {
//					spriteBatch.draw(squareTextures[colorIndexArray[i_color][j_color]], i, j, 1, 1);
//					j_color++;
//				}
//				i_color++;
//			}
//		
//		spriteBatch.end();
		
		// Sprite cache;    
        spriteCache.begin();  
        spriteCache.draw(_spriteCacheIndex);  
        spriteCache.end();  
		
		
		// Draw big square
		debugRenderer.begin(ShapeType.Rectangle);
		debugRenderer.rect( -RECT_WIDTH_UNITS/2, -RECT_HEIGHT_UNITS/2, RECT_WIDTH_UNITS, RECT_HEIGHT_UNITS);
		
		// Draw quadrillage
		for (int i = -RECT_WIDTH_UNITS/2; i < RECT_WIDTH_UNITS/2; i++) {
			for (int j = -RECT_HEIGHT_UNITS/2; j < RECT_HEIGHT_UNITS/2; j++) {
				//debugRenderer.rect(i,j,1,1);
			}
		}
		debugRenderer.end();
		
		
		
		cam.update();
		//cam.apply(Gdx.gl10);
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
        float aspectRatio = (float) width / (float) height;
        cam = new OrthographicCamera(VIEWPORT_WIDTH_UNITS * aspectRatio, VIEWPORT_HEIGHT_UNITS);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
    private void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
        	if(cam.zoom < 1000)
                cam.zoom += 0.5;        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
        	if(cam.zoom > 0.5)
                cam.zoom -= 0.5;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (cam.position.x > (-RECT_WIDTH_UNITS / 2) - 5)
                        cam.translate(-1, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (cam.position.x <  (RECT_WIDTH_UNITS / 2) + 5)
                        cam.translate(1, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (cam.position.y >  (-RECT_HEIGHT_UNITS / 2) - 5)
                        cam.translate(0, -1, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (cam.position.y <  (RECT_HEIGHT_UNITS / 2) + 5)
                        cam.translate(0, 1, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
                cam.rotate(-rotationSpeed, 0, 0, 1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
                cam.rotate(rotationSpeed, 0, 0, 1);
        }
}
}
