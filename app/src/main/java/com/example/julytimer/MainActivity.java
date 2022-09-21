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
    private TextView darkmodeBeginText1;
    private TextView darkmodeBeginText2;
    private TextView darkmodeEndText1;
    private TextView darkmodeEndText2;
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
    public int height;
    public int width;
    public int end;
    public int multiper;
    public int u = 0;
    public int darkMode;
    private int PROGRESS_CURRENT = 0;
    private int PROGRESS_MAX;
    public int textSize;
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
     * save(int) saves the given  int for later use under the given name
     */
    public void save(int a, String name) {
        editor.putInt(name, a);
        editor.apply();
    }

    /*
     * initialise initialises the UI and loads the saved Values in the Variables
     * uses initialiseNotifications and initialiseUI.
     */
    public void initialise() {
        secondsDone = new TextView(this);
        secondsLeft = new TextView(this);
        percent = new TextView(this);
        darkmodeBeginText1 = new TextView(this);
        darkmodeBeginText2 = new TextView(this);
        darkmodeEndText1 = new TextView(this);
        darkmodeEndText2 = new TextView(this);
        secondsSwitch = new Button(this);
        darkmodeSwitch = new Button(this);
        darkmodeSettings = new Button(this);
        darkmodeBegin = new EditText(this);
        darkmodeEnd = new EditText(this);
        ran = false;
        PROGRESS_MAX = 28814999;                                // This has to be updated for changeable Dates
        darkmodeBegin.setVisibility(View.INVISIBLE);
        darkmodeEnd.setVisibility(View.INVISIBLE);
        darkmodeBeginText1.setVisibility(View.INVISIBLE);
        darkmodeBeginText2.setVisibility(View.INVISIBLE);
        darkmodeEndText1.setVisibility(View.INVISIBLE);
        darkmodeEndText2.setVisibility(View.INVISIBLE);
        darkmodeSettings.setVisibility(View.INVISIBLE);
        darkmodeSettings.setText("Einstellen");
        dform = new DecimalFormat("#.#######");

        initialiseNotifications();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        initialiseUI();
        measure();
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
        multiper = sharedPref.getInt("timeMode", 1);

        darkMode = sharedPref.getInt("Darkmode", 0);
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
     * Measures everything on Screen.
     */
    public void measure() {
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        secondsSwitch.measure(0, 0);
        darkmodeSwitch.measure(0, 0);
        darkmodeSettings.measure(0, 0);
        secondsLeft.measure(0, 0);
        secondsDone.measure(0, 0);
        percent.measure(0, 0);
        darkmodeBeginText1.measure(0, 0);
        darkmodeBeginText2.measure(0, 0);
        darkmodeEndText1.measure(0, 0);
        darkmodeEndText2.measure(0, 0);
        darkmodeBegin.measure(0, 0);
        darkmodeEnd.measure(0, 0);
    }

    public void setTextSizes(int textSize) {
        secondsSwitch.setTextSize(textSize);
        darkmodeSwitch.setTextSize(textSize);
        darkmodeSettings.setTextSize(textSize);
        secondsLeft.setTextSize(textSize);
        secondsDone.setTextSize(textSize);
        percent.setTextSize(textSize);
        darkmodeBeginText1.setTextSize(textSize);
        darkmodeBeginText2.setTextSize(textSize);
        darkmodeEndText1.setTextSize(textSize);
        darkmodeEndText2.setTextSize(textSize);
        darkmodeBegin.setTextSize(textSize);
        darkmodeEnd.setTextSize(textSize);
    }

    public void setText() {
        secondsDone.setText(xString);
        secondsLeft.setText(yString);
        percent.setText(zString);

        if(multiper == 60) {
            secondsSwitch.setText("Minuten");
        }
        if(multiper == 3600) {
            secondsSwitch.setText("Stunden");
        }
        if(multiper == 86400) {
            secondsSwitch.setText("Tage");
        }
        if(multiper == 1) {
            secondsSwitch.setText("Sekunden");
        }
        darkmodeBeginText1.setText("Beginn des dunklen Modus:");
        darkmodeBeginText2.setText("Uhr");
        darkmodeEndText1.setText("Beginn des hellen Modus:");
        darkmodeEndText2.setText("Uhr");
        if(darkMode == 0) {
            darkmodeSwitch.setText("Darkmode: Aus");
        }
        if(darkMode == 1) {
            darkmodeSwitch.setText("Darkmode: An");
        }
        if(darkMode == 2) {
            darkmodeSwitch.setText("Darkmode: Auto");
        }
    }

    public void setPaddingSizes(int height, int width, int textSize) {
        measure();
        int horizontal = width / 27;
        int vertical = horizontal / 2;
        int buttonWidth = textSize * 27 + horizontal * 2;
        int buttonHeight = textSize * 12 + vertical * 2;
        secondsSwitch.setWidth(buttonWidth);
        darkmodeSwitch.setWidth(buttonWidth);
        darkmodeSettings.setWidth(buttonWidth);
        secondsSwitch.setHeight(buttonHeight);
        darkmodeSwitch.setHeight(buttonHeight);
        darkmodeSettings.setHeight(buttonHeight);
        secondsLeft.setPaddingRelative(horizontal, vertical, horizontal, vertical);
        secondsDone.setPaddingRelative(horizontal, vertical, horizontal, vertical);
        percent.setPaddingRelative(horizontal, vertical, horizontal, vertical);
        darkmodeBeginText1.setPaddingRelative(horizontal, vertical, 0, vertical);
        darkmodeBeginText2.setPaddingRelative(0, vertical, horizontal, vertical);
        darkmodeEndText1.setPaddingRelative(horizontal, vertical, 0, vertical);
        darkmodeEndText2.setPaddingRelative(0, vertical, horizontal, vertical);
        darkmodeBegin.setPaddingRelative(0, vertical, 0, vertical);
        darkmodeEnd.setPaddingRelative(0, vertical, 0, vertical);
    }

    public void setPositions(int height, int width, int mode) {
        measure();
        secondsSwitch.setX(width / 2 - secondsSwitch.getMeasuredWidth() / 2 - width / 4);
        darkmodeSwitch.setX(width / 2 - darkmodeSwitch.getMeasuredWidth() / 2 + width / 4);
        secondsDone.setX((width / 2) - secondsDone.getMeasuredWidth() / 2);
        secondsLeft.setX(width / 2 - secondsLeft.getMeasuredWidth() / 2);
        percent.setX(width / 2 - percent.getMeasuredWidth() / 2);
        darkmodeBeginText1.setX(width / 2 - (darkmodeBeginText1.getMeasuredWidth() + darkmodeBegin.getMeasuredWidth() + darkmodeBeginText2.getMeasuredWidth()) / 2);
        darkmodeBegin.setX(darkmodeBeginText1.getX() + darkmodeBeginText1.getMeasuredWidth());
        darkmodeBeginText2.setX(darkmodeBegin.getX() + darkmodeBegin.getMeasuredWidth());
        darkmodeEndText1.setX(width / 2 - (darkmodeEndText1.getMeasuredWidth() + darkmodeEnd.getMeasuredWidth() + darkmodeEndText2.getMeasuredWidth()) / 2);
        darkmodeEnd.setX(darkmodeEndText1.getX() + darkmodeEndText1.getMeasuredWidth());
        darkmodeEndText2.setX(darkmodeEnd.getX() + darkmodeEnd.getMeasuredWidth());
        if(mode == 0) darkmodeSettings.setX(darkmodeSwitch.getX());
        if(mode == 1) darkmodeSettings.setX(width / 2 - darkmodeSettings.getMeasuredWidth() / 2);

        darkmodeBeginText1.setY(height/10);
        if(mode == 0) {
            secondsDone.setY(height / 2 - secondsDone.getMeasuredHeight() / 2 - height / 4);
            secondsLeft.setY(height / 2 - secondsLeft.getMeasuredHeight() / 2 - height / 4 + textSize * 10);
            percent.setY(height / 2 - percent.getMeasuredHeight() / 2 - height / 4 + textSize * 20);
            darkmodeSettings.setY(percent.getY() + textSize * 60);
        }
        if(mode == 1) {
            secondsDone.setY(height / 2 - secondsDone.getMeasuredHeight() / 2 - textSize * 5);
            secondsLeft.setY(height / 2 - secondsLeft.getMeasuredHeight() / 2 + textSize * 5);
            percent.setY(height / 2 - percent.getMeasuredHeight() / 2 + textSize * 15);
            darkmodeSettings.setY(darkmodeEndText1.getY() + textSize * 15);
        }
        darkmodeEndText1.setY(darkmodeBeginText1.getY() + textSize * 10);
        secondsSwitch.setY(percent.getY() + textSize * 40);
        darkmodeSwitch.setY(secondsSwitch.getY());
        darkmodeBegin.setY(darkmodeBeginText1.getY());
        darkmodeEnd.setY(darkmodeEndText1.getY());
        darkmodeBeginText2.setY(darkmodeBeginText1.getY());
        darkmodeEndText2.setY(darkmodeEndText1.getY());
    }

    public void setColors() {
        if(darkMode == 0) {
            backgroundcolor = "#99B9F9"; //#B7C8EA
            textcolor = "#3A5A9B"; //#3A5A9B
            buttoncolor = "#B7C8EA"; //#648AD6
        }
        if (darkMode == 1) {
            backgroundcolor = "#2E2E2E";
            textcolor = "#C5C5C5";
            buttoncolor = "#464646";
        }
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
        layout.setBackgroundColor(Color.parseColor(backgroundcolor));
        secondsDone.setTextColor(Color.parseColor(textcolor));
        secondsLeft.setTextColor(Color.parseColor(textcolor));
        percent.setTextColor(Color.parseColor(textcolor));
        darkmodeBeginText1.setTextColor(Color.parseColor(textcolor));
        darkmodeBeginText2.setTextColor(Color.parseColor(textcolor));
        darkmodeEndText1.setTextColor(Color.parseColor(textcolor));
        darkmodeEndText2.setTextColor(Color.parseColor(textcolor));
        secondsSwitch.setTextColor(Color.parseColor(textcolor));
        darkmodeSwitch.setTextColor(Color.parseColor(textcolor));
        darkmodeSettings.setTextColor(Color.parseColor(textcolor));
        darkmodeBegin.setTextColor(Color.parseColor(textcolor));
        darkmodeEnd.setTextColor(Color.parseColor(textcolor));
        secondsDone.setBackgroundColor(Color.parseColor(buttoncolor));
        secondsLeft.setBackgroundColor(Color.parseColor(buttoncolor));
        percent.setBackgroundColor(Color.parseColor(buttoncolor));
        darkmodeBeginText1.setBackgroundColor(Color.parseColor(buttoncolor));
        darkmodeBeginText2.setBackgroundColor(Color.parseColor(buttoncolor));
        darkmodeEndText1.setBackgroundColor(Color.parseColor(buttoncolor));
        darkmodeEndText2.setBackgroundColor(Color.parseColor(buttoncolor));
        secondsSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
        darkmodeSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
        darkmodeSettings.setBackgroundColor(Color.parseColor(buttoncolor));
        darkmodeBegin.setBackgroundColor(Color.parseColor(buttoncolor));
        darkmodeEnd.setBackgroundColor(Color.parseColor(buttoncolor));
    }

    public void updateUI2(int mode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                measure();
                textSize = (int)(width / 90);
                setTextSizes(textSize);
                setText();
                setPaddingSizes(height, width, textSize);
                setPositions(height, width, mode);
                setColors();
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
                darkmodeBeginText1.setTypeface(Typeface.MONOSPACE);
                darkmodeBeginText2.setTypeface(Typeface.MONOSPACE);
                darkmodeEndText1.setTypeface(Typeface.MONOSPACE);
                darkmodeEndText2.setTypeface(Typeface.MONOSPACE);
                secondsSwitch.setTypeface(Typeface.MONOSPACE);
                darkmodeSwitch.setTypeface(Typeface.MONOSPACE);
                darkmodeSettings.setTypeface(Typeface.MONOSPACE);
                darkmodeBegin.setTypeface(Typeface.MONOSPACE);
                darkmodeEnd.setTypeface(Typeface.MONOSPACE);
                measure();
                darkmodeBegin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                darkmodeEnd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                darkmodeBegin.setInputType(InputType.TYPE_CLASS_NUMBER);
                darkmodeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                                    save(begin, "darkmodeBegin");
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
                                save(end, "darkmodeEnd");
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
                                darkmodeBeginText1.setVisibility(view.INVISIBLE);
                                darkmodeBeginText2.setVisibility(view.INVISIBLE);
                                darkmodeEndText1.setVisibility(view.INVISIBLE);
                                darkmodeEndText2.setVisibility(view.INVISIBLE);
                                darkmodeSwitch.setText("Darkmode: aus");
                            }
                        }
                        save(darkMode, "Darkmode");
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
                            updateUI2(1);
                            darkmodeBegin.setVisibility(View.VISIBLE);
                            darkmodeEnd.setVisibility(View.VISIBLE);
                            darkmodeBeginText1.setVisibility(View.VISIBLE);
                            darkmodeBeginText2.setVisibility(View.VISIBLE);
                            darkmodeEndText1.setVisibility(View.VISIBLE);
                            darkmodeEndText2.setVisibility(View.VISIBLE);
                            darkmodeSettings.setText("Fertig");
                        } else {
                            updateUI2(0);
                            darkmodeBegin.setVisibility(View.INVISIBLE);
                            darkmodeEnd.setVisibility(View.INVISIBLE);
                            darkmodeBeginText1.setVisibility(View.INVISIBLE);
                            darkmodeBeginText2.setVisibility(View.INVISIBLE);
                            darkmodeEndText1.setVisibility(View.INVISIBLE);
                            darkmodeEndText2.setVisibility(View.INVISIBLE);
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
                            } else if(secondsSwitch.getText().equals("Stunden")){
                                secondsSwitch.setText("Tage");
                                multiper = 86400;
                            } else {
                                secondsSwitch.setText("Sekunden");
                                multiper = 1;
                            }
                        }
                        save(multiper, "timeMode");
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
            System.out.println("Debug: ParseException in timestamp");
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
        if(multiper == 3600) {
            return "Stunden" + message;
        }
        return "Tage" + message;
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
        if(darkmodeSettings.getText().equals("Einstellen"))
            updateUI2(0);
        if(darkmodeSettings.getText().equals("Fertig"))
            updateUI2(1);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(darkmodeSwitch.getText().toString().equals("Darkmode: Auto"))
                    darkmodeSettings.setVisibility(View.VISIBLE);
            }
        });
        PROGRESS_CURRENT = (int) x;
        PROGRESS_MAX = (int) (completeTime / 1000);
        sendNotification(1, y + " " +secondsSwitch.getText().toString(), "noch in Ghana! " + z + " Prozent schon geschafft!");
    }

    /*
     * onCreate is the Method called on the start of the App. Here is most of the Code found located.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        System.out.println("Hallo");
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
        homeScreenLayout.addView(darkmodeBeginText1);
        homeScreenLayout.addView(darkmodeBeginText2);
        homeScreenLayout.addView(darkmodeEndText1);
        homeScreenLayout.addView(darkmodeEndText2);
        homeScreenLayout.addView(darkmodeBegin);
        homeScreenLayout.addView(darkmodeEnd);
        homeScreenLayout.addView(secondsSwitch);
        homeScreenLayout.addView(darkmodeSwitch);
        homeScreenLayout.addView(darkmodeSettings);
        tm1.schedule(tmTk1, 0, 100);
    }
}