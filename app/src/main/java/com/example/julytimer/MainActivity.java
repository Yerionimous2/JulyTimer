package com.example.julytimer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    /*
    * Usage of the Variables:
    * sharedPref and editor are used to save the Variables:
    *   begin
    *   end
    *   multiper
    *   darkMode
    * df is used to save the time Pattern, in which the Dates and the time right now are saved
    * ar is used to save the time Pattern, in which the Dates and the time right now are saved
    * dz is used to save the time Pattern, in which the hour right now is saved
    * displayMetrics is used to Measure the Display
    *
    * secondsLeft is used to Display the "Sekunden, bis du mich wiedersiehst" text
    * secondsDone is used to Display the "Sekunden, seit du mich gesehen hast" text
    * percent is used to Display the "Prozent schon geschafft!" text
    * darkmodeBeginText is used to Display the "Beginn des dunklen Modus" text
    * darkmodeEndText is used to Display the "Beginn des hellen Modus" text
    * secondsSwitch is used to change the time displayed from seconds to minutes or hours
    * darkmodeSwitch is used to switch the mode the display is colored
    * darkmodeSettings is used to activate the Settings of the Times the automatic dark mode starts end ends and to validate the changes made
    * darkmodeBegin is used to let the user edit the time where the darkmode starts
    * darkmodeEnd is used to let the user edit the time where the darkmode ends
    *
    * begin is used to store time where the darkmode starts
    * end is used to store the time where the darkmode ends
    * multiper is the multiplier used to change from seconds to minutes an hours. It is either 1 for Seconds, 60 for Minutes or 3600 for Hours
    * u is used to push the buttons and texts down to make space for the darkmode settings. It is either 0 for normal use or 500 for pushing downwards
    * darkMode is used to check which mode is used to change the colors of the Display elements. It is either 0 for off, 1 for on or 2 for automatic
    * PROGRESS_CURRENT is used to make the Progress Bar in the Notification happen. It is a Value between 0 and completeTime, being the number of Seconds passed
    * ran is used to check whether the App has updated at least once this session in the percent Progress message. It is either false for not run or true for run
    * backgroundcolor, textcolor and buttoncolor are used for easier access to the specific Color Values. They are defined in colors.txt
    * a is used to make the Conversion from String to int happen when the User types in a new time for the darkmode automatic
    * percentage is used to check wheather an increase in percent has happened. It is a Value from 0 to 100, just being the floored percentage
    * x is used to save the seconds already passed
    * y is used to save the seconds needing to pass
    * completeTime is used to save the seconds between the start and end dates
    * z is used to save the percent of time already passed
    */
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    View layout;
    @SuppressLint("SimpleDateFormat")
    LocalDateTime now;
    LocalDateTime nowWithoutZone;
    public final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dz = DateTimeFormatter.ofPattern("HH");
    private final DisplayMetrics displayMetrics = new DisplayMetrics();
    TimerTask tmTk1;
    private DecimalFormat dform;
    private TextView secondsLeft;
    public TextView secondsDone;
    private TextView percent;
    private TextView darkmodeBeginText;
    private TextView darkmodeEndText;
    public Button secondsSwitch;
    public Button darkmodeSwitch;
    public Button darkmodeSettings;
    public EditText darkmodeBegin;
    public EditText darkmodeEnd;
    NotificationChannel channel;
    NotificationChannel channel2;
    NotificationManager notificationManager;
    NotificationManager notificationManager2;
    NotificationCompat.Builder builder;
    NotificationCompat.Builder builder2;
    public final Timer tm1 = new Timer();
    public int begin;
    public int end;
    public int multiper;
    public int u = 0;
    public int darkMode;
    private int PROGRESS_CURRENT = 0;
    private int PROGRESS_MAX;
    private boolean ran;
    private String backgroundcolor = "#000000";
    private String textcolor = "#000000";
    private String buttoncolor = "#000000";
    public String a;
    public String xString, yString, zString;
    public String title = "";
    public String message = "";
    private double percentage;
    private long x;
    private long y;
    private long completeTime;
    private double z;

    /*
     * save(int) saves the given  int for later use
     */
    public void save(int a) {

    }

    /*
     * initialise initialises the UI and loads the saved Values in the Variables
     * uses initialiseNotifications and initialiseUI.
     */
    public void initialise() {
        secondsDone = new TextView(this);
        secondsLeft = new TextView(this);
        percent = new TextView(this);
        darkmodeBeginText = new TextView(this);
        darkmodeEndText = new TextView(this);
        secondsSwitch = new Button(this);
        darkmodeSwitch = new Button(this);
        darkmodeSettings = new Button(this);
        darkmodeBegin = new EditText(this);
        darkmodeEnd = new EditText(this);
        ran = false;
        PROGRESS_MAX = 28814999;                                // This has to be updated for changeable Dates
        darkmodeBegin.setVisibility(View.INVISIBLE);
        darkmodeEnd.setVisibility(View.INVISIBLE);
        darkmodeBeginText.setVisibility(View.INVISIBLE);
        darkmodeEndText.setVisibility(View.INVISIBLE);
        darkmodeSettings.setVisibility(View.INVISIBLE);
        darkmodeSettings.setText("Einstellen");
        dform = new DecimalFormat("#.#######");

        initialiseNotifications();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        initialiseUI();

        initialiseListeners();
    }

    public void initialiseNotifications() {
        channel = new NotificationChannel("35", "channel", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Die coole progress-Bar");
        notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        builder = new NotificationCompat.Builder(this, "35");
        builder.setContentText("Ghana in progress")
                .setContentTitle("Ghana in progress")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle());

        channel2 = new NotificationChannel("36", "channel2", NotificationManager.IMPORTANCE_HIGH);
        channel2.setDescription("Die Meilensteinbenachrichtigung");
        notificationManager2 = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel2);
        builder2 = new NotificationCompat.Builder(this, "36");
    }

    /*
     * All the stored Data is being Initialized.
     * The default Values are:
     *   multiper: 1
     *   darkmode: 0
     *   begin: 19
     *   end: 7
     */
    public void loadVariables() {
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        begin = sharedPref.getInt("darkmodeBegin", 19);
        end = sharedPref.getInt("darkmodeEnd", 7);

        secondsSwitch.setText(sharedPref.getString("timeMode", "Sekunden"));
        if(secondsSwitch.getText().equals("Minuten")) {
            multiper = 60;
        }
        if(secondsSwitch.getText().equals("Stunden")) {
            multiper = 3600;
        }
        if(secondsSwitch.getText().equals("Sekunden")){
            multiper = 1;
        }

        darkMode = sharedPref.getInt("Darkmode", 0);
        if(darkMode == 1) {
            backgroundcolor = "#2E2E2E";
            textcolor = "#C5C5C5";
            buttoncolor = "#464646";
            darkmodeSwitch.setText("Darkmode: an");
        }
        if(darkMode == 2) {
            darkmodeSwitch.setText("Darkmode: auto");
            darkmodeBegin.setText(Integer.toString(begin));
            darkmodeEnd.setText(Integer.toString(end));
            darkmodeSettings.setVisibility(View.VISIBLE);
        }
        if(darkMode == 0) {
            backgroundcolor = "#99B9F9"; //#B7C8EA
            textcolor = "#3A5A9B"; //#3A5A9B
            buttoncolor = "#B7C8EA"; //#648AD6
            darkmodeSwitch.setText("Darkmode: aus");
        }
    }

    /*
     * Sends a Notification to the User, either in the "normal" Channel(1) used for the Time showing or
     * the "meilenstein" Channel(2) used for the milestone achievements
     */
    public void sendNotification(int channel, String title, String message) {
        if(channel == 1) {
            builder.setContentText(message)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigTextStyle())
                    .setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
            notificationManager.notify(1, builder.build());
        }
        if(channel == 2) {
            builder2.setContentText(message)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigTextStyle());
            notificationManager2.notify(2, builder2.build());
        }
    }

    /*
     * updateUI updates the UI
     */
    public void updateUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int width = displayMetrics.widthPixels;
                int height = displayMetrics.heightPixels;
                secondsDone.measure(0, 0);
                darkmodeBeginText.measure(0, 0);
                darkmodeEndText.measure(0, 0);
                secondsLeft.measure(0, 0);
                percent.measure(0, 0);
                secondsSwitch.setX(width / 2 - 450);
                secondsSwitch.setY(height / 4 * 3 - 550 + u);
                darkmodeSwitch.setX(width / 2 + 50);
                darkmodeSwitch.setY(height / 4 * 3 - 550 + u);
                if(darkmodeSettings.getText().equals("Einstellen")) {
                    darkmodeSettings.setX(darkmodeSwitch.getX());
                } else {
                    darkmodeSettings.setX(width / 2 - 200);
                }
                secondsDone.setX((width / 2) - (secondsDone.getMeasuredWidth() / 2));
                secondsLeft.setX(width / 2 - secondsLeft.getMeasuredWidth() / 2);
                percent.setX(width / 2 - percent.getMeasuredWidth() / 2);
                secondsDone.setY(height / 2 - secondsDone.getMeasuredHeight() / 2 - height / 4 + u);
                secondsLeft.setY(height / 2 - secondsLeft.getMeasuredHeight() / 2 - height / 4 + 130 + u);
                percent.setY(height / 2 - percent.getMeasuredHeight() / 2 - height / 4 + 260 + u);
                darkmodeBeginText.setX(width / 2 - darkmodeBeginText.getMeasuredWidth() / 2);
                darkmodeEndText.setX(width / 2 - darkmodeEndText.getMeasuredWidth() / 2);
                if(darkmodeSettings.getText().equals("Einstellen")) {
                    darkmodeSettings.setY(secondsDone.getY() + 850);
                }
                darkmodeBeginText.setY(150);
                darkmodeEndText.setY(280);
                darkmodeBegin.setX(darkmodeBeginText.getX() + 635);
                darkmodeEnd.setX(darkmodeEndText.getX() + 610);
                darkmodeBegin.setY(darkmodeBeginText.getY());
                darkmodeEnd.setY(darkmodeEndText.getY());
                layout.setBackgroundColor(Color.parseColor(backgroundcolor));
                secondsDone.setTextColor(Color.parseColor(textcolor));
                secondsLeft.setTextColor(Color.parseColor(textcolor));
                percent.setTextColor(Color.parseColor(textcolor));
                darkmodeBeginText.setTextColor(Color.parseColor(textcolor));
                darkmodeEndText.setTextColor(Color.parseColor(textcolor));
                secondsSwitch.setTextColor(Color.parseColor(textcolor));
                darkmodeSwitch.setTextColor(Color.parseColor(textcolor));
                darkmodeSettings.setTextColor(Color.parseColor(textcolor));
                darkmodeBegin.setTextColor(Color.parseColor(textcolor));
                darkmodeEnd.setTextColor(Color.parseColor(textcolor));
                secondsDone.setBackgroundColor(Color.parseColor(buttoncolor));
                secondsLeft.setBackgroundColor(Color.parseColor(buttoncolor));
                percent.setBackgroundColor(Color.parseColor(buttoncolor));
                darkmodeBeginText.setBackgroundColor(Color.parseColor(buttoncolor));
                darkmodeEndText.setBackgroundColor(Color.parseColor(buttoncolor));
                secondsSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
                darkmodeSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
                darkmodeSettings.setBackgroundColor(Color.parseColor(buttoncolor));
                darkmodeBegin.setBackgroundColor(Color.parseColor(buttoncolor));
                darkmodeEnd.setBackgroundColor(Color.parseColor(buttoncolor));

                secondsDone.setText(xString);
                secondsLeft.setText(yString);
                percent.setText(zString);
                secondsDone.setPaddingRelative(40, 15, 40, 20);
                darkmodeBeginText.setPaddingRelative(40, 15, 40, 20);
                darkmodeBegin.setPaddingRelative(0, 15, 0, 20);
                darkmodeEndText.setPaddingRelative(40, 15, 40, 20);
                darkmodeEnd.setPaddingRelative(0, 15, 0, 20);
                secondsLeft.setPaddingRelative(40, 15, 40, 20);
                percent.setPaddingRelative(40, 15, 40, 20);

                if(darkMode == 2)
                    if ((Integer.parseInt(nowWithoutZone.format(dz)) > begin) || (Integer.parseInt(nowWithoutZone.format(dz)) < end)) {
                        backgroundcolor = "#2E2E2E";
                        textcolor = "#C5C5C5";
                        buttoncolor = "#464646";
                    } else {
                        backgroundcolor = "#99B9F9"; //#B7C8EA
                        textcolor = "#3A5A9B"; //#3A5A9B
                        buttoncolor = "#B7C8EA"; //#648AD6
                    }
            }
        });
    }

    /*
     * Initialises the UI
     */
    public void initialiseUI() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                secondsDone.setTypeface(Typeface.MONOSPACE);
                secondsLeft.setTypeface(Typeface.MONOSPACE);
                percent.setTypeface(Typeface.MONOSPACE);
                darkmodeBeginText.setTypeface(Typeface.MONOSPACE);
                darkmodeEndText.setTypeface(Typeface.MONOSPACE);
                secondsSwitch.setTypeface(Typeface.MONOSPACE);
                darkmodeSwitch.setTypeface(Typeface.MONOSPACE);
                darkmodeSettings.setTypeface(Typeface.MONOSPACE);
                darkmodeBegin.setTypeface(Typeface.MONOSPACE);
                darkmodeEnd.setTypeface(Typeface.MONOSPACE);
                secondsDone.setTextSize(12);
                secondsLeft.setTextSize(12);
                percent.setTextSize(12);
                darkmodeBeginText.setTextSize(12);
                darkmodeEndText.setTextSize(12);
                secondsSwitch.setTextSize(12);
                darkmodeSwitch.setTextSize(12);
                darkmodeSettings.setTextSize(12);
                darkmodeBegin.setTextSize(12);
                darkmodeEnd.setTextSize(12);
                secondsSwitch.setWidth(400);
                secondsSwitch.setHeight(175);
                darkmodeSwitch.setWidth(400);
                darkmodeSwitch.setHeight(175);
                darkmodeSettings.setWidth(400);
                darkmodeSettings.setHeight(175);
                darkmodeBegin.setWidth(100);
                darkmodeBegin.setHeight(110);
                darkmodeEnd.setWidth(100);
                darkmodeEnd.setHeight(110);
                darkmodeBegin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                darkmodeEnd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                darkmodeBegin.setInputType(InputType.TYPE_CLASS_NUMBER);
                darkmodeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
                darkmodeBeginText.setText("Beginn des dunklen Modus:          Uhr");
                darkmodeEndText.setText("Beginn des hellen Modus:          Uhr");
            }
        });
    }

    public void initialiseListeners() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                darkmodeBegin.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            a = darkmodeBegin.getText().toString();
                            if (!a.equals("")) {
                                int aNum = Integer.parseInt(a);
                                if ((aNum > 24) || ((aNum < 0)) || (aNum < end)) {
                                    darkmodeBegin.setText(Integer.toString(begin));
                                } else {
                                    begin = aNum;
                                    darkmodeBegin.setText(Integer.toString(begin));
                                    editor.putInt("darkmodeBegin", begin);
                                    editor.apply();
                                }
                            } else darkmodeBegin.setText(Integer.toString(begin));
                        }
                    }
                });

                /*
                 * When the Focus on the EditText is lost, the Number gets Validated and moved to the
                 * end variable. The Focus is lost when a Button is pushed or the other EditText gets
                 * focussed.
                 */
                darkmodeEnd.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            a = darkmodeEnd.getText().toString();
                            int aNum = Integer.parseInt(a);
                            if ((aNum > 24) || ((aNum < 0)) || (aNum > begin)) {
                                darkmodeEnd.setText(Integer.toString(end));
                            } else {
                                end = aNum;
                                darkmodeEnd.setText(Integer.toString(end));
                                editor.putInt("darkmodeEnd", end);
                                editor.apply();
                            }
                        }
                    }
                });

                /*
                 * Changes the Dark mode by adding 1 to the counter until 2, otherwise set it to 0.
                 * the 2 activates the automatic. Afterwards the darkmode is saved.
                 */
                darkmodeSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        darkmodeBegin.clearFocus();
                        darkmodeEnd.clearFocus();
                        if (darkMode == 0) {
                            darkMode = 1;
                            backgroundcolor = "#2E2E2E";
                            textcolor = "#C5C5C5";
                            buttoncolor = "#464646";
                            darkmodeSwitch.setText("Darkmode: an");
                        } else {
                            if (darkMode == 1) {
                                darkMode = 2;
                                darkmodeSwitch.setText("Darkmode: auto");
                                darkmodeBegin.setText(Integer.toString(begin));
                                darkmodeEnd.setText(Integer.toString(end));
                                darkmodeSettings.setVisibility(View.VISIBLE);
                            } else {
                                darkMode = 0;
                                backgroundcolor = "#99B9F9"; //#B7C8EA
                                textcolor = "#3A5A9B"; //#3A5A9B
                                buttoncolor = "#B7C8EA"; //#648AD6
                                darkmodeSettings.setVisibility(view.INVISIBLE);
                                darkmodeBegin.setVisibility(view.INVISIBLE);
                                darkmodeEnd.setVisibility(view.INVISIBLE);
                                darkmodeBeginText.setVisibility(view.INVISIBLE);
                                darkmodeEndText.setVisibility(view.INVISIBLE);
                                darkmodeSwitch.setText("Darkmode: aus");
                            }
                        }
                        editor.putInt("Darkmode", darkMode);
                        editor.apply();
                    }
                });

                /*
                 * Activates the Settings if the Settings are off and the other way around.
                 * Validates the Settings changed and saves them. Also closes the Keyboard.
                 */
                darkmodeSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!darkmodeSettings.getText().equals("Fertig")) {
                            u = 500;
                            int width = displayMetrics.widthPixels;
                            int height = displayMetrics.heightPixels;
                            secondsDone.measure(0, 0);
                            darkmodeBeginText.measure(0, 0);
                            darkmodeEndText.measure(0, 0);
                            secondsLeft.measure(0, 0);
                            percent.measure(0, 0);
                            darkmodeSettings.setX(width / 2 - 200);
                            darkmodeSettings.setY(secondsDone.getY());
                            darkmodeSwitch.setX(width / 2 + 50);
                            darkmodeSwitch.setY(height / 4 * 3 - 550 + u);
                            secondsSwitch.setX(width / 2 - 450);
                            secondsSwitch.setY(height / 4 * 3 - 550 + u);
                            secondsDone.setX((width / 2) - (secondsDone.getMeasuredWidth() / 2));
                            secondsDone.setY(height / 2 - secondsDone.getMeasuredHeight() / 2 - height / 4 + u);
                            secondsLeft.setX(width / 2 - secondsLeft.getMeasuredWidth() / 2);
                            secondsLeft.setY(height / 2 - secondsLeft.getMeasuredHeight() / 2 - height / 4 + 130 + u);
                            percent.setX(width / 2 - percent.getMeasuredWidth() / 2);
                            percent.setY(height / 2 - percent.getMeasuredHeight() / 2 - height / 4 + 260 + u);
                            darkmodeBeginText.setX(width / 2 - darkmodeBeginText.getMeasuredWidth() / 2);
                            darkmodeBeginText.setY(150);
                            darkmodeEndText.setX(width / 2 - darkmodeEndText.getMeasuredWidth() / 2);
                            darkmodeEndText.setY(280);
                            darkmodeBegin.setX(darkmodeBeginText.getX() + 635);
                            darkmodeBegin.setY(darkmodeBeginText.getY());
                            darkmodeEnd.setX(darkmodeEndText.getX() + 610);
                            darkmodeEnd.setY(darkmodeEndText.getY());
                            darkmodeBegin.setVisibility(View.VISIBLE);
                            darkmodeEnd.setVisibility(View.VISIBLE);
                            darkmodeBeginText.setVisibility(View.VISIBLE);
                            darkmodeEndText.setVisibility(View.VISIBLE);
                            darkmodeSettings.setText("Fertig");
                        } else {
                            u = 0;
                            int width = displayMetrics.widthPixels;
                            int height = displayMetrics.heightPixels;
                            secondsDone.measure(0, 0);
                            darkmodeBeginText.measure(0, 0);
                            darkmodeEndText.measure(0, 0);
                            secondsLeft.measure(0, 0);
                            percent.measure(0, 0);
                            secondsSwitch.setX(width / 2 - 450);
                            secondsSwitch.setY(height / 4 * 3 - 550 + u);
                            darkmodeSwitch.setX(width / 2 + 50);
                            darkmodeSwitch.setY(height / 4 * 3 - 550 + u);
                            darkmodeSettings.setX(darkmodeSwitch.getX());
                            secondsDone.setX((width / 2) - (secondsDone.getMeasuredWidth() / 2));
                            secondsLeft.setX(width / 2 - secondsLeft.getMeasuredWidth() / 2);
                            percent.setX(width / 2 - percent.getMeasuredWidth() / 2);
                            secondsDone.setY(height / 2 - secondsDone.getMeasuredHeight() / 2 - height / 4 + u);
                            secondsLeft.setY(height / 2 - secondsLeft.getMeasuredHeight() / 2 - height / 4 + 130 + u);
                            percent.setY(height / 2 - percent.getMeasuredHeight() / 2 - height / 4 + 260 + u);
                            darkmodeBeginText.setX(width / 2 - darkmodeBeginText.getMeasuredWidth() / 2);
                            darkmodeEndText.setX(width / 2 - darkmodeEndText.getMeasuredWidth() / 2);
                            darkmodeSettings.setY(secondsDone.getY() + 850);
                            darkmodeBeginText.setY(150);
                            darkmodeEndText.setY(280);
                            darkmodeBegin.setX(darkmodeBeginText.getX() + 635);
                            darkmodeEnd.setX(darkmodeEndText.getX() + 610);
                            darkmodeBegin.setY(darkmodeBeginText.getY());
                            darkmodeEnd.setY(darkmodeEndText.getY());
                            darkmodeBegin.setVisibility(View.INVISIBLE);
                            darkmodeEnd.setVisibility(View.INVISIBLE);
                            darkmodeBeginText.setVisibility(View.INVISIBLE);
                            darkmodeEndText.setVisibility(View.INVISIBLE);
                            darkmodeSettings.setText("Einstellen");
                        }
                        hideSoftKeyboard((View) findViewById(R.id.Layout1));
                    }
                });

                secondsSwitch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        darkmodeBegin.clearFocus();
                        darkmodeEnd.clearFocus();
                        if (secondsSwitch.getText().equals("Sekunden")) {
                            secondsSwitch.setText("Minuten");
                            multiper = 60;
                        } else {
                            if (secondsSwitch.getText().equals("Minuten")) {
                                secondsSwitch.setText("Stunden");
                                multiper = 3600;
                            } else {
                                secondsSwitch.setText("Sekunden");
                                multiper = 1;
                            }
                        }
                        editor.putString("timeMode", secondsSwitch.getText().toString());
                        editor.apply();
                    }
                });
            }
        });
    }

    public void sendMileStoneNotifications() {
        if ((percentage < Math.floor(z)) && (ran)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Noch ein Prozent geschafft! Ich liebe dich <3", Toast.LENGTH_LONG);
            toast.show();
        }

        if((z>33.334)&&(z<33.34)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Ein Drittel ist schon geschafft! Wie cool <3", Toast.LENGTH_LONG);
            toast.show();
            sendNotification(2, "JulyTimer-Meilenstein", "Die Hälfte ist schon geschafft!");
        }

        if((z>66.667)&&(z<66.67)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Zwei Drittel ist schon geschafft! Wie cool <3", Toast.LENGTH_LONG);
            toast.show();
            sendNotification(2, "JulyTimer-Meilenstein", "Zwei Drittel ist schon geschafft!");
        }

        if((z>99.99)&&(z<100)) {
            Toast toast = Toast.makeText(getApplicationContext(), "Fast alles geschafft! Bis bald :)", Toast.LENGTH_LONG);
            toast.show();
            sendNotification(2, "JulyTimer-Meilenstein", "Fast geschafft! Bis bald :)");
        }

        if((now.format(DateTimeFormatter.ofPattern("dd")) == "24")&&((now.format(DateTimeFormatter.ofPattern("HH:mm:ss")) == "07:30:00"))) {
            Toast toast = Toast.makeText(getApplicationContext(), "Noch einen Monat geschafft! Ich bin stolz auf dich <3", Toast.LENGTH_LONG);
            toast.show();
            sendNotification(2, "JulyTimer-Meilenstein", "Noch einen Monat geschafft! Ich bin stolz auf dich <3");
        }
    }

    /*
     * hideSoftKeyboard is used to close the Android soft Keyboard.
     * The Input "view" is usually just (View) findViewById(R.id.Layout1)
     */
    public void hideSoftKeyboard(View view){
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*
     * timestamp is used to convert a Date and Time combo to the UNIX Milliseconds.
     * This is used in the calculations for the seconds passed and the seconds to pass
     */
    public long timestamp(String arg) throws ParseException {
        return df.parse(arg).getTime();
    }

    /*
     * Calculates the Seconds between two given Dates.
     */
    public long calcMilSeconds(String first, String second) {
        long a = 0, b = 0;
        try {
            a = timestamp(first);
            b = timestamp(second);
        } catch(ParseException oi) {
            System.out.println("Error: ParseExceptionin timestamp");
        }
        if(a > b) {
            return a - b;
        } else {
            return b - a;
        }
    }

    public String setString(int multiper, String message) {
        if(multiper == 1) {
            return "Sekunden" + message;
        }
        if(multiper == 60) {
            return "Minuten" + message;
        }
        return "Stunden" + message;
    }

    public void setXYZ() {
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        nowWithoutZone = LocalDateTime.now();
        x = calcMilSeconds(now.format(dr), "2022-08-24 09:30:00.000");
        y = calcMilSeconds(now.format(dr), "2023-07-23 21:40:00.000");
        completeTime = calcMilSeconds("2022-08-24 09:30:00.000", "2023-07-23 21:40:00.000");

        y = y / 1000;
        z = (double) x / (double) (completeTime);
        z = z * 100;
        x = x / 1000;
        x = x / multiper;
        y = y / multiper;
        xString = setString(multiper, ", seit du mich gesehen hast: " + x);
        yString = setString(multiper, ", bis du mich wiedersiehst: " + y);
        zString = dform.format(z) + " Prozent schon geschafft!";
    }

    public void update() {
        setXYZ();

        sendMileStoneNotifications();

        percentage = Math.floor(z);

        ran = true;

        if(percentage >= 100) {
            Toast toast = Toast.makeText(getApplicationContext(), "Jaaaaaaa <3", Toast.LENGTH_LONG);
            toast.show();
            tm1.cancel();
            secondsDone.setText("Sekunden, seit du mich gesehen hast: " + completeTime);
            secondsLeft.setText("Sekunden, bist du mich wiedersiehst: 0");
            percent.setText("100 Prozent schon geschafft!");
        }
        updateUI();
        PROGRESS_CURRENT = (int) (x*multiper);
        sendNotification(1, y + " " +secondsSwitch.getText().toString(), "noch in Ghana! " + z + " Prozent schon geschafft!");
    }
    /*
     * onCreate is the Method called on the start of the App. Here is most of the Code found located.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ConstraintLayout homeScreenLayout = findViewById(R.id.Layout1);

        initialise();

        loadVariables();

        layout = findViewById(R.id.Layout1);
        tmTk1 = new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                update();
            }
        };

        /*
         * The display modules are put on the Screen, even though some of them are invisible.
         * The Timer gets started and the Timertask gets executed roughly every 100 Milliseconds.
         */
        homeScreenLayout.addView(secondsDone);
        homeScreenLayout.addView(secondsLeft);
        homeScreenLayout.addView(percent);
        homeScreenLayout.addView(darkmodeBeginText);
        homeScreenLayout.addView(darkmodeEndText);
        homeScreenLayout.addView(darkmodeBegin);
        homeScreenLayout.addView(darkmodeEnd);
        homeScreenLayout.addView(secondsSwitch);
        homeScreenLayout.addView(darkmodeSwitch);
        homeScreenLayout.addView(darkmodeSettings);
        tm1.schedule(tmTk1, 0, 100);
    }
}