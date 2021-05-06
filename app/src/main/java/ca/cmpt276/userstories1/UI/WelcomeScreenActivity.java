package ca.cmpt276.userstories1.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import ca.cmpt276.userstories1.R;

/**
 * First activity to be seen on game
 * least for maximum 8 seconds
 * Uses image button to skip the animations
 * Timer for automatic switch to mainactivity
 * Displays author information with animation
 */
public class WelcomeScreenActivity extends AppCompatActivity {

    Timer timer;
    ImageButton button;
    Animation topAnimation, bottomAnimation;
    ImageView image;
    TextView GroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen_acitivity);
        hideGameUI();
        launchAnimations();
        launchTimer();
        skipAnimationButton();

    }

    /**
     *  Helps to hide UI such as toolbar
     *  Citation : developer.android.com/reference/android/view
     */
    private void hideGameUI() {
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

    private void launchAnimations() {
        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        image = findViewById(R.id.WelcomeLogo);
        GroupName = findViewById(R.id.designers);

        image.setAnimation(topAnimation);
        GroupName.setAnimation(bottomAnimation);
    }

    private void launchTimer() {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                launchMainActivity();
                finish();
            }
        },8000);
    }

    //Citation : Gurpreet's Assignment 3 (Author : Dr. Brian Fraser)
    // Red skip button
    // Skips welcomescreen animation
    private void skipAnimationButton() {

        button = findViewById(R.id.SkipAnimation);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.cancel();
                launchMainActivity();
                finish();
            }
        });
    }

    private void launchMainActivity() {
        Intent intent = new Intent(WelcomeScreenActivity.this, MainActivity.class);
        startActivity(intent);
    }

    // Citation :https://stackoverflow.com/questions/2354336/how-to-exit-when-back-button-is-pressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}