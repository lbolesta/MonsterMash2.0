package com.oreilly.demo.android.pa.uidemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.content.Intent;

import com.oreilly.demo.android.pa.uidemo.model.Monster;
import com.oreilly.demo.android.pa.uidemo.model.Monsters;
import com.oreilly.demo.android.pa.uidemo.view.MonsterView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {


    /** time for game */
    static int time;

    /** earned score */
    static int score;
    static TextView score_text;
    Button reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MonsterView mview = (MonsterView) findViewById(R.id.dots);
        Monsters monsters = new Monsters(4, 4);
        for (int i = 0; i < monsters.n; i++) {
            for (int j = 0; j < monsters.m; j++) {
                if (Math.random() < 0.7) monsters.addMonster(i, j, 150 + 250*j, 150 + 250*i, 100);
            }
        }
        mview.setParams(this, monsters);
        mview.setOnTouchListener(new MonsterTouchListener(monsters));

        TextView timer_text = (TextView) findViewById(R.id.textView);
        score_text = (TextView) findViewById(R.id.textView2);

        score = 0;
        score_text.setText("Score: " + score);

        time = 10;
        Timer time_timer = new Timer();
        time_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> countTimer(timer_text));
            }
        }, /*initial delay*/ 0, /*periodic delay*/ 1000);

        Timer migrate_timer = new Timer();
        migrate_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> migrate(mview, monsters));
            }
        }, 0, 1000);

        Timer change_state_timer = new Timer();
        change_state_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> changeState(mview, monsters));
            }
        }, 0, 1000);

        DisplayMetrics metrics = new DisplayMetrics();//need to add this to a function, but this is a start
        //XML as well?
        getWindowManager().getDefaultDisplay().getMetrics(metrics);


    }

    private void countTimer(TextView timer_text) {
        if (time <= 0) {
            timer_text.setText("timeout");
            return;
        }
        timer_text.setText("time: " + time);
        time--;

    }

    private void migrate(MonsterView mview, Monsters monsters) {
        monsters.migrate();
        mview.invalidate();

    }

    private void changeState(MonsterView mview, Monsters monsters) {
        monsters.changeState();

        mview.invalidate();

    }

    /**
     * Touch listener for field and monsters
     */
    private final class MonsterTouchListener implements View.OnTouchListener {

        Monsters monsters;
        public MonsterTouchListener(Monsters _monsters) {
            monsters = _monsters;
        }

        @Override
        public boolean onTouch(View view, MotionEvent me) {
            if (time <= 0) return false;

            int action = me.getAction();
            int x = (int) me.getX();
            int y = (int) me.getY();
            if (action == MotionEvent.ACTION_DOWN) {
                for (int i = 0; i < monsters.monsters.size(); i++) {
                    Monster monster = monsters.monsters.get(i);
                    if (x > monster.x - monster.r && x < monster.x + monster.r &&
                            y > monster.y - monster.r && y < monster.y + monster.r) {
                        if (monster.canBeEliminated()) clickMonster(view, i);
                        break;
                    }
                }

            }

            return true;
        }

        public void resetButton(View view)
        {
           reset =  (Button) findViewById(R.id.reset); //xml add?
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

        private void clickMonster(View view, int i) {
            monsters.monsters.remove(i);
            score += 10;
            score_text.setText("Score: " + score);

            view.invalidate();
        }
    }
}
