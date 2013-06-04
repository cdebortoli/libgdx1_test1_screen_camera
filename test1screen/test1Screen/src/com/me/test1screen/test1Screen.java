package com.me.test1screen;

import com.badlogic.gdx.Game;
import com.me.test1screen.screens.gameScreen;

public class test1Screen extends Game {

	
	@Override
	public void create() {		
		setScreen(new gameScreen(this));
	}

}
