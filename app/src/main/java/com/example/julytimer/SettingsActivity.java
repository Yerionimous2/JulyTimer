package com.example.julytimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@SuppressWarnings("SpellCheckingInspection")
public class SettingsActivity extends AppCompatActivity {
    View layout;
    @SuppressLint("SimpleDateFormat")
    public final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dz = DateTimeFormatter.ofPattern("HH");
    private final DateTimeFormatter dr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private SharedPreferences.Editor editor;
    public Drawable background;
    LocalDateTime now;
    LocalDateTime nowWithoutZone;
    private final Timer tm2 = new Timer();
    TimerTask tmTk2;
    private TextView darkmodeBeginText1;
    private TextView darkmodeBeginText2;
    private TextView darkmodeEndText1;
    private TextView darkmodeEndText2;
    private TextView lbstartDate;
    public EditText darkmodeBegin;
    public EditText darkmodeEnd;
    public Button darkmodeSettings;
    public Button secondsSwitch;
    public Button changeDates;
    public Button darkmodeSwitch;
    private DecimalFormat dform;
    private int height;
    private int width;
    private int darkMode;
    public int begin = 0;
    public int end = 0;
    private String buttoncolor;
    private String textcolor;
    private String[] log;
    public String startDate = "2022-08-24 09:30:00.000";
    public String endDate = "2023-07-23 21:40:00.000";
    private String a;
    private int multiper;
    private long x;
    private long completeTime;
    private double z;
    private int textSize = 12; //TODO: An Bildschirm anpassen.
    private long startDateUNIX, endDateUNIX;
    public boolean changedStart, changedEnd;
    private String xString2, yString2, zString2;

    /*
     * save(long) saves the given int for later use under the given name.
     */
    public void save(long a, String name) {
        editor.putLong(name, a);
        editor.apply();
        log("Saved long " + a + " as " + "\"" + name + "\".");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        homeScreenLayout.addView(darkmodeBeginText1);
        homeScreenLayout.addView(darkmodeBeginText2);
        homeScreenLayout.addView(darkmodeEndText1);
        homeScreenLayout.addView(darkmodeEndText2);
        homeScreenLayout.addView(darkmodeBegin);
        homeScreenLayout.addView(darkmodeEnd);
        homeScreenLayout.addView(darkmodeSettings);
        homeScreenLayout.addView(secondsSwitch);
        homeScreenLayout.addView(changeDates);
        homeScreenLayout.addView(darkmodeSwitch);
        tm2.schedule(tmTk2, 0, 500);
    }

    public void initialise() {
        Context a = this;
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        dform = new DecimalFormat("#.######");
        multiper = 1;
        editor = sharedPref.edit();
        darkmodeBegin = new EditText(a);
        darkmodeEnd = new EditText(a);
        darkmodeBeginText1 = new TextView(a);
        darkmodeBeginText2 = new TextView(a);
        darkmodeEndText1 = new TextView(a);
        darkmodeEndText2 = new TextView(a);
        secondsSwitch = new Button(a);
        changeDates = new Button(a);
        darkmodeSwitch = new Button(a);
        darkmodeSettings = new Button(a);
        runOnUiThread(()-> {
            darkmodeBeginText1.setVisibility(View.INVISIBLE);
            darkmodeBeginText2.setVisibility(View.INVISIBLE);
            darkmodeEndText1.setVisibility(View.INVISIBLE);
            darkmodeEndText2.setVisibility(View.INVISIBLE);
            darkmodeSettings.setVisibility(View.INVISIBLE);
            darkmodeBegin.setVisibility(View.INVISIBLE);
            darkmodeEnd.setVisibility(View.INVISIBLE);
            darkmodeSettings.setText(getString(R.string.setup_darkmode_times));
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
            darkmodeSwitch.measure(0, 0);
            darkmodeSettings.measure(0, 0);
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
            darkmodeSwitch.setTextSize(textSize);
            darkmodeSettings.setTextSize(textSize);
            darkmodeBegin.setTextSize(textSize);
            darkmodeEnd.setTextSize(textSize);
        });
    }

