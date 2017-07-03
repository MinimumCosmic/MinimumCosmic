package org.minimumcosmic.game.controller;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Kostin on 02.07.2017.
 */

public class TouchscreenController implements InputProcessor {
    @Override
    public boolean keyDown(int keycode) {
        boolean keyProcessed = false;
        if(keycode == Input.Keys.BACK){
            keyProcessed = true;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyUp(int keycode) {
        boolean keyProcessed = false;
        if(keycode == Input.Keys.BACK){
            keyProcessed = true;
        }
        else if(keycode == Input.Keys.ESCAPE){
            keyProcessed = true;
        }
        return keyProcessed;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
