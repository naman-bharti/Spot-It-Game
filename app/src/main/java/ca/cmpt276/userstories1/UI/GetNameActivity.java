package ca.cmpt276.userstories1.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.Timer;
import java.util.TimerTask;

import ca.cmpt276.userstories1.R;

/**
 * Asks the user for name before the gamestarts
 * The user has 20 seconds to enter their name
 * If user doesn't enter any name, gives a default name to the user
 */
public class GetNameActivity extends AppCompatActivity {

    Timer timer;
    ImageButton button;
    EditText edit;
    public static String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_name);

        button = findViewById(R.id.tosaveName);
        edit = findViewById(R.id.playerNickname);

        hideNameUI();
        launchTimer();
        startGameOnSave();
    }

    /**
     * Helps to hide UI such as toolbar
     * Citation : developer.android.com/reference/android/view
     */
    private void hideNameUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        //Hide the navigation and status bar
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

        // Re-hide the status bar when it reappears
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                View decorView = getWindow().getDecorView();
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
                }
            }
        });
    }

    // *************** MAKE GAME
    private void launchGamePlay(String message) {
        String currentOrders = SettingActivity.getOrdersSelected(this);
        Intent intent;
        if (currentOrders.equals("3 images per card")) {
            intent = GameOrderTwoActivity.makeIntent(GetNameActivity.this, message);
            startActivity(intent);
        } else if (currentOrders.equals("4 images per card")) {
            intent = GameOrderThreeActivity.makeIntent(GetNameActivity.this, message);
            startActivity(intent);
        } else { //6 images per card
            intent = GameOrderFiveActivity.makeIntent(GetNameActivity.this, message);
            startActivity(intent);
        }
        finish();
    }

    private void startGameOnSave() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                message = edit.getText().toString();

                if (message.isEmpty()) {
                    message = "Anonymous";
                }
                launchGamePlay(message);
            }
        });
    }

    private void launchTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                message = "Anonymous";
                launchGamePlay(message);
            }
        }, 20000);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
    }
}