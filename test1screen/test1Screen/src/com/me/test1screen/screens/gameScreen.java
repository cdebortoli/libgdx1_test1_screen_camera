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
import com.badlogic.gdx.math.Vector3;
import com.me.test1screen.test1Screen;

public class gameScreen extends abstractScreen {

	// Camera & shapes
	private OrthographicCamera cam;
    ShapeRenderer debugRenderer = new ShapeRenderer();
    float rotationSpeed = 0.5f;

    // Size values
    static final int VIEWPORT_WIDTH_UNITS  = 10;
    static final int VIEWPORT_HEIGHT_UNITS = 10;

    static final int RECT_WIDTH_UNITS  = 21000;
    static final int RECT_HEIGHT_UNITS = 21000;
    
    static final int CACHE_GROUP_REF = 25;
    
    // Used for images
    private TextureRegion[] squareTextures = new TextureRegion[5];
    private SpriteBatch spriteBatch;
    private SpriteCache spriteCache;
    private int _spriteCacheIndex;  
    private int[][] _spriteCachIndexArray;
    private int[][] colorIndexArray;
    
    public gameScreen(test1Screen game) {
		super(game);
		
		// Init
        spriteBatch = new SpriteBatch();

		// Load images
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/textures/textures.pack"));
        for (int i = 1; i <= 5; i++) {
			squareTextures[i-1] = atlas.findRegion("square"+String.valueOf(i));
		}
        
        // Create the random grid of color images
        colorIndexArray = new int[RECT_WIDTH_UNITS][RECT_HEIGHT_UNITS];
        for (int i = 0; i < RECT_WIDTH_UNITS; i++) {
        	for (int j = 0; j < RECT_HEIGHT_UNITS; j++) {
				int random = (int)(Math.random() * (5-1)) + 1;
				colorIndexArray[i][j] = random;
        	}
		}
        
        // Cache
//        spriteCache = new SpriteCache(RECT_WIDTH_UNITS*RECT_HEIGHT_UNITS, false);
//        _spriteCachIndexArray = new int[RECT_WIDTH_UNITS][RECT_HEIGHT_UNITS];
        //create_one_cache();
        //create_multiple_cache();
		

	}
    
    private void create_one_cache()
    {
        // Create the sprite cache
        spriteCache.beginCache();
		for (int i = 0; i < RECT_WIDTH_UNITS; i++) {
			for (int j = 0; j < RECT_HEIGHT_UNITS; j++) {
				spriteCache.add(squareTextures[colorIndexArray[i][j]], i, j, 1, 1);
			}
		}
		_spriteCacheIndex = spriteCache.endCache();
		
    }
    
    private void create_multiple_cache()
    {
		int group_by_cache_x 	= CACHE_GROUP_REF; // if 2500 columns, 2500/125 = 20 by lines
		int group_by_cache_y 	= CACHE_GROUP_REF; // if 2500 rows, 2500/125 = 20 by lines (20*20 = 400 sprite by cache) (125*125 = 15625 caches)
		int sprite_by_cache_x 	=  RECT_WIDTH_UNITS / group_by_cache_x;	// 2500 / 125 = 20
		int sprite_by_cache_y 	=  RECT_HEIGHT_UNITS / group_by_cache_y;

		for (int index_cache_x = 0; index_cache_x < group_by_cache_x; index_cache_x++) 
		{
			
			// Bound X
			int first_sprite_x = index_cache_x * sprite_by_cache_x;
			int last_sprite_x  = first_sprite_x + sprite_by_cache_x;
			for (int index_cache_y = 0; index_cache_y < group_by_cache_y; index_cache_y++) 
			{
				spriteCache.beginCache();
				
				// Bound Y
				int first_sprite_y = index_cache_y * sprite_by_cache_y;
				int last_sprite_y  = first_sprite_y + sprite_by_cache_y;
				
				for (int i = first_sprite_x; i < last_sprite_x; i++) 
				{
					for (int j = first_sprite_y; j < last_sprite_y; j++) 
					{
						spriteCache.add(squareTextures[colorIndexArray[i][j]], i, j, 1, 1);
					}
				}

				Gdx.app.log("CACHE CREATED", String.valueOf(index_cache_x) +"-"+ String.valueOf(index_cache_y));
				_spriteCachIndexArray[index_cache_x][index_cache_y] = spriteCache.endCache();
				
			}
		}
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
	    //spriteCache.setProjectionMatrix(cam.combined);
		
		/*
		 *  Sprite batch (too loud)
		 */
	    //spriteBatchExec();
		
		/*
		 *  Sprite cache (a little better, but still too loud)
		 */
	    //spriteCacheExec();
		
	    /*
	     * Sprite multie cache (better, but still too loud and very slow to create)
	     */
	    //spriteCacheSpecificExec();
	    
		/*
		 * Sprite Batch selected with frustum (too loud)
		 */
	    //spriteBatchSelectedWithFrustum();
	    
	    /*
	     * SpriteBatch Selected (camera zoom limit, but best solution)
	     */
	    spriteBatchSelectedExec();
	    
        /*
         * DEBUG RENDERER
         */
	    //debugRendererExec();

		cam.update();
		
	}

