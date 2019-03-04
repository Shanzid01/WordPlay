package com.shanzid.wordPlay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

public class Unscramble extends Activity {
    static ArrayList<String> lettrs;
    static String raw_word;
    ImageView btn_go;
    ImageView btn_stp;
    CheckBox checkB;
    Context context;
    String dictionary;
    Search search;
    EditText txt_word_raw;

    private class Search extends AsyncTask<Void, String, String> {
        BufferedReader bfr;
        int count;
        LinearLayout ln;
        ProgressBar progress;

        private Search() {
            this.progress = (ProgressBar) Unscramble.this.findViewById(C0068R.id.scr_prg);
            this.ln = (LinearLayout) Unscramble.this.findViewById(C0068R.id.scr_results);
        }

        protected void onPreExecute() {
            this.ln.removeAllViews();
            this.progress.setVisibility(0);
            this.count = 0;
            Unscramble.this.btn_go.setVisibility(8);
            Unscramble.this.btn_stp.setVisibility(0);
            Unscramble.this.txt_word_raw.setFocusable(false);
            Unscramble.this.txt_word_raw.setFocusableInTouchMode(false);
            Unscramble.this.checkB.setClickable(false);
        }

        protected String doInBackground(Void... arg0) {
            String dic_wrd = new String();
            String temp_wrd = new String();
            int letr_siz = Unscramble.lettrs.size();
            this.bfr = new BufferedReader(new InputStreamReader(Unscramble.this.context.getAssets().open(Unscramble.this.dictionary)));
            while (true) {
                dic_wrd = this.bfr.readLine();
                if (dic_wrd == null) {
                    break;
                } else if (dic_wrd.length() == letr_siz) {
                    temp_wrd = dic_wrd;
                    int o = 0;
                    while (o < letr_siz) {
                        try {
                            String as = (String) Unscramble.lettrs.get(o);
                            if (!temp_wrd.contains(as)) {
                                break;
                            }
                            temp_wrd = temp_wrd.replaceFirst(as, "");
                            o++;
                        } catch (Exception e) {
                            return "Error: " + e;
                        }
                    }
                    publishProgress(new String[]{dic_wrd});
                }
            }
            if (this.count == 0) {
                return "no_match";
            }
            return "okay";
        }

        protected void onProgressUpdate(String... n) {
            TextView tv_r = new TextView(Unscramble.this.context);
            tv_r.setTextSize(19.0f);
            tv_r.setText(n[0]);
            this.ln.addView(tv_r);
            this.count++;
        }

        protected void onPostExecute(String s) {
            try {
                this.bfr.close();
            } catch (Exception e) {
                Unscramble.this.error("Error: " + e);
            }
            this.progress.setVisibility(8);
            Unscramble.this.btn_stp.setVisibility(8);
            Unscramble.this.btn_go.setVisibility(0);
            Unscramble.this.txt_word_raw.setFocusable(true);
            Unscramble.this.txt_word_raw.setFocusableInTouchMode(true);
            Unscramble.this.checkB.setClickable(true);
            if (s.equals("no_match")) {
                Toast.makeText(Unscramble.this.getBaseContext(), "Sorry no matches found", 0).show();
            } else if (s.contains("Error: ")) {
                Unscramble.this.error(s);
            } else if (s.equals("okay")) {
                String mat;
                if (this.count == 1) {
                    mat = "match";
                } else {
                    mat = "matches";
                }
                Toast.makeText(Unscramble.this.getBaseContext(), this.count + " " + mat + " found", 0).show();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0068R.layout.activity_unscramble);
        this.context = this;
        this.btn_go = (ImageView) findViewById(C0068R.id.scr_btn_go);
        this.btn_stp = (ImageView) findViewById(C0068R.id.scr_btn_stop);
        this.txt_word_raw = (EditText) findViewById(C0068R.id.scr_wrd_txt);
        this.checkB = (CheckBox) findViewById(C0068R.id.quk_src);
        this.txt_word_raw.clearFocus();
        AlphaAnimation al = new AlphaAnimation(0.5f, 0.5f);
        al.setDuration(0);
        al.setFillAfter(true);
        findViewById(C0068R.id.up_dwn).startAnimation(al);
    }

    public void start(View v) {
        raw_word = new String();
        lettrs = new ArrayList();
        if (validate()) {
            break_letters();
            possComb();
            if (this.checkB.isChecked()) {
                this.dictionary = "quick.dis";
            } else {
                this.dictionary = "master.dis";
            }
            this.search = new Search();
            this.search.execute(new Void[0]);
        }
    }

    @SuppressLint({"DefaultLocale"})
    public boolean validate() {
        raw_word = this.txt_word_raw.getText().toString();
        if (raw_word.length() <= 0 || raw_word.contains(" ")) {
            error("Please enter a valid word");
            return false;
        }
        raw_word = raw_word.toLowerCase();
        return true;
    }

    public void break_letters() {
        for (int i = 0; i < raw_word.length(); i++) {
            lettrs.add(String.valueOf(raw_word.charAt(i)));
        }
        ((TextView) findViewById(C0068R.id.scr_word_ttl)).setText(lettrs + " (" + lettrs.size() + ")");
    }

    public void possComb() {
        ArrayList<Long> nums = new ArrayList();
        for (long q = 1; q <= ((long) lettrs.size()); q++) {
            nums.add(Long.valueOf(q));
        }
        long a = 1;
        Iterator it = nums.iterator();
        while (it.hasNext()) {
            a *= ((Long) it.next()).longValue();
        }
        ((TextView) findViewById(C0068R.id.scr_possComb_ttl)).setText(" " + String.valueOf(a));
    }

    public void stop(View v) {
        if (this.search.cancel(true)) {
            this.btn_stp.setVisibility(8);
            this.btn_go.setVisibility(0);
            ((ProgressBar) findViewById(C0068R.id.scr_prg)).setVisibility(8);
            this.txt_word_raw.setFocusable(true);
            this.txt_word_raw.setFocusableInTouchMode(true);
            this.checkB.setClickable(true);
            return;
        }
        error("Task could not be closed");
    }

    public void error(String error) {
        Toast.makeText(this.context, error, 0).show();
    }

    public void up_dnw(View v) {
        LinearLayout ln = (LinearLayout) findViewById(C0068R.id.info_wrd);
        LayoutParams lnp = (LayoutParams) ln.getLayoutParams();
        AlphaAnimation al = null;
        LinearLayout img = (LinearLayout) findViewById(C0068R.id.up_dwn);
        if (ln.getHeight() > 0) {
            lnp.height = 0;
            al = new AlphaAnimation(1.0f, 0.5f);
        } else if (ln.getHeight() <= 0) {
            lnp.height = -2;
            al = new AlphaAnimation(0.5f, 1.0f);
        }
        al.setDuration(300);
        al.setFillAfter(true);
        img.startAnimation(al);
        ln.setLayoutParams(lnp);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0068R.menu.unscramble, menu);
        return false;
    }

    public void onBackPressed() {
        finish();
        MainActivity.started = false;
        overridePendingTransition(C0068R.anim.left_in, C0068R.anim.right_out);
    }
}
