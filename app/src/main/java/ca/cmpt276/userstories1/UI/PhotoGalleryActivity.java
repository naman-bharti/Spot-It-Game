package ca.cmpt276.userstories1.UI;

import androidx.fragment.app.Fragment;

/**
 * Launches the photo gallery fragment
 * Code from the Chapter 27 Android Programming The Big Nerd Ranch Guide
 */

//CITATION: https://www.bignerdranch.com/solutions/AndroidProgramming3e.zip

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}