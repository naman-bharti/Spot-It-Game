package ca.cmpt276.userstories1.UI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.cmpt276.userstories1.R;

/**
 *  Helps in changing the theme of the Game
 *  Theme can be either Flags or Food
 *  Uses Shared Preferences
 *  Helps in exporting the card images to gallery
 *  Supports Flickr Function
 *  Can choose what order and cards the user wants
 *  Supports gallery feature
 *  Helps the user to pick the game difficulty
 */
public class SettingActivity extends AppCompatActivity {

    public static final String ORDER_TEXT = "OrderTextPref";
    public static final String ORDER_NUMBER = "OrderNumberPrefs";
    // TODO: Why is the number of cards called time? Should fix.
    public static final String TIME_PREF = "TimePref";
    public static final String CARD_NUMBER_PREFS = "CardNumberPrefs";
    public static final String EXPORT_BOOLEAN_PREF = "EXPORT_BOOLEAN_PREF";
    private static final String FLICKR_IMAGES_PREF = "FLICKR_IMAGES_PREF";


    RadioGroup grpForTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);

        grpForTime = findViewById(R.id.selectTime_grp);

        Button btnForAddFlickr = findViewById(R.id.add_imgs_to_flickr);
        btnForAddFlickr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchPhotoGalleryFrag();
            }
        });

        Button btnForEdit = findViewById(R.id.btn_for_edit_img_set);
        btnForEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SettingActivity.this,  EditPhotoSetActivity.class);
                startActivity(i);
            }
      });

        setUpThemesRadioButtons();
        setUpOrdersRadioButtons();
        setUpTimeRadioButtons();
        setUpGameModeRadioButtons();
        setUpGameLevelsRadioButtons();
        setupExportCheckBox();
        checkOrderButtons();


        Toolbar toolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Citation: https://www.youtube.com/watch?v=AmOmA6Ih3bE
        Button gallery_btn = findViewById(R.id.btn_for_gallery);
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SettingActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    return;
                }

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
    }

    private void setupExportCheckBox() {
        SharedPreferences sharedPrefs = getSharedPreferences(EXPORT_BOOLEAN_PREF, Activity.MODE_PRIVATE);
        boolean isExport = sharedPrefs.getBoolean(EXPORT_BOOLEAN_PREF, false);
        CheckBox checkBox = findViewById(R.id.exportCheckBox);
        checkBox.setChecked(isExport);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefs = getSharedPreferences(EXPORT_BOOLEAN_PREF, Activity.MODE_PRIVATE);
                if (((CheckBox) view).isChecked()) {
                    if (ContextCompat.checkSelfPermission(SettingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(SettingActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                5);
                    }
                    sharedPrefs.edit().putBoolean(EXPORT_BOOLEAN_PREF, true).apply();
                } else {
                    sharedPrefs.edit().putBoolean(EXPORT_BOOLEAN_PREF, false).apply();
                }
            }
        });

    }


    private void setUpThemesRadioButtons() {
        RadioGroup grp = findViewById(R.id.themes_grp);
        String[] themes_array = getResources().getStringArray(R.array.gameThemes);

        for (final String themes : themes_array) {
            RadioButton btn = new RadioButton(this);
            btn.setText(themes);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingActivity.this, "You selected " + themes + " theme", Toast.LENGTH_SHORT).show();

                    saveThemesSelected(themes);
                }
            });

            grp.addView(btn);
            if (themes.equals(getThemesSelected(this))) {
                btn.setChecked(true);
            }
        }
    }

    private void setUpOrdersRadioButtons() {
        RadioGroup grp = findViewById(R.id.selectOrders_grp);
        String[] orders_array = getResources().getStringArray(R.array.gameOrders);

        SharedPreferences prefs = getSharedPreferences(ORDER_TEXT, MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.default_orders);
        String order = prefs.getString(ORDER_TEXT, defaultValue);
        if (getGameModeSelected(this).equals("Flickr and Gallery Images") && !(isCardsGreaterFlickr(order))) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(ORDER_TEXT, defaultValue);
            editor.apply();
        }

        for (final String orders : orders_array) {
            RadioButton btn = new RadioButton(this);
            btn.setText(orders);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingActivity.this, "You selected " + orders, Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < grpForTime.getChildCount(); i++) {
                        RadioButton btnForTime = (RadioButton) grpForTime.getChildAt(i);
                        if (orders.equals("3 images per card")) {
                            if (btnForTime.getText().toString().equals("10 cards")) {
                                btnForTime.setEnabled(false);
                                checkIfBtnSelected(btnForTime);
                            }
                            if (btnForTime.getText().toString().equals("15 cards")) {
                                btnForTime.setEnabled(false);
                                checkIfBtnSelected(btnForTime);
                            }
                            if (btnForTime.getText().toString().equals("20 cards")) {
                                btnForTime.setEnabled(false);
                                checkIfBtnSelected(btnForTime);
                            }
                        }
                        if (orders.equals("4 images per card")) {
                            if (btnForTime.getText().toString().equals("15 cards")) {
                                btnForTime.setEnabled(false);
                                checkIfBtnSelected(btnForTime);
                            }
                            if (btnForTime.getText().toString().equals("20 cards")) {
                                btnForTime.setEnabled(false);
                                checkIfBtnSelected(btnForTime);
                            }
                            if (btnForTime.getText().toString().equals("10 cards")) {
                                btnForTime.setEnabled(true);
                            }
                        }
                        if (orders.equals("6 images per card")) {
                            if (btnForTime.getText().toString().equals("15 cards")) {
                                btnForTime.setEnabled(true);
                            }
                            if (btnForTime.getText().toString().equals("20 cards")) {
                                btnForTime.setEnabled(true);
                            }
                            if (btnForTime.getText().toString().equals("10 cards")) {
                                btnForTime.setEnabled(true);
                            }
                        }
                    }

                    saveOrdersSelected(orders);
                }
            });

            String gamemode = getGameModeSelected(SettingActivity.this);
            if (gamemode.equals("Flickr and Gallery Images") && !(isCardsGreaterFlickr(orders))) {
                btn.setEnabled(false);

            }

            grp.addView(btn);

            if (orders.equals(getOrdersSelected(this))) {
                btn.setChecked(true);
            }
        }
    }

    // Flickr radio button checker functions

    // Check for Gamemode Flickr button
    private boolean isEnoughFlickrImages() {
        ArrayList<String> imgs;

        SharedPreferences sharedPrefs = getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(FLICKR_IMAGES_PREF, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        imgs = gson.fromJson(json, type);

        sharedPrefs = getSharedPreferences(ORDER_NUMBER, Activity.MODE_PRIVATE);
        int cards = getImagesRequired(sharedPrefs.getString(ORDER_TEXT, "3 images per card"));

        if (!(json.equals("")) && imgs.size() >= cards) {
            return true;
        } else {
            return false;
        }
    }

    // Check for Orders button
    private boolean isCardsGreaterFlickr(String order_str) {
        ArrayList<String> imgs;
        int cards = getImagesRequired(order_str);
        SharedPreferences sharedPrefs = getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(FLICKR_IMAGES_PREF, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        imgs = gson.fromJson(json, type);

        if (!(json.equals("")) && imgs.size() >= cards) {
            return true;
        } else {
            return false;
        }
    }

    // Gets required cards for a specific order
    private int getImagesRequired(String order) {
        String[] orders_array = getResources().getStringArray(R.array.gameOrders);
        int cards = 31;
        switch (Arrays.asList(orders_array).indexOf(order)) {
            case 0:
                cards = 7;
                break;
            case 1:
                cards = 13;
                break;
            case 2:
                cards = 31;
        }
        return cards;
    }


    private void checkIfBtnSelected(RadioButton btnForTime) {
        if (btnForTime.isChecked()) {
            for (int j = 0; j < grpForTime.getChildCount(); j++) {
                RadioButton BFT = (RadioButton) grpForTime.getChildAt(j);
                if (BFT.getText().toString().equals("all cards")) {
                    BFT.setChecked(true);
                }
            }
        }
    }

    private void setUpTimeRadioButtons() {
        String[] time_array = getResources().getStringArray(R.array.gameTime);

        for (final String time : time_array) {
            final RadioButton btn = new RadioButton(this);
            btn.setText(time);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingActivity.this, "You selected " + time, Toast.LENGTH_SHORT).show();
                    saveTimeSelected(time);
                }
            });

            grpForTime.addView(btn);

            if (time.equals(getTimeSelected(this))) {
                btn.setChecked(true);
            }
        }
    }

    private void setUpGameModeRadioButtons() {
        RadioGroup grp = findViewById(R.id.selectGameMode_grp);
        String[] mode_array = getResources().getStringArray(R.array.gameModes);

        if (getGameModeSelected(this).equals("Flickr and Gallery Images") && !(isEnoughFlickrImages())) {
            SharedPreferences prefs = getSharedPreferences("GameModePrefs", MODE_PRIVATE);
            String defaultValue = getResources().getString(R.string.default_mode);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("GameModePrefs", defaultValue);
            editor.apply();
        }

        for (final String game_mode : mode_array) {
            RadioButton btn = new RadioButton(this);
            btn.setText(game_mode);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingActivity.this, "You selected " + game_mode, Toast.LENGTH_SHORT).show();
                    saveGameModeSelected(game_mode);
                }
            });

            grp.addView(btn);

            if (game_mode.equals("Flickr and Gallery Images") && !(isEnoughFlickrImages())) {
                btn.setEnabled(false);
                btn.setChecked(false);
            }
            if (game_mode.equals(getGameModeSelected(this))) {
                btn.setChecked(true);
            }
        }
    }

    private void setUpGameLevelsRadioButtons() {
        RadioGroup grp = findViewById(R.id.selectGameLevels_grp);
        String[] levels_array = getResources().getStringArray(R.array.gameLevels);

        for (final String levels : levels_array) {
            RadioButton btn = new RadioButton(this);
            btn.setText(levels);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(SettingActivity.this, "You selected " + levels + " theme", Toast.LENGTH_SHORT).show();

                    saveGameLevelsSelected(levels);
                }
            });

            grp.addView(btn);

            if (levels.equals(getGameLevelsSelected(this))) {
                btn.setChecked(true);
            }
        }
    }

    private void launchPhotoGalleryFrag() {
        Intent i = new Intent(this, PhotoGalleryActivity.class);
        startActivity(i);
    }

    private void saveThemesSelected(String theme) {

        SharedPreferences prefs = this.getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Theme", theme);
        editor.apply();
    }

    private int convertOrderStrToInt(String order) {
        String[] orders_array = getResources().getStringArray(R.array.gameOrders);
        int orderNumber = 3;
        switch (Arrays.asList(orders_array).indexOf(order)) {
            case 0:
                orderNumber = 3;
                break;
            case 1:
                orderNumber = 4;
                break;
            case 2:
                orderNumber = 6;
        }
        return orderNumber;
    }

    private void saveOrdersSelected(String order) {
        // Save selection for the radio buttons
        SharedPreferences prefs = this.getSharedPreferences(ORDER_TEXT, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ORDER_TEXT, order);
        editor.apply();

        // Save as int for everyone else to use because comparing strings is too annoying
        prefs = this.getSharedPreferences(ORDER_NUMBER, MODE_PRIVATE);
        editor = prefs.edit();
        int orderNumber = convertOrderStrToInt(order);
        editor.putInt(ORDER_NUMBER, orderNumber);
        editor.apply();

        //update gamemode radio buttons
        RadioGroup grp = findViewById(R.id.selectGameMode_grp);
        grp.removeAllViews();
        setUpGameModeRadioButtons();
    }

    private void saveTimeSelected(String time) {
        // Save selection for the radio buttons
        SharedPreferences prefs = this.getSharedPreferences(TIME_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(TIME_PREF, time);
        editor.apply();

        // Save as int for everyone else to use because comparing strings is too annoying
        prefs = this.getSharedPreferences(CARD_NUMBER_PREFS, MODE_PRIVATE);
        editor = prefs.edit();
        String[] time_array = getResources().getStringArray(R.array.gameTime);
        int cardsNumber = 5;
        switch (Arrays.asList(time_array).indexOf(time)) {
            case 0:
                cardsNumber = 5;
                break;
            case 1:
                cardsNumber = 10;
                break;
            case 2:
                cardsNumber = 15;
                break;
            case 3:
                cardsNumber = 20;
                break;
            case 4:
                // Leave zero for all cards
                cardsNumber = 0;
        }
        editor.putInt(CARD_NUMBER_PREFS, cardsNumber);
        editor.commit();
    }

    private void saveGameModeSelected(String game_mode) {
        SharedPreferences prefs = this.getSharedPreferences("GameModePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("GameModePrefs", game_mode);
        editor.apply();

        //update order radio buttons
        RadioGroup grp = findViewById(R.id.selectOrders_grp);
        grp.removeAllViews();
        setUpOrdersRadioButtons();
    }

    private void saveGameLevelsSelected(String theme) {

        SharedPreferences prefs = this.getSharedPreferences("GameLevelsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("GameLevelsPrefs", theme);
        editor.apply();
    }

    static public String getThemesSelected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ThemePrefs", MODE_PRIVATE);
        String defaultValue = context.getResources().getString(R.string.default_theme);
        return prefs.getString("Theme", defaultValue);
    }

    static public String getOrdersSelected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(ORDER_TEXT, MODE_PRIVATE);
        String defaultValue = context.getResources().getString(R.string.default_orders);
        return prefs.getString(ORDER_TEXT, defaultValue);
    }

    static public String getTimeSelected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TIME_PREF, MODE_PRIVATE);
        String defaultValue = context.getResources().getString(R.string.default_time);
        return prefs.getString(TIME_PREF, defaultValue);
    }

    static public String getGameModeSelected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("GameModePrefs", MODE_PRIVATE);
        String defaultValue = context.getResources().getString(R.string.default_mode);
        return prefs.getString("GameModePrefs", defaultValue);
    }

    static public String getGameLevelsSelected(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("GameLevelsPrefs", MODE_PRIVATE);
        String defaultValue = context.getResources().getString(R.string.default_level);
        return prefs.getString("GameLevelsPrefs", defaultValue);
    }

    private void checkOrderButtons() {
        if (getOrdersSelected(this).equals("3 images per card")) {
            for (int k = 0; k < grpForTime.getChildCount(); k++) {
                RadioButton btnForTime = (RadioButton) grpForTime.getChildAt(k);
                if (btnForTime.getText().toString().equals("10 cards")) {
                    btnForTime.setEnabled(false);
                    checkIfBtnSelected(btnForTime);
                }
                if (btnForTime.getText().toString().equals("15 cards")) {
                    btnForTime.setEnabled(false);
                    checkIfBtnSelected(btnForTime);
                }
                if (btnForTime.getText().toString().equals("20 cards")) {
                    btnForTime.setEnabled(false);
                    checkIfBtnSelected(btnForTime);
                }
            }
        }
        if (getOrdersSelected(this).equals("4 images per card")) {
            for (int k = 0; k < grpForTime.getChildCount(); k++) {
                RadioButton btnForTime = (RadioButton) grpForTime.getChildAt(k);
                if (btnForTime.getText().toString().equals("15 cards")) {
                    btnForTime.setEnabled(false);
                    checkIfBtnSelected(btnForTime);
                }
                if (btnForTime.getText().toString().equals("20 cards")) {
                    btnForTime.setEnabled(false);
                    checkIfBtnSelected(btnForTime);
                }
                if (btnForTime.getText().toString().equals("10 cards")) {
                    btnForTime.setEnabled(true);
                }
            }
        }
        if (getOrdersSelected(this).equals("6 images per card")) {
            for (int k = 0; k < grpForTime.getChildCount(); k++) {
                RadioButton btnForTime = (RadioButton) grpForTime.getChildAt(k);
                if (btnForTime.getText().toString().equals("15 cards")) {
                    btnForTime.setEnabled(true);
                }
                if (btnForTime.getText().toString().equals("20 cards")) {
                    btnForTime.setEnabled(true);
                }
                if (btnForTime.getText().toString().equals("10 cards")) {
                    btnForTime.setEnabled(true);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        RadioGroup grp = findViewById(R.id.selectGameMode_grp);
        grp.removeAllViews();
        setUpGameModeRadioButtons();
        RadioGroup grp2 = findViewById(R.id.selectOrders_grp);
        grp2.removeAllViews();
        setUpOrdersRadioButtons();
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            List<String> images;
            SharedPreferences sharedPrefs = this.getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sharedPrefs.getString(FLICKR_IMAGES_PREF, "");
            Type type = new TypeToken<List<String>>() {
            }.getType();
            if (!(json.equals(""))) {
                images = gson.fromJson(json, type);
            } else {
                images = new ArrayList<>();
            }

            ClipData clipdata = data.getClipData();

            if (clipdata != null) {
                for (int i = 0; i < clipdata.getItemCount(); i++) {
                    Uri imageUri = clipdata.getItemAt(i).getUri();
                    String uriStr = imageUri.toString();
                    images.add(uriStr);
                }
                saveThisList(images);
            } else {
                Uri imageUri = data.getData();
                assert imageUri != null;
                String uriStr = imageUri.toString();
                images.add(uriStr);
                saveThisList(images);
            }
        }
    }

    private void saveThisList(List<String> imgs) {
        SharedPreferences sharedPrefs = this.getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(imgs);
        editor.putString(FLICKR_IMAGES_PREF, json);
        editor.apply();
    }
}