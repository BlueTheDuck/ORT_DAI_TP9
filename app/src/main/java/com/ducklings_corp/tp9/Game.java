package com.ducklings_corp.tp9;

import android.util.Log;

import org.cocos2d.actions.instant.CallFuncN;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCPoint;
import org.cocos2d.types.CCSize;

import java.util.ArrayList;

public class Game {
    CCGLSurfaceView _gameView;
    CCSize _screen;

    public Game(CCGLSurfaceView view) {
        Director.sharedDirector().attachInView(view);
        _gameView = view;
        _screen = Director.sharedDirector().displaySize();
        Log.d("Game", String.format("WxH %sx%s", Director.sharedDirector().displaySize().getWidth(), Director.sharedDirector().displaySize().getHeight()));
    }

    void startGame() {
        Director.sharedDirector().attachInView(this._gameView);
        Scene scene = startScene();
        Director.sharedDirector().runWithScene(scene);
    }

    private Scene startScene() {
        Scene returnScene;
        returnScene = Scene.node();

        MainLayer mainLayer = new MainLayer(_screen);
        returnScene.addChild(mainLayer, -1);
        PlayerLayer playerLayer = new PlayerLayer(_screen);
        returnScene.addChild(playerLayer, 1);

        return returnScene;
    }


}

class MainLayer extends Layer {
    final float SCROLL_SPEED = 15.0f;
    CCSize _screen;

    public MainLayer(CCSize screen) {
        _screen = screen;
        createBg(0);
        createBg(1);
        super.schedule("backgroundGen",SCROLL_SPEED/2);
    }

    public void backgroundGen(float dt) {
        createBg(1);
    }

    public void endScroll(CocosNode node) {
        super.removeChild(node,true);
    }

    public void createBg(int i) {
            Sprite bg = Sprite.sprite("background.png");
            bg.setPosition(_screen.getWidth() / 2, _screen.getHeight() / 2 + _screen.getHeight() * i);

            float fWidth, fHeight;
            fWidth = _screen.getWidth() / bg.getWidth();
            fHeight = _screen.getHeight() / bg.getHeight();
            Log.d("MainLayer", String.format("sx %s sy %s", fWidth, fHeight));
            bg.runAction(ScaleBy.action(0.0000001f, fWidth, fHeight));


            CallFuncN sequenceCall = CallFuncN.action(this, "endScroll");
            IntervalAction scroll = Sequence.actions(MoveBy.action(SCROLL_SPEED, 0, -_screen.height*2), sequenceCall);
            bg.runAction(scroll);
            super.addChild(bg);

    }
}

class PlayerLayer extends Layer {
    Sprite _player;

    public PlayerLayer(CCSize screen) {
        _player = Sprite.sprite("player.png");
        _player.setPosition(screen.getWidth() / 2, screen.getHeight() / 2);
        _player.runAction(ScaleBy.action(0.1f, 3, 3));
        super.addChild(_player);
    }
}

class EnemyLayer extends Layer {
    public EnemyLayer() {

    }
}