	private void spriteCacheExec()
	{
        spriteCache.begin();  
        spriteCache.draw(_spriteCacheIndex);  
        spriteCache.end();  
	}
	
	private void spriteCacheSpecificExec()
	{
        spriteCache.begin();  
		
		// Camera view bounds
		int sprite_margin = 4;

		float cam_min_x = cam.position.x - ((cam.viewportWidth * cam.zoom) / 2) - sprite_margin;
		float cam_max_x = cam.position.x + ((cam.viewportWidth * cam.zoom) / 2) + sprite_margin;

		float cam_min_y = cam.position.y - ((cam.viewportHeight * cam.zoom) / 2) - sprite_margin;
		float cam_max_y = cam.position.y + ((cam.viewportHeight * cam.zoom) / 2) + sprite_margin;
		
		
		// Cache analysis
		int group_by_cache_x 	= CACHE_GROUP_REF; // if 2500 columns, 2500/125 = 20 by lines
		int group_by_cache_y 	= CACHE_GROUP_REF; // if 2500 rows, 2500/125 = 20 by lines (20*20 = 400 sprite by cache) (125*125 = 15625 caches)
		int sprite_by_cache_x 	=  RECT_WIDTH_UNITS / group_by_cache_x;	// 2500 / 125 = 20
		int sprite_by_cache_y 	=  RECT_HEIGHT_UNITS / group_by_cache_y;

		for (int index_cache_x = 0; index_cache_x < group_by_cache_x; index_cache_x++) 
		{
			
			// Bound X
			int first_sprite_x = (index_cache_x * sprite_by_cache_x) + (-RECT_WIDTH_UNITS/2);
			int last_sprite_x  = first_sprite_x + sprite_by_cache_x;
			for (int index_cache_y = 0; index_cache_y < group_by_cache_y; index_cache_y++) 
			{
				// Bound Y
				int first_sprite_y = (index_cache_y * sprite_by_cache_y) + (-RECT_HEIGHT_UNITS/2);
				int last_sprite_y  = first_sprite_y + sprite_by_cache_y;
				
				if ((first_sprite_x >= cam_min_x && first_sprite_x <= cam_max_x) ||
					(last_sprite_x >= cam_min_x && last_sprite_x <= cam_max_x) ||
					(first_sprite_y >= cam_min_y && first_sprite_y <= cam_max_y) ||
					(last_sprite_y >= cam_min_y && last_sprite_y <= cam_max_y))
				{
					spriteCache.draw(_spriteCachIndexArray[index_cache_x][index_cache_y]);
				}
			}
		}
        spriteCache.end();  

	}
	
	private void debugRendererExec()
	{
		// Draw big square
		debugRenderer.begin(ShapeType.Rectangle);
		debugRenderer.rect( 0, 0, RECT_WIDTH_UNITS, RECT_HEIGHT_UNITS);
		
		// Draw grid
		for (int i = 0; i < RECT_WIDTH_UNITS; i++) {
			for (int j = 0; j < RECT_HEIGHT_UNITS; j++) {
				debugRenderer.rect(i,j,1,1);
			}
		}
		debugRenderer.end();
	}
	
	private void spriteBatchExec()
	{
		spriteBatch.begin();
		for (int i = 0; i < RECT_WIDTH_UNITS; i++) {
			int j_color = 0;
			for (int j = 0; j < RECT_HEIGHT_UNITS; j++) {
				spriteBatch.draw(squareTextures[colorIndexArray[i][j]], i, j, 1, 1);
			}
		}
	
		spriteBatch.end();
	}
	
