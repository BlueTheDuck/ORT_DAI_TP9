package com.ducklings_corp.tp9;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;

import org.cocos2d.actions.instant.CallFuncN;
import org.cocos2d.actions.interval.Animate;
import org.cocos2d.actions.interval.IntervalAction;
import org.cocos2d.actions.interval.MoveBy;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.Sequence;
import org.cocos2d.layers.Layer;
import org.cocos2d.menus.Menu;
import org.cocos2d.menus.MenuItemImage;
import org.cocos2d.nodes.Animation;
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
    MediaPlayer _BGMusic;
    MainLayer mainLayer;

    public Game(CCGLSurfaceView view) {
        Director.sharedDirector().attachInView(view);
        _gameView = view;
        _screen = Director.sharedDirector().displaySize();

        Log.d("Game", String.format("WxH %sx%s", Director.sharedDirector().displaySize().getWidth(), Director.sharedDirector().displaySize().getHeight()));

        // Set background music
        _BGMusic = MediaPlayer.create(Director.sharedDirector().getActivity(), R.raw.big_rock_by_kevin_macleod);
        _BGMusic.setVolume(0.3f, 0.3f);
        _BGMusic.setLooping(true);
        _BGMusic.start();
    }

    void init() {
        // Start game
        Director.sharedDirector().attachInView(_gameView);
        startMenu(MenuLayer.Type.START);
    }

    private void startMenu(MenuLayer.Type type) {
        Log.d("menu", "Starting main menu");
        Scene returnScene = Scene.node();
        returnScene.addChild(new MenuLayer(_screen, this, type));
        Director.sharedDirector().runWithScene(returnScene);
    }

    public void startGame() {
        Log.d("game", "Starting game");
        score = 0;
        Scene returnScene = Scene.node();

        mainLayer = new MainLayer(_screen, this);
        returnScene.addChild(mainLayer, -1);
        EntitiesLayer entitiesLayer = new EntitiesLayer(_screen, this);
        returnScene.addChild(entitiesLayer, 1);
        Director.sharedDirector().replaceScene(returnScene);
    }

    public void endGame() {
        Log.d("menu", "Game ended");
        startMenu(MenuLayer.Type.END);
    }

    public void updateScore(int add) {
        score += add;
        Log.d("score", "Setting score to " + score);
        mainLayer.updateScore(score);
    }
}

class MenuLayer extends Layer {
    enum Type {
        START,
        END,
    }

    CCSize _screen;
    Game _game;

    MenuLayer(CCSize screen, Game game, Type type) {
        _screen = screen;
        _game = game;

        if (type == Type.START) {
            Log.d("menu", "Loading start menu");
            Sprite title = Sprite.sprite("1943.png");
            Sprite bg = Sprite.sprite("background.png");
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
        } else if (type == Type.END) {
            Log.d("menu", "Loading end menu");
            Sprite title = Sprite.sprite("1943.png");
            Sprite bg = Sprite.sprite("background.png");
            Sprite go = Sprite.sprite("game_over.png");
            MenuItemImage startButton = MenuItemImage.item("start_again.png", "start_again_2.png", this, "startGame");
            Menu menu = Menu.menu(startButton);
            Label score = Label.label("Puntuación: " + _game.score, "MS Comic Sans", 70);


            title.setPosition(_screen.width / 2f, _screen.height / 5f * 4f);
            title.setScale(2.5f);
            bg.setScaleY(_screen.getHeight() / bg.getHeight());
            bg.setScaleX(_screen.getWidth() / bg.getWidth());
            bg.setPosition(_screen.width / 2f, _screen.height / 2f);
            bg.setZOrder(-100);
            go.setPosition(_screen.width / 2f, _screen.height / 2f);
            go.setScale(2f);
            startButton.setPosition(0, -_screen.height / 5f * 2f);
            score.setPosition(_screen.width / 2f, _screen.height / 4f);
            score.setColor(new CCColor3B(0, 0, 0));

            super.addChild(title);
            super.addChild(bg);
            super.addChild(go);
            super.addChild(menu);
            super.addChild(score);
        }

    }

    // On click button
    public void startGame() {
        Log.d("game", "Start game button pressed");
        _game.startGame();
    }
}

class MainLayer extends Layer {
    final float SCROLL_SPEED = 15.0f;
    CCSize _screen;
    Game _game;
    Label _score_label;

    public MainLayer(CCSize screen, Game game) {
        String score_str = "Score: 0";

        _screen = screen;
        _game = game;
        _score_label = Label.label(score_str, "Droid Sans Mono", 50);

        createBg(0);
        createBg(1);

        _score_label.setPosition(score_str.length() * 15f, _screen.height - 50f);

        super.schedule("backgroundGen", SCROLL_SPEED / 2);
        super.addChild(_score_label);
    }

    public void backgroundGen(float dt) {
        Log.d("game", "Creating new background");
        createBg(1);
    }

    public void endScroll(CocosNode node) {
        Log.d("game", "Bg scrolled out of the screen");
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
        bg.setZOrder(-100);


        CallFuncN sequenceCall = CallFuncN.action(this, "endScroll");
        IntervalAction scroll = Sequence.actions(MoveBy.action(SCROLL_SPEED, 0, -_screen.height * 2), sequenceCall);
        bg.runAction(scroll);
        super.addChild(bg);

    }

