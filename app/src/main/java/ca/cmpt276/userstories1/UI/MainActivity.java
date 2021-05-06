package ca.cmpt276.userstories1.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import ca.cmpt276.userstories1.R;

/**
 * Main Menu
 * Is the main menu of game
 * User can select either to play game,view highscore,view rule or change configuration of game
 */
public class MainActivity extends AppCompatActivity {

    Button startBtn;
    Button rankingBtn;
    Button settingBtn;
    Button rulesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.main_start_btn);
        rankingBtn = findViewById(R.id.main_rank_btn);
        settingBtn = findViewById(R.id.main_setting_btn);
        rulesBtn = findViewById(R.id.main_rule_btn);

        //Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getMenuButtons();
    }

    private void getMenuButtons() {

        startBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GetNameActivity.class);
                startActivity(i);
            }
        });

        rankingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RankingActivity.class);
                startActivity(i);
            }
        });

        settingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(i);
            }
        });

        rulesBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RuleActivity.class);
                startActivity(i);
            }
        });
    }

    //Citation : https://stackoverflow.com/questions/2354336/how-to-exit-when-back-button-is-pressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}