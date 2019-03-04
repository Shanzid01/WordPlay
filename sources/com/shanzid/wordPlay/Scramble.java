package com.shanzid.wordPlay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

@SuppressLint({"DefaultLocale"})
public class Scramble extends Activity {
    Context context;
    ArrayList<String> lettrs;
    String word;
    String wrd = new String();
    EditText wrd_txt_edit;

    private class Scramble_proc extends AsyncTask<Void, Void, String> {
        LinearLayout ln;
        ProgressBar prog;
        String scrambled;
        String used;

        private Scramble_proc() {
            this.scrambled = new String();
            this.used = new String();
            this.ln = (LinearLayout) Scramble.this.findViewById(C0068R.id.scramble_results);
            this.prog = (ProgressBar) Scramble.this.findViewById(C0068R.id.scramble_prg);
        }

        protected void onPreExecute() {
            Scramble.this.word = Scramble.this.wrd_txt_edit.getText().toString();
            this.prog.setVisibility(0);
            Scramble.this.wrd_txt_edit.setFocusableInTouchMode(false);
            Scramble.this.wrd_txt_edit.setFocusable(false);
            if (!Scramble.this.wrd_txt_edit.getText().toString().equals(Scramble.this.wrd)) {
                this.ln.removeAllViews();
            }
        }

        protected String doInBackground(Void... arg0) {
            Scramble.this.wrd = Scramble.this.word;
            int wrd_length = Scramble.this.word.length();
            Random rndm = new Random();
            this.used = " ";
            this.scrambled = "";
            int i = 0;
            while (i < wrd_length) {
                int rndom = rndm.nextInt(wrd_length);
                if (!this.used.contains(" " + String.valueOf(rndom) + ", ")) {
                    this.used += rndom + ", ";
                    this.scrambled += Scramble.this.word.charAt(rndom);
                    i++;
                }
                i = (i + 1) - 1;
            }
            return null;
        }

        protected void onPostExecute(String s) {
            this.prog.setVisibility(8);
            TextView tv = new TextView(Scramble.this.context);
            tv.setTextSize(20.0f);
            tv.setText(this.scrambled);
            this.ln.addView(tv);
            Scramble.this.wrd_txt_edit.setFocusableInTouchMode(true);
            Scramble.this.wrd_txt_edit.setFocusable(true);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0068R.layout.activity_scramble);
        this.wrd_txt_edit = (EditText) findViewById(C0068R.id.wrd_txt);
        this.context = this;
        this.wrd_txt_edit.clearFocus();
    }

    public void scrm_start(View v) {
        this.word = this.wrd_txt_edit.getText().toString();
        this.lettrs = new ArrayList();
        if (validate()) {
            break_letters();
            new Scramble_proc().execute(new Void[0]);
        }
    }

    public boolean validate() {
        if (this.word.length() <= 0 || this.word.contains(" ")) {
            error("Please enter a valid word");
            return false;
        }
        this.word = this.word.toLowerCase();
        return true;
    }

    public void break_letters() {
        for (int i = 0; i < this.word.length(); i++) {
            this.lettrs.add(String.valueOf(this.word.charAt(i)));
        }
        ((TextView) findViewById(C0068R.id.scramble_word_ttl)).setText(this.lettrs + " (" + this.lettrs.size() + ")");
    }

    public void error(String e) {
        Toast.makeText(getBaseContext(), e, 0).show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0068R.menu.scramble, menu);
        return false;
    }

    public void onBackPressed() {
        finish();
        MainActivity.started = false;
        overridePendingTransition(C0068R.anim.right_in, C0068R.anim.left_out);
    }
}