    public void updateScore(int score) {
        String score_str = "Score: " + score;
        _score_label.setString(score_str);
        _score_label.setPosition(score_str.length() * 15f, _screen.height - 50f);
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

    Animation blowUpAnimation;

    public EntitiesLayer(CCSize screen, Game game) {
        _screen = screen;
        _game = game;
        _player = Sprite.sprite("player.png");

        _player.setPosition(screen.getWidth() / 2, screen.getHeight() / 2);
        _player.runAction(ScaleBy.action(0.1f, 3, 3));
        _player.setZOrder(10);

        setIsTouchEnabled(true);

        blowUpAnimation = new Animation("blowUp", 0.2f, "exp/01.png", "exp/02.png", "exp/03.png", "exp/04.png", "exp/05.png");

        super.addChild(_player);
        super.schedule("spawnSmall", 3);
        super.schedule("detectPlayerCol", 0.001f);
        super.schedule("detectEnemyCol", 0.001f);
        super.schedule("detectBulletCol", 0.001f);
        super.schedule("enemyShoot", shooting_speed);
        super.schedule("playerShoot", 0.5f);
    }


    // Collision detection
    public void detectEnemyCol(float dt) {
        ArrayList<Integer> remove_enemies = new ArrayList<>();
        ArrayList<Integer> remove_bullets = new ArrayList<>();
        for (int b = 0; b < _player_bullets.size(); b++) {
            CocosNode bullet = _player_bullets.get(b);
            for (int e = 0; e < _enemies.size(); e++) {
                CocosNode enemy = _enemies.get(e);
                if (enemy.getUserData() == "dying") {
                    Log.d("game", "Collided with dying enemy");
                } else if (detectColWith(enemy, bullet, BULLET_PY + ENEMY_PY)) {
                    Log.d("game", "Collided with enemy");
                    enemy.setUserData("dying");
                    remove_enemies.add(e);
                    remove_bullets.add(b);
                    bullet.setPosition(-1000, -1000);
                }
            }
        }
        for (int r = remove_enemies.size() - 1; r >= 0; r--) {
            Log.d("game", "Removing enemy " + r);
            IntervalAction action = Sequence.actions(
                    CallFuncN.action(this, "playExplosionSound"),
                    Animate.action(blowUpAnimation),
                    CallFuncN.action(this, "endMyLife"));
            _enemies.get(r).stopAllActions();
            _enemies.get(r).runAction(action);
            _game.updateScore(10);
        }
        for (int r = remove_bullets.size() - 1; r >= 0; r--) {
            super.removeChild(_player_bullets.get(r), false);
            _player_bullets.remove(r);
            Log.d("detectCol", "Remove bullet that collided w/ enemy");
        }
        if (_game.score > 200) {
            shooting_speed = 0.2f;
            Log.d("game", "Speeding up shooting speed");
        } else if (_game.score > 100) {
            shooting_speed = 0.4f;
            Log.d("game", "Speeding up shooting speed");
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
            Log.d("game", "Player died");
            super.unschedule("spawnSmall");
            super.unschedule("detectPlayerCol");
            super.unschedule("detectEnemyCol");
            super.unschedule("enemyShoot");
            super.unschedule("playerShoot");
            _game.endGame();
        }
    }

    public void detectBulletCol(float dt) {
        ArrayList<Integer> remove_player_bullets = new ArrayList<>();
        ArrayList<Integer> remove_enemy_bullets = new ArrayList<>();
        for (int pb = 0; pb < _player_bullets.size(); pb++) {
            CocosNode player_bullet = _player_bullets.get(pb);
            for (int eb = 0; eb < _enemy_bullets.size(); eb++) {
                CocosNode enemy_bullet = _enemy_bullets.get(eb);
                if (detectColWith(player_bullet, enemy_bullet,BULLET_PY*2)) {
                    remove_player_bullets.add(pb);
                    remove_enemy_bullets.add(eb);
                }
            }
        }
        for (int r = remove_player_bullets.size() - 1; r >= 0; r--) {
            super.removeChild(_player_bullets.get(r),true);
            _player_bullets.remove(r);
        }
        for (int r = remove_enemy_bullets.size() - 1; r >= 0; r--) {
            super.removeChild(_enemy_bullets.get(r),true);
            _enemy_bullets.remove(r);
        }
    }

    public boolean detectColWith(CocosNode a, CocosNode b, float req_dist) {
        /*
         * If the dist between 2 nodes is < req_dist, then they are colliding
         * */
        float calc_dist = pythagoras(a, b);
        return calc_dist < req_dist;
    }

    public float pythagoras(CocosNode a, CocosNode b) {
        /*
         * This function calcs the distance between 2 nodes
         * */
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
            sprite.setRotation(180);
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
        sprite.setZOrder(10);

        // Go to the opposite side of the screen
        float xtarget_1, xtarget_2;
        if (sprite.getPositionX() > _screen.getWidth() / 2) {
            xtarget_1 = 0;
        } else {
            xtarget_1 = _screen.getWidth();
        }

        xtarget_2 = _screen.getWidth() - xtarget_1;

        // Zig-zag
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
        // Remove entites outside of the screen
        if (_enemies.contains(node)) {
            _enemies.remove(node);
            Log.d("cleanup", "Remove enemy");
        }
        if (_enemy_bullets.contains(node)) {
            _enemy_bullets.remove(node);
            Log.d("cleanup", "Remove enemy bullet");
        }
        if (_player_bullets.contains(node)) {
            _player_bullets.remove(node);
            Log.d("cleanup", "Remove player bullet");
        }
        super.removeChild(node, true);
        // I am sorry little one
    }

    public void playExplosionSound(CocosNode node) {
        MediaPlayer expPlayer = MediaPlayer.create(Director.sharedDirector().getActivity(), R.raw.explosion);
        expPlayer.start();
    }
}
