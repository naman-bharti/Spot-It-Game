package ca.cmpt276.userstories1.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import ca.cmpt276.userstories1.R;

/**
 * Explains the user how to play the game
 * Citations for Images used throughout the application
 */
public class RuleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_rule);

        Toolbar toolbar = findViewById(R.id.rule_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activateLinksForImages();

    }

    private void activateLinksForImages() {

        TextView foodPicCite = (TextView) findViewById(R.id.rule_cite_food);
        TextView animalPicCite = (TextView) findViewById(R.id.rule_cite_flags);

        TextView welcomePic = (TextView) findViewById(R.id.rule_cite_welcome);
        TextView alertboxPic = (TextView) findViewById(R.id.rule_cite_alertbox);
        TextView namePic = (TextView) findViewById(R.id.rule_cite_name);
        TextView teamPic = (TextView) findViewById(R.id.rule_cite_teampic);

        foodPicCite.setMovementMethod(LinkMovementMethod.getInstance());
        animalPicCite.setMovementMethod(LinkMovementMethod.getInstance());

        welcomePic.setMovementMethod(LinkMovementMethod.getInstance());
        alertboxPic.setMovementMethod(LinkMovementMethod.getInstance());
        namePic.setMovementMethod(LinkMovementMethod.getInstance());
        teamPic.setMovementMethod(LinkMovementMethod.getInstance());

    }

}