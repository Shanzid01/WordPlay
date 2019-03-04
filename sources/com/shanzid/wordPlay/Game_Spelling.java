package com.shanzid.wordPlay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class Game_Spelling extends Activity implements OnInitListener {
    Context context;
    int scor;
    ArrayList<String> skipped;
    int sscr = 0;
    CountDownTimer tm;
    TextToSpeech tts;
    ArrayList<String> words;
    boolean working;

    private class GetWords extends AsyncTask<Void, String, String> {
        BufferedReader bfr;
        String dic;
        String dic_wrd;
        Random random;

        private GetWords() {
        }

        protected void onPreExecute() {
            Game_Spelling.this.working = true;
            ((TextView) Game_Spelling.this.findViewById(C0068R.id.spl_usr_txt)).setText("");
            Game_Spelling.this.words = new ArrayList();
            this.random = new Random();
            if (Game_Spelling.this.tm != null) {
                Game_Spelling.this.tm.cancel();
            }
        }

        protected String doInBackground(Void... arg0) {
            AssetManager asset = Game_Spelling.this.context.getAssets();
            for (int i = 0; i < 1; i++) {
                int row = 0;
                if (Game_Spelling.this.scor <= 15) {
                    row = this.random.nextInt(852);
                    this.dic = "game_easy.dis";
                } else if (Game_Spelling.this.scor > 15 && Game_Spelling.this.scor < 50) {
                    row = this.random.nextInt(1000);
                    this.dic = "game_med.dis";
                } else if (Game_Spelling.this.scor > 50) {
                    row = this.random.nextInt(1290);
                    this.dic = "game_hard.dis";
                }
                try {
                    this.bfr = new BufferedReader(new InputStreamReader(asset.open(this.dic)));
                    int count = 0;
                    do {
                        String readLine = this.bfr.readLine();
                        this.dic_wrd = readLine;
                        if (readLine == null) {
                            break;
                        }
                        count++;
                    } while (count != row);
                    Game_Spelling.this.words.add(this.dic_wrd);
                    this.bfr.close();
                } catch (Exception e) {
                    Toast.makeText(Game_Spelling.this.context, "Error occurred when reading file.", 0).show();
                }
            }
            return "okay";
        }

        protected void onPostExecute(String s) {
            Game_Spelling.this.working = false;
            ((TextView) Game_Spelling.this.findViewById(C0068R.id.spl_answer)).setText((CharSequence) Game_Spelling.this.words.get(0));
            Game_Spelling.this.speak();
            Game_Spelling.this.time();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0068R.layout.activity_game_spelling);
        this.context = this;
        final Dialog dialog = new Dialog(this.context);
        dialog.setTitle("Spelling Game");
        dialog.setCancelable(false);
        dialog.setContentView(C0068R.layout.game_spl_main);
        ((TextView) dialog.findViewById(C0068R.id.game_spl_main_ttl)).setText(String.valueOf("Spell a given word. \nNo clues or meanings but you get to keep on going even if you spell it wrong. \nYou just gotta spell it right within 45 secs."));
        Button btn = (Button) dialog.findViewById(C0068R.id.game_spl_main_btn);
        btn.setText("Start");
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Game_Spelling.this.start();
            }
        });
        ((Button) dialog.findViewById(C0068R.id.game_spl_main_btn_exit)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Game_Spelling.this.finish();
                Game_Spelling.this.overridePendingTransition(C0068R.anim.top_out, C0068R.anim.bottom_in);
            }
        });
        dialog.show();
    }

    public void start() {
        Button skp = (Button) findViewById(C0068R.id.spl_skp_btn);
        skp.setText("Skip (3)");
        skp.setEnabled(true);
        skp.setBackgroundResource(C0068R.drawable.game_spl_skp_head);
        skp.setTextColor(getResources().getColor(C0068R.color.white));
        this.skipped = new ArrayList();
        this.tts = new TextToSpeech(this, this);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction("android.speech.tts.engine.CHECK_TTS_DATA");
        startActivityForResult(checkTTSIntent, 0);
        this.tts.setSpeechRate(0.89f);
        this.scor = 0;
        ((TextView) findViewById(C0068R.id.spl_score)).setText(String.valueOf(this.scor));
        new GetWords().execute(new Void[0]);
    }

    public void speak() {
        this.tts.speak((String) this.words.get(0), 0, null);
    }

    public void time() {
        final TextView txt_at = (TextView) findViewById(C0068R.id.time);
        this.tm = new CountDownTimer(45000, 100) {
            public void onTick(long millisUntilFinished) {
                txt_at.setText((millisUntilFinished / 1000));
            }

            public void onFinish() {
                Game_Spelling.this.game_over();
            }
        }.start();
    }

    public void onInit(int status) {
        if (status == 0) {
            int result = this.tts.setLanguage(Locale.US);
            if (result == -1 || result == -2) {
                Toast.makeText(this.context, "US Language not supported", 0).show();
                if (this.tts.setLanguage(Locale.UK) == -1 || result == -2) {
                    Toast.makeText(this.context, "UK Language not supported", 0).show();
                    final Dialog dialog = new Dialog(this.context);
                    dialog.setTitle("No TTS Engine");
                    dialog.setCancelable(false);
                    dialog.setContentView(C0068R.layout.game_spl_main);
                    ((TextView) dialog.findViewById(C0068R.id.game_spl_main_ttl)).setText(String.valueOf("Voice cannont be played; missing TTS engine.\nPlease download and install a TTS engine to continue."));
                    ((Button) dialog.findViewById(C0068R.id.game_spl_main_btn)).setVisibility(8);
                    ((Button) dialog.findViewById(C0068R.id.game_spl_main_btn_exit)).setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            dialog.dismiss();
                            Game_Spelling.this.finish();
                            Game_Spelling.this.overridePendingTransition(C0068R.anim.top_out, C0068R.anim.bottom_in);
                        }
                    });
                    dialog.show();
                    return;
                }
                Toast.makeText(this.context, "Using UK accent instead.", 0).show();
                return;
            }
            return;
        }
        Toast.makeText(this.context, "Initialization failed.", 0).show();
    }

    public void skip(View c) {
        if (!this.working) {
            Button skp = (Button) findViewById(C0068R.id.spl_skp_btn);
            int pre = Integer.valueOf(String.valueOf(skp.getText().toString().charAt(6))).intValue();
            if (pre > 0) {
                pre--;
                if (pre == 0) {
                    skp.setEnabled(false);
                    skp.setBackgroundResource(C0068R.drawable.game_spl_skp_disabled);
                    skp.setTextColor(getResources().getColor(C0068R.color.black));
                    Toast.makeText(this.context, "Skips finished. Score another " + (10 - this.sscr) + " to get more 3 skips", 1).show();
                }
                this.skipped.add((String) this.words.get(0));
                skp.setText("Skip (" + pre + ")");
                final LinearLayout ans_lay = (LinearLayout) findViewById(C0068R.id.game_spl_answer_lay);
                ((TextView) findViewById(C0068R.id.spl_answer)).setText((CharSequence) this.words.get(0));
                ans_lay.setVisibility(0);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ans_lay.setVisibility(8);
                        new GetWords().execute(new Void[0]);
                    }
                }, 1500);
            }
        }
    }

    @SuppressLint({"DefaultLocale"})
    public void submit(View v) {
        String user = ((TextView) findViewById(C0068R.id.spl_usr_txt)).getText().toString().toLowerCase();
        String real_wrd = ((String) this.words.get(0)).toLowerCase();
        if (real_wrd.contains(" ")) {
            real_wrd = real_wrd.replace(" ", "");
        }
        if (user.equals(real_wrd)) {
            TextView score = (TextView) findViewById(C0068R.id.spl_score);
            score.setText(String.valueOf(Integer.valueOf(score.getText().toString()).intValue() + 1));
            this.scor++;
            this.sscr++;
            Button skp = (Button) findViewById(C0068R.id.spl_skp_btn);
            int pre = Integer.valueOf(String.valueOf(skp.getText().toString().charAt(6))).intValue();
            if (this.sscr >= 10 && pre < 3) {
                this.sscr = 0;
                skp.setText("Skip (3)");
                skp.setEnabled(true);
                skp.setBackgroundResource(C0068R.drawable.game_spl_skp_head);
                skp.setTextColor(getResources().getColor(C0068R.color.white));
                Toast.makeText(this.context, "More " + (3 - pre) + " Skips achieved!", 0).show();
            }
            final LinearLayout right_lay = (LinearLayout) findViewById(C0068R.id.game_spl_correct_lay);
            right_lay.setVisibility(0);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    right_lay.setVisibility(8);
                    new GetWords().execute(new Void[0]);
                }
            }, 1500);
            return;
        }
        LinearLayout wrng_lay = (LinearLayout) findViewById(C0068R.id.game_spl_wong_lay);
        wrng_lay.setVisibility(0);
        final LinearLayout linearLayout = wrng_lay;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                linearLayout.setVisibility(8);
            }
        }, 1500);
    }

    public void speakUsr(View v) {
        speak();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C0068R.menu.game__spelling, menu);
        return false;
    }

    public void kb(View v) {
        TextView txt = (TextView) findViewById(C0068R.id.spl_usr_txt);
        txt.setText(new StringBuilder(String.valueOf(txt.getText().toString())).append(((Button) v).getText().toString()).toString());
    }

    public void backspace(View v) {
        TextView usr_txt = (TextView) findViewById(C0068R.id.spl_usr_txt);
        if (usr_txt.getText().length() > 0) {
            usr_txt.setText(usr_txt.getText().toString().substring(0, usr_txt.getText().length() - 1));
        }
    }

    public void game_over() {
        final Dialog dialog = new Dialog(this.context);
        dialog.setTitle("Game Over");
        dialog.setCancelable(false);
        dialog.setContentView(C0068R.layout.game_spl_main);
        TextView txt = (TextView) dialog.findViewById(C0068R.id.game_spl_main_ttl);
        String comment = "";
        if (this.scor >= 3) {
            comment = "Good Try";
        }
        if (this.scor >= 10) {
            comment = "Good Job!";
        }
        if (this.scor >= 20) {
            comment = "Awesome playing!";
        }
        if (this.scor >= 30) {
            comment = "Great Work!";
        }
        if (this.scor >= 40) {
            comment = "You are one speller to look out for!";
        }
        if (this.scor >= 50) {
            comment = "Did you take a degree on spelling?";
        }
        if (this.scor >= 100) {
            comment = "You are one crazy speller!";
        }
        String skped = "";
        Iterator it = this.skipped.iterator();
        while (it.hasNext()) {
            String x = (String) it.next();
            if (x.equals(this.skipped.get(this.skipped.size() - 1))) {
                skped = new StringBuilder(String.valueOf(skped)).append(" & \"").append(x).append("\"").toString();
            } else {
                skped = new StringBuilder(String.valueOf(skped)).append(", \"").append(x).append("\"").toString();
            }
        }
        String result = "";
        if (!skped.equals("")) {
            result = "\nYou skipped " + skped;
        }
        txt.setText(String.valueOf("You scored " + this.scor + "\nThe word was \"" + ((String) this.words.get(0)) + "\"." + result + "\n" + comment));
        Button btn = (Button) dialog.findViewById(C0068R.id.game_spl_main_btn);
        btn.setText("Try Agian");
        btn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Game_Spelling.this.start();
            }
        });
        ((Button) dialog.findViewById(C0068R.id.game_spl_main_btn_exit)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Game_Spelling.this.finish();
                Game_Spelling.this.overridePendingTransition(C0068R.anim.top_out, C0068R.anim.bottom_in);
            }
        });
        dialog.show();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.tts != null) {
            if (this.tts.isSpeaking()) {
                this.tts.stop();
            }
            this.tts.shutdown();
        }
    }

    public void onBackPressed() {
        if (this.scor > 0) {
            Builder alertDialog = new Builder(this.context);
            alertDialog.setMessage("Are you sure you want to exit?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Game_Spelling.this.finish();
                    Game_Spelling.this.overridePendingTransition(C0068R.anim.top_out, C0068R.anim.bottom_in);
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
            return;
        }
        finish();
        overridePendingTransition(C0068R.anim.top_out, C0068R.anim.bottom_in);
    }
}