	private void spriteBatchSelectedExec()
	{
		int sprite_margin = 4;
		
		spriteBatch.begin();
//		int test = 0;
//		for (int i = 0; i < RECT_WIDTH_UNITS; i++) {
//			for (int j = 0; j < RECT_HEIGHT_UNITS; j++) {
//				
//				float min_x = cam.position.x - ((cam.viewportWidth * cam.zoom) / 2) - sprite_margin;
//				float max_x = cam.position.x + ((cam.viewportWidth * cam.zoom) / 2) + sprite_margin;
//
//				float min_y = cam.position.y - ((cam.viewportHeight * cam.zoom) / 2) - sprite_margin;
//				float max_y = cam.position.y + ((cam.viewportHeight * cam.zoom) / 2) + sprite_margin;
//				
//				if ((i >= min_x && i <= max_x) && (j >= min_y && j <= max_y)) 
//				{
//					spriteBatch.draw(squareTextures[colorIndexArray[i][j]], i, j, 1, 1);
//					test++;
//				}
//			}
//		}
		int test = 0;
				
		int min_x = (int) (cam.position.x - ((cam.viewportWidth * cam.zoom) / 2) - sprite_margin);
		int max_x = (int) (cam.position.x + ((cam.viewportWidth * cam.zoom) / 2) + sprite_margin);
		int min_y = (int) (cam.position.y - ((cam.viewportHeight * cam.zoom) / 2) - sprite_margin);
		int max_y = (int) (cam.position.y + ((cam.viewportHeight * cam.zoom) / 2) + sprite_margin);
		Gdx.app.log("min X max X min Y max Y", String.valueOf(min_y)+"-"+String.valueOf(max_x)+"-"+String.valueOf(min_y)+"-"+String.valueOf(max_y));
		for (int i = min_x; i < max_x; i++) {
			for (int j = min_y; j < max_y; j++) {
				if ((i>= 0) && (i < RECT_WIDTH_UNITS) && (j >= 0) && (j < RECT_HEIGHT_UNITS))
				{
					spriteBatch.draw(squareTextures[colorIndexArray[i][j]], i, j, 1, 1);
					test++;
				}
			}
		}
		//Gdx.app.log("Total sprite drawed", String.valueOf(test));

		spriteBatch.end();	

	
	}
	
	private void spriteBatchSelectedWithFrustum()
	{
		spriteBatch.begin();
		int i_color = 0;
		int test = 0;
		for (int i = 0; i < RECT_WIDTH_UNITS; i++) {
			for (int j = 0; j < RECT_HEIGHT_UNITS; j++) {
				
				
				if (cam.frustum.pointInFrustum(new Vector3(i, j, 0)))
				{
					spriteBatch.draw(squareTextures[colorIndexArray[i][j]], i, j, 1, 1);
					test++;
				}
			}
		}
		Gdx.app.log("Total sprite drawed", String.valueOf(test));
		spriteBatch.end();	
	}
	
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
        float aspectRatio = (float) width / (float) height;
        cam = new OrthographicCamera(VIEWPORT_WIDTH_UNITS * aspectRatio, VIEWPORT_HEIGHT_UNITS);
        cam.translate((VIEWPORT_WIDTH_UNITS * aspectRatio)/2, VIEWPORT_HEIGHT_UNITS/2, 0);
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
		spriteBatch.dispose();
		//spriteCache.dispose();
		debugRenderer.dispose();		
	}
	
    private void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
        	if(cam.zoom < 1000)
                cam.zoom += 0.5;        
        	Gdx.app.log("ZOOM", String.valueOf(cam.zoom));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
        	if(cam.zoom > 0.5)
                cam.zoom -= 0.5;
        	Gdx.app.log("ZOOM", String.valueOf(cam.zoom));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                if (cam.position.x > 0 - 5)
                        cam.translate(-100, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                if (cam.position.x <  RECT_WIDTH_UNITS + 5)
                        cam.translate(10, 0, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                if (cam.position.y >  0 - 5)
                        cam.translate(0, -10, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
                if (cam.position.y <  RECT_HEIGHT_UNITS + 5)
                        cam.translate(0, 10, 0);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Z)) {
                cam.rotate(-rotationSpeed, 0, 0, 1);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
                cam.rotate(rotationSpeed, 0, 0, 1);
        }
}
}
