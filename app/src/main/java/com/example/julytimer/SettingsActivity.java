package com.example.julytimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import yuku.ambilwarna.AmbilWarnaDialog;

@SuppressWarnings("SpellCheckingInspection")
public class SettingsActivity extends AppCompatActivity {
    View layout;
    @SuppressLint("SimpleDateFormat")
    public final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dz = DateTimeFormatter.ofPattern("HH");
    private final DateTimeFormatter dr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private SharedPreferences.Editor editor;
    LocalDateTime now;
    LocalDateTime nowWithoutZone;
    private final Timer tm2 = new Timer();
    TimerTask tmTk2;
    private ImageView backgroundImage;
    private TextView darkmodeBeginText1;
    private TextView darkmodeBeginText2;
    private TextView darkmodeEndText1;
    private TextView darkmodeEndText2;
    private TextView lbstartDate;
    private TextView oneThird;
    private TextView oneHalf;
    private TextView twoThird;
    private TextView ninetyPercent;
    private TextView showDarkmodeColors;
    private TextView showBrightmodeColors;
    Context context;
    public Bitmap BackgroundImageBitmap;
    public EditText darkmodeBegin;
    public EditText darkmodeEnd;
    public Button darkmodeSettings;
    public Button darkmodeSettingsDone;
    public Button secondsSwitch;
    public Button changeDates;
    public Button darkmodeSwitch;
    public Button backToMain;
    public Button pickBackgroundImage;
    public Button showMilestones;
    public Button hideMilestones;
    public Button changeDarkmodeButtoncolor;
    public Button changeDarkmodeTextcolor;
    public Button changeDarkmodeBackgroundcolor;
    public Button changeBrightmodeButtoncolor;
    public Button changeBrightmodeTextcolor;
    public Button changeBrightmodeBackgroundcolor;
    private int height;
    private int width;
    private int darkMode;
    public int begin = 0;
    public int end = 0;
    private String darkmodeButtoncolor;
    private String darkmodeTextcolor;
    private String darkmodeBackgroundcolor;
    private String brightmodeButtoncolor;
    private String brightmodeTextcolor;
    private String brightmodeBackgroundcolor;
    private String buttoncolor;
    private String textcolor;
    private String[] log;
    public String startDate = "2022-08-24 09:30:00.000";
    public String endDate = "2023-07-23 21:40:00.000";
    private String a;
    private int multiper;
    private int textSize;
    private long startDateUNIX, endDateUNIX;
    public boolean changedStart, changedEnd;
    private String backgroundcolor;
    SharedPreferences sharedPref;
    Activity act;


    /*
     * save(long) saves the given int for later use under the given name.
     */
    public void save(long a, String name) {
        editor.putLong(name, a);
        editor.apply();
        log("Saved long " + a + " as " + "\"" + name + "\".");
    }

    public double loadDouble(String name, double defaultValue) {
        return Double.longBitsToDouble(sharedPref.getLong(name, Double.doubleToLongBits(defaultValue)));
    }

    /*
     * save(int) saves the given int for later use under the given name.
     */
    public void save(int a, String name) {
        editor.putInt(name, a);
        editor.apply();
    }

    /*
     * save(String) saves the given String for later use under the given name.
     */
    public void save(String a, String name) {
        editor.putString(name, a);
        editor.apply();
        log("Saved String " + a + " as " + "\"" + name + "\".");
    }