    public void setText() {
        runOnUiThread(() -> {
            if(darkmodeBeginText1 != null) darkmodeBeginText1.setText(getString(R.string.begin_dark_mode));
            if(darkmodeBeginText2 != null) darkmodeBeginText2.setText(getString(R.string.o_clock));
            if(darkmodeEndText1   != null) darkmodeEndText1.setText(getString(R.string.begin_bright_mode));
            if(darkmodeEndText2   != null) darkmodeEndText2.setText(getString(R.string.o_clock));
            if(secondsSwitch != null) {
                if (multiper == 60) {
                    secondsSwitch.setText(getString(R.string.minutes));
                }
                if (multiper == 3600) {
                    secondsSwitch.setText(getString(R.string.hours));
                }
                if (multiper == 86400) {
                    secondsSwitch.setText(getString(R.string.days));
                }
                if (multiper == 1) {
                    secondsSwitch.setText(getString(R.string.seconds));
                }
                if(multiper == 15000) {
                    secondsSwitch.setText(getString(R.string.customTime));
                }
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
            int buttonWidth = (int) (width / 2.5);
            int buttonHeight = (int) (buttonWidth / 2.2);
            darkmodeBeginText1.setPaddingRelative(horizontal, vertical, 0, vertical);
            darkmodeBeginText2.setPaddingRelative(0, vertical, horizontal, vertical);
            darkmodeEndText1.setPaddingRelative(horizontal, vertical, 0, vertical);
            darkmodeEndText2.setPaddingRelative(0, vertical, horizontal, vertical);
            darkmodeBegin.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            darkmodeEnd.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            secondsSwitch.setWidth(buttonWidth);
            changeDates.setWidth(buttonWidth);
            darkmodeSwitch.setWidth(buttonWidth);
            darkmodeSettings.setWidth(buttonWidth);
            secondsSwitch.setHeight(buttonHeight);
            changeDates.setHeight(buttonHeight);
            darkmodeSwitch.setHeight(buttonHeight);
            darkmodeSettings.setHeight(buttonHeight);
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

    public void setPositions(int height, int width, int mode) {
        runOnUiThread(() -> {
            measure();
            // <----------------- [X Values] -----------------> \\
            secondsSwitch.setX((float) (width / 2.0 - secondsSwitch.getMeasuredWidth() / 2.0 - width / 4.0));
            darkmodeSwitch.setX((float) (width / 2.0 - darkmodeSwitch.getMeasuredWidth() / 2.0 + width / 4.0));
            darkmodeBeginText1.setX((float) (width / 2.0 - (darkmodeBeginText1.getMeasuredWidth() + darkmodeBegin.getMeasuredWidth()
                    + darkmodeBeginText2.getMeasuredWidth()) / 2.0));
            darkmodeBegin.setX(darkmodeBeginText1.getX() + darkmodeBeginText1.getMeasuredWidth());
            darkmodeBeginText2.setX(darkmodeBegin.getX() + darkmodeBegin.getMeasuredWidth());
            darkmodeEndText1.setX((float) (width / 2.0 - (darkmodeEndText1.getMeasuredWidth() + darkmodeEnd.getMeasuredWidth()
                    + darkmodeEndText2.getMeasuredWidth()) / 2.0));
            darkmodeEnd.setX(darkmodeEndText1.getX() + darkmodeEndText1.getMeasuredWidth());
            darkmodeEndText2.setX(darkmodeEnd.getX() + darkmodeEnd.getMeasuredWidth());
            if(mode == 0) darkmodeSettings.setX(darkmodeSwitch.getX());
            if(mode == 1) darkmodeSettings.setX((float) (width / 2.0 - darkmodeSettings.getMeasuredWidth() / 2.0));
            if(darkmodeSwitch.getText().toString().equals(getString(R.string.darkmode_auto)))
                changeDates.setX(secondsSwitch.getX());
            else
                changeDates.setX((float) (width / 2.0 - changeDates.getMeasuredWidth() / 2.0));

            // <----------------- [Y Values] -----------------> \\
            if(mode == 0) {
                secondsSwitch.setY(height - secondsSwitch.getMeasuredHeight() * 4);
                darkmodeSettings.setY((float) (secondsSwitch.getY() + darkmodeSettings.getMeasuredHeight() + darkmodeSettings.getMeasuredHeight() / 4.0));
                changeDates.setY(darkmodeSettings.getY());
                changeDates.setVisibility(View.VISIBLE);
            }
            if(mode == 1) {
                secondsSwitch.setY((float) (height - secondsSwitch.getMeasuredHeight() * 2.5));
                changeDates.setVisibility(View.INVISIBLE);
            }
            darkmodeBeginText1.setY((float) (/*percent2.getY() + percent2.getMeasuredHeight() + */darkmodeBeginText1.getMeasuredHeight() * 2.0));
            darkmodeEndText1.setY((float) (darkmodeBeginText1.getY() + darkmodeEndText1.getMeasuredHeight() * 1.5));
            darkmodeBegin.setY(darkmodeBeginText1.getY());
            darkmodeEnd.setY(darkmodeEndText1.getY());
            darkmodeBeginText2.setY(darkmodeBeginText1.getY());
            darkmodeEndText2.setY(darkmodeEndText1.getY());
            darkmodeSwitch.setY(secondsSwitch.getY());
            if(mode == 1) darkmodeSettings.setY((float) (darkmodeEndText1.getY() + darkmodeEndText1.getMeasuredHeight() * 2.0));
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setColors() {
        runOnUiThread(() -> {
            nowWithoutZone = LocalDateTime.now();
            if(darkMode == 0) {
                background = getDrawable(R.drawable.brightmodebackground);
                textcolor = "#3A5A9B"; //#3A5A9B
                buttoncolor = "#B7C8EA"; //#648AD6
            }
            if (darkMode == 1) {
                background = getDrawable(R.drawable.darkmodebackground);
                textcolor = "#C5C5C5";
                buttoncolor = "#464646";
            }
            if(darkMode == 2)
                if ((Integer.parseInt(nowWithoutZone.format(dz)) >= begin) || (Integer.parseInt(nowWithoutZone.format(dz)) < end)) {

                    background = getDrawable(R.drawable.darkmodebackground);
                    textcolor = "#C5C5C5";
                    buttoncolor = "#464646";
                } else {
                    background = getDrawable(R.drawable.brightmodebackground);
                    textcolor = "#3A5A9B"; //#3A5A9B
                    buttoncolor = "#B7C8EA"; //#648AD6
                }
            layout.setBackground(background);
            darkmodeBeginText1.setTextColor(Color.parseColor(textcolor));
            darkmodeBeginText2.setTextColor(Color.parseColor(textcolor));
            darkmodeEndText1.setTextColor(Color.parseColor(textcolor));
            darkmodeEndText2.setTextColor(Color.parseColor(textcolor));
            secondsSwitch.setTextColor(Color.parseColor(textcolor));
            changeDates.setTextColor(Color.parseColor(textcolor));
            darkmodeSwitch.setTextColor(Color.parseColor(textcolor));
            darkmodeSettings.setTextColor(Color.parseColor(textcolor));
            darkmodeBegin.setTextColor(Color.parseColor(textcolor));
            darkmodeEnd.setTextColor(Color.parseColor(textcolor));
            darkmodeBeginText1.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeBeginText2.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeEndText1.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeEndText2.setBackgroundColor(Color.parseColor(buttoncolor));
            secondsSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
            changeDates.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeSettings.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeBegin.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeEnd.setBackgroundColor(Color.parseColor(buttoncolor));
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
            darkmodeSwitch.setTypeface(Typeface.MONOSPACE);
            darkmodeSettings.setTypeface(Typeface.MONOSPACE);
            darkmodeBegin.setTypeface(Typeface.MONOSPACE);
            darkmodeEnd.setTypeface(Typeface.MONOSPACE);
            darkmodeBeginText1.setText("");
            darkmodeBeginText2.setText("");
            darkmodeEndText1.setText("");
            darkmodeEndText2.setText("");
            secondsSwitch.setText("");
            changeDates.setText(getString(R.string.change_data_button));
            darkmodeSwitch.setText("");
            darkmodeSettings.setText("");
            darkmodeBegin.setText("");
            darkmodeEnd.setText("");
            darkmodeBegin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            darkmodeEnd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            darkmodeBegin.setInputType(InputType.TYPE_CLASS_NUMBER);
            darkmodeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
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
            darkmodeBeginText1.setY((float) (/*percent2.getY() + percent2.getMeasuredHeight() + */darkmodeBeginText1.getMeasuredHeight() * 2.0));
            darkmodeEndText1.setY((float) (darkmodeBeginText1.getY() + darkmodeEndText1.getMeasuredHeight() * 1.5));
            darkmodeBegin.setY(darkmodeBeginText1.getY());
            darkmodeEnd.setY(darkmodeEndText1.getY());
            darkmodeBeginText2.setY(darkmodeBeginText1.getY());
            darkmodeEndText2.setY(darkmodeEndText1.getY());
        });
    }

    public void updateUI(int mode) {
        runOnUiThread(() -> changeDates.setText(getString(R.string.change_data_button)));
        setColors();
        setText();
        setTextSizes(textSize);
        measure();
        setPositions(height, width, mode);
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
                    int aNum = Integer.parseInt(a);
                    if ((aNum > 24) || ((aNum < 0)) || (aNum > begin)) {
                        darkmodeEnd.setText(Integer.toString(end));
                    } else {
                        end = aNum;
                        darkmodeEnd.setText(Integer.toString(end));
                        save(end, "darkmodeEnd");
                    }
                }
            });

            /*
             * If the Button gets clicked, the mode of displaying the time is changed
             *  in this order: seconds, minutes, hours, days.
             * The Mode is saved.
             */
            secondsSwitch.setOnClickListener(view -> {
                darkmodeBegin.clearFocus();
                darkmodeEnd.clearFocus();
                if (secondsSwitch.getText().equals(getString(R.string.seconds))) {
                    secondsSwitch.setText(getString(R.string.minutes));
                    multiper = 60;
                } else {
                    if (secondsSwitch.getText().equals(getString(R.string.minutes))) {
                        secondsSwitch.setText(getString(R.string.hours));
                        multiper = 3600;
                    } else if(secondsSwitch.getText().equals(getString(R.string.hours))){
                        secondsSwitch.setText(getString(R.string.days));
                        multiper = 86400;
                    } else if(secondsSwitch.getText().equals(getString(R.string.days))){
                        secondsSwitch.setText(getString(R.string.customTime));
                        multiper = 15000;
                    } else {
                        secondsSwitch.setText(getString(R.string.seconds));
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
                if (!darkmodeSettings.getText().equals(getString(R.string.done))) {
                    updateUI(1);
                    darkmodeBegin.setVisibility(View.VISIBLE);
                    darkmodeEnd.setVisibility(View.VISIBLE);
                    darkmodeBeginText1.setVisibility(View.VISIBLE);
                    darkmodeBeginText2.setVisibility(View.VISIBLE);
                    darkmodeEndText1.setVisibility(View.VISIBLE);
                    darkmodeEndText2.setVisibility(View.VISIBLE);
                    darkmodeSettings.setText(getString(R.string.done));
                } else {
                    updateUI(0);
                    darkmodeBegin.setVisibility(View.INVISIBLE);
                    darkmodeEnd.setVisibility(View.INVISIBLE);
                    darkmodeBeginText1.setVisibility(View.INVISIBLE);
                    darkmodeBeginText2.setVisibility(View.INVISIBLE);
                    darkmodeEndText1.setVisibility(View.INVISIBLE);
                    darkmodeEndText2.setVisibility(View.INVISIBLE);
                    darkmodeSettings.setText(getString(R.string.setup));
                    log("Set the Darkmode-Times: From " + darkmodeBegin.getText() + " to " + darkmodeEnd.getText() + " o' clock.");
                }
                hideSoftKeyboard(findViewById(R.id.Layout1));
            });

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
                    background = getDrawable(R.drawable.darkmodebackground);
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
                        background = getDrawable(R.drawable.brightmodebackground);
                        textcolor = "#3A5A9B"; //#3A5A9B
                        buttoncolor = "#B7C8EA"; //#648AD6
                        darkmodeSettings.setVisibility(View.INVISIBLE);
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

    /*
     * hideSoftKeyboard is used to close the Android soft Keyboard.
     * The Input "view" is usually just (View) findViewById(R.id.Layout1)
     */
    public void hideSoftKeyboard(View view){
        runOnUiThread(() -> {
            InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
    }

    private void update() {
        setXYZ();
        if(darkmodeSwitch.getText().equals(getString(R.string.darkmode_auto))) {
            if(darkmodeSettings.getText().equals(getString(R.string.setup)))
                updateUI(0);
            if(darkmodeSettings.getText().equals(getString(R.string.done)))
                updateUI(1);
        } else {
            updateUI(0);
            runOnUiThread(() -> darkmodeSettings.setText(getString(R.string.setup)));
        }
        setPaddingSizes();
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
                        save(endDateUNIX, "EndeUNIX");
                    }

                secondsSwitch.setVisibility(View.VISIBLE);
                darkmodeSwitch.setVisibility(View.VISIBLE);
                changeDates.setVisibility(View.VISIBLE);
                if(darkmodeSwitch.getText().equals(getString(R.string.darkmode_auto)))
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

    public long calcMilSeconds(String first, long second) {
        long a = 0, b;
        b = second * 1000;
        try {
            a = timestamp(first);
        } catch(ParseException oi) {
            System.out.println("Debug: ParseException in timestamp");
        }
        if(a > b) {
            return a - b;
        } else {
            return b - a;
        }
    }

    /*
     * Does the Math to set x, y and z and their corresponding Strings.
     */
    public void setXYZ() {
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        nowWithoutZone = LocalDateTime.now();

        x = calcMilSeconds(now.format(dr), startDateUNIX - calcOffSeconds());
        long y = calcMilSeconds(now.format(dr), endDateUNIX - calcOffSeconds());
        completeTime = x + y;

        y = y / 1000;
        z = (double) x / (double) (completeTime);
        z = z * 100;
        x = x / 1000;
        if(multiper != 15000) x = x / multiper;
        if(multiper != 15000) y = y / multiper;
        if(multiper == 15000) {
            xString2 = createCustomTime(x);
        } else xString2 = convertToReadableString(x);
        if(multiper == 15000) {
            yString2 = createCustomTime(y);
        } else yString2 = convertToReadableString(y);
        zString2 = dform.format(z) + "";
    }

    private String convertToReadableString(long r) {
        String result = "" + r;
        int length = result.length();
        for(int i = length-1; i >= 1; i--) {
            if(((length - i) % 3) == 0) {
                result = result.substring(0, i) + "." + result.substring(i);
            }
        }
        return result;
    }

    private String createCustomTime(long x) {
        System.out.println("CreateCustomTime called with Value " + x);
        String result = "";
        if(x >= 86400) {
            result += (int)x/86400;
            result += "d ";
        }
        x = x % 86400;
        if(x >= 3600) {
            result += (int)x/3600;
            result += "h ";
        }
        x = x % 3600;
        if(x >= 60) {
            result += (int)x/60;
            result += "min ";
        }
        x = x % 60;
        if(x >= 0) {
            result += x;
            result += "s";
        }
        System.out.println("  result = " + result);
        return result;
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
            changeDates.setVisibility(View.INVISIBLE);
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