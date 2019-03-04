package com.shanzid.wordPlay;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Game_unsramble extends Activity {
    ArrayList<Object> btuns;
    Context context;
    ArrayList<String> lettrs;
    int score = 0;
    int scr = 0;
    ArrayList<String> words;
    boolean working;

    /* renamed from: com.shanzid.wordPlay.Game_unsramble$1 */
    class C00641 implements OnClickListener {
        C00641() {
        }

        public void onClick(View v) {
            TextView usr_txt = (TextView) Game_unsramble.this.findViewById(C0068R.id.user_txt);
            usr_txt.setText(new StringBuilder(String.valueOf(usr_txt.getText().toString())).append((String) Game_unsramble.this.lettrs.get(v.getId())).toString());
            v.setVisibility(8);
            Game_unsramble.this.btuns.add(v);
            if (usr_txt.getText().length() == ((String) Game_unsramble.this.words.get(0)).length()) {
                ((Button) Game_unsramble.this.findViewById(C0068R.id.submit_btn)).setVisibility(0);
            }
        }
    }

    /* renamed from: com.shanzid.wordPlay.Game_unsramble$2 */
    class C00652 implements DialogInterface.OnClickListener {
        C00652() {
        }

        public void onClick(DialogInterface dialog, int which) {
            Game_unsramble.this.finish();
            Game_unsramble.this.overridePendingTransition(C0068R.anim.top_in, C0068R.anim.bottom_out);
        }
    }

    /* renamed from: com.shanzid.wordPlay.Game_unsramble$3 */
    class C00663 implements DialogInterface.OnClickListener {
        C00663() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    private class GetWords extends AsyncTask<Void, String, String> {
        BufferedReader bfr;
        String dic_wrd;
        Random random;

        private GetWords() {
        }

        protected void onPreExecute() {
            Game_unsramble.this.working = true;
            ((TextView) Game_unsramble.this.findViewById(C0068R.id.user_txt)).setText("");
            ((Button) Game_unsramble.this.findViewById(C0068R.id.submit_btn)).setVisibility(8);
            LinearLayout btn_l_r2 = (LinearLayout) Game_unsramble.this.findViewById(C0068R.id.button_layout_row2);
            ((LinearLayout) Game_unsramble.this.findViewById(C0068R.id.button_layout_row1)).removeAllViews();
            btn_l_r2.removeAllViews();
            Game_unsramble.this.words = new ArrayList();
            this.random = new Random();
            Game_unsramble.this.btuns = new ArrayList();
        }

        protected String doInBackground(Void... arg0) {
            AssetManager asset = Game_unsramble.this.context.getAssets();
            for (int i = 0; i < 1; i++) {
                int row = this.random.nextInt(9994);
                try {
                    this.bfr = new BufferedReader(new InputStreamReader(asset.open("gm_wrd.dis")));
                    int count = 0;
                    do {
                        String readLine = this.bfr.readLine();
                        this.dic_wrd = readLine;
                        if (readLine == null) {
                            break;
                        }
                        count++;
                    } while (count != row);
                    Game_unsramble.this.words.add(this.dic_wrd);
                } catch (Exception e) {
                    Toast.makeText(Game_unsramble.this.context, "Error occurred when reading file.", 0).show();
                }
            }
            return "okay";
        }

        protected void onProgressUpdate(String... n) {
        }

        protected void onPostExecute(String s) {
            Game_unsramble.this.working = false;
            Game_unsramble.this.showWord();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0068R.layout.activity_game_unsramble);
        this.context = this;
        new GetWords().execute(new Void[0]);
    }

    public void showWord() {
        String word = (String) this.words.get(0);
        int wrd_length = word.length();
        Random rndm = new Random();
        String used = " ";
        String scrambled = "";
        this.lettrs = new ArrayList();
        int i = 0;
        while (i < wrd_length) {
            int rndom = rndm.nextInt(wrd_length);
            if (!used.contains(" " + String.valueOf(rndom) + ", ")) {
                used = new StringBuilder(String.valueOf(used)).append(rndom).append(", ").toString();
                scrambled = new StringBuilder(String.valueOf(scrambled)).append(word.charAt(rndom)).toString();
                this.lettrs.add(String.valueOf(word.charAt(rndom)));
                i++;
            }
            i = (i + 1) - 1;
        }
        ((TextView) findViewById(C0068R.id.given_word)).setText(scrambled);
        AddBtns(this.lettrs);
    }

    public void AddBtns(ArrayList<String> lettrs) {
        LinearLayout btn_layout_rw1 = (LinearLayout) findViewById(C0068R.id.button_layout_row1);
        LinearLayout btn_layout_rw2 = (LinearLayout) findViewById(C0068R.id.button_layout_row2);
        LayoutParams param = ((TextView) findViewById(C0068R.id.usr_score)).getLayoutParams();
        int width = this.context.getResources().getDisplayMetrics().widthPixels;
        param.height = 35;
        param.width = 35;
        int cnt = 0;
        int number = Math.round((float) (width / param.width));
        int cna = 0;
        Iterator it = lettrs.iterator();
        while (it.hasNext()) {
            String x = (String) it.next();
            Button btn = new Button(this.context);
            btn.setBackgroundResource(C0068R.drawable.game_un_btnbg);
            btn.setLayoutParams(param);
            btn.setId(cnt);
            btn.setText(x);
            btn.setOnClickListener(get(btn));
            if (cna < number) {
                btn_layout_rw1.addView(btn);
            } else {
                btn_layout_rw2.addView(btn);
            }
            cna++;
            cnt++;
        }
    }

    OnClickListener get(View button) {
        return new C00641();
    }

    public void backspace(View v) {
        TextView usr_txt = (TextView) findViewById(C0068R.id.user_txt);
        if (usr_txt.getText().length() > 0) {
            usr_txt.setText(usr_txt.getText().toString().substring(0, usr_txt.getText().length() - 1));
            ((Button) this.btuns.get(this.btuns.size() - 1)).setVisibility(0);
            this.btuns.remove(this.btuns.size() - 1);
            ((Button) findViewById(C0068R.id.submit_btn)).setVisibility(8);
        }
    }

    public void submit(View a) {
        if (((TextView) findViewById(C0068R.id.user_txt)).getText().equals(this.words.get(0))) {
            TextView usr_score = (TextView) findViewById(C0068R.id.usr_score);
            usr_score.setText(String.valueOf(Integer.valueOf(usr_score.getText().toString()).intValue() + 1));
            new GetWords().execute(new Void[0]);
            this.scr++;
            this.score++;
            if (this.scr >= 5) {
                this.scr = 0;
                Button skp = (Button) findViewById(C0068R.id.skp_btn);
                skp.setText("Skip (5)");
                skp.setEnabled(true);
                return;
            }
            return;
        }
        Toast.makeText(this.context, "Wrong", 0).show();
    }

    public void skip(View c) {
        if (!this.working) {
            Button skp = (Button) findViewById(C0068R.id.skp_btn);
            int pre = Integer.valueOf(String.valueOf(skp.getText().toString().charAt(6))).intValue();
            if (pre > 0) {
                pre--;
                if (pre == 0) {
                    skp.setEnabled(false);
                }
                skp.setText("Skip (" + pre + ")");
                new GetWords().execute(new Void[0]);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0068R.menu.game_unsramble, menu);
        return false;
    }

    public void onBackPressed() {
        if (this.score > 0) {
            Builder alertDialog = new Builder(this.context);
            alertDialog.setMessage("Are you sure you want to exit?");
            alertDialog.setPositiveButton("Yes", new C00652());
            alertDialog.setNegativeButton("No", new C00663());
            alertDialog.show();
            return;
        }
        finish();
        overridePendingTransition(C0068R.anim.top_in, C0068R.anim.bottom_out);
    }
}
