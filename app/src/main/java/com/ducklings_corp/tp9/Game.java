package com.ducklings_corp.tp9;

import android.util.Log;

import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCSize;

public class Game {
    CCGLSurfaceView _gameView;
    CCSize _screen;

    public Game(CCGLSurfaceView view) {
        Director.sharedDirector().attachInView(view);
        _gameView = view;
        _screen = Director.sharedDirector().displaySize();
        Log.d("Game",String.format("WxH %sx%s",Director.sharedDirector().displaySize().getWidth(),Director.sharedDirector().displaySize().getHeight()));
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
        returnScene.addChild(mainLayer,-1);
        PlayerLayer playerLayer = new PlayerLayer(_screen);
        returnScene.addChild(playerLayer,1);

        return returnScene;
    }


}

class MainLayer extends Layer {
     public MainLayer(CCSize screen) {
         Sprite bg = Sprite.sprite("fondo.png");
         bg.setPosition(screen.getWidth() / 2,screen.getHeight()/2);

         float fWidth, fHeight;
         fWidth=screen.getWidth()/bg.getWidth();
         fHeight=screen.getHeight()/bg.getHeight();
         Log.d("MainLayer",String.format("sx %s sy %s",fWidth,fHeight));

         bg.runAction(ScaleBy.action(0.1f,fWidth,fHeight));
         super.addChild(bg);
     }
}
class PlayerLayer extends Layer {
    Sprite _player;
    public PlayerLayer(CCSize screen) {
        _player = Sprite.sprite("player.png");
        _player.setPosition(screen.getWidth() / 2,screen.getHeight()/2);
        _player.runAction(ScaleBy.action(0.1f,3,3));
        super.addChild(_player);
    }
}
class EnemLayer extends Layer {
    public EnemLayer() {

    }
}