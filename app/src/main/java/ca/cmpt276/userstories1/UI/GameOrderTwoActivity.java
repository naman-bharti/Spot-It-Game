package ca.cmpt276.userstories1.UI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;


import ca.cmpt276.userstories1.R;
import ca.cmpt276.userstories1.model.Deck;
import ca.cmpt276.userstories1.model.ScoreBoardHelper;

import static android.os.Environment.DIRECTORY_PICTURES;

/**
 * GameAcitivity for order 2
 * Links the Deck to UI
 * Clickable Image on Draw pile
 * Pops up an alertMessage at the end of the game
 * Supports normal,easy and hard modes
 * Supports Flickr,Gallery, Spot it and Word&Image mode.
 * Supports sound features for the game
 */
public class GameOrderTwoActivity extends AppCompatActivity {

    public static final String CARD_NUMBER_PREFS = "CardNumberPrefs";
    public static final String EXPORT_BOOLEAN_PREF = "EXPORT_BOOLEAN_PREF";

    public static final String DRAW_CARD = "Draw card";
    public static final String DISCARD_CARD = "Discard card";

    ImageView draw_first;
    ImageView draw_second;
    ImageView draw_third;

    ImageView discard_first, discard_second, discard_third;

    TextView draw_one, draw_two, draw_three;
    TextView discard_one, discard_two, discard_three;

    Deck deck;
    Random random = new Random();
    List<Integer> startingCard;
    List<Integer> topOfDeck;


    boolean[] forDraw;
    boolean[] forDiscard;
    ImageView[] imageView_buttons;
    TextView[] textView_buttons;

    private Chronometer timer_for_order2;
    private ScoreBoardHelper scoreBoard;

    TypedArray theTheme, theWord;

    String getWord;
    String currGameMode;
    String currGameLevel;

    public static String nickName;

    float toRotate = 45;

    private boolean isExport;
    private boolean isDiscardExported = false;
    private static final String FLICKR_IMAGES_PREF = "FLICKR_IMAGES_PREF";

    private ArrayList<String> urls;
    private List<Integer> forDrawRotation;
    private List<Integer> forDiscardRotation;
    private List<Float> forResizingDraw;
    private List<Float> forResizingDiscard;
    private List<Integer> forTextResizeDraw;
    private List<Integer> forTextResizeDiscard;

    private MediaPlayer game_music;
    public static MediaPlayer win_sound_order;

    public static Intent makeIntent(Context context, String message) {
        Intent intent = new Intent(context, GameOrderTwoActivity.class);
        intent.putExtra("Player Name", message);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        extractDataFromIntent();
        getExportBool();

        game_music = MediaPlayer.create(GameOrderTwoActivity.this, R.raw.game_music);
        game_music.setLooping(true);
        game_music.setVolume((float) 0.1, (float) 0.1);
        game_music.start();

        win_sound_order = MediaPlayer.create(GameOrderTwoActivity.this, R.raw.win_sound);
        win_sound_order.setLooping(true);

        getSavedImages();
        SharedPreferences preferences = this.getSharedPreferences(CARD_NUMBER_PREFS, Activity.MODE_PRIVATE);
        int cards = preferences.getInt(CARD_NUMBER_PREFS, 0);
        deck = new Deck(3, cards);
        scoreBoard = new ScoreBoardHelper(this, 3, cards);


        setupIntialButtons();
        updateUI();

        timer_for_order2 = findViewById(R.id.game_timer_for_order2);
        timer_for_order2.setBase(SystemClock.elapsedRealtime());
        timer_for_order2.start();

        Picasso pic = Picasso.get();
        pic.setIndicatorsEnabled(true);
        pic.setLoggingEnabled(true);
    }

    private void getExportBool() {
        SharedPreferences sharedPrefs = getSharedPreferences(EXPORT_BOOLEAN_PREF, Activity.MODE_PRIVATE);
        isExport = sharedPrefs.getBoolean(EXPORT_BOOLEAN_PREF, false);
    }

