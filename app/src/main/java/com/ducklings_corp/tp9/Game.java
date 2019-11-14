package com.ducklings_corp.tp9;

import android.util.Log;
import android.view.MotionEvent;

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
import org.cocos2d.types.CCSize;
import org.cocos2d.nodes.Label;

import java.nio.charset.CoderMalfunctionError;
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

    void init() {
        Director.sharedDirector().attachInView(this._gameView);
        Scene scene = startMenu();
        Director.sharedDirector().runWithScene(scene);

    }

    private Scene startMenu() {
        Scene returnScene = Scene.node();
        returnScene.addChild(new MenuLayer(_screen));

        return returnScene;
    }

    private Scene startGame() {
        Scene returnScene;
        returnScene = Scene.node();

        MainLayer mainLayer = new MainLayer(_screen);
        returnScene.addChild(mainLayer, -1);
        EntitiesLayer entitiesLayer = new EntitiesLayer(_screen);
        returnScene.addChild(entitiesLayer, 1);

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
        super.schedule("backgroundGen", SCROLL_SPEED / 2);
    }

    public void backgroundGen(float dt) {
        createBg(1);
    }

    public void endScroll(CocosNode node) {
        super.removeChild(node, true);
    }

    public void createBg(int i) {
        Sprite bg = Sprite.sprite("background.png");
        bg.setPosition(_screen.getWidth() / 2, _screen.getHeight() / 2 + _screen.getHeight() * i);

        float fWidth, fHeight;
        fWidth = _screen.getWidth() / bg.getWidth();
        fHeight = _screen.getHeight() / bg.getHeight();
        Log.d("createBg", String.format("sx %s sy %s", fWidth, fHeight));
        bg.setScaleX(fWidth);
        bg.setScaleY(fHeight);


        CallFuncN sequenceCall = CallFuncN.action(this, "endScroll");
        IntervalAction scroll = Sequence.actions(MoveBy.action(SCROLL_SPEED, 0, -_screen.height * 2), sequenceCall);
        bg.runAction(scroll);
        super.addChild(bg);

    }
}

class MenuLayer extends Layer {
    CCSize _screen;
    MenuLayer(CCSize screen) {
        this._screen = screen;
        Sprite title = Sprite.sprite("1943.png");
        Sprite start = Sprite.sprite("start.png");
        Sprite bg = Sprite.sprite("bakrgaund.jpg");
        title.setPosition(_screen.width / 2f,_screen.height / 4f * 3f);
        title.setScale(2.5f);
        start.setPosition(_screen.width / 2f,_screen.height / 4f);
        start.setScale(1.5f);
        bg.setScaleY(_screen.getHeight() / bg.getHeight());
        bg.setScaleX(_screen.getWidth() / bg.getWidth());
        bg.setPosition(_screen.width / 2f,_screen.height / 2f);
        super.addChild(bg);
        super.addChild(title);
        super.addChild(start);
    }
}

class EntitiesLayer extends Layer {
    Sprite _player;
    CCSize _screen;

    final int ENEMY_PY = 40;
    final int PLAYER_PY = 37;
    final int BULLET_PY = 25;
    float shooting_speed = 0.6f;

    public ArrayList<Sprite> _enemies = new ArrayList();
    public ArrayList<Sprite> _enemy_bullets = new ArrayList<>();
    public ArrayList<Sprite> _player_bullets = new ArrayList<>();

    private boolean _touching = false;

    public EntitiesLayer(CCSize screen) {
        _screen = screen;
        _player = Sprite.sprite("player.png");
        _player.setPosition(screen.getWidth() / 2, screen.getHeight() / 2);
        _player.runAction(ScaleBy.action(0.1f, 3, 3));
        super.addChild(_player);
        setIsTouchEnabled(true);

        super.schedule("spawnSmall", 3);
        super.schedule("detectColWithPlayer", 0.01f);
        super.schedule("enemyShoot", shooting_speed);
        super.schedule("playerShoot", 0.5f);
    }

    public void detectColWithPlayer(float dt) {
        boolean has_collided = false;
        for (CocosNode enemy : _enemies) {
            if (detectColWith(_player, enemy, ENEMY_PY + PLAYER_PY)) {
                has_collided = true;
            }
        }
        for (CocosNode bullet : _enemy_bullets) {
            if (detectColWith(_player, bullet, BULLET_PY + PLAYER_PY)) {
                has_collided = true;
            }
        }
        if(has_collided){
            Log.d("detectColWithPlayer", "Player died");
        }
    }

    public boolean detectColWith(CocosNode a, CocosNode b, float req_dist) {
        float calc_dist = pythagoras(a, b);
        // Log.d("detectColWith", String.format("%s %s",calc_dist,req_dist));
        return calc_dist < req_dist;
    }

    public float pythagoras(CocosNode a, CocosNode b) {
        float dx = Math.abs(a.getPositionX() - b.getPositionX());
        float dy = Math.abs(a.getPositionY() - b.getPositionY());
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public boolean intersectionWithSprites(Sprite Sprite1, Sprite Sprite2) {
        Boolean intersection = false;

        return intersection;
    }

    public void enemyShoot(float dt) {
        for(Sprite enemy: _enemies) {
            if(enemy.getPositionY() > 15) {
                if (Math.random() > 0.5) {
                    shoot(enemy,false);
                }
            }
        }
    }

    public void playerShoot(float dt) {
        if (_touching) {
            shoot(_player, true);
        }
    }

    public void shoot(Sprite plane, boolean is_player) {
        Sprite sprite = Sprite.sprite("bullet.png");
        int dir;
        if (is_player) {
            dir = -1;
            _player_bullets.add(sprite);
        } else {
            dir = 1;
            _enemy_bullets.add(sprite);
        }
        float x = plane.getPositionX();
        float y = plane.getPositionY();
        sprite.setPosition(x, y);
        IntervalAction action = Sequence.actions(
                MoveBy.action(4, 0, -_screen.height * dir)
        );
        sprite.setScale(2);
        sprite.runAction(action);
        super.addChild(sprite);
    }

    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        _touching = true;
        return super.ccTouchesBegan(event);
    }

    @Override
    public boolean ccTouchesMoved(MotionEvent event) {
        _player.setPosition(event.getX(), this.getHeight() - event.getY());

        return super.ccTouchesMoved(event);
    }

    @Override
    public boolean ccTouchesEnded(MotionEvent event) {
        _touching = false;
        return super.ccTouchesEnded(event);
    }

    public void spawnSmall(float dt) {
        Sprite sprite = Sprite.sprite("enemy.png");
        sprite.setPosition((float) Math.random() * _screen.getWidth(), _screen.getHeight());
        sprite.setScale(3);
        //sprite.runAction(ScaleBy.action(0.1f, 3, 3));

        float xtarget_1, xtarget_2;
        if (sprite.getPositionX() > _screen.getWidth() / 2) {
            xtarget_1 = 0;
        } else {
            xtarget_1 = _screen.getWidth();
        }

        xtarget_2 = _screen.getWidth() - xtarget_1;

        IntervalAction action = Sequence.actions(
                MoveTo.action(1, xtarget_1, _screen.getHeight() / 4 * 3),
                MoveTo.action(2, xtarget_2, _screen.getHeight() / 4 * 2),
                MoveTo.action(2, xtarget_1, _screen.getHeight() / 4 * 1),
                MoveTo.action(2, xtarget_2, 0),
                MoveBy.action(0.5f, 0, -150)/*,
                CallFuncN.action(this, "endMyLife")*/
        );

        sprite.runAction(action);
        _enemies.add(sprite);
        super.addChild(sprite);
    }

}
