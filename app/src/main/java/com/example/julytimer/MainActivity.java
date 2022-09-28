package com.example.julytimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
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
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
@SuppressWarnings("SpellCheckingInspection")

public class MainActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    View layout;
    LocalDateTime now;
    LocalDateTime nowWithoutZone;
    @SuppressLint("SimpleDateFormat")
    public final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dz = DateTimeFormatter.ofPattern("HH");
    private DecimalFormat dform;
    //private final DisplayMetrics displayMetrics = new DisplayMetrics();
    TimerTask tmTk1;
    private TextView secondsLeft;
    public TextView secondsDone;
    private TextView percent;
    private TextView darkmodeBeginText1;
    private TextView darkmodeBeginText2;
    private TextView darkmodeEndText1;
    private TextView darkmodeEndText2;
    private TextView lbstartDate;
    public Button secondsSwitch;
    public Button changeDates;
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
    public int begin = 0;
    public int end = 0;
    public int height;
    public int width;
    public int multiper;
    public int darkMode;
    private int PROGRESS_CURRENT = 0;
    private int PROGRESS_MAX;
    public int textSize;
    private boolean ran;
    private boolean timerBool = true;
    public boolean changedStart, changedEnd;
    private String backgroundcolor = "#000000";
    private String textcolor = "#000000";
    private String buttoncolor = "#000000";
    public String a;
    public String xString, yString, zString;
    public String startDate = "2022-08-24 09:30:00.000";
    public String endDate = "2023-07-23 21:40:00.000";
    private long completeTime;
    private long x;
    private long y;
    private double z;
    private double percentage;

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
    }


    /*
     * initialise initialises the UI and loads the saved Values in the Variables
     * uses initialiseNotifications and initialiseUI.
     */
    public void initialise() {
        Context a = this;
        runOnUiThread(() -> {
            secondsDone = new TextView(a);
            secondsLeft = new TextView(a);
            percent = new TextView(a);
            darkmodeBeginText1 = new TextView(a);
            darkmodeBeginText2 = new TextView(a);
            darkmodeEndText1 = new TextView(a);
            darkmodeEndText2 = new TextView(a);
            secondsSwitch = new Button(a);
            changeDates = new Button(a);
            darkmodeSwitch = new Button(a);
            darkmodeSettings = new Button(a);
            darkmodeBegin = new EditText(a);
            darkmodeEnd = new EditText(a);
            ran = false;
            PROGRESS_MAX = 28814999;                                // Failsafe for Notification
            darkmodeBegin.setVisibility(View.INVISIBLE);
            darkmodeEnd.setVisibility(View.INVISIBLE);
            darkmodeBeginText1.setVisibility(View.INVISIBLE);
            darkmodeBeginText2.setVisibility(View.INVISIBLE);
            darkmodeEndText1.setVisibility(View.INVISIBLE);
            darkmodeEndText2.setVisibility(View.INVISIBLE);
            darkmodeSettings.setVisibility(View.INVISIBLE);
            darkmodeSettings.setText(getString(R.string.setup_darkmode_times));
            dform = new DecimalFormat("#.######");

            initialiseNotifications();

            //getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            initialiseUI();
            measure();
            initialiseListeners();
        });

    }

    /*
     * initialiseNotifications initialises the Notificationchannels and their builders needed to send
     *  Notifications to the User.
     */
    public void initialiseNotifications() {
        Context context = this;
        runOnUiThread(() -> {
            /*
             * Stuff needed for the Progressbar Notification
             */
            channel = new NotificationChannel("35", "channel", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.notification_name_1));
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(context, "35");
            builder.setContentText("")
                    .setContentTitle("")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new NotificationCompat.BigTextStyle());

            /*
             * Stuff needed for the Milestone Notifications
             */
            channel2 = new NotificationChannel("36", "channel2", NotificationManager.IMPORTANCE_HIGH);
            channel2.setDescription(getString(R.string.notification_name_2));
            notificationManager2 = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel2);
            builder2 = new NotificationCompat.Builder(context, "36");
        });
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
         * startDate, endDate are used to Store the start and end date
         * completeTime is used to save the amount of seconds between start and end date
         * x is used to save the amount of seconds passed since the startDate
         * y is used to save the amount of seconds between now and the endDate
         * z is used to save the percentage of time passed
         */
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        begin = sharedPref.getInt("darkmodeBegin", 19);
        end = sharedPref.getInt("darkmodeEnd", 7);
        multiper = sharedPref.getInt("timeMode", 1);

        darkMode = sharedPref.getInt("Darkmode", 0);

        startDate = sharedPref.getString("Start", "2022-08-24 09:30:00.000");
        endDate = sharedPref.getString("Ende", "2023-07-23 21:40:00.000");
    }

    /*
     * Sends a Notification to the User, either in the "normal" Channel(1) used for the Progressbar or
     *  the "milestone" Channel(2) used for the milestone achievements.
     * channel: the Channel used.
     *  1 = progress-bar
     *  2 = milestones
     * title:   the title of the Notification
     * message: the content of the Notification
     */
    public void sendNotification(int channel, String title, String message) {
        runOnUiThread(() -> {
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
        });
    }

    public static int getScreenWidth(@NonNull Activity activity) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().width() - insets.left - insets.right;
    }

    public static int getScreenHeight(@NonNull Activity activity) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            return windowMetrics.getBounds().height() - insets.top - insets.bottom;
    }

    /*
     * Measures everything on Screen.
     */
    public void measure() {
        height = getScreenHeight(this);
        width = getScreenWidth(this);

        runOnUiThread(() -> {
            secondsSwitch.measure(0, 0);
            changeDates.measure(0, 0);
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
        });

    }

    /*
     * Sets the Textsizes of anything containing Text to the given textSize.
     */
    public void setTextSizes(int textSize) {
        runOnUiThread(() -> {
            secondsSwitch.setTextSize(textSize);
            changeDates.setTextSize(textSize);
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
        });

    }

    /*
     * Sets the Texts of anything onScreen to their Values.
     */
    public void setText() {
        runOnUiThread(() -> {
            secondsDone.setText(xString);
            secondsLeft.setText(yString);
            percent.setText(zString);

            if(multiper == 60) {
                secondsSwitch.setText(getString(R.string.minutes));
            }
            if(multiper == 3600) {
                secondsSwitch.setText(getString(R.string.hours));
            }
            if(multiper == 86400) {
                secondsSwitch.setText(getString(R.string.days));
            }
            if(multiper == 1) {
                secondsSwitch.setText(getString(R.string.seconds));
            }
            darkmodeBeginText1.setText(getString(R.string.begin_dark_mode));
            darkmodeBeginText2.setText(getString(R.string.o_clock));
            darkmodeEndText1.setText(getString(R.string.begin_bright_mode));
            darkmodeEndText2.setText(getString(R.string.o_clock));
            if(darkMode == 0) {
                darkmodeSwitch.setText(getString(R.string.darkmode_off));
            }
            if(darkMode == 1) {
                darkmodeSwitch.setText(getString(R.string.darkmode_on));
            }
            if(darkMode == 2) {
                darkmodeSwitch.setText(getString(R.string.darkmode_auto));
            }
        });

    }

    /*
     * Sets the Boundaries of the things on screen.
     * Depends from the width of the screen and the textSize.
     */
    public void setPaddingSizes(int width, int textSize) {
        runOnUiThread(() -> {
            measure();
            int horizontal = width / 27;
            int vertical = horizontal / 2;
            int buttonWidth = textSize * 27 + horizontal * 2;
            int buttonHeight = textSize * 12 + vertical * 2;
            secondsSwitch.setWidth(buttonWidth);
            changeDates.setWidth(buttonWidth);
            darkmodeSwitch.setWidth(buttonWidth);
            darkmodeSettings.setWidth(buttonWidth);
            secondsSwitch.setHeight(buttonHeight);
            changeDates.setHeight(buttonHeight);
            darkmodeSwitch.setHeight(buttonHeight);
            darkmodeSettings.setHeight(buttonHeight);
            secondsLeft.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            secondsDone.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            percent.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            darkmodeBeginText1.setPaddingRelative(horizontal, vertical, 0, vertical);
            darkmodeBeginText2.setPaddingRelative(0, vertical, horizontal, vertical);
            darkmodeEndText1.setPaddingRelative(horizontal, vertical, 0, vertical);
            darkmodeEndText2.setPaddingRelative(0, vertical, horizontal, vertical);
            darkmodeBegin.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            darkmodeEnd.setPaddingRelative(horizontal, vertical, horizontal, vertical);
        });

    }

    /*
     * Sets everything to where it should be on screen.
     * mode: weather the Elements should be in the normal place or lower.
     *  0 = normal
     *  1 = low
     */
    public void setPositions(int height, int width, int mode) {
        runOnUiThread(() -> {
            measure();
            secondsSwitch.setX((float) (width / 2.0 - secondsSwitch.getMeasuredWidth() / 2.0 - width / 4.0));
            darkmodeSwitch.setX((float) (width / 2.0 - darkmodeSwitch.getMeasuredWidth() / 2.0 + width / 4.0));
            secondsDone.setX((float) ((float) (width / 2.0) - secondsDone.getMeasuredWidth() / 2.0));
            secondsLeft.setX((float) (width / 2.0 - secondsLeft.getMeasuredWidth() / 2.0));
            percent.setX((float) (width / 2.0 - percent.getMeasuredWidth() / 2.0));
            darkmodeBeginText1.setX((float) (width / 2.0 - (darkmodeBeginText1.getMeasuredWidth() + darkmodeBegin.getMeasuredWidth() + darkmodeBeginText2.getMeasuredWidth()) / 2.0));
            darkmodeBegin.setX(darkmodeBeginText1.getX() + darkmodeBeginText1.getMeasuredWidth());
            darkmodeBeginText2.setX(darkmodeBegin.getX() + darkmodeBegin.getMeasuredWidth());
            darkmodeEndText1.setX((float) (width / 2.0 - (darkmodeEndText1.getMeasuredWidth() + darkmodeEnd.getMeasuredWidth() + darkmodeEndText2.getMeasuredWidth()) / 2.0));
            darkmodeEnd.setX(darkmodeEndText1.getX() + darkmodeEndText1.getMeasuredWidth());
            darkmodeEndText2.setX(darkmodeEnd.getX() + darkmodeEnd.getMeasuredWidth());
            if(mode == 0) darkmodeSettings.setX(darkmodeSwitch.getX());
            if(mode == 1) darkmodeSettings.setX((float) (width / 2.0 - darkmodeSettings.getMeasuredWidth() / 2.0));
            if(darkmodeSwitch.getText().toString().equals(getString(R.string.darkmode_auto)))
                changeDates.setX(secondsSwitch.getX());
            else
                changeDates.setX((float) (width / 2.0 - changeDates.getMeasuredWidth() / 2.0));

            darkmodeBeginText1.setY((float) (height/10.0));
            if(mode == 0) {
                secondsDone.setY((float) (height / 2.0 - secondsDone.getMeasuredHeight() / 2.0 - height / 4.0));
                secondsLeft.setY((float) (height / 2.0 - secondsLeft.getMeasuredHeight() / 2.0 - height / 4.0 + textSize * 10));
                percent.setY((float) (height / 2.0 - percent.getMeasuredHeight() / 2.0 - height / 4.0 + textSize * 20));
                darkmodeSettings.setY(percent.getY() + textSize * 60);
                changeDates.setY(percent.getY() + textSize * 60);
                changeDates.setVisibility(View.VISIBLE);
            }
            if(mode == 1) {
                secondsDone.setY((float) (height / 2.0 - secondsDone.getMeasuredHeight() / 2.0 - textSize * 5));
                secondsLeft.setY((float) (height / 2.0 - secondsLeft.getMeasuredHeight() / 2.0 + textSize * 5));
                percent.setY((float) (height / 2.0 - percent.getMeasuredHeight() / 2.0 + textSize * 15));
                darkmodeSettings.setY(darkmodeEndText1.getY() + textSize * 15);
                changeDates.setVisibility(View.INVISIBLE);
            }
            darkmodeEndText1.setY(darkmodeBeginText1.getY() + textSize * 10);
            secondsSwitch.setY(percent.getY() + textSize * 40);
            darkmodeSwitch.setY(secondsSwitch.getY());
            darkmodeBegin.setY(darkmodeBeginText1.getY());
            darkmodeEnd.setY(darkmodeEndText1.getY());
            darkmodeBeginText2.setY(darkmodeBeginText1.getY());
            darkmodeEndText2.setY(darkmodeEndText1.getY());
        });
    }

    /*
     * Updates the colorscheme and makes everything look good.
     */
    public void setColors() {
        runOnUiThread(() -> {
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
            changeDates.setTextColor(Color.parseColor(textcolor));
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
            changeDates.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeSettings.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeBegin.setBackgroundColor(Color.parseColor(buttoncolor));
            darkmodeEnd.setBackgroundColor(Color.parseColor(buttoncolor));
        });
    }

    /*
     * Updates the UI with new Texts, new Textsizes, new Positions and new Colorscheme.
     */
    public void updateUI2(int mode) {
        runOnUiThread(() -> {
            measure();
            textSize = width / 90;
            setTextSizes(textSize);
            setText();
            setPaddingSizes(width, textSize);
            setPositions(height, width, mode);
            setColors();
            changeDates.setText(getString(R.string.change_data_button));
        });
    }

    /*
     * Initialises the UI by
     *  setting the Typeface to Monospace
     *  clearing the Texts
     *  setting the Textalignments
     *  setting the Textsizes
     *  setting the Positions
     *  setting the Colors
     */
    public void initialiseUI() {
        runOnUiThread(() -> {
            secondsDone.setTypeface(Typeface.MONOSPACE);
            secondsLeft.setTypeface(Typeface.MONOSPACE);
            percent.setTypeface(Typeface.MONOSPACE);
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
            secondsDone.setText("");
            secondsLeft.setText("");
            percent.setText("");
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
            measure();
            darkmodeBegin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            darkmodeEnd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            darkmodeBegin.setInputType(InputType.TYPE_CLASS_NUMBER);
            darkmodeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
            measure();
            textSize = width / 90;
            setTextSizes(textSize);
            setText();
            setPaddingSizes(width, textSize);
            setPositions(height, width, 0);
            setColors();
        });
    }

    /*
     * Defines the Listeners for all Buttons and EditTexts.
     */
    @SuppressLint("SetTextI18n")
    public void initialiseListeners() {
        runOnUiThread(() -> {
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
             * When the Button gets pressed the timer gets paused and the UI changes to the PickDate UI.
             */
            changeDates.setOnClickListener(view -> {
                timerBool = false;
               pickDate();
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
                    backgroundcolor = "#2E2E2E";
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
                        backgroundcolor = "#99B9F9"; //#B7C8EA
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
                update();
                save(darkMode, "Darkmode");
            });

            /*
             * If the Button is clicked the Settings are activated if they are off and
             *  deactivated if they are on.
             * The Settings are validated and saved. The Keyboard gets closed.
             */
            darkmodeSettings.setOnClickListener(view -> {
                if (!darkmodeSettings.getText().equals(getString(R.string.done))) {
                    updateUI2(1);
                    darkmodeBegin.setVisibility(View.VISIBLE);
                    darkmodeEnd.setVisibility(View.VISIBLE);
                    darkmodeBeginText1.setVisibility(View.VISIBLE);
                    darkmodeBeginText2.setVisibility(View.VISIBLE);
                    darkmodeEndText1.setVisibility(View.VISIBLE);
                    darkmodeEndText2.setVisibility(View.VISIBLE);
                    darkmodeSettings.setText(getString(R.string.done));
                } else {
                    updateUI2(0);
                    darkmodeBegin.setVisibility(View.INVISIBLE);
                    darkmodeEnd.setVisibility(View.INVISIBLE);
                    darkmodeBeginText1.setVisibility(View.INVISIBLE);
                    darkmodeBeginText2.setVisibility(View.INVISIBLE);
                    darkmodeEndText1.setVisibility(View.INVISIBLE);
                    darkmodeEndText2.setVisibility(View.INVISIBLE);
                    darkmodeSettings.setText(getString(R.string.setup));
                }
                hideSoftKeyboard(findViewById(R.id.Layout1));
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
                    } else {
                        secondsSwitch.setText(getString(R.string.seconds));
                        multiper = 1;
                    }
                }
                update();
                save(multiper, "timeMode");
            });
        });
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
     * Converts a String of the Form "yyyy-MM-dd HH:mm:ss.mmm" into a easily readable Date like:
     *  "12 JAN 2022 12:30" for input "2022-01-12 12:30:00.000"
     */
    private String createReadableDate(String date) {
        return createReadableDate(parseDate(date));
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
     * Creates a new UI, where the User can change the Dates.
     */
    private void pickDate() {
        Context context = this;
        runOnUiThread(() -> {
            /*
             * Make the other UI invisible and create the new Items.
             */
            secondsSwitch.setVisibility(View.INVISIBLE);
            secondsDone.setVisibility(View.INVISIBLE);
            percent.setVisibility(View.INVISIBLE);
            secondsLeft.setVisibility(View.INVISIBLE);
            darkmodeSettings.setVisibility(View.INVISIBLE);
            darkmodeSwitch.setVisibility(View.INVISIBLE);
            changeDates.setVisibility(View.INVISIBLE);
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
            showStartDatePicker.setText(createReadableDate(startDate));
            done.setText(getString(R.string.done));
            showEndDatePicker.setText(createReadableDate(endDate));

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
            int buttonWidth = textSize * 27 + horizontal * 2;
            int buttonHeight = textSize * 12 + vertical * 2;
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
            lbstartDate.setY((float) (height / 2.0 - height/4.0 - textSize * 10));
            showStartDatePicker.setY((float) (height / 2.0 - height / 4.0));
            done.setY((float) (height/2.0 + height / 4.0));
            lbendDate.setY((float) (height / 2.0 - textSize * 10));
            showEndDatePicker.setY((float) (height / 2.0));
            ConstraintLayout lyout = findViewById(R.id.Layout1);
            lyout.addView(lbstartDate);
            lyout.addView(lbendDate);
            lyout.addView(showStartDatePicker);
            lyout.addView(done);
            lyout.addView(showEndDatePicker);

            /*
             * Initialises the Datapickers and defines the Button onClickListeners
             */
            int[] a = parseDate(startDate);
            int[] c = parseDate(endDate);
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
                        save(startDate, "Start");
                        endDate = dateParseString(d);
                        save(endDate, "Ende");
                    }
                if(changedStart && !changedEnd)
                    if(checkDates(dateParseString(b), endDate)) {
                        startDate = dateParseString(b);
                        save(startDate, "Start");
                    }
                if(!changedStart && changedEnd)
                    if(checkDates(startDate, dateParseString(d))) {
                        endDate = dateParseString(d);
                        save(endDate, "Ende");
                    }

                secondsSwitch.setVisibility(View.VISIBLE);
                secondsDone.setVisibility(View.VISIBLE);
                percent.setVisibility(View.VISIBLE);
                secondsLeft.setVisibility(View.VISIBLE);
                darkmodeSwitch.setVisibility(View.VISIBLE);
                changeDates.setVisibility(View.VISIBLE);
                if(darkmodeSwitch.getText().equals(getString(R.string.darkmode_auto)))
                    darkmodeSettings.setVisibility(View.VISIBLE);

                timerBool = true;
            });
        });
    }

    /*
     * Sends notifications and/or Toasts to the user if one of the Milestones are reached:
     *  the percentage increased by 1: toast
     *  one third is done:             toast and notification
     *  the half is done:              toast and notification
     *  two thirds are done:           toast and notification
     *  the percentage is at 90:       toast and notification
     *  a month has passed:            toast and notification
     */
    public void sendMileStoneNotifications() {
        runOnUiThread(() -> {
            /*
             * If the Percentage rises by one percent while the App is running, a Toast is shown to the user
             *  with the message set in milestone_percent(default: Noch ein Prozent geschafft! Ich liebe dich!)
             */
            if ((percentage < Math.floor(z)) && (ran)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_percent), Toast.LENGTH_LONG);
                toast.show();
            }

            /*
             * If the Percentage is roughly one third while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_third_toast(default: Ein Drittel ist schon geschafft! Wie cool!)
             *  and in milestone_third_notification(default: Ein Drittel ist schon geschafft!).
             */
            if((z>33.334)&&(z<33.34)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_third_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_third_notification));
            }

            /*
             * If the Percentage is roughly a half while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_half_toast(default: Die Hälfte ist schon geschafft! Wie cool!)
             *  and milestone_half_notification(default: Die Hälfte ist schon geschafft!).
             */
            if((z>50)&&(z<50.01)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_half_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_half_notification));
            }

            /*
             * If the Percentage is roughly two thirds while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_two_third_toast(default: Zwei Drittel sind schon geschafft! Wie cool!)
             *  and milestone_two_third_notification(default: Zwei Drittel sind schon geschafft!).
             */
            if((z>66.667)&&(z<66.67)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_two_third_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_two_third_notification));
            }

            /*
             * If the Percentage is roughly at 90 while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_almost_done_toast(default: Fast alles geschafft! Bis bald :))
             *  and milestone_almost_done_notification(default: Fast alles geschafft! Bis bald :)).
             */
            if((z>89.99)&&(z<90.01)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_almost_done_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_almost_done_notification));
            }

            /*
             * If the Day and Time are the same as in the start date while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_month_toast(default: Noch einen Monat geschafft! Ich bin stolz auf dich!)
             *  and milestone_month_notification(default: Noch einen Monat geschafft! Ich bin stolz auf dich!).
             */
            if((now.format(DateTimeFormatter.ofPattern("dd")).equals(startDate.substring(8, 10)))
                    &&((now.format(DateTimeFormatter.ofPattern("HH:mm:ss")).equals(startDate.substring(11, 19))))) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_month_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_month_notification));
            }
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

    /*
     * timestamp is used to convert a Date and Time combo to the UNIX Milliseconds.
     * This is used in the calculations for the seconds passed and the seconds to pass
     */
    public long timestamp(String arg) throws ParseException {
        return Objects.requireNonNull(df.parse(arg)).getTime();
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

    /*
     * Puts a prefix to a String depending on what time showing mode the App is.
     */
    public String setString(int multiper, String message) {
        if(multiper == 1) {
            return getString(R.string.seconds) + message;
        }
        if(multiper == 60) {
            return getString(R.string.minutes) + message;
        }
        if(multiper == 3600) {
            return getString(R.string.hours) + message;
        }
        return getString(R.string.days) + message;
    }

    /*
     * Does the Math to set x, y and z and their corresponding Strings.
     */
    public void setXYZ() {
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        nowWithoutZone = LocalDateTime.now();
        x = calcMilSeconds(now.format(dr), startDate);
        y = calcMilSeconds(now.format(dr), endDate);
        completeTime = calcMilSeconds(startDate, endDate);

        y = y / 1000;
        z = (double) x / (double) (completeTime);
        z = z * 100;
        x = x / 1000;
        x = x / multiper;
        y = y / multiper;
        xString = setString(multiper, getString(R.string.since_seen) + " " + x);
        yString = setString(multiper, getString(R.string.until_seeing)+ " " + y);
        zString = dform.format(z) + " " + getString(R.string.percent_done);
    }

    /*
     * Updates the App, this includes updating the UI, sending out the milestone notifications and
     *  updates the Progressbar notification.
     */
    @SuppressLint("SetTextI18n")
    public void update() {
        runOnUiThread(() -> {
            setXYZ();

            sendMileStoneNotifications();

            percentage = Math.floor(z);

            ran = true;

            if(percentage >= 100) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_done_toast), Toast.LENGTH_LONG);
                toast.show();
                tm1.cancel();
                secondsDone.setText(getString(R.string.seconds) + getString(R.string.since_seen) + " " + completeTime);
                secondsLeft.setText(getString(R.string.seconds) + getString(R.string.until_seeing) + " 0");
                percent.setText("100" + " " + getString(R.string.percent_done));
            }
            if(darkmodeSwitch.getText().equals(getString(R.string.darkmode_auto))) {
                if(darkmodeSettings.getText().equals(getString(R.string.setup)))
                    updateUI2(0);
                if(darkmodeSettings.getText().equals(getString(R.string.done)))
                    updateUI2(1);
            } else {
                updateUI2(0);
                runOnUiThread(() -> darkmodeSettings.setText(getString(R.string.setup)));
            }
            runOnUiThread(() -> {
                if(darkmodeSwitch.getText().toString().equals(getString(R.string.darkmode_auto)))
                    darkmodeSettings.setVisibility(View.VISIBLE);
                if(darkmodeSettings.getText().equals(getString(R.string.setup))) {
                    darkmodeBegin.setText(Integer.toString(begin));
                    darkmodeEnd.setText(Integer.toString(end));
                }
            });
            PROGRESS_CURRENT = (int) x * multiper;
            PROGRESS_MAX = (int) (completeTime / 1000);
            sendNotification(1, y + " " +secondsSwitch.getText().toString(),
                    getString(R.string.still_there) + " " + z + " " + getString(R.string.percent_done));
        });
    }

    /*
     * onCreate is the Method called on the start of the App.
     * It initialises the UI, loads in all the Variables and initialises the timerTask.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.Layout1);
        ConstraintLayout homeScreenLayout = (ConstraintLayout) layout;

        runOnUiThread(() -> {
            initialise();

            loadVariables();

            tmTk1 = new TimerTask() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if(timerBool)
                        update();
                    if(!timerBool) {
                        secondsSwitch.setVisibility(View.INVISIBLE);
                        secondsDone.setVisibility(View.INVISIBLE);
                        percent.setVisibility(View.INVISIBLE);
                        secondsLeft.setVisibility(View.INVISIBLE);
                        darkmodeSettings.setVisibility(View.INVISIBLE);
                        darkmodeSwitch.setVisibility(View.INVISIBLE);
                        changeDates.setVisibility(View.INVISIBLE);
                    }
                }
            };

            /*
             * The display modules are put on the Screen, even though some of them are invisible.
             * The Timer gets started and the Timertask gets executed roughly every 100 Milliseconds.
             * Only here the TimerTask tm1 gets executed the first time.
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
            homeScreenLayout.addView(changeDates);
            homeScreenLayout.addView(darkmodeSwitch);
            homeScreenLayout.addView(darkmodeSettings);
            tm1.schedule(tmTk1, 0, 400);
        });
    }
}