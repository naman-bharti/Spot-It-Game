package ca.cmpt276.userstories1.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import ca.cmpt276.userstories1.R;
import ca.cmpt276.userstories1.model.ScoreBoardHelper;

/**
 * Displays Top 5 HighScore (nickname,date,time)
 * Can also reset the highscores to default highscores
 * The user can also select the order and number of cards for highscore
 */
public class RankingActivity extends AppCompatActivity {
    public static final String SETTINGS_CARDS_NUM_PREF = "CardNumberPrefs";
    public static final String SETTING_ORDER_NUM_PREF = "OrderNumberPrefs";
    public static final String RANK_CARDS_NUM_PREF = "RankCardsNumPref";
    public static final String RANK_ORDER_NUM_PREF = "RankOrderNumPref";


    private ScoreBoardHelper scoreBoard;
    private int order;
    private int cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ranking);
        Toolbar toolbar = findViewById(R.id.rank_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSavedValues();

        scoreBoard = new ScoreBoardHelper(this, order, cards);

        updateScoreBoard();
    }

    private void getSavedValues() {
        SharedPreferences preferences = this.getSharedPreferences(RANK_ORDER_NUM_PREF, Activity.MODE_PRIVATE);
        order = preferences.getInt(RANK_ORDER_NUM_PREF, -1);
        if (order == -1) {
            preferences = this.getSharedPreferences(SETTING_ORDER_NUM_PREF, Activity.MODE_PRIVATE);
            order = preferences.getInt(SETTING_ORDER_NUM_PREF, 3);
        }

        preferences = this.getSharedPreferences(RANK_CARDS_NUM_PREF, MODE_PRIVATE);
        cards = preferences.getInt(RANK_CARDS_NUM_PREF, -1);
        if (cards == -1) {
            preferences = this.getSharedPreferences(SETTINGS_CARDS_NUM_PREF, Activity.MODE_PRIVATE);
            cards = preferences.getInt(SETTINGS_CARDS_NUM_PREF, 0);
        }

    }

    private void updateScoreBoard() {
        JSONArray scoresJSON = scoreBoard.getScoresAsJSON();

        final TextView highScoreTitle = findViewById(R.id.highScoreText);
        final LinearLayout names = findViewById(R.id.LinearLayoutNames);
        final LinearLayout scores = findViewById(R.id.LinearLayoutScores);
        final LinearLayout ranks = findViewById(R.id.LinearLayoutRanks);
        final LinearLayout dates = findViewById(R.id.LinearLayoutDates);

        if (cards == 0) {
            String title = getString(R.string.rankTitleAll, order);
            highScoreTitle.setText(title);
        } else {
            String title = getString(R.string.rankTitle, order, cards);
            highScoreTitle.setText(title);
        }

        for (int i = 0; i < 5; i++) {
            JSONObject jsonObject;
            String scoreText;
            String nameText;
            String dateText;
            try {
                jsonObject = scoresJSON.getJSONObject(i);
                scoreText = jsonObject.getString("Score");
                nameText = jsonObject.getString("Name");
                dateText = jsonObject.getString("Date");
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }

            TextView rank = new TextView(this);
            rank.setTypeface(null, Typeface.BOLD_ITALIC);
            rank.setTextSize(14);
            rank.setText((i + 1) + ".");
            ranks.addView(rank);

            TextView name = new TextView(this);
            name.setTextSize(14);
            name.setText(nameText);
            names.addView(name);

            TextView score = new TextView(this);
            score.setTextSize(14);
            score.setText(scoreText);
            scores.addView(score);

            TextView date = new TextView(this);
            date.setTextSize(14);
            date.setText(dateText);
            dates.addView(date);
        }
    }

    private void resetScores() {
        scoreBoard.setDefaultValues();
        recreate();
    }

    private void showConfigDialog() {
        FragmentManager fm = getSupportFragmentManager();
        RankingConfigFragment rankConfig = RankingConfigFragment.newInstance("Config");
        rankConfig.show(fm, "fragment_rankingconfig");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rank_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rank_subitem2:
                resetScores();
                return true;
            case R.id.rank_subitem3:
                showConfigDialog();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}