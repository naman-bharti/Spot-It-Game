package ca.cmpt276.userstories1.UI;

import androidx.fragment.app.Fragment;

/**
 *  Returns the EditPhotoSet fragment
 *  Code from the Chapter 27 Android Programming The Big Nerd Ranch Guide
 */

// CITATION: https://www.bignerdranch.com/solutions/AndroidProgramming3e.zip

public class EditPhotoSetActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return EditPhotoSetFragment.newInstance();
    }
}