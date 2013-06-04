package com.me.test1screen.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.me.test1screen.test1Screen;

public class gameScreen extends abstractScreen {

	private OrthographicCamera cam;
    ShapeRenderer debugRenderer = new ShapeRenderer();
    float rotationSpeed = 0.5f;

    static final int VIEWPORT_WIDTH_UNITS  = 11;
    static final int VIEWPORT_HEIGHT_UNITS = 11;

    static final int RECT_WIDTH_UNITS  = 50;
    static final int RECT_HEIGHT_UNITS = 50;
    
    
    public gameScreen(test1Screen game) {
		super(game);
	}
    
	@Override
	public void render(float delta) {

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		handleInput();

		debugRenderer.begin(ShapeType.Rectangle);
		debugRenderer.rect( -RECT_WIDTH_UNITS/2, -RECT_HEIGHT_UNITS/2, RECT_WIDTH_UNITS, RECT_HEIGHT_UNITS);
		
		for (int i = -RECT_WIDTH_UNITS/2; i < RECT_WIDTH_UNITS/2; i++) {
			for (int j = -RECT_HEIGHT_UNITS/2; j < RECT_HEIGHT_UNITS/2; j++) {
				debugRenderer.rect(i,j,1,1);
			}
			
		}
		debugRenderer.setProjectionMatrix(cam.combined);
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
                cam.zoom += 0.02;
        }
        if(Gdx.input.isKeyPressed(Input.Keys.Q)) {
                cam.zoom -= 0.02;
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
