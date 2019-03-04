package com.shanzid.wordPlay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {
    public static boolean started = true;
    LayoutParams mParams;
    View mView;
    WindowManager mWindowManager;
    WindowManager wm;

    /* renamed from: com.shanzid.wordPlay.MainActivity$1 */
    class C00671 implements Runnable {
        C00671() {
        }

        public void run() {
            MainActivity.this.mWindowManager.removeView(MainActivity.this.mView);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0068R.layout.activity_main);
        this.mView = LayoutInflater.from(this).inflate(C0068R.layout.begin_app, null);
        TextView wrd_ply_lbl = (TextView) this.mView.findViewById(C0068R.id.word_ply_lbl);
        TextView shan_lbl = (TextView) this.mView.findViewById(C0068R.id.by_shn_lbl);
        Typeface font = Typeface.createFromAsset(getAssets(), "font.ttf");
        wrd_ply_lbl.setTypeface(font);
        shan_lbl.setTypeface(font);
        this.mParams = new LayoutParams(-1, -1, 0, 0, 2, 8, -1);
        this.mParams.setTitle("Welcome to Word Play");
        this.mWindowManager = (WindowManager) getSystemService("window");
        this.mWindowManager.addView(this.mView, this.mParams);
        new Handler().postDelayed(new C00671(), 3000);
    }

    public void click(View v) {
        startActivity(new Intent(this, Unscramble.class));
        overridePendingTransition(C0068R.anim.right_in, C0068R.anim.left_out);
    }

    public void scramble_go(View v) {
        startActivity(new Intent(this, Scramble.class));
        overridePendingTransition(C0068R.anim.left_in, C0068R.anim.right_out);
    }

    public void game_unsc_go(View c) {
        startActivity(new Intent(this, Game_unsramble.class));
        overridePendingTransition(C0068R.anim.bottom_in, C0068R.anim.top_out);
    }

    public void game_spl_go(View v) {
        startActivity(new Intent(this, Game_Spelling.class));
        overridePendingTransition(C0068R.anim.bottom_out, C0068R.anim.top_in);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0068R.menu.main, menu);
        return false;
    }
}
