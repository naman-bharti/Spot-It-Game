package ca.cmpt276.userstories1.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *  Helps in Ranking Activity
 *  Modifies scoreboard (Highscore) using shared preferences
 */
public class ScoreBoardHelper {
    private String SAVED_SCOREBOARD = "ScoreBoard" ;
    private SharedPreferences preferences;
    private JSONArray scoreboard;

    // Creates a scoreboard class, requires current context of the activity and the "order" of cards
    public ScoreBoardHelper(Context context, int order, int cards){
        SAVED_SCOREBOARD = SAVED_SCOREBOARD + order + "_" + cards;
        preferences = context.getSharedPreferences(SAVED_SCOREBOARD, Activity.MODE_PRIVATE);
        String jsonAsString = preferences.getString(SAVED_SCOREBOARD , null);
        if (jsonAsString == null){
            setDefaultValues();
        } else {
            try {
                scoreboard = new JSONArray(jsonAsString);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Check the score against the other scores
    public boolean checkScore(int score){
        try {
            int lowestScore = scoreboard.getJSONObject(4).getInt("Score");
            if ((score / 1000) < lowestScore){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Sent the score to be set in the scoreboard
    public void sentScore(String name, int score){
        if (!(checkScore(score))){
            throw new IllegalArgumentException("Calling sentScore when it shouldn't be!");
        }
        // Citation : https://stackoverflow.com/questions/8654990/how-can-i-get-current-date-in-android/15698784
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        try {
            scoreboard.getJSONObject(4).put("Score", score / 1000);
            scoreboard.getJSONObject(4).put("Name", name);
            scoreboard.getJSONObject(4).put("Date", currentDate);
            sortScoreboard();
            saveScoreboard();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Returns the scoreboard as a json array
    public JSONArray getScoresAsJSON(){
        return scoreboard;
    }

    // Create JSONarray of default high score values
    public void setDefaultValues() {
        try {
            final String defaultScores = "[ { \"Score\": \"120\", \"Name\": \"Default Player\", \"Date\": \"N/A\" }," +
                    "{ \"Score\": \"60\", \"Name\": \"Default Player\", \"Date\": \"N/A\"  }," +
                    "{ \"Score\": \"40\", \"Name\": \"Default Player\", \"Date\": \"N/A\" }," +
                    "{ \"Score\": \"30\", \"Name\": \"Default Player\", \"Date\": \"N/A\" }," +
                    "{ \"Score\": \"300\", \"Name\": \"Default Player\", \"Date\": \"N/A\"}]";
            scoreboard = new JSONArray(defaultScores);
            sortScoreboard();
            saveScoreboard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Convert JSONarray to string to save in SharedPreferences
    private void saveScoreboard() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SAVED_SCOREBOARD, scoreboard.toString());
        editor.apply();
    }

    // Citation : https://stackoverflow.com/questions/31828574/java-android-how-to-sort-a-jsonarray-based-on-keys
    // Sorts scoreboard
    private boolean sortScoreboard() {
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonList = new ArrayList<>();

        for (int i = 0; i < scoreboard.length(); i++) {
            try {
                jsonList.add(scoreboard.getJSONObject(i));
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        Collections.sort( jsonList, new Comparator<JSONObject>() {
            public int compare(JSONObject a, JSONObject b) {
                int valA = 0;
                int valB = 0;

                try {
                    valA = Integer.parseInt(a.getString("Score"));
                    valB = Integer.parseInt(b.getString("Score"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return valA - valB;
            }
        });

        for (int i = 0; i < scoreboard.length(); i++) {
            sortedJsonArray.put(jsonList.get(i));
        }

        this.scoreboard = sortedJsonArray;
        return true;
    }


}
