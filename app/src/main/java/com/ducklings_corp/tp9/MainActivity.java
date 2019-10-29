package com.ducklings_corp.tp9;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import org.cocos2d.opengl.CCGLSurfaceView;

public class MainActivity extends Activity {
    CCGLSurfaceView mainView;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        mainView = new CCGLSurfaceView(this);
        setContentView(mainView);
    }

    @Override
    protected void onStart() {
        super.onStart();

        game = new Game(mainView);
        game.startGame();
    }
}
