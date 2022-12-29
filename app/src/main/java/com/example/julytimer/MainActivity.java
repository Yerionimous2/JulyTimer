package com.example.julytimer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
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
    private ImageView backgroundImage;
    TimerTask tmTk1;
    private TextView secondsLeft;
    private TextView secondsLeft2;
    public TextView secondsDone;
    public TextView secondsDone2;
    private TextView percent;
    private TextView percent2;
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
    private int standardChannel;
    public int buttonWidth;
    private boolean ran, ran2;
    private boolean timerBool = true;
    private Button settings;

    private String darkmodeButtoncolor;
    private String darkmodeTextcolor;
    private String darkmodeBackgroundcolor;
    private String brightmodeButtoncolor;
    private String brightmodeTextcolor;
    private String brightmodeBackgroundcolor;

    private String textcolor = "#000000";
    private String buttoncolor = "#000000";
    public String xString, yString, zString;
    public String xString2, yString2, zString2;
    public String startDate = "2022-08-24 09:30:00.000";
    public String endDate = "2023-07-23 21:40:00.000";

    private String[] log;
    Drawable myIcon;
    private long completeTime;
    private long x;
    private double z;
    private double zbefore;
    private double percentage;
    private double percentageLastOpened;
    private long startDateUNIX;
    private long endDateUNIX;
    private boolean settingsOpen;
    private String backgroundcolor;
    SharedPreferences sharedPref;
    public Bitmap BackgroundImageBitmap;

    /*
     * save(int) saves the given int for later use under the given name.
     */
    public void save(int a, String name) {
        editor.putInt(name, a);
        editor.apply();
    }

    private byte[] loadByteArray(String name) {
        String string = sharedPref.getString(name, null);
        if(string == null) {return null;
        }
        return Base64.getDecoder().decode(string);
    }

    /*
     * save(String) saves the given String for later use under the given name.
     */
    public void save(String a, String name) {
        editor.putString(name, a);
        editor.apply();
        log("Saved String " + a + " as " + "\"" + name + "\".");
    }

    public void save(double a, String name) {
        editor.putLong(name, Double.doubleToRawLongBits(a));
    }



    /*
     * initialise initialises the UI and loads the saved Values in the Variables
     * uses initialiseNotifications and initialiseUI.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void initialise() {
        Context a = this;
        runOnUiThread(() -> {
            secondsDone = new TextView(a);
            secondsDone2 = new TextView(a);
            secondsLeft = new TextView(a);
            secondsLeft2 = new TextView(a);
            percent = new TextView(a);
            percent2 = new TextView(a);
            settings = new Button(a);
            backgroundImage = new ImageView(a);
            backgroundImage.setBackground(getDrawable(R.drawable.normalbackground));
            backgroundImage.setImageBitmap(BackgroundImageBitmap);
            backgroundImage.setAlpha(0.23F);
            backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ran = false;
            ran2 = false;
            standardChannel = 1;
            PROGRESS_MAX = 28814999;                                // Failsafe for Notification
            dform = new DecimalFormat("#.######");

            log("Initialising Notifications.");
            initialiseNotifications();
            log("Initialising the UI.");
            initialiseUI();
            log("Starting Services.");
            startService(new Intent(getBaseContext(), KillService.class));
            measure();
            log("Initialising the Listeners.");
            initialiseListeners();
        });

    }

    /*
     * Makes the Notification open the App.
     */
    public void onPause() {
        log("The App has been closed but not terminated.");
        percentageLastOpened = percentage;

        Intent startIntent = new Intent(this, this.getClass());
        startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                resultIntent, 0);
        builder.setContentIntent(pendingIntent);
        super.onPause();
    }

    public void onResume() {
        log("The App has been opened again.");
        resetNotification();
        settingsOpen = false;
        if(percentage > percentageLastOpened) {
            Toast toast = Toast.makeText(getApplicationContext(), createMissedPercentString(percentage, (int) percentageLastOpened), Toast.LENGTH_LONG);
            toast.show();
        }
        super.onResume();
    }

    /*
     * re-initialises all the Notification Channels, Managers and Builders so that they operate exactly as a completely new one.
     */
    private void resetNotification() {
        removeNotification();
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
    @SuppressLint("UseCompatLoadingForDrawables")
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
        editor = sharedPref.edit();

        int beginSave = begin;
        int endSave   = end;
        int multiperSave = multiper;
        int darkModeSave = darkMode;
        String startDateSave = startDate;
        String endDateSave = endDate;

        byte[] BackgroundImageByteArray = loadByteArray("BackgroundImage");
        if(BackgroundImageByteArray != null) {
            BackgroundImageBitmap = BitmapFactory.decodeByteArray(BackgroundImageByteArray, 0, BackgroundImageByteArray.length);
        }

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
        myIcon = getResources().getDrawable(R.drawable.settings_icon);
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
                        .setSmallIcon(R.drawable.herz)
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                notificationManager.notify(1, builder.build());
            } else
            if(channel == 2) {
                builder2.setContentText(message)
                        .setContentTitle(title)
                        .setSmallIcon(R.drawable.herz)
                        .setStyle(new NotificationCompat.BigTextStyle());
                notificationManager2.notify(2, builder2.build());
            } else
            if(channel == 3) {
                builder.setContentText(message)
                        .setContentTitle(title)
                        .setSmallIcon(R.drawable.herz)
                        .setStyle(new NotificationCompat.BigTextStyle())
                        .setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                notificationManager.notify(1, builder.build());
            }
        });
    }

    /*
     * Removes the Notification from the Notification center.
     */
    @SuppressLint("SuspiciousIndentation")
    public void removeNotification() {
        if(notificationManager != null) {
            notificationManager.cancel(standardChannel);
        } else {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("35", "channel", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(getString(R.string.notification_name_1));
            notificationManager.createNotificationChannel(channel);
            notificationManager.cancel(standardChannel);
        }
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

    /*
     * Measures everything on Screen.
     */
    public void measure() {
        height = getScreenHeight(this);
        width = getScreenWidth(this);

        runOnUiThread(() -> {
            secondsLeft.measure(0, 0);
            secondsLeft2.measure(0, 0);
            secondsDone.measure(0, 0);
            secondsDone2.measure(0, 0);
            percent.measure(0, 0);
            percent2.measure(0, 0);
            settings.measure(0, 0);
        });

    }

    /*
     * Sets the Textsizes of anything containing Text to the given textSize.
     */
    public void setTextSizes(int textSize) {
        runOnUiThread(() -> {
            secondsLeft.setTextSize(textSize);
            secondsLeft2.setTextSize((float) (textSize * 1.5));
            secondsDone.setTextSize(textSize);
            secondsDone2.setTextSize((float) (textSize * 1.5));
            percent.setTextSize(textSize);
            percent2.setTextSize((float) (textSize * 1.5));
            settings.setTextSize(textSize);
        });

    }

    /*
     * Gets called when the App gets closed.
     */
    public void onDestroy() {
        log("The App has been terminated.");
        super.onDestroy();
        removeNotification();
    }

    /*
     * Sets the Texts of anything onScreen to their Values.
     */
    public void setText() {
        runOnUiThread(() -> {
            if(xString != null)  if(secondsDone  != null) secondsDone.setText(xString);
            if(xString2 != null) if(secondsDone2 != null) secondsDone2.setText(xString2);
            if(yString != null)  if(secondsLeft  != null) secondsLeft.setText(yString);
            if(yString2 != null) if(secondsLeft2 != null) secondsLeft2.setText(yString2);
            if(zString != null)  if(percent      != null) percent.setText(zString);
            StringBuilder sb = new StringBuilder();
            if(zString2 != null) {
                sb.append(zString2);
                if(zString2.length() <= 2) sb.append(",");
                for(int r= zString2.length(); r < 9; r++) {
                    sb.append("0");
                }
                zString2 = sb.toString();
                if(percent2 != null) percent2.setText(zString2);
            }
        });

    }

    /*
     * Sets the Boundaries of the things on screen.
     * Depends from the width of the screen and the textSize.
     */
    public void setPaddingSizes(int width) {
        runOnUiThread(() -> {
            measure();
            int horizontal = width / 27;
            int vertical = horizontal / 2;
            buttonWidth = width / 10;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    buttonWidth, // width in dp
                    buttonWidth // height in dp
            );
            settings.setLayoutParams(layoutParams);

            settings.setHeight(buttonWidth);
            settings.setWidth(buttonWidth);
            secondsLeft.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            secondsDone.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            percent.setPaddingRelative(horizontal, vertical, horizontal, vertical);
            secondsLeft2.setPaddingRelative(0, vertical, 0, vertical);
            secondsDone2.setPaddingRelative(0, vertical, 0, vertical);
            percent2.setPaddingRelative(0, vertical, 0, vertical);
            measure();
            secondsLeft2.setPaddingRelative((secondsLeft.getMeasuredWidth()-secondsLeft2.getMeasuredWidth()) / 2, vertical,
                    (secondsLeft.getMeasuredWidth()-secondsLeft2.getMeasuredWidth()) / 2, vertical);
            secondsDone2.setPaddingRelative((secondsDone.getMeasuredWidth()-secondsDone2.getMeasuredWidth()) / 2, vertical,
                    (secondsDone.getMeasuredWidth()-secondsDone2.getMeasuredWidth()) / 2, vertical);
            percent2.setPaddingRelative((percent.getMeasuredWidth()-percent2.getMeasuredWidth()) / 2, vertical,
                    (percent.getMeasuredWidth()-percent2.getMeasuredWidth()) / 2, vertical);
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
            // <----------------- [X Values] -----------------> \\
            secondsDone.setX((float) ((float) (width / 2.0) - secondsDone.getMeasuredWidth() / 2.0));
            secondsDone2.setX((float) ((float) (width / 2.0) - secondsDone2.getMeasuredWidth() / 2.0));
            secondsLeft.setX((float) (width / 2.0 - secondsLeft.getMeasuredWidth() / 2.0));
            secondsLeft2.setX((float) (width / 2.0 - secondsLeft2.getMeasuredWidth() / 2.0));
            percent.setX((float) (width / 2.0 - percent.getMeasuredWidth() / 2.0));
            percent2.setX((float) (width / 2.0 - percent2.getMeasuredWidth() / 2.0));
            settings.setX((float) (width - buttonWidth - 20));

            // <----------------- [Y Values] -----------------> \\

            if(mode == 0) {
                secondsDone.setY((float) (height / 2.0 - secondsDone.getMeasuredHeight() / 2.0 - height / 3.5));
            }
            if(mode == 1) {
                secondsDone.setY((float) (secondsDone.getMeasuredHeight() * 1.5));
            }
            secondsDone2.setY(secondsDone.getY() + secondsDone.getMeasuredHeight() - 1);
            secondsLeft.setY((float) (secondsDone2.getY() + secondsDone2.getMeasuredHeight() + secondsLeft.getMeasuredHeight() / 3.0));
            secondsLeft2.setY(secondsLeft.getY() + secondsLeft.getMeasuredHeight() - 1);
            percent.setY((float) (secondsLeft2.getY() + secondsLeft2.getMeasuredHeight() + percent.getMeasuredHeight() / 3.0));
            percent2.setY(percent.getY() + percent.getMeasuredHeight() - 1);
            settings.setY(20);
        });
    }

    /*
     * Updates the colorscheme and makes everything look good.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setColors() {
        runOnUiThread(() -> {
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
            secondsDone.setTextColor(Color.parseColor(textcolor));
            secondsDone2.setTextColor(Color.parseColor(textcolor));
            secondsLeft.setTextColor(Color.parseColor(textcolor));
            secondsLeft2.setTextColor(Color.parseColor(textcolor));
            percent.setTextColor(Color.parseColor(textcolor));
            percent2.setTextColor(Color.parseColor(textcolor));
            settings.setTextColor(Color.parseColor(textcolor));
            secondsDone.setBackgroundColor(Color.parseColor(buttoncolor));
            secondsDone2.setBackgroundColor(Color.parseColor(buttoncolor));
            secondsLeft.setBackgroundColor(Color.parseColor(buttoncolor));
            secondsLeft2.setBackgroundColor(Color.parseColor(buttoncolor));
            percent.setBackgroundColor(Color.parseColor(buttoncolor));
            percent2.setBackgroundColor(Color.parseColor(buttoncolor));
            if(myIcon != null) myIcon.setColorFilter(Color.parseColor(textcolor), PorterDuff.Mode.SRC_IN);
            settings.setBackground(myIcon);
        });
    }

    /*
     * Updates the UI with new Texts, new Textsizes, new Positions and new Colorscheme.
     */
    public void updateUI(int mode) {
        runOnUiThread(() -> {
            measure();
            if(!ran2) {
                xString = setString(1, getString(R.string.since_seen));
                setText();
                textSize = measureMaxTextsize();
                xString = setString(multiper, getString(R.string.since_seen));
            }
            setText();
            setTextSizes(textSize);
            setPaddingSizes(width);
            setPositions(height, width, mode);
            setColors();
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
            secondsDone2.setTypeface(Typeface.MONOSPACE);
            secondsLeft.setTypeface(Typeface.MONOSPACE);
            secondsLeft2.setTypeface(Typeface.MONOSPACE);
            percent.setTypeface(Typeface.MONOSPACE);
            percent2.setTypeface(Typeface.MONOSPACE);
            settings.setTypeface(Typeface.MONOSPACE);
            secondsLeft.setText("");
            percent.setText("");
            measure();
            textSize = measureMaxTextsize();
            setTextSizes(textSize);
            setText();
            setPaddingSizes(width);
            setPositions(height, width, 0);
            if(ran) setColors();
        });
    }

    /*
     * Measures the maximum TextSize possible on this Device.
     */
    public int measureMaxTextsize() {
        int ts;
        width = getScreenWidth(this);
        setTextSizes(0);
        secondsDone.setText(setString(multiper, getString(R.string.since_seen)));
        secondsDone.measure(0, 0);
        for(ts = 0; (width-200) >= secondsDone.getMeasuredWidth(); ts += 1) {
            setTextSizes(ts);
            secondsDone.measure(0, 0);
        }
        return ts - 3;
    }

    /*
     * Defines the Listeners for all Buttons and EditTexts.
     */
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    public void initialiseListeners() {
        runOnUiThread(() -> settings.setOnClickListener(view -> {
            settingsOpen = true;
            save(textSize, "textSize");
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }));
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
                log("Sent Milestone-Notification for a percentjump.");
                save((int) percentage, "Percent");
            }

            /*
             * If the Percentage is roughly one third while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_third_toast(default: Ein Drittel ist schon geschafft! Wie cool!)
             *  and in milestone_third_notification(default: Ein Drittel ist schon geschafft!).
             */
            if(jumpedTo(100.0/3)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_third_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_third_notification));
                log("Sent Milestone-Notification for one third done.");
            }

            /*
             * If the Percentage is roughly a half while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_half_toast(default: Die Hälfte ist schon geschafft! Wie cool!)
             *  and milestone_half_notification(default: Die Hälfte ist schon geschafft!).
             */
            if(jumpedTo(50)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_half_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_half_notification));
                log("Sent Milestone-Notification for one half done.");
            }

            /*
             * If the Percentage is roughly two thirds while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_two_third_toast(default: Zwei Drittel sind schon geschafft! Wie cool!)
             *  and milestone_two_third_notification(default: Zwei Drittel sind schon geschafft!).
             */
            if(jumpedTo(200.0/3)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_two_third_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_two_third_notification));
                log("Sent Milestone-Notification for two thirds done.");
            }

            /*
             * If the Percentage is roughly at 90 while the App is running, a Toast is shown and a notification is send to the user
             *  with the messages set in milestone_almost_done_toast(default: Fast alles geschafft! Bis bald :))
             *  and milestone_almost_done_notification(default: Fast alles geschafft! Bis bald :)).
             */
            if(jumpedTo(90)) {
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_almost_done_toast), Toast.LENGTH_LONG);
                toast.show();
                sendNotification(2, getString(R.string.milestone_notification_title), getString(R.string.milestone_almost_done_notification));
                log("Sent Milestone-Notification for 90% done.");
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
                log("Sent Milestone-Notification for a Month done.");
            }
        });
    }

    private boolean jumpedTo(double i) {
        return (zbefore < i) && (z >= i);
    }

    /*
     * timestamp is used to convert a Date and Time combo to the UNIX Milliseconds.
     * This is used in the calculations for the seconds passed and the seconds to pass
     */
    public long timestamp(String arg) throws ParseException {
        return Objects.requireNonNull(df.parse(arg)).getTime();
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
        if(multiper == 15000) {
            return getString(R.string.customTime) + message;
        }
        return getString(R.string.days) + message;
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
        zbefore = z;
        z = (double) x / (double) (completeTime);
        z = z * 100;
        x = x / 1000;
        if(multiper != 15000) x = x / multiper;
        if(multiper != 15000) y = y / multiper;
        xString = setString(multiper, getString(R.string.since_seen));
        if(multiper == 15000) {
            xString2 = createCustomTime(x);
        } else xString2 = convertToReadableString(x);
        yString = setString(multiper, getString(R.string.until_seeing));
        if(multiper == 15000) {
            yString2 = createCustomTime(y);
        } else yString2 = convertToReadableString(y);
        zString = getString(R.string.percent_done);
        zString2 = dform.format(z) + "";
        save(z, "PercentZ");
    }

    private String createCustomTime(long x) {
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
        return result;
    }

    private void log(String messageline) {
        System.out.println(messageline);
        log = addLine(log, messageline);
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

    private void printlog(String[] log) {
        if(log != null) {
            for (String s : log) System.out.println(s);
        }
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

    /*
     * Updates the App, this includes updating the UI, sending out the milestone notifications and
     *  updates the Progressbar notification.
     */
    @SuppressLint("SetTextI18n")
    public void update() {
        runOnUiThread(() -> {
            setXYZ();

            updateUI(0);

            if(ran) sendMileStoneNotifications();

            percentage = Math.floor(z);
            if(backgroundImage != null)  backgroundImage.setImageBitmap(BackgroundImageBitmap);
            if(!ran) {
                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                editor = sharedPref.edit();
                if(percentage > sharedPref.getInt("Percent", 0)) {
                    Toast toast = Toast.makeText(getApplicationContext(), createMissedPercentString(percentage, sharedPref.getInt("Percent", 0)), Toast.LENGTH_LONG);
                    toast.show();
                }
            }
            ran = true;
            save((int) percentage, "Percent");

            if(percentage >= 100) {
                timerBool = false;
                Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.milestone_done_toast), Toast.LENGTH_LONG);
                toast.show();
                tm1.cancel();
                secondsDone.setText(getString(R.string.seconds) + getString(R.string.since_seen));
                secondsDone2.setText("" + completeTime/1000);
                secondsLeft.setText(getString(R.string.seconds) + getString(R.string.until_seeing));
                secondsLeft2.setText("0");
                percent.setText(getString(R.string.percent_done));
                percent2.setText("100,000000");
                log("100% are reached.");
            }
            if(multiper != 15000) {
                PROGRESS_CURRENT = (int) x * multiper;
            } else PROGRESS_CURRENT = (int) x;
            PROGRESS_MAX = (int) (completeTime / 1000);

            if(multiper != 15000) {
                sendNotification(standardChannel, yString2 + " " + getTimeString(multiper),
                        getString(R.string.still_there) + " " + zString2+ " " + getString(R.string.percent_done));
            } else {
                sendNotification(standardChannel, yString2,
                        getString(R.string.still_there) + " " + zString2+ " " + getString(R.string.percent_done));
            }
        });
        ran2 = true;
    }

    private String getTimeString(int multiper) {
        if(multiper == 1) {
            return getString(R.string.seconds);
        }
        if(multiper == 60) {
            return getString(R.string.minutes);
        }
        if(multiper == 3600) {
            return getString(R.string.hours);
        }
        return getString(R.string.days);
    }

    /*
     * puts together a String for a missed Percent.
     */
    private String createMissedPercentString(double percentage, int percent) {
        if((percentage - percent) > 1) return getString(R.string.missed_percent_1) + " " + (int)(percentage - percent) + " " + getString(R.string.missed_percent_2_singular);
        return getString(R.string.missed_percent_1) + " " + (int)(percentage - percent) + " " + getString(R.string.missed_percent_2_plural);
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

    /*
     * onCreate is the Method called on the start of the App.
     * It initialises the UI, loads in all the Variables and initialises the timerTask.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences("JulyTimer", Context.MODE_PRIVATE);
        now = LocalDateTime.now(ZoneId.of("Europe/Berlin"));
        log("Initialising...");
        setContentView(R.layout.activity_main);
        layout = findViewById(R.id.Layout1);
        ConstraintLayout homeScreenLayout = (ConstraintLayout) layout;

        runOnUiThread(() -> {
            initialise();
            log("Loading Variables...");
            loadVariables();

            tmTk1 = new TimerTask() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if(settingsOpen) {
                        loadVariables();
                    }
                    if(timerBool)
                        update();
                    if(!timerBool) {
                        secondsDone.setVisibility(View.INVISIBLE);
                        secondsDone2.setVisibility(View.INVISIBLE);
                        percent.setVisibility(View.INVISIBLE);
                        percent2.setVisibility(View.INVISIBLE);
                        settings.setVisibility(View.INVISIBLE);
                        secondsLeft.setVisibility(View.INVISIBLE);
                        secondsLeft2.setVisibility(View.INVISIBLE);
                    }
                }
            };

            /*
             * The display modules are put on the Screen, even though some of them are invisible.
             * The Timer gets started and the Timertask gets executed roughly every 100 Milliseconds.
             * Only here the TimerTask tm1 gets executed the first time.
             */
            homeScreenLayout.addView(backgroundImage);
            homeScreenLayout.addView(secondsDone);
            homeScreenLayout.addView(secondsDone2);
            homeScreenLayout.addView(secondsLeft);
            homeScreenLayout.addView(secondsLeft2);
            homeScreenLayout.addView(percent);
            homeScreenLayout.addView(percent2);
            homeScreenLayout.addView(settings);
            tm1.schedule(tmTk1, 0, 500);
            log("Initialised!");
            System.out.println("InitialiseLog:");
            printlog(log);
        });
    }
}