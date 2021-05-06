package ca.cmpt276.userstories1.UI;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ca.cmpt276.userstories1.R;


/**
 * Creates Fragment to Launch
 * Helps in Flickr Feature
 */


//Code from the Chapter 27 Android Programming The Big Nerd Ranch Guide
// CITATION: https://www.bignerdranch.com/solutions/AndroidProgramming3e.zip

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId() { return R.layout.activity_fragment;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}