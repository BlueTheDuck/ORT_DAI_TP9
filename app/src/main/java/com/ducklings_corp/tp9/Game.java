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
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.CocosNode;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCColor3B;
import org.cocos2d.types.CCSize;

import java.util.ArrayList;

public class Game {
    CCGLSurfaceView _gameView;
    CCSize _screen;
    int score = 0;

    public Game(CCGLSurfaceView view) {
        Director.sharedDirector().attachInView(view);
        _gameView = view;
        _screen = Director.sharedDirector().displaySize();
        Log.d("Game", String.format("WxH %sx%s", Director.sharedDirector().displaySize().getWidth(), Director.sharedDirector().displaySize().getHeight()));
    }

    void init() {
        Director.sharedDirector().attachInView(_gameView);
        startMenu(MenuLayer.Type.START);
    }

    private void startMenu(MenuLayer.Type type) {
        Scene returnScene = Scene.node();
        returnScene.addChild(new MenuLayer(_screen, this,type));
        Director.sharedDirector().runWithScene(returnScene);
    }

    public void startGame() {
        score = 0;
        Scene returnScene = Scene.node();

        MainLayer mainLayer = new MainLayer(_screen,this);
        returnScene.addChild(mainLayer, -1);
        EntitiesLayer entitiesLayer = new EntitiesLayer(_screen,this);
        returnScene.addChild(entitiesLayer, 1);
        Director.sharedDirector().replaceScene(returnScene);
    }

    public void endGame() {
        startMenu(MenuLayer.Type.END);
    }
}

class MenuLayer extends Layer {
    enum Type {
        START,
        END,
    }

    CCSize _screen;
    Game _game;

    MenuLayer(CCSize screen, Game game,Type type) {
        _screen = screen;
        _game = game;

        if (type ==Type.START){
            Sprite title = Sprite.sprite("1943.png");
            Sprite bg = Sprite.sprite("bakrgaund.jpg");
            MenuItemImage startButton = MenuItemImage.item("start.png", "start_2.png", this, "startGame");
            Menu menu = Menu.menu(startButton);

            title.setPosition(_screen.width / 2f, _screen.height / 4f * 3f);
            title.setScale(2.5f);
            bg.setScaleY(_screen.getHeight() / bg.getHeight());
            bg.setScaleX(_screen.getWidth() / bg.getWidth());
            bg.setPosition(_screen.width / 2f, _screen.height / 2f);
            bg.setZOrder(-100);
            startButton.setPosition(_screen.width / 2f, _screen.height / 4f);
            startButton.setScale(1.5f);
            menu.setPosition(0, 0);

            super.addChild(menu);
            super.addChild(bg);
            super.addChild(title);

        } else if (type==Type.END){
            Sprite title = Sprite.sprite("1943.png");
            Sprite bg = Sprite.sprite("background.png");
            Sprite go = Sprite.sprite("game_over.png");
            MenuItemImage startButton = MenuItemImage.item("start_again.png", "start_again_2.png", this, "startGame");
            Menu menu = Menu.menu(startButton);
            Label score = Label.label("Puntuación: "+_game.score,"MS Comic Sans",70);


            title.setPosition(_screen.width / 2f, _screen.height / 5f * 4f);
            title.setScale(2.5f);
            bg.setScaleY(_screen.getHeight() / bg.getHeight());
            bg.setScaleX(_screen.getWidth() / bg.getWidth());
            bg.setPosition(_screen.width / 2f, _screen.height / 2f);
            bg.setZOrder(-100);
            go.setPosition(_screen.width / 2f, _screen.height / 2f);
            go.setScale(2f);
            startButton.setPosition(0, -_screen.height/5f*2f);
            score.setPosition(_screen.width / 2f, _screen.height/4f);
            score.setColor(new CCColor3B(0,0,0));

            super.addChild(title);
            super.addChild(bg);
            super.addChild(go);
            super.addChild(menu);
            super.addChild(score);
        }

    }

    // On click button
    public void startGame() {
        _game.startGame();
    }
}

class MainLayer extends Layer {
    final float SCROLL_SPEED = 15.0f;
    CCSize _screen;
    Game _game;
    Label _score_label;

    public MainLayer(CCSize screen,Game game) {
        String score_str = "Score: ";
        _screen = screen;
        _game = game;
        _score_label = Label.label(score_str,"Droid Sans Mono",50);
        createBg(0);
        createBg(1);
        _score_label.setPosition(score_str.length()*15f,_screen.height-50f);
        super.schedule("backgroundGen", SCROLL_SPEED / 2);
        super.addChild(_score_label);
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

class EntitiesLayer extends Layer {
    Sprite _player;
    CCSize _screen;
    Game _game;

    final int ENEMY_PY = 40;
    final int PLAYER_PY = 37;
    final int BULLET_PY = 25;
    float shooting_speed = 0.6f;//0-100: 0.6 / 100-200: 0.4 / 200-...: 0.2

    public ArrayList<Sprite> _enemies = new ArrayList();
    public ArrayList<Sprite> _enemy_bullets = new ArrayList<>();
    public ArrayList<Sprite> _player_bullets = new ArrayList<>();

    private boolean _touching = false;

    public EntitiesLayer(CCSize screen,Game game) {
        _screen = screen;
        _game = game;
        _player = Sprite.sprite("player.png");
        _player.setPosition(screen.getWidth() / 2, screen.getHeight() / 2);
        _player.runAction(ScaleBy.action(0.1f, 3, 3));
        super.addChild(_player);
        setIsTouchEnabled(true);

        super.schedule("spawnSmall", 3);
        super.schedule("detectPlayerCol", 0.001f);
        super.schedule("detectEnemyCol", 0.001f);
        super.schedule("enemyShoot", shooting_speed);
        super.schedule("playerShoot", 0.5f);
    }


    // Collision detection
    public void detectEnemyCol(float dt) {
        boolean has_collided = false;
        for (CocosNode bullet : _player_bullets) {
            for (CocosNode enemy : _enemies) {
                if (detectColWith(enemy, bullet, BULLET_PY + ENEMY_PY)) {
                    has_collided = true;
                    super.removeChild(enemy,true);
                    _enemies.remove(enemy);
                }
            }
        }
        if (has_collided) {
            _game.score += 10;
            if(_game.score > 200) {
                shooting_speed = 0.2f;
            }else if(_game.score > 100) {
                shooting_speed = 0.4f;
            }
            Log.d("detectCol", "Enemy died");
        }
    }
    public void detectPlayerCol(float dt) {
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
        if (has_collided) {
            Log.d("detectCol", "Player died");
            super.unschedule("spawnSmall");
            super.unschedule("detectPlayerCol");
            super.unschedule("detectEnemyCol");
            super.unschedule("enemyShoot");
            super.unschedule("playerShoot");
            _game.endGame();
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

    // Firing
    public void enemyShoot(float dt) {
        for (Sprite enemy : _enemies) {
            if (enemy.getPositionY() > 15) {
                if (Math.random() > 0.5) {
                    shoot(enemy, false);
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
                MoveBy.action(4, 0, -_screen.height * dir),
                CallFuncN.action(this, "endMyLife")
        );
        sprite.setScale(2);
        sprite.runAction(action);
        super.addChild(sprite);
    }

    // Move player
    @Override
    public boolean ccTouchesBegan(MotionEvent event) {
        _touching = true;
        return super.ccTouchesBegan(event);
    }
    @Override
    public boolean ccTouchesMoved(MotionEvent event) {
        _player.setPosition(event.getX(), getHeight() - event.getY());

        return super.ccTouchesMoved(event);
    }
    @Override
    public boolean ccTouchesEnded(MotionEvent event) {
        _touching = false;
        return super.ccTouchesEnded(event);
    }

    // Spawners
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
                MoveBy.action(0.5f, 0, -150),
                CallFuncN.action(this, "endMyLife")
        );

        sprite.runAction(action);
        _enemies.add(sprite);
        super.addChild(sprite);
    }

    // Thanos
    public void endMyLife(CocosNode node) {
        if(_enemies.contains(node)) {
            _enemies.remove(node);
            Log.d("cleanup", "Remove enemy");
        }
        if(_enemy_bullets.contains(node)) {
            _enemy_bullets.remove(node);
            Log.d("cleanup", "Remove enemy bullet");
        }
        if(_player_bullets.contains(node)) {
            _player_bullets.remove(node);
            Log.d("cleanup", "Remove player bullet");
        }
        // I am sorry little one
    }

}