    private void getSavedImages() {
        SharedPreferences sharedPrefs = this.getSharedPreferences(FLICKR_IMAGES_PREF, Activity.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPrefs.getString(FLICKR_IMAGES_PREF, "");
        Type type = new TypeToken<List<String>>() {
        }.getType();
        if (!(json.equals(""))) {
            urls = gson.fromJson(json, type);
        } else {
            urls = new ArrayList<>();
        }
    }

    private void setupIntialButtons() {

        draw_first = findViewById(R.id.topbutton_image);
        draw_second = findViewById(R.id.middlebutton_image);
        draw_third = findViewById(R.id.cornerbutton_image);

        discard_first = findViewById(R.id.topImage);
        discard_second = findViewById(R.id.middleImage);
        discard_third = findViewById(R.id.cornerImage);

        draw_one = findViewById(R.id.draw_textone);
        draw_two = findViewById(R.id.draw_texttwo);
        draw_three = findViewById(R.id.draw_textthree);

        discard_one = findViewById(R.id.discard_textone);
        discard_two = findViewById(R.id.discard_texttwo);
        discard_three = findViewById(R.id.discard_textthree);

        currGameMode = SettingActivity.getGameModeSelected(this);
        currGameLevel = SettingActivity.getGameLevelsSelected(this);

        forDiscard = new boolean[7];
        forDraw = new boolean[7];
        forDrawRotation = new ArrayList<>();
        forDiscardRotation = new ArrayList<>();
        forResizingDraw = new ArrayList<>();
        forResizingDiscard = new ArrayList<>();
        forTextResizeDraw = new ArrayList<>();
        forTextResizeDiscard = new ArrayList<>();

        if (currGameMode.equals("Spot it!") || currGameMode.equals("Flickr and Gallery Images")) {
            fillDiscardWithTrue();
        } else {
            fillDiscardWithRandom();
        }
        fillRotation();
        fillResizingImage();
        fillResizingWord();
        Collections.shuffle(forDrawRotation);
        Collections.shuffle(forDiscardRotation);
        Collections.shuffle(forResizingDraw);
        Collections.shuffle(forResizingDiscard);
        Collections.shuffle(forTextResizeDraw);
        Collections.shuffle(forTextResizeDiscard);
    }

    private void fillResizingWord() {
        forTextResizeDraw.addAll(Arrays.asList(18, 18, 15, 15,12 ,12 ));
        forTextResizeDiscard.addAll(Arrays.asList(18, 18, 15, 15, 20, 20));
    }

    private void fillResizingImage() {

        forResizingDraw.add((float) 0.25);
        forResizingDraw.add((float) 0.25);
        forResizingDraw.add((float) 0.50);
        forResizingDraw.add((float) 0.50);
        forResizingDraw.add((float) 1);
        forResizingDraw.add((float) 1);

        forResizingDiscard.add((float) 0.25);
        forResizingDiscard.add((float) 0.25);
        forResizingDiscard.add((float) 0.50);
        forResizingDiscard.add((float) 0.50);
        forResizingDiscard.add((float) 1);
        forResizingDiscard.add((float) 1);

    }

    private void fillRotation() {

        if (currGameLevel.equals("Normal") || currGameLevel.equals("Hard")) {
            for (int i = 0; i < 5; i++) {

                forDrawRotation.add(i);
                forDiscardRotation.add(i);
            }
        } else
            for (int i = 0; i < 5; i++) {

                forDrawRotation.add(0);
                forDiscardRotation.add(0);
            }
    }

    private void fillDiscardWithTrue() {

        for (int i = 0; i < forDiscard.length; i++) {
            forDiscard[i] = true;
        }
    }

    private void fillDiscardWithRandom() {

        for (int i = 0; i < forDiscard.length; i++) {
            forDiscard[i] = random.nextBoolean();
        }
    }


    private void extractDataFromIntent() {
        Intent intent = getIntent();
        String newMessage = intent.getStringExtra("Player Name");
        nickName = newMessage;
        if (nickName.isEmpty()) {
            nickName = "Anonymous";
        }
    }

    private void setUpClickables() {
        imageView_buttons = new ImageView[3];
        textView_buttons = new TextView[3];
        fillButtonArrays();

        for (int i = 0; i < imageView_buttons.length; i++) {
            if (startingCard.contains(topOfDeck.get(i))) {
                imageView_buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExport) {
                            exportDrawCard();
                        }
                        if (!isDiscardExported) {
                            isDiscardExported = true;
                            exportDiscardCard();
                        }
                        MediaPlayer correct_sound = MediaPlayer.create(GameOrderTwoActivity.this, R.raw.sound_for_correct_card);
                        correct_sound.seekTo(0);
                        correct_sound.setVolume(10, 10);
                        correct_sound.start();
                        Collections.shuffle(forDrawRotation);
                        Collections.shuffle(forResizingDraw);
                        Collections.shuffle(forTextResizeDraw);
                        updateUI();
                    }
                });
                textView_buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExport) {
                            exportDrawCard();
                        }
                        if (!isDiscardExported) {
                            isDiscardExported = true;
                            exportDiscardCard();
                        }
                        MediaPlayer correct_sound = MediaPlayer.create(GameOrderTwoActivity.this, R.raw.sound_for_correct_card);
                        correct_sound.seekTo(0);
                        correct_sound.setVolume(10, 10);
                        correct_sound.start();
                        Collections.shuffle(forDrawRotation);
                        Collections.shuffle(forResizingDraw);
                        Collections.shuffle(forTextResizeDraw);
                        updateUI();
                    }
                });
            } else {
                imageView_buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(GameOrderTwoActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                        MediaPlayer incorrect_sound = MediaPlayer.create(GameOrderTwoActivity.this, R.raw.sound_for_incorrect_card);
                        incorrect_sound.seekTo(0);
                        incorrect_sound.setVolume(50, 50);
                        incorrect_sound.start();
                    }
                });
                textView_buttons[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(GameOrderTwoActivity.this, "Try again!", Toast.LENGTH_SHORT).show();
                        MediaPlayer incorrect_sound = MediaPlayer.create(GameOrderTwoActivity.this, R.raw.sound_for_incorrect_card);
                        incorrect_sound.seekTo(0);
                        incorrect_sound.setVolume(50, 50);
                        incorrect_sound.start();
                    }
                });
            }

        }

    }

    private void fillButtonArrays() {
        imageView_buttons[0] = draw_first;
        imageView_buttons[1] = draw_second;
        imageView_buttons[2] = draw_third;
        textView_buttons[0] = draw_one;
        textView_buttons[1] = draw_two;
        textView_buttons[2] = draw_three;
    }

    @SuppressLint("ResourceType")
    private void updateUI() {
        if (!(deck.isEmpty())) {
            startingCard = deck.popTopCard();
            checkForDraw();
        }
        if (deck.isEmpty()) {
            timer_for_order2.stop();
            int elapsed = (int) (SystemClock.elapsedRealtime() - timer_for_order2.getBase());

            if (scoreBoard.checkScore(elapsed)) {
                scoreBoard.sentScore(nickName, elapsed);
            }
            setupGameOver();
            return;
        }

        topOfDeck = deck.getTopCard();
        String currentTheme = SettingActivity.getThemesSelected(this);
        if (currentTheme.equals("Food")) {
            theTheme = getResources().obtainTypedArray(R.array.card2);
            theWord = getResources().obtainTypedArray(R.array.word2);
        } else {
            theTheme = getResources().obtainTypedArray(R.array.card);
            theWord = getResources().obtainTypedArray(R.array.word);
        }
        if (!(currGameMode.equals("Spot it!") || currGameMode.equals("Flickr and Gallery Images"))) {
            FixDrawArray();
            FixDiscardArray();
        }


        setWordImageForDiscard();
        setWordImageForDraw();
        Collections.copy(forDiscardRotation, forDrawRotation);
        Collections.copy(forResizingDiscard, forResizingDraw);
        Collections.copy(forTextResizeDiscard, forTextResizeDraw);
        forDiscard = forDraw.clone();
        setUpClickables();
    }

    private void exportDrawCard() {
        View draw_card = findViewById(R.id.draw_card);
        Bitmap card = getBitmapFromView(draw_card);
        if (card != null) {
            saveCard(card);
        } else {
            Log.i("CardExport", "Card is NULL, not saving.");
        }
    }

    private void exportDiscardCard() {
        View discard_card = findViewById(R.id.discard_card);
        Bitmap card = getBitmapFromView(discard_card);
        if (card != null) {
            saveCard(card);
        } else {
            Log.i("CardExport", "Card is NULL, not saving.");
        }
    }

    private void saveCard(Bitmap card) {
        String root = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES).toString();
        File saveDir = new File(root + "/saved_cards");
        if (!saveDir.exists()) {
            saveDir.mkdirs();
        }
        final File file = new File(saveDir, System.currentTimeMillis() + ".png");
        try {
            FileOutputStream output = new FileOutputStream(file);
            card.compress(Bitmap.CompressFormat.PNG, 100, output);
            Log.i("CardExport", "Saving image at " + file);
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Updates the Photos app of new images
        MediaScannerConnection.scanFile(this, new String[]{file.getPath()}, new String[]{"image/png"}, null);
    }

    // CITATION: https://stackoverflow.com/questions/10374547/how-to-convert-a-linearlayout-to-image/28067777
    private Bitmap getBitmapFromView(View view) {
        if (view != null && view.getWidth() != 0) {
            Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            view.draw(canvas);
            return returnedBitmap;
        }
        return null;
    }

    private void checkForDraw() {
        if (currGameMode.equals("Spot it!") || currGameMode.equals("Flickr and Gallery Images")) {
            fillDrawWithTrue();
        } else {
            fillDrawWithRandom();
        }

    }

    private void fillDrawWithRandom() {
        for (int i = 0; i < forDraw.length; i++) {
            forDraw[i] = random.nextBoolean();
        }
    }

    private void fillDrawWithTrue() {
        for (int i = 0; i < forDraw.length; i++) {
            forDraw[i] = true;
        }
    }

    private void FixDiscardArray() {
        int i = startingCard.get(0);
        int j = startingCard.get(1);
        int k = startingCard.get(2);

        if ((forDiscard[i] == forDiscard[j]) && (forDiscard[i] == forDiscard[k])
                && (forDiscard[k] == forDiscard[i])) {

            forDiscard[i] = !(forDiscard[j]);
        }

    }

    private void FixDrawArray() {

        int i = topOfDeck.get(0);
        int j = topOfDeck.get(1);
        int k = topOfDeck.get(2);
        if ((forDraw[i] == forDraw[j]) && (forDraw[i] == forDraw[k])
                && (forDraw[k] == forDraw[i])) {

            forDraw[i] = !(forDraw[i]);
        }

    }

    private void setImage(ImageView image, TextView text, int id, int index, String cardDescription) {
        text.setVisibility(View.INVISIBLE);
        if (currGameMode.equals("Flickr and Gallery Images")) {
            Picasso.get().load(urls.get(id)).into(image);
        } else {
            Drawable draw = theTheme.getDrawable(id);
            image.setImageDrawable(draw);
        }

        if (cardDescription.equals(DRAW_CARD)) {
            image.setRotation((toRotate * forDrawRotation.get(index)));
        } else {
            image.setRotation((toRotate * forDiscardRotation.get(index)));
        }

        if (currGameLevel.equals("Hard")) {
            if (cardDescription.equals(DRAW_CARD)) {
                image.setScaleX(forResizingDraw.get(index));
                image.setScaleY(forResizingDraw.get(index));

            } else {
                image.setScaleX(forResizingDiscard.get(index));
                image.setScaleY(forResizingDiscard.get(index));
            }
        }
        image.setVisibility(View.VISIBLE);
    }

    private void setWord(ImageView image, TextView text, int id, int index, String cardDescription) {
        image.setVisibility(View.INVISIBLE);
        getWord = theWord.getString(id);
        text.setText(getWord);
        text.setTextSize(15);
        if (currGameLevel.equals("Normal") || currGameLevel.equals("Hard")) {
            if (cardDescription.equals(DRAW_CARD)) {
                text.setRotation((toRotate * forDrawRotation.get(index)));
            } else {
                text.setRotation((toRotate * forDiscardRotation.get(index)));
            }
        }
        if (currGameLevel.equals("Hard")) {
            if (cardDescription.equals(DRAW_CARD)) {
                text.setTextSize(forTextResizeDraw.get(index));
            } else {
                text.setTextSize(forTextResizeDiscard.get(index));
            }
        }
        text.setVisibility(View.VISIBLE);
    }

    private void setWordImageForDraw() {
        if (forDraw[topOfDeck.get(0)]) {
            setImage(draw_first, draw_one, topOfDeck.get(0), 0, DRAW_CARD);
        } else {
            setWord(draw_first, draw_one, topOfDeck.get(0), 0, DRAW_CARD);
        }

        if (forDraw[topOfDeck.get(1)]) {
            setImage(draw_second, draw_two, topOfDeck.get(1), 1, DRAW_CARD);
        } else {
            setWord(draw_second, draw_two, topOfDeck.get(1), 1, DRAW_CARD);
        }


        if (forDraw[topOfDeck.get(2)]) {
            setImage(draw_third, draw_three, topOfDeck.get(2), 2, DRAW_CARD);
        } else {
            setWord(draw_third, draw_three, topOfDeck.get(2), 2, DRAW_CARD);
        }

    }

    private void setWordImageForDiscard() {
        if (forDiscard[startingCard.get(0)]) {
            setImage(discard_first, discard_one, startingCard.get(0), 0, DISCARD_CARD);
        } else {
            setWord(discard_first, discard_one, startingCard.get(0), 0, DISCARD_CARD);
        }

        if (forDiscard[startingCard.get(1)]) {
            setImage(discard_second, discard_two, startingCard.get(1), 1, DISCARD_CARD);
        } else {
            setWord(discard_second, discard_two, startingCard.get(1), 1, DISCARD_CARD);
        }

        if (forDiscard[startingCard.get(2)]) {
            setImage(discard_third, discard_three, startingCard.get(2), 2, DISCARD_CARD);
        } else {
            setWord(discard_third, discard_three, startingCard.get(2), 2, DISCARD_CARD);
        }
    }

    private void setupGameOver() {
        game_music.pause();
        win_sound_order.start();
        FragmentManager fragManager = getSupportFragmentManager();
        MessageFragment dialog = new MessageFragment();
        dialog.show(fragManager, "MessageDialog");
    }

    @Override
    public void onBackPressed() {
        game_music.pause();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (win_sound_order.isPlaying()) {
            win_sound_order.pause();
        }
    }
}