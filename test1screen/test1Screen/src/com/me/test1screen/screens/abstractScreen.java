package com.me.test1screen.screens;

import com.badlogic.gdx.Screen;
import com.me.test1screen.test1Screen;

public class abstractScreen implements Screen {

	protected test1Screen _game;
	public abstractScreen(test1Screen game)
	{
		this._game = game;
	}
	
	@Override
	public void render(float delta) {
	};

	@Override
	public void resize(int width, int height) {};

	@Override
	public void show() {};

	@Override
	public void hide() {};

	@Override
	public void pause() {};
	
	@Override
	public void resume() {};

	@Override
	public void dispose() {};

}