    private void save(Bitmap a, String name) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        a.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        save(imageInByte, name);
        editor.apply();
    }

    private void save(byte[] a, String name) {
        String byteArrayString = Base64.getEncoder().encodeToString(a);
        System.out.println(byteArrayString);
        save(byteArrayString, name);
        editor.apply();
    }

    private byte[] loadByteArray(String name) {
        String string = sharedPref.getString(name, null);
        if(string == null) {return null;
        }
        return Base64.getDecoder().decode(string);
    }

    /*
     * All the stored Data is being loaded from the Disk.
     * The default Values are:
     *  multiper:  1
     *  darkmode:  0
     *  begin:     19
     *  end:       7
     *  startDate: "2022-08-24 09:30:00.000"
     *  endDate:   "2023-07-23 21:40:00.000"
     */
    public void loadVariables() {
        /*
         * sharedPref and editor are used to save the Variables:
         *  begin
         *  end
         *  multiper
         *  darkMode
         *  startDate
         *  endDate
         *  startDateUNIX
         *  endDateUNIX
         * df, dr, dz, dform are used for formatting purposes
         * displayMetrics is used to measure the Display
         * secondsLeft displays the seconds left to the endDate
         * secondsDone displays the seconds elapsed since the startDate
         * percent displays the percentage of seconds done
         * darkmodeBeginText1, darkmodeBeginText2, darkmodeEndText1, darkmodeEndText2 display the labels used for editing the darkmode auto mode
         * secondsSwitch switches the Displays between seconds, minutes, hours and days
         * changeDates accesses the Options to change the times
         * darkmodeSwitch switches the Colorscheme between darkmode, brightmode and the auto mode
         * darkmodeSettings accesses the Options for editing the darkmode auto mode
         * darkmodeBegin is used to let the user change the Time when the auto changes to dark mode
         * darkmodeEnd is used to let the user change the Time when the auto changes to bright mode
         * channel, channel2, notificationManager, notificationManager2, builder, builder2 are used to send the notifications
         * begin, end set the times when the dark mode begins or ends
         * height, width are used to save the height and width of the screen
         * multiper is used to save the modes in which the time is displayed:
         *  1     = seconds
         *  60    = minutes
         *  3600  = hours
         *  86400 = days
         * darkmode is used to save the mode in which the colorscheme changes:
         *  0 = bright mode
         *  1 = dark mode
         *  2 = auto mode
         * PROGRESS_CURRENT, PROGRESS_MAX are used to display the progressbar
         * textsize is used to scale the text and all display elements
         * ran is used to check weather the update() method has run already
         * timerbool is used to pause the timer
         *  0 = timer paused
         *  1 = timer running
         * changedStart, changedEnd are used to check weather the start or end date have been changed
         *  0 = not been changed
         *  1 = been changed
         * backgroundcolor, textcolor, buttoncolor are used to store the current colorscheme
         * xString, yString, zString are used to convert x, y, z to their String and surround them with Text
         * startDate, endDate, startDateUNIX, endDateUNIX are used to Store the start and end date
         * completeTime is used to save the amount of seconds between start and end date
         * x is used to save the amount of seconds passed since the startDate
         * y is used to save the amount of seconds between now and the endDate
         * z is used to save the percentage of time passed
         */
        sharedPref = getSharedPreferences("JulyTimer", Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        byte[] BackgroundImageByteArray = loadByteArray("BackgroundImage");
        if(BackgroundImageByteArray != null) {
            BackgroundImageBitmap = BitmapFactory.decodeByteArray(BackgroundImageByteArray, 0, BackgroundImageByteArray.length);
        }

        int beginSave = begin;
        int endSave   = end;
        int multiperSave = multiper;
        int darkModeSave = darkMode;
        String startDateSave = startDate;
        String endDateSave = endDate;

        begin = sharedPref.getInt("darkmodeBegin", 19);
        end = sharedPref.getInt("darkmodeEnd", 7);
        multiper = sharedPref.getInt("timeMode", 1);

        darkmodeButtoncolor = sharedPref.getString("darkmodeButtoncolor", "#464646");
        darkmodeTextcolor   = sharedPref.getString("darkmodeTextcolor", "#C5C5C5");
        darkmodeBackgroundcolor = sharedPref.getString("darkmodeBackgroundcolor", "#555555");

        brightmodeButtoncolor = sharedPref.getString("brightmodeButtoncolor", "#B7C8EA");
        brightmodeTextcolor = sharedPref.getString("brightmodeTextcolor", "#3A5A9B");
        brightmodeBackgroundcolor = sharedPref.getString("brightmodeBackgroundcolor", "#7CA3F1");

        darkMode = sharedPref.getInt("Darkmode", 2);

        startDate = sharedPref.getString("Start", "2022-08-24 09:30:00.000");
        endDate = sharedPref.getString("Ende", "2023-07-23 21:40:00.000");
        startDateUNIX = sharedPref.getLong("StartUNIX", 1661326200);
        endDateUNIX = sharedPref.getLong("EndUNIX", 1690141200);

        textSize = sharedPref.getInt("textSize", 12);

        if((beginSave != begin)||(endSave != end)||(multiperSave != multiper)||(darkModeSave != darkMode)||(!startDateSave.equals(startDate))||(!endDateSave.equals(endDate))) {
            log("Loaded Variables: ");
            log("begin         = " + begin);
            log("end           = " + end);
            log("multiper      = " + multiper);
            log("darkMode      = " + darkMode);
            log("startDate     = " + startDate);
            log("endDate       = " + endDate);
            log("startDateUNIX = " + startDateUNIX);
            log("endDateUNIX   = " + endDateUNIX);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        act = this;
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        loadVariables();
        setContentView(R.layout.activity_settings);
        layout = findViewById(R.id.Layout2);
        ConstraintLayout homeScreenLayout = (ConstraintLayout) layout;
        initialise();
        tmTk2 = new TimerTask() {
            @Override
            public void run() {
                update();
            }
        };
        showDarkmodeColors.setVisibility(View.INVISIBLE);
        changeDarkmodeBackgroundcolor.setVisibility(View.INVISIBLE);
        changeDarkmodeTextcolor.setVisibility(View.INVISIBLE);
        changeDarkmodeButtoncolor.setVisibility(View.INVISIBLE);
        showBrightmodeColors.setVisibility(View.INVISIBLE);
        changeBrightmodeBackgroundcolor.setVisibility(View.INVISIBLE);
        changeBrightmodeTextcolor.setVisibility(View.INVISIBLE);
        changeBrightmodeButtoncolor.setVisibility(View.INVISIBLE);
        homeScreenLayout.addView(backgroundImage);
        homeScreenLayout.addView(darkmodeBeginText1);
        homeScreenLayout.addView(darkmodeBeginText2);
        homeScreenLayout.addView(darkmodeEndText1);
        homeScreenLayout.addView(darkmodeEndText2);
        homeScreenLayout.addView(darkmodeBegin);
        homeScreenLayout.addView(darkmodeEnd);
        homeScreenLayout.addView(darkmodeSettings);
        homeScreenLayout.addView(darkmodeSettingsDone);
        homeScreenLayout.addView(secondsSwitch);
        homeScreenLayout.addView(changeDates);
        homeScreenLayout.addView(showDarkmodeColors);
        homeScreenLayout.addView(changeDarkmodeTextcolor);
        homeScreenLayout.addView(changeDarkmodeBackgroundcolor);
        homeScreenLayout.addView(changeDarkmodeButtoncolor);
        homeScreenLayout.addView(showBrightmodeColors);
        homeScreenLayout.addView(changeBrightmodeTextcolor);
        homeScreenLayout.addView(changeBrightmodeBackgroundcolor);
        homeScreenLayout.addView(changeBrightmodeButtoncolor);
        homeScreenLayout.addView(showMilestones);
        homeScreenLayout.addView(darkmodeSwitch);
        homeScreenLayout.addView(backToMain);
        homeScreenLayout.addView(pickBackgroundImage);
        tm2.schedule(tmTk2, 0, 500);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void initialise() {
        Context a = this;
        SharedPreferences sharedPref = getSharedPreferences("JulyTimer", Context.MODE_PRIVATE);
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        editor = sharedPref.edit();
        darkmodeBegin = new EditText(a);
        darkmodeEnd = new EditText(a);
        backgroundImage = new ImageView(a);
        darkmodeBeginText1 = new TextView(a);
        darkmodeBeginText2 = new TextView(a);
        darkmodeEndText1 = new TextView(a);
        darkmodeEndText2 = new TextView(a);
        showDarkmodeColors = new TextView(a);
        showBrightmodeColors = new TextView(a);
        secondsSwitch = new Button(a);
        changeDates = new Button(a);
        changeDarkmodeTextcolor = new Button(a);
        changeDarkmodeButtoncolor = new Button(a);
        changeDarkmodeBackgroundcolor = new Button(a);
        changeBrightmodeTextcolor = new Button(a);
        changeBrightmodeButtoncolor = new Button(a);
        changeBrightmodeBackgroundcolor = new Button(a);
        darkmodeSwitch = new Button(a);
        backToMain = new Button(a);
        pickBackgroundImage = new Button(a);
        darkmodeSettings = new Button(a);
        darkmodeSettingsDone = new Button(a);
        showMilestones = new Button(a);
        backgroundImage.setBackground(getDrawable(R.drawable.normalbackground));
        backgroundImage.setImageBitmap(BackgroundImageBitmap);
        backgroundImage.setAlpha(0.23F);
        backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
        runOnUiThread(()-> {
            darkmodeBeginText1.setVisibility(View.INVISIBLE);
            darkmodeBeginText2.setVisibility(View.INVISIBLE);
            darkmodeEndText1.setVisibility(View.INVISIBLE);
            darkmodeEndText2.setVisibility(View.INVISIBLE);
            darkmodeBegin.setVisibility(View.INVISIBLE);
            darkmodeEnd.setVisibility(View.INVISIBLE);
            darkmodeSettingsDone.setVisibility(View.INVISIBLE);
            darkmodeSettings.setText(getString(R.string.setup_darkmode_times));
            darkmodeSettingsDone.setText(getString(R.string.done));
        });
        initialiseListeners();
        initialiseUI();
    }
    public void measure() {
        height = getScreenHeight(this);
        width = getScreenWidth(this);
        runOnUiThread(() -> {
            darkmodeBeginText1.measure(0, 0);
            darkmodeBeginText2.measure(0, 0);
            darkmodeEndText1.measure(0, 0);
            darkmodeEndText2.measure(0, 0);
            secondsSwitch.measure(0, 0);
            changeDates.measure(0, 0);
            showDarkmodeColors.measure(0, 0);
            changeDarkmodeBackgroundcolor.measure(0, 0);
            changeDarkmodeButtoncolor.measure(0, 0);
            changeDarkmodeTextcolor.measure(0, 0);
            showBrightmodeColors.measure(0, 0);
            changeBrightmodeBackgroundcolor.measure(0, 0);
            changeBrightmodeButtoncolor.measure(0, 0);
            changeBrightmodeTextcolor.measure(0, 0);
            showMilestones.measure(0, 0);
            darkmodeSwitch.measure(0, 0);
            backToMain.measure(0, 0);
            pickBackgroundImage.measure(0, 0);
            darkmodeSettings.measure(0, 0);
            darkmodeSettingsDone.measure(0, 0);
            darkmodeBegin.measure(0, 0);
            darkmodeEnd.measure(0, 0);
        });
    }

    public void setTextSizes(int textSize) {
        runOnUiThread(() -> {
            darkmodeBeginText1.setTextSize(textSize);
            darkmodeBeginText2.setTextSize(textSize);
            darkmodeEndText1.setTextSize(textSize);
            darkmodeEndText2.setTextSize(textSize);
            secondsSwitch.setTextSize(textSize);
            changeDates.setTextSize(textSize);
            showDarkmodeColors.setTextSize(textSize);
            showBrightmodeColors.setTextSize(textSize);
            changeDarkmodeBackgroundcolor.setTextSize(textSize);
            changeDarkmodeButtoncolor.setTextSize(textSize);
            changeDarkmodeTextcolor.setTextSize(textSize);
            changeBrightmodeBackgroundcolor.setTextSize(textSize);
            changeBrightmodeButtoncolor.setTextSize(textSize);
            changeBrightmodeTextcolor.setTextSize(textSize);
            showMilestones.setTextSize(textSize);
            darkmodeSwitch.setTextSize(textSize);
            backToMain.setTextSize(textSize);
            pickBackgroundImage.setTextSize(textSize);
            darkmodeSettingsDone.setTextSize(textSize);
            darkmodeSettings.setTextSize(textSize);
            darkmodeBegin.setTextSize(textSize);
            darkmodeEnd.setTextSize(textSize);
        });
    }

    @SuppressLint("SetTextI18n")
    public void setText() {
        runOnUiThread(() -> {
            if(changeDates != null) changeDates.setText(getString(R.string.change_data_button));
            if(showDarkmodeColors != null) showDarkmodeColors.setText(R.string.darkmodeColorsNow);
            if(showBrightmodeColors != null) showBrightmodeColors.setText(R.string.brightmodeColorsNow);
            if(changeDarkmodeButtoncolor != null) changeDarkmodeButtoncolor.setText(R.string.button);
            if(changeDarkmodeTextcolor != null) changeDarkmodeTextcolor.setText(R.string.text);
            if(changeDarkmodeBackgroundcolor != null) changeDarkmodeBackgroundcolor.setText(R.string.back2);
            if(changeBrightmodeButtoncolor != null) changeBrightmodeButtoncolor.setText(R.string.button);
            if(changeBrightmodeTextcolor != null) changeBrightmodeTextcolor.setText(R.string.text);
            if(changeBrightmodeBackgroundcolor != null) changeBrightmodeBackgroundcolor.setText(R.string.back2);
            if(showMilestones != null) showMilestones.setText(getString(R.string.showMilestones));
            if(darkmodeBeginText1 != null) darkmodeBeginText1.setText(getString(R.string.begin_dark_mode));
            if(darkmodeBeginText2 != null) darkmodeBeginText2.setText(getString(R.string.o_clock));
            if(darkmodeEndText1   != null) darkmodeEndText1.setText(getString(R.string.begin_bright_mode));
            if(darkmodeEndText2   != null) darkmodeEndText2.setText(getString(R.string.o_clock));
            if(secondsSwitch != null) {
                if (multiper == 60) {
                    secondsSwitch.setText(getString(R.string.settings_minutes));
                }
                if (multiper == 3600) {
                    secondsSwitch.setText(getString(R.string.settings_hours));
                }
                if (multiper == 86400) {
                    secondsSwitch.setText(getString(R.string.settings_days));
                }
                if (multiper == 1) {
                    secondsSwitch.setText(getString(R.string.settings_seconds));
                }
                if(multiper == 15000) {
                    secondsSwitch.setText(getString(R.string.settings_customTime));
                }
            }
            if(pickBackgroundImage != null) pickBackgroundImage.setText(getString(R.string.pickBackground) + " (beta..)");
            if(backToMain != null) {
                backToMain.setText(getString(R.string.back));
            }
            if(darkmodeSwitch != null) {
                if(darkMode == 0) {
                    darkmodeSwitch.setText(getString(R.string.darkmode_off));
                }
                if(darkMode == 1) {
                    darkmodeSwitch.setText(getString(R.string.darkmode_on));
                }
                if(darkMode == 2) {
                    darkmodeSwitch.setText(getString(R.string.darkmode_auto));
                }
            }
        });
    }

    public void setPaddingSizes() {
        measure();
        runOnUiThread(() -> {
            int horizontal = width / 27;
            int vertical = horizontal / 2;
            int buttonWidth = width - 50;
            int buttonHeight = (int) (width / 5.5);
            int buttonWidth2 = (int) (width / 2.2);
            darkmodeBeginText1.setPaddingRelative(horizontal, vertical, 0, vertical);
            darkmodeBeginText2.setPaddingRelative(0, vertical, horizontal, vertical);
            darkmodeEndText1.setPaddingRelative(horizontal, vertical, 0, vertical);
            darkmodeEndText2.setPaddingRelative(0, vertical, horizontal, vertical);
            darkmodeBegin.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            darkmodeEnd.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            showDarkmodeColors.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            showBrightmodeColors.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            secondsSwitch.setWidth(buttonWidth);
            changeDates.setWidth(buttonWidth);
            showMilestones.setWidth(buttonWidth);
            darkmodeSwitch.setWidth(buttonWidth);
            backToMain.setWidth(buttonWidth);
            pickBackgroundImage.setWidth(buttonWidth);
            darkmodeSettings.setWidth(buttonWidth);
            changeDarkmodeBackgroundcolor.setWidth(width / 4);
            changeDarkmodeButtoncolor.setWidth(width / 4);
            changeDarkmodeTextcolor.setWidth(width / 4);
            changeBrightmodeBackgroundcolor.setWidth(width / 4);
            changeBrightmodeButtoncolor.setWidth(width / 4);
            changeBrightmodeTextcolor.setWidth(width / 4);
            darkmodeSettingsDone.setWidth(buttonWidth2);
            secondsSwitch.setHeight(buttonHeight);
            changeDates.setHeight(buttonHeight);
            showMilestones.setHeight(buttonHeight);
            darkmodeSwitch.setHeight(buttonHeight);
            backToMain.setHeight(buttonHeight);
            pickBackgroundImage.setHeight(buttonHeight);
            darkmodeSettings.setHeight(buttonHeight);
            darkmodeSettingsDone.setHeight(buttonHeight);
            changeDarkmodeTextcolor.setHeight(buttonHeight);
            changeDarkmodeButtoncolor.setHeight(buttonHeight);
            changeDarkmodeBackgroundcolor.setHeight(buttonHeight);
            changeBrightmodeTextcolor.setHeight(buttonHeight);
            changeBrightmodeButtoncolor.setHeight(buttonHeight);
            changeBrightmodeBackgroundcolor.setHeight(buttonHeight);
        });
    }

    /*
     * returns the width from the primary Display.
     */
    public int getScreenWidth(@NonNull Activity activity) {
        WindowMetrics windowMetrics;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.widthPixels;
        }
    }

    /*
     * returns the height from the primary Display.
     */
    public int getScreenHeight(@NonNull Activity activity) {
        WindowMetrics windowMetrics;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            return displayMetrics.heightPixels;
        }
    }

    public void setPositions() {
        runOnUiThread(() -> {
            measure();
            // <----------------- [X Values] -----------------> \\
            backToMain.setX((float) (width / 2.0 - backToMain.getMeasuredWidth() / 2.0));
            secondsSwitch.setX((float) (width / 2.0 - secondsSwitch.getMeasuredWidth() / 2.0));
            darkmodeSwitch.setX((float) (width / 2.0 - darkmodeSwitch.getMeasuredWidth() / 2.0));
            darkmodeSettings.setX((float) (width / 2.0 - darkmodeSettings.getMeasuredWidth() / 2.0));
            changeDates.setX((float) (width / 2.0 - changeDates.getMeasuredWidth() / 2.0));
            showMilestones.setX((float) (width / 2.0 - showMilestones.getMeasuredWidth() / 2.0));
            pickBackgroundImage.setX((float) (width / 2.0 - pickBackgroundImage.getMeasuredWidth() / 2.0));
            showDarkmodeColors.setX((float) (width / 2.0 - showDarkmodeColors.getMeasuredWidth() / 2.0));
            showBrightmodeColors.setX((float) (width / 2.0 - showBrightmodeColors.getMeasuredWidth() / 2.0));
            changeDarkmodeButtoncolor.setX((float) (width / 2.0 - changeDarkmodeButtoncolor.getMeasuredWidth() / 2.0));
            changeDarkmodeBackgroundcolor.setX((float) (changeDarkmodeButtoncolor.getX() - changeDarkmodeBackgroundcolor.getMeasuredWidth() - 30));
            changeDarkmodeTextcolor.setX((float) (changeDarkmodeButtoncolor.getX() + changeDarkmodeTextcolor.getMeasuredWidth() + 30));

            changeBrightmodeButtoncolor.setX((float) (width / 2.0 - changeBrightmodeButtoncolor.getMeasuredWidth() / 2.0));
            changeBrightmodeBackgroundcolor.setX((float) (changeBrightmodeButtoncolor.getX() - changeBrightmodeBackgroundcolor.getMeasuredWidth() - 30));
            changeBrightmodeTextcolor.setX((float) (changeBrightmodeButtoncolor.getX() + changeBrightmodeTextcolor.getMeasuredWidth() + 30));

            darkmodeSettingsDone.setX((float) (width / 2.0 - darkmodeSettingsDone.getMeasuredWidth() / 2.0));
            darkmodeBeginText1.setX((float) (width / 2.0 - (darkmodeBeginText1.getMeasuredWidth() + darkmodeBegin.getMeasuredWidth()
                    + darkmodeBeginText2.getMeasuredWidth()) / 2.0));
            darkmodeBegin.setX(darkmodeBeginText1.getX() + darkmodeBeginText1.getMeasuredWidth());
            darkmodeBeginText2.setX(darkmodeBegin.getX() + darkmodeBegin.getMeasuredWidth());
            darkmodeEndText1.setX((float) (width / 2.0 - (darkmodeEndText1.getMeasuredWidth() + darkmodeEnd.getMeasuredWidth()
                    + darkmodeEndText2.getMeasuredWidth()) / 2.0));
            darkmodeEnd.setX(darkmodeEndText1.getX() + darkmodeEndText1.getMeasuredWidth());
            darkmodeEndText2.setX(darkmodeEnd.getX() + darkmodeEnd.getMeasuredWidth());

            // <----------------- [Y Values] -----------------> \\
            backToMain.setY((float) (backToMain.getMeasuredHeight() / 6.0));
            secondsSwitch.setY((float) (backToMain.getY() + backToMain.getMeasuredHeight() + secondsSwitch.getMeasuredHeight() / 6.0));
            changeDates.setY((float) (secondsSwitch.getY() + secondsSwitch.getMeasuredHeight() + changeDates.getMeasuredHeight() / 6.0));
            darkmodeSwitch.setY((float) (changeDates.getY() + changeDates.getMeasuredHeight() + darkmodeSwitch.getMeasuredHeight() / 6.0));
            darkmodeSettings.setY((float) (darkmodeSwitch.getY() + darkmodeSwitch.getMeasuredHeight() + darkmodeSettings.getMeasuredHeight() / 6.0));
            pickBackgroundImage.setY((float) (darkmodeSettings.getY() + darkmodeSettings.getMeasuredHeight() + pickBackgroundImage.getMeasuredHeight() / 6.0));
            showMilestones.setY((float) (pickBackgroundImage.getY() + pickBackgroundImage.getMeasuredHeight() + showMilestones.getMeasuredHeight() / 6.0));

            showDarkmodeColors.setY(20);
            changeDarkmodeBackgroundcolor.setY(showDarkmodeColors.getY() + showDarkmodeColors.getMeasuredHeight() + 20);
            changeDarkmodeButtoncolor.setY(changeDarkmodeBackgroundcolor.getY());
            changeDarkmodeTextcolor.setY(changeDarkmodeBackgroundcolor.getY());

            showBrightmodeColors.setY(changeDarkmodeTextcolor.getY() + changeDarkmodeTextcolor.getMeasuredHeight() + 60);
            changeBrightmodeBackgroundcolor.setY(showBrightmodeColors.getY() + showBrightmodeColors.getMeasuredHeight() + 20);
            changeBrightmodeButtoncolor.setY(changeBrightmodeBackgroundcolor.getY());
            changeBrightmodeTextcolor.setY(changeBrightmodeBackgroundcolor.getY());

            darkmodeBeginText1.setY(changeBrightmodeTextcolor.getY() + changeBrightmodeTextcolor.getMeasuredHeight() + 60);
            darkmodeEndText1.setY((float) (darkmodeBeginText1.getY() + darkmodeEndText1.getMeasuredHeight() * 1.5));
            darkmodeBegin.setY(darkmodeBeginText1.getY());
            darkmodeEnd.setY(darkmodeEndText1.getY());
            darkmodeBeginText2.setY(darkmodeBeginText1.getY());
            darkmodeEndText2.setY(darkmodeEndText1.getY());
            darkmodeSettingsDone.setY((float) (darkmodeEnd.getY() + darkmodeEnd.getMeasuredHeight() + darkmodeSettingsDone.getMeasuredHeight() / 1.5));
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setColors() {
        runOnUiThread(() -> {
            nowWithoutZone = LocalDateTime.now();
            if(darkMode == 0) {
                backgroundcolor = brightmodeBackgroundcolor;
                textcolor = brightmodeTextcolor;
                buttoncolor = brightmodeButtoncolor;
            }
            if (darkMode == 1) {
                backgroundcolor = darkmodeBackgroundcolor;
                textcolor = darkmodeTextcolor;
                buttoncolor = darkmodeButtoncolor;
            }
            if(darkMode == 2)
                if ((Integer.parseInt(nowWithoutZone.format(dz)) >= begin) || (Integer.parseInt(nowWithoutZone.format(dz)) < end)) {
                    backgroundcolor = darkmodeBackgroundcolor;
                    textcolor = darkmodeTextcolor;
                    buttoncolor = darkmodeButtoncolor;
                } else {
                    backgroundcolor = brightmodeBackgroundcolor;
                    textcolor = brightmodeTextcolor;
                    buttoncolor = brightmodeButtoncolor;
                }
            layout.setBackgroundColor(Color.parseColor(backgroundcolor));
            darkmodeBeginText1.setTextColor(Color.parseColor(textcolor));
            darkmodeBeginText2.setTextColor(Color.parseColor(textcolor));
            darkmodeEndText1.setTextColor(Color.parseColor(textcolor));
            darkmodeEndText2.setTextColor(Color.parseColor(textcolor));
            secondsSwitch.setTextColor(Color.parseColor(textcolor));
            changeDates.setTextColor(Color.parseColor(textcolor));
            showDarkmodeColors.setTextColor(Color.parseColor(textcolor));
            showBrightmodeColors.setTextColor(Color.parseColor(textcolor));
            showMilestones.setTextColor(Color.parseColor(textcolor));
            darkmodeSwitch.setTextColor(Color.parseColor(textcolor));
            backToMain.setTextColor(Color.parseColor(textcolor));
            pickBackgroundImage.setTextColor(Color.parseColor(textcolor));
            darkmodeSettings.setTextColor(Color.parseColor(textcolor));
            darkmodeSettingsDone.setTextColor(Color.parseColor(textcolor));
            darkmodeBegin.setTextColor(Color.parseColor(textcolor));
            darkmodeEnd.setTextColor(Color.parseColor(textcolor));
            darkmodeBeginText1.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeBeginText2.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeEndText1.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeEndText2.setBackgroundColor(Color.parseColor(buttoncolor));
            showDarkmodeColors.setBackgroundColor(Color.parseColor(buttoncolor));
            showBrightmodeColors.setBackgroundColor(Color.parseColor(buttoncolor));
            secondsSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
            changeDates.setBackgroundColor(Color.parseColor(buttoncolor));
            showMilestones.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
            backToMain.setBackgroundColor(Color.parseColor(buttoncolor));
            pickBackgroundImage.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeSettings.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeSettingsDone.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeBegin.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeEnd.setBackgroundColor(Color.parseColor(buttoncolor));

            changeDarkmodeTextcolor.setBackgroundColor(Color.parseColor(darkmodeTextcolor));
            changeDarkmodeButtoncolor.setBackgroundColor(Color.parseColor(darkmodeButtoncolor));
            changeDarkmodeBackgroundcolor.setBackgroundColor(Color.parseColor(darkmodeBackgroundcolor));
            changeBrightmodeTextcolor.setBackgroundColor(Color.parseColor(brightmodeTextcolor));
            changeBrightmodeButtoncolor.setBackgroundColor(Color.parseColor(brightmodeButtoncolor));
            changeBrightmodeBackgroundcolor.setBackgroundColor(Color.parseColor(brightmodeBackgroundcolor));
        });
    }

    public void initialiseUI() {
        runOnUiThread(() -> {
            darkmodeBeginText1.setTypeface(Typeface.MONOSPACE);
            darkmodeBeginText2.setTypeface(Typeface.MONOSPACE);
            darkmodeEndText1.setTypeface(Typeface.MONOSPACE);
            darkmodeEndText2.setTypeface(Typeface.MONOSPACE);
            secondsSwitch.setTypeface(Typeface.MONOSPACE);
            changeDates.setTypeface(Typeface.MONOSPACE);
            showDarkmodeColors.setTypeface(Typeface.MONOSPACE);
            showBrightmodeColors.setTypeface(Typeface.MONOSPACE);
            showMilestones.setTypeface(Typeface.MONOSPACE);
            darkmodeSwitch.setTypeface(Typeface.MONOSPACE);
            backToMain.setTypeface(Typeface.MONOSPACE);
            pickBackgroundImage.setTypeface(Typeface.MONOSPACE);
            darkmodeSettings.setTypeface(Typeface.MONOSPACE);
            darkmodeSettingsDone.setTypeface(Typeface.MONOSPACE);
            darkmodeBegin.setTypeface(Typeface.MONOSPACE);
            darkmodeEnd.setTypeface(Typeface.MONOSPACE);
            darkmodeBeginText1.setText("");
            darkmodeBeginText2.setText("");
            darkmodeEndText1.setText("");
            darkmodeEndText2.setText("");
            secondsSwitch.setText("");
            changeDates.setText(getString(R.string.change_data_button));
            darkmodeSwitch.setText("");
            backToMain.setText("");
            pickBackgroundImage.setText("");
            darkmodeBegin.setText("");
            darkmodeEnd.setText("");
            darkmodeBegin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            darkmodeEnd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            darkmodeBegin.setInputType(InputType.TYPE_CLASS_NUMBER);
            darkmodeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
            darkmodeSettings.setVisibility(View.VISIBLE);
            measure();
            setPositions();
        });
    }

    /*
     * resets the Positions of the Edittexts and the surrounding Textviews.
     */
    private void resetEditTexts() {
        runOnUiThread(() -> {
            measure();
            darkmodeBeginText1.setX((float) (width / 2.0 - (darkmodeBeginText1.getMeasuredWidth() + darkmodeBegin.getMeasuredWidth()
                    + darkmodeBeginText2.getMeasuredWidth()) / 2.0));
            darkmodeBegin.setX(darkmodeBeginText1.getX() + darkmodeBeginText1.getMeasuredWidth());
            darkmodeBeginText2.setX(darkmodeBegin.getX() + darkmodeBegin.getMeasuredWidth());
            darkmodeEndText1.setX((float) (width / 2.0 - (darkmodeEndText1.getMeasuredWidth() + darkmodeEnd.getMeasuredWidth()
                    + darkmodeEndText2.getMeasuredWidth()) / 2.0));
            darkmodeEnd.setX(darkmodeEndText1.getX() + darkmodeEndText1.getMeasuredWidth());
            darkmodeEndText2.setX(darkmodeEnd.getX() + darkmodeEnd.getMeasuredWidth());
        });
    }

    public void updateUI() {
        setColors();
        setText();
        setTextSizes(textSize);
        measure();
        setPositions();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    public void initialiseListeners() {
        runOnUiThread(() -> {
            darkmodeBegin.setOnKeyListener((v, keyCode, event) -> {
                resetEditTexts();
                return false;
            });

            /*
             * When the Focus on the EditText is lost, the Number gets Validated and moved to the
             *  start variable. The Focus is lost when a Button is pushed or the other EditText gets
             *  focussed.
             */
            darkmodeBegin.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    a = darkmodeBegin.getText().toString();
                    if (!a.equals("")) {
                        int aNum = Integer.parseInt(a);
                        if ((aNum > 24) || ((aNum < 0)) || (aNum < end)) {
                            darkmodeBegin.setText(Integer.toString(begin));
                        } else {
                            begin = aNum;
                            darkmodeBegin.setText(Integer.toString(begin));
                            save(begin, "darkmodeBegin");
                        }
                    } else darkmodeBegin.setText(Integer.toString(begin));
                }
            });

            /*
             * When the Focus on the EditText is lost, the Number gets Validated and moved to the
             *  end variable. The Focus is lost when a Button is pushed or the other EditText gets
             *  focussed.
             */
            darkmodeEnd.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    a = darkmodeEnd.getText().toString();
                    if(!a.equals("")) {
                        int aNum = Integer.parseInt(a);
                        if ((aNum > 24) || ((aNum < 0)) || (aNum > begin)) {
                            darkmodeEnd.setText(Integer.toString(end));
                        } else {
                            end = aNum;
                            darkmodeEnd.setText(Integer.toString(end));
                            save(end, "darkmodeEnd");
                        }
                    } else darkmodeEnd.setText(Integer.toString(end));
                }
            });

            changeDarkmodeBackgroundcolor.setOnClickListener(view -> {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.parseColor(darkmodeBackgroundcolor), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        darkmodeBackgroundcolor = String.format("#%06X", (0xFFFFFF & color));
                        save(darkmodeBackgroundcolor, "darkmodeBackgroundcolor");
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();
            });

            changeDarkmodeTextcolor.setOnClickListener(view -> {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.parseColor(darkmodeTextcolor), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        darkmodeTextcolor = String.format("#%06X", (0xFFFFFF & color));
                        save(darkmodeTextcolor, "darkmodeTextcolor");
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();
            });

            changeDarkmodeButtoncolor.setOnClickListener(view -> {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.parseColor(darkmodeButtoncolor), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        darkmodeButtoncolor = String.format("#%06X", (0xFFFFFF & color));
                        save(darkmodeButtoncolor, "darkmodeButtoncolor");
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();
            });

            changeBrightmodeBackgroundcolor.setOnClickListener(view -> {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.parseColor(brightmodeBackgroundcolor), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        brightmodeBackgroundcolor = String.format("#%06X", (0xFFFFFF & color));
                        save(brightmodeBackgroundcolor, "brightmodeBackgroundcolor");
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();
            });

            changeBrightmodeTextcolor.setOnClickListener(view -> {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.parseColor(brightmodeTextcolor), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        brightmodeTextcolor = String.format("#%06X", (0xFFFFFF & color));
                        save(brightmodeTextcolor, "brightmodeTextcolor");
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();
            });

            changeBrightmodeButtoncolor.setOnClickListener(view -> {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.parseColor(brightmodeButtoncolor), false, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        brightmodeButtoncolor = String.format("#%06X", (0xFFFFFF & color));
                        save(brightmodeButtoncolor, "brightmodeButtoncolor");
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();
            });

            showMilestones.setOnClickListener(view -> {
                darkmodeSettings.setVisibility(View.INVISIBLE);
                changeDates.setVisibility(View.INVISIBLE);
                showMilestones.setVisibility(View.INVISIBLE);
                darkmodeSwitch.setVisibility(View.INVISIBLE);
                backToMain.setVisibility(View.INVISIBLE);
                pickBackgroundImage.setVisibility(View.INVISIBLE);
                secondsSwitch.setVisibility(View.INVISIBLE);

                hideMilestones = new Button(context);
                oneThird = new TextView(context);
                oneHalf = new TextView(context);
                twoThird = new TextView(context);
                ninetyPercent = new TextView(context);

                hideMilestones.setTextSize(textSize);
                oneThird.setTextSize((float) (textSize * 1.2));
                oneHalf.setTextSize((float) (textSize * 1.2));
                twoThird.setTextSize((float) (textSize * 1.2));
                ninetyPercent.setTextSize((float) (textSize * 1.2));

                hideMilestones.setTypeface(Typeface.MONOSPACE);
                oneThird.setTypeface(Typeface.MONOSPACE);
                oneHalf.setTypeface(Typeface.MONOSPACE);
                twoThird.setTypeface(Typeface.MONOSPACE);
                ninetyPercent.setTypeface(Typeface.MONOSPACE);

                width = getScreenWidth(act);
                height = getScreenHeight(act);
                int horizontal = width / 27;
                int vertical = horizontal / 2;
                int buttonWidth = width - 50;
                int buttonHeight = (int) (width / 5.5);
                hideMilestones.setWidth(buttonWidth);
                hideMilestones.setHeight(buttonHeight);

                oneThird.setPaddingRelative(horizontal, vertical, horizontal, vertical);
                oneHalf.setPaddingRelative(horizontal, vertical, horizontal, vertical);
                twoThird.setPaddingRelative(horizontal, vertical, horizontal, vertical);
                ninetyPercent.setPaddingRelative(horizontal, vertical, horizontal, vertical);



                oneThird.setWidth(buttonWidth);
                oneHalf.setWidth(buttonWidth);
                twoThird.setWidth(buttonWidth);
                ninetyPercent.setWidth(buttonWidth);

                oneThird.setText(getString(R.string.milestone_third_notification));
                oneHalf.setText(getString(R.string.milestone_half_notification));
                twoThird.setText(getString(R.string.milestone_two_third_notification));
                ninetyPercent.setText(getString(R.string.milestone_ninety_percent));
                hideMilestones.setText(getString(R.string.back));

                oneThird.measure(0, 0);
                oneHalf.measure(0, 0);
                twoThird.measure(0, 0);
                ninetyPercent.measure(0, 0);
                hideMilestones.measure(0, 0);

                oneThird.setX((float)(width / 2.0 - oneThird.getMeasuredWidth() / 2.0));
                oneHalf.setX((float)(width / 2.0 - oneHalf.getMeasuredWidth() / 2.0));
                twoThird.setX((float)(width / 2.0 - twoThird.getMeasuredWidth() / 2.0));
                ninetyPercent.setX((float)(width / 2.0 - ninetyPercent.getMeasuredWidth() / 2.0));
                hideMilestones.setX((float)(width / 2.0 - hideMilestones.getMeasuredWidth() / 2.0));

                hideMilestones.setY((float) (hideMilestones.getMeasuredHeight() / 6.0));
                oneThird.setY(hideMilestones.getY() + hideMilestones.getMeasuredHeight() + 60);
                oneHalf.setY(oneThird.getY() + oneThird.getMeasuredHeight() + 10);
                twoThird.setY(oneHalf.getY() + oneHalf.getMeasuredHeight() + 10);
                ninetyPercent.setY(twoThird.getY() + twoThird.getMeasuredHeight() + 10);

                hideMilestones.setBackgroundColor(Color.parseColor(buttoncolor));
                hideMilestones.setTextColor(Color.parseColor(textcolor));
                if(done(100.0/3)) {
                    oneThird.setBackgroundColor(Color.parseColor("#20B000"));
                    oneThird.setTextColor(Color.parseColor("#76FF80"));
                } else {
                    oneThird.setBackgroundColor(Color.parseColor(buttoncolor));
                    oneThird.setTextColor(Color.parseColor(textcolor));
                }

                if(done(50)) {
                    oneHalf.setBackgroundColor(Color.parseColor("#20B000"));
                    oneHalf.setTextColor(Color.parseColor("#76FF80"));
                } else {
                    oneHalf.setBackgroundColor(Color.parseColor(buttoncolor));
                    oneHalf.setTextColor(Color.parseColor(textcolor));
                }

                if(done(200.0/3)) {
                    twoThird.setBackgroundColor(Color.parseColor("#20B000"));
                    twoThird.setTextColor(Color.parseColor("#76FF80"));
                } else {
                    twoThird.setBackgroundColor(Color.parseColor(buttoncolor));
                    twoThird.setTextColor(Color.parseColor(textcolor));
                }

                if(done(90)) {
                    ninetyPercent.setBackgroundColor(Color.parseColor("#20B000"));
                    ninetyPercent.setTextColor(Color.parseColor("#76FF80"));
                } else {
                    ninetyPercent.setBackgroundColor(Color.parseColor(buttoncolor));
                    ninetyPercent.setTextColor(Color.parseColor(textcolor));
                }

                ConstraintLayout lyout = findViewById(R.id.Layout2);
                lyout.addView(oneThird);
                lyout.addView(oneHalf);
                lyout.addView(twoThird);
                lyout.addView(ninetyPercent);
                lyout.addView(hideMilestones);

                hideMilestones.setOnClickListener(view2 -> {
                    darkmodeSettings.setVisibility(View.VISIBLE);
                    changeDates.setVisibility(View.VISIBLE);
                    showMilestones.setVisibility(View.VISIBLE);
                    darkmodeSwitch.setVisibility(View.VISIBLE);
                    backToMain.setVisibility(View.VISIBLE);
                    pickBackgroundImage.setVisibility(View.VISIBLE);
                    secondsSwitch.setVisibility(View.VISIBLE);
                    oneThird.setVisibility(View.INVISIBLE);
                    oneHalf.setVisibility(View.INVISIBLE);
                    twoThird.setVisibility(View.INVISIBLE);
                    ninetyPercent.setVisibility(View.INVISIBLE);
                    hideMilestones.setVisibility(View.INVISIBLE);
                });
            });

            pickBackgroundImage.setOnClickListener(view -> {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, getString(R.string.pick_Image)), 1);
            });

            /*
             * If the Button gets clicked, the mode of displaying the time is changed
             *  in this order: seconds, minutes, hours, days.
             * The Mode is saved.
             */
            secondsSwitch.setOnClickListener(view -> {
                darkmodeBegin.clearFocus();
                darkmodeEnd.clearFocus();
                if (secondsSwitch.getText().equals(getString(R.string.settings_seconds))) {
                    secondsSwitch.setText(getString(R.string.settings_minutes));
                    multiper = 60;
                } else {
                    if (secondsSwitch.getText().equals(getString(R.string.settings_minutes))) {
                        secondsSwitch.setText(getString(R.string.settings_hours));
                        multiper = 3600;
                    } else if(secondsSwitch.getText().equals(getString(R.string.settings_hours))){
                        secondsSwitch.setText(getString(R.string.settings_days));
                        multiper = 86400;
                    } else if(secondsSwitch.getText().equals(getString(R.string.settings_days))){
                        secondsSwitch.setText(getString(R.string.settings_customTime));
                        multiper = 15000;
                    } else {
                        secondsSwitch.setText(getString(R.string.settings_seconds));
                        multiper = 1;
                    }
                }
                log("Set the time display mode to " + secondsSwitch.getText());
                update();
                save(multiper, "timeMode");
            });

            /*
             * If the Button is clicked the Settings are activated if they are off and
             *  deactivated if they are on.
             * The Settings are validated and saved. The Keyboard gets closed.
             */
            darkmodeSettings.setOnClickListener(view -> {
                darkmodeEnd.setText(Integer.toString(end));
                darkmodeBegin.setText(Integer.toString(begin));
                updateUI();
                darkmodeSettings.setVisibility(View.INVISIBLE);
                changeDates.setVisibility(View.INVISIBLE);
                showMilestones.setVisibility(View.INVISIBLE);
                darkmodeSwitch.setVisibility(View.INVISIBLE);
                backToMain.setVisibility(View.INVISIBLE);
                pickBackgroundImage.setVisibility(View.INVISIBLE);
                secondsSwitch.setVisibility(View.INVISIBLE);
                darkmodeBegin.setVisibility(View.VISIBLE);
                darkmodeEnd.setVisibility(View.VISIBLE);
                darkmodeBeginText1.setVisibility(View.VISIBLE);
                darkmodeBeginText2.setVisibility(View.VISIBLE);
                darkmodeEndText1.setVisibility(View.VISIBLE);
                darkmodeEndText2.setVisibility(View.VISIBLE);
                darkmodeSettingsDone.setVisibility(View.VISIBLE);
                showDarkmodeColors.setVisibility(View.VISIBLE);
                changeDarkmodeBackgroundcolor.setVisibility(View.VISIBLE);
                changeDarkmodeTextcolor.setVisibility(View.VISIBLE);
                changeDarkmodeButtoncolor.setVisibility(View.VISIBLE);
                showBrightmodeColors.setVisibility(View.VISIBLE);
                changeBrightmodeBackgroundcolor.setVisibility(View.VISIBLE);
                changeBrightmodeTextcolor.setVisibility(View.VISIBLE);
                changeBrightmodeButtoncolor.setVisibility(View.VISIBLE);

                hideSoftKeyboard(findViewById(R.id.Layout2));
            });

            darkmodeSettingsDone.setOnClickListener(view -> {
                updateUI();
                secondsSwitch.setVisibility(View.VISIBLE);
                changeDates.setVisibility(View.VISIBLE);
                showMilestones.setVisibility(View.VISIBLE);
                darkmodeSwitch.setVisibility(View.VISIBLE);
                backToMain.setVisibility(View.VISIBLE);
                pickBackgroundImage.setVisibility(View.VISIBLE);
                darkmodeSettings.setVisibility(View.VISIBLE);
                darkmodeBegin.setVisibility(View.INVISIBLE);
                darkmodeEnd.setVisibility(View.INVISIBLE);
                darkmodeBeginText1.setVisibility(View.INVISIBLE);
                darkmodeBeginText2.setVisibility(View.INVISIBLE);
                darkmodeEndText1.setVisibility(View.INVISIBLE);
                darkmodeEndText2.setVisibility(View.INVISIBLE);
                darkmodeSettingsDone.setVisibility(View.INVISIBLE);
                showDarkmodeColors.setVisibility(View.INVISIBLE);
                changeDarkmodeBackgroundcolor.setVisibility(View.INVISIBLE);
                changeDarkmodeTextcolor.setVisibility(View.INVISIBLE);
                changeDarkmodeButtoncolor.setVisibility(View.INVISIBLE);
                showBrightmodeColors.setVisibility(View.INVISIBLE);
                changeBrightmodeBackgroundcolor.setVisibility(View.INVISIBLE);
                changeBrightmodeTextcolor.setVisibility(View.INVISIBLE);
                changeBrightmodeButtoncolor.setVisibility(View.INVISIBLE);
                log("Set the Darkmode-Times: From " + darkmodeBegin.getText() + " to " + darkmodeEnd.getText() + " o' clock.");
                log("Set the Darkmode-Theme: Textcolor = " + darkmodeTextcolor + ", Buttoncolor = " + darkmodeButtoncolor + ", Backgroundcolor = " + darkmodeBackgroundcolor);
                log("Set the Brightmode-Theme: Textcolor = " + brightmodeTextcolor + ", Buttoncolor = " + brightmodeButtoncolor + ", Backgroundcolor = " + brightmodeBackgroundcolor);
                hideSoftKeyboard(findViewById(R.id.Layout2));
            });

            backToMain.setOnClickListener(view -> finish());

            /*
             * Changes the Dark mode by adding 1 to the counter up to 2, otherwise set it to 0.
             *  the 2 activates the automatic. Afterwards the Buttontext and the colors are
             *  updated and the darkmode is saved.
             */
            darkmodeSwitch.setOnClickListener(view -> {
                darkmodeBegin.clearFocus();
                darkmodeEnd.clearFocus();
                if (darkMode == 0) {
                    darkMode = 1;
                    textcolor = "#C5C5C5";
                    buttoncolor = "#464646";
                    darkmodeSwitch.setText(getString(R.string.darkmode_on));
                } else {
                    if (darkMode == 1) {
                        darkMode = 2;
                        darkmodeSwitch.setText(getString(R.string.darkmode_auto));
                        darkmodeBegin.setText(Integer.toString(begin));
                        darkmodeEnd.setText(Integer.toString(end));
                        darkmodeSettings.setVisibility(View.VISIBLE);
                    } else {
                        darkMode = 0;
                        textcolor = "#3A5A9B"; //#3A5A9B
                        buttoncolor = "#B7C8EA"; //#648AD6
                        darkmodeBegin.setVisibility(View.INVISIBLE);
                        darkmodeEnd.setVisibility(View.INVISIBLE);
                        darkmodeBeginText1.setVisibility(View.INVISIBLE);
                        darkmodeBeginText2.setVisibility(View.INVISIBLE);
                        darkmodeEndText1.setVisibility(View.INVISIBLE);
                        darkmodeEndText2.setVisibility(View.INVISIBLE);
                        darkmodeSwitch.setText(getString(R.string.darkmode_off));
                    }
                }
                log("Changed the Darkmode to " + darkmodeSwitch.getText());
                update();
                save(darkMode, "Darkmode");
            });

            darkmodeEnd.setOnKeyListener((v, keyCode, event) -> {
                resetEditTexts();
                return false;
            });
            /*
             * When the Button gets pressed the timer gets paused and the UI changes to the PickDate UI.
             */
            changeDates.setOnClickListener(view -> pickDate());
        });
    }

    private boolean done(double v) {
        double z = loadDouble("PercentZ", 0);
        return z >= v;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == 1) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    backgroundImage.setImageURI(selectedImageUri);
                    backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    BitmapDrawable BitmapBackground = (BitmapDrawable) backgroundImage.getDrawable();
                    Bitmap bitmap = BitmapBackground.getBitmap();
                    save(bitmap, "BackgroundImage");
                }
            }
        }
    }

    /*
     * hideSoftKeyboard is used to close the Android soft Keyboard.
     * The Input "view" is usually just (View) findViewById(R.id.Layout2)
     */
    public void hideSoftKeyboard(View view){
        runOnUiThread(() -> {
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
    }

    private void update() {
        setTime();
        setPaddingSizes();
        updateUI();
    }

    private void log(String s) {
        System.out.println(s);
        log = addLine(log, s);
    }

    private String[] addLine(String[] log, String messageline) {
        String[] result;
        if(log != null) result = new String[log.length + 1];
        else result = new String[1];
        if(log != null) System.arraycopy(log, 0, result, 0, log.length);
        if(log != null) result[log.length] = "[" + now.format(dr) + "]: " + messageline;
        else result[0] = "[" + LocalDateTime.now(ZoneId.of("Europe/Berlin")).format(dr) + "]: " + messageline;
        return result;
    }

    /*
     * Creates a new UI, where the User can change the Dates.
     */
    private void pickDate() {
        Context context = this;
        runOnUiThread(() -> {

            makeUIinvisible();

            TextView lbendDate = new TextView(context);
            lbstartDate = new TextView(context);
            Button showStartDatePicker = new Button(context);
            Button showEndDatePicker = new Button(context);
            Button done = new Button(context);
            /*
             * Sets the Text for all of the Items on Display and makes them look good.
             */
            lbstartDate.setText(getString(R.string.startdate));
            lbendDate.setText(getString(R.string.enddate));
            done.setText(getString(R.string.done));

            measure();
            lbstartDate.setTextSize(textSize);
            lbendDate.setTextSize(textSize);
            showStartDatePicker.setTextSize(textSize);
            done.setTextSize(textSize);
            showEndDatePicker.setTextSize(textSize);
            lbstartDate.setBackgroundColor(Color.parseColor(buttoncolor));
            lbendDate.setBackgroundColor(Color.parseColor(buttoncolor));
            showStartDatePicker.setBackgroundColor(Color.parseColor(buttoncolor));
            done.setBackgroundColor(Color.parseColor(buttoncolor));
            showEndDatePicker.setBackgroundColor(Color.parseColor(buttoncolor));
            lbstartDate.setTextColor(Color.parseColor(textcolor));
            lbendDate.setTextColor(Color.parseColor(textcolor));
            showStartDatePicker.setTextColor(Color.parseColor(textcolor));
            done.setTextColor(Color.parseColor(textcolor));
            showEndDatePicker.setTextColor(Color.parseColor(textcolor));
            lbstartDate.setTypeface(Typeface.MONOSPACE);
            lbendDate.setTypeface(Typeface.MONOSPACE);
            showStartDatePicker.setTypeface(Typeface.MONOSPACE);
            done.setTypeface(Typeface.MONOSPACE);
            showEndDatePicker.setTypeface(Typeface.MONOSPACE);

            /*
             * Places the Items on the right spot on the Display
             */
            int horizontal = width / 27;
            int vertical = horizontal / 2;
            int buttonWidth = (int) (width / 2.5);
            int buttonHeight = (int) (buttonWidth / 2.2);
            lbstartDate.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            lbendDate.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            showStartDatePicker.setWidth(buttonWidth);
            done.setWidth(buttonWidth);
            showEndDatePicker.setWidth(buttonWidth);
            showStartDatePicker.setHeight(buttonHeight);
            done.setHeight(buttonHeight);
            showEndDatePicker.setHeight(buttonHeight);
            lbstartDate.measure(0, 0);
            lbendDate.measure(0, 0);
            showStartDatePicker.measure(0, 0);
            done.measure(0, 0);
            showEndDatePicker.measure(0, 0);
            lbstartDate.setX((float) (width / 2.0 - lbstartDate.getMeasuredWidth() / 2.0));
            lbendDate.setX((float) (width / 2.0 - lbendDate.getMeasuredWidth() / 2.0));
            showStartDatePicker.setX((float) (width / 2.0 - showStartDatePicker.getMeasuredWidth() / 2.0));
            done.setX((float) (width / 2.0 - done.getMeasuredWidth() / 2.0));
            showEndDatePicker.setX((float) (width / 2.0 - showEndDatePicker.getMeasuredWidth() / 2.0));
            lbstartDate.setY((float) (lbstartDate.getMeasuredHeight()));
            showStartDatePicker.setY((float) (lbstartDate.getY() + lbstartDate.getMeasuredHeight() + showStartDatePicker.getMeasuredHeight() * 0.2));
            done.setY((float) (height/2.0 + height / 4.0));
            lbendDate.setY((float) (showStartDatePicker.getY() + showStartDatePicker.getMeasuredHeight() + lbendDate.getMeasuredHeight() * 2.0));
            showEndDatePicker.setY((float) (lbendDate.getY() + lbendDate.getMeasuredHeight() + showEndDatePicker.getMeasuredHeight() * 0.2));
            ConstraintLayout lyout = findViewById(R.id.Layout2);
            lyout.addView(lbstartDate);
            lyout.addView(lbendDate);
            lyout.addView(showStartDatePicker);
            lyout.addView(done);
            lyout.addView(showEndDatePicker);

            /*
             * Initialises the Datapickers and defines the Button onClickListener Events
             */

            Date date = new java.util.Date(startDateUNIX * 1000L - calcOffSeconds());
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            String formattedDate = sdf.format(date);
            int[] a = parseDate(formattedDate);
            showStartDatePicker.setText(createReadableDate(a));

            date = new java.util.Date(endDateUNIX * 1000L - calcOffSeconds());
            formattedDate = sdf.format(date);

            int[] c = parseDate(formattedDate);
            showEndDatePicker.setText(createReadableDate(c));

            int[] b = new int[5];
            int[] d = new int[5];
            changedStart = false;
            changedEnd = false;
            DatePickerDialog datePickerDialog1 = new DatePickerDialog(context,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        b[0] = year;
                        b[1] = monthOfYear + 1;
                        b[2] = dayOfMonth;
                    }, a[0], a[1] - 1, a[2]);
            TimePickerDialog timePickerDialog1 = new TimePickerDialog(context,
                    (view, hourOfDay, minute) -> {

                        b[3] = hourOfDay;
                        b[4] = minute;
                        showStartDatePicker.setText(createReadableDate(b));
                    }, a[3], a[4], true);
            DatePickerDialog datePickerDialog2 = new DatePickerDialog(context,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        d[0] = year;
                        d[1] = monthOfYear + 1;
                        d[2] = dayOfMonth;
                    },c[0], c[1] - 1, c[2]);
            TimePickerDialog timePickerDialog2 = new TimePickerDialog(context,
                    (view, hourOfDay, minute) -> {

                        d[3] = hourOfDay;
                        d[4] = minute;
                        showEndDatePicker.setText(createReadableDate(d));
                    }, c[3], c[4], true);

            /*
             * If the upper Date change Button is pressed, the Date and Time pickers are shown and
             *  changedStart is set to true to indicate the first Date may have been changed.
             */
            showStartDatePicker.setOnClickListener(view -> {
                timePickerDialog1.show();
                datePickerDialog1.show();
                changedStart = true;
            });

            /*
             * If the lower Date change Button is pressed, the Date and Time pickers are shown and
             *  changedEnd is set to true to indicate the second Date may have been changed.
             */
            showEndDatePicker.setOnClickListener(view -> {
                timePickerDialog2.show();
                datePickerDialog2.show();
                changedEnd = true;
            });

            /*
             * If the done Button is pressed, the changed Dates get Validated and saved.
             * The Timer gets resumed and the normal UI gets Visible again.
             */
            done.setOnClickListener(view -> {
                lbstartDate.setVisibility(View.INVISIBLE);
                lbendDate.setVisibility(View.INVISIBLE);
                lbendDate.setVisibility(View.INVISIBLE);
                showStartDatePicker.setVisibility(View.INVISIBLE);
                showEndDatePicker.setVisibility(View.INVISIBLE);
                done.setVisibility(View.INVISIBLE);

                if(changedStart && changedEnd)
                    if(checkDates(dateParseString(b), dateParseString(d))) {
                        startDate = dateParseString(b);
                        try {
                            startDateUNIX = timestamp(startDate) + calcOffSeconds();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        startDateUNIX /= 1000;
                        save(startDate, "Start");
                        save(startDateUNIX, "StartUNIX");
                        endDate = dateParseString(d);
                        try {
                            endDateUNIX = timestamp(endDate) + calcOffSeconds();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        endDateUNIX /= 1000;
                        save(endDate, "Ende");
                        save(endDateUNIX, "EndUNIX");
                    }
                if(changedStart && !changedEnd)
                    if(checkDates(dateParseString(b), endDate)) {
                        startDate = dateParseString(b);
                        try {
                            startDateUNIX = timestamp(startDate) + calcOffSeconds();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        startDateUNIX /= 1000;
                        save(startDate, "Start");
                        save(startDateUNIX, "StartUNIX");
                    }
                if(!changedStart && changedEnd)
                    if(checkDates(startDate, dateParseString(d))) {
                        endDate = dateParseString(d);
                        try {
                            endDateUNIX = timestamp(endDate) + calcOffSeconds();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        endDateUNIX /= 1000;
                        save(endDate, "Ende");
                        save(endDateUNIX, "EndUNIX");
                    }

                secondsSwitch.setVisibility(View.VISIBLE);
                darkmodeSwitch.setVisibility(View.VISIBLE);
                changeDates.setVisibility(View.VISIBLE);
                showMilestones.setVisibility(View.VISIBLE);
                backToMain.setVisibility(View.VISIBLE);
                pickBackgroundImage.setVisibility(View.VISIBLE);
                darkmodeSettings.setVisibility(View.VISIBLE);

                log("Changed Dates: ");
                if(changedStart) log(" Startdate = " + startDateUNIX + ", " + createReadableDate(b));
                else log(" Startdate = " + startDateUNIX + ", " + createReadableDate(a));
                if(changedEnd) log(" Startdate = " + startDateUNIX + ", " + createReadableDate(d));
                else log(" Enddate   = " + endDateUNIX + ", " + createReadableDate(c));
            });
        });
    }

    /*
     * converts an array of int where
     *  0 = Year
     *  1 = Month
     *  2 = Day
     *  3 = Hour
     *  4 = Minute
     * into a String of the Form "yyyy-MM-dd HH:mm:00.000"
     */
    private String dateParseString(int[] date) {
        String result = "";
        if(date[0] < 1000) result += "0";
        if(date[0] < 100) result += "0";
        if(date[0] < 10) result += "0";
        result += date[0] + "-";
        if(date[1] < 10) result += "0";
        result += date[1] + "-";
        if(date[2] < 10) result += "0";
        result += date[2] + " ";
        if(date[3] < 10) result += "0";
        result += date[3] + ":";
        if(date[4] < 10) result += "0";
        result += date[4] + ":00.000";
        return result;
    }

    /*
     * Checks, weather the changed Dates make sense for the Apps.
     */
    private boolean checkDates(String beginDate, String endDate) {
        long a, b, c;
        a = 0;
        b = 0;
        c = 0;
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        try {
            a = timestamp(beginDate);
            b = timestamp(endDate);
            c = timestamp(now.format(dr));
        } catch(ParseException oi) {
            System.out.println("Debug: ParseException in timestamp");
        }
        return (a < b) && (c <= b);
    }

    /*
     * Converts a int Array of the Form of the Output of ParseDate into a easily readable Date like:
     *  "12 JAN 2022 12:30" for input
     *   0 = 2022
     *   1 = 1
     *   2 = 12
     *   3 = 12
     *   4 = 30
     */
    private String createReadableDate(int[] date) {
        String result = Integer.toString(date[2]);
        switch (date[1]) {
            case 1:  result += " JAN ";
                break;
            case 2:  result += " FEB ";
                break;
            case 3:  result += " MAR ";
                break;
            case 4:  result += " APR ";
                break;
            case 5:  result += " MAI ";
                break;
            case 6:  result += " JUN ";
                break;
            case 7:  result += " JUL ";
                break;
            case 8:  result += " AUG ";
                break;
            case 9:  result += " SEP ";
                break;
            case 10: result += " OKT ";
                break;
            case 11: result += " NOV ";
                break;
            case 12: result += " DEC ";
                break;
            default: result += " ERR ";
        }
        result += Integer.toString(date[0]);
        result += " " + date[3] + ":" + date[4];
        return result;
    }

    /*
     * Does the Math to set x, y and z and their corresponding Strings.
     */
    public void setTime() {
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        nowWithoutZone = LocalDateTime.now();
    }

    /*
     * Converts a given Date in  the Format "yyyy-MM-dd HH:mm" in an Array, where the Element
     *  0 = Year
     *  1 = Month
     *  2 = Day
     *  3 = Hour
     *  4 = Minute
     */
    private int[] parseDate(String date) {
        int[] result = new int[5];
        result[0] = Integer.parseInt(date.substring(0, 4));
        result[1] = Integer.parseInt(date.substring(5, 7));
        result[2] = Integer.parseInt(date.substring(8, 10));
        result[3] = Integer.parseInt(date.substring(11, 13));
        result[4] = Integer.parseInt(date.substring(14, 16));
        return result;
    }

    /*
     * Calculates the Seconds the users Time is off to the Time in Berlin(GMT+2).
     */
    public int calcOffSeconds() {
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        nowWithoutZone = LocalDateTime.now();
        float a = 0,b = 0;
        try {
            a = timestamp(now.format(dr));
            b = timestamp(nowWithoutZone.format(dr));
        } catch(ParseException oi) {
            System.out.println("Debug: ParseException in timestamp");
        }
        return (int)(b-a)/1000;
    }

    private void makeUIinvisible() {
        runOnUiThread(() -> {
            secondsSwitch.setVisibility(View.INVISIBLE);
            darkmodeSettings.setVisibility(View.INVISIBLE);
            darkmodeSwitch.setVisibility(View.INVISIBLE);
            backToMain.setVisibility(View.INVISIBLE);
            pickBackgroundImage.setVisibility(View.INVISIBLE);
            changeDates.setVisibility(View.INVISIBLE);
            showMilestones.setVisibility(View.INVISIBLE);
        });
    }

    /*
     * timestamp is used to convert a Date and Time combo to the UNIX Milliseconds.
     * This is used in the calculations for the seconds passed and the seconds to pass
     */
    public long timestamp(String arg) throws ParseException {
        return Objects.requireNonNull(df.parse(arg)).getTime();
    }
}