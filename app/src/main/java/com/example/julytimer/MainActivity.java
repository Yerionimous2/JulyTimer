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
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private TextView secondsLeft;
    public TextView secondsDone;
    private TextView percent;
    private TextView darkmodeBeginText;
    private TextView darkmodeEndText;
    public Button secondsSwitch;
    public Button darkmodeSwitch;
    public EditText darkmodeBegin;
    public EditText darkmodeEnd;
    public int begin;
    public int end;
    public int multiper;
    private boolean ran;
    public int darkMode;
    private String backgroundcolor, textcolor, buttoncolor;
    private double percentage;
    private int PROGRESS_CURRENT = 0;
    private long x;
    public String a;
    private long y;
    private long completeTime;
    private double z;
    @SuppressLint("SimpleDateFormat")
    public final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final DateTimeFormatter dz = DateTimeFormatter.ofPattern("HH");
    private final DisplayMetrics displayMetrics = new DisplayMetrics();



    public long timestamp(String arg) throws ParseException {
        return df.parse(arg).getTime();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ConstraintLayout homeScreenLayout = findViewById(R.id.Layout1);
        secondsDone = new TextView(this);
        secondsLeft = new TextView(this);
        percent = new TextView(this);
        darkmodeBeginText = new TextView(this);
        darkmodeEndText = new TextView(this);
        secondsSwitch = new Button(this);
        darkmodeSwitch = new Button(this);
        darkmodeBegin = new EditText(this);
        darkmodeEnd = new EditText(this);
        backgroundcolor = "#B7C8EA";
        textcolor = "#3A5A9B";
        buttoncolor = "#648AD6";
        ran = false;

        darkmodeBegin.setVisibility(View.INVISIBLE);
        darkmodeEnd.setVisibility(View.INVISIBLE);
        darkmodeBeginText.setVisibility(View.INVISIBLE);
        darkmodeEndText.setVisibility(View.INVISIBLE);

        NotificationChannel channel = new NotificationChannel("35", "channel", NotificationManager.IMPORTANCE_LOW);
        channel.setDescription("Die coole progress-Bar");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "35");
        builder.setContentText("Ghana in progress")
                .setContentTitle("Ghana in progress")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle());
        int PROGRESS_MAX = 28814999;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(1, builder.build());

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        darkMode = sharedPref.getInt("Darkmode", 0);
        begin = sharedPref.getInt("darkmodeBegin", 19);
        end = sharedPref.getInt("darkmodeEnd", 7);
        secondsSwitch.setText(sharedPref.getString("timeMode", "Sekunden"));
        System.out.println(secondsSwitch.getText());
        if(secondsSwitch.getText().equals("Minuten")) {
            multiper = 60;
        }
        if(secondsSwitch.getText().equals("Stunden")) {
            multiper = 3600;
        }
        if(secondsSwitch.getText().equals("Sekunden")){
            multiper = 1;
        }

        if(darkMode == 1) {
            backgroundcolor = "#2E2E2E";
            textcolor = "#C5C5C5";
            buttoncolor = "#464646";
            darkmodeSwitch.setText("Darkmode: an");
        }
        if(darkMode == 2) {
            darkmodeSwitch.setText("Darkmode: auto");
            //TODO: Eingabefelder für Zeiten im Darkmode hinzufügen.
            darkmodeBegin.setText(Integer.toString(begin));
            darkmodeEnd.setText(Integer.toString(end));
            darkmodeBegin.setVisibility(View.VISIBLE);
            darkmodeEnd.setVisibility(View.VISIBLE);
            darkmodeBeginText.setVisibility(View.VISIBLE);
            darkmodeEndText.setVisibility(View.VISIBLE);
        }
        if(darkMode == 0) {
            backgroundcolor = "#99B9F9"; //#B7C8EA
            textcolor = "#3A5A9B"; //#3A5A9B
            buttoncolor = "#B7C8EA"; //#648AD6
            darkmodeSwitch.setText("Darkmode: aus");
        }

        NotificationChannel channel2 = new NotificationChannel("36", "channel2", NotificationManager.IMPORTANCE_HIGH);
        channel2.setDescription("Die Meilensteinbenachrichtigung");
        NotificationManager notificationManager2 = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel2);
        NotificationCompat.Builder builder2 = new NotificationCompat.Builder(this, "36");

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final Timer tm1 = new Timer();

        //Hier wird alles geupdated
        // Stuff that updates the UI

        runOnUiThread(() -> {
            secondsDone.setTypeface(Typeface.MONOSPACE);
            secondsLeft.setTypeface(Typeface.MONOSPACE);
            percent.setTypeface(Typeface.MONOSPACE);
            darkmodeBeginText.setTypeface(Typeface.MONOSPACE);
            darkmodeEndText.setTypeface(Typeface.MONOSPACE);
            secondsSwitch.setTypeface(Typeface.MONOSPACE);
            darkmodeSwitch.setTypeface(Typeface.MONOSPACE);
            darkmodeBegin.setTypeface(Typeface.MONOSPACE);
            darkmodeEnd.setTypeface(Typeface.MONOSPACE);
            secondsDone.setTextSize(12);
            secondsLeft.setTextSize(12);
            percent.setTextSize(12);
            darkmodeBeginText.setTextSize(12);
            darkmodeEndText.setTextSize(12);
            secondsSwitch.setTextSize(12);
            darkmodeSwitch.setTextSize(12);
            darkmodeBegin.setTextSize(12);
            darkmodeEnd.setTextSize(12);
            homeScreenLayout.addView(darkmodeBeginText);
            homeScreenLayout.addView(darkmodeEndText);
            homeScreenLayout.addView(darkmodeBegin);
            homeScreenLayout.addView(darkmodeEnd);
            homeScreenLayout.addView(secondsSwitch);
            secondsSwitch.setWidth(400);
            secondsSwitch.setHeight(175);
            homeScreenLayout.addView(darkmodeSwitch);
            darkmodeSwitch.setWidth(400);
            darkmodeSwitch.setHeight(175);
            darkmodeBegin.setWidth(130);
            //darkmodeBegin.setHeight(110);
            darkmodeEnd.setWidth(130);
            darkmodeEnd.setHeight(110);
            darkmodeBegin.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            darkmodeEnd.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            darkmodeBegin.setInputType(InputType.TYPE_CLASS_NUMBER);
            darkmodeEnd.setInputType(InputType.TYPE_CLASS_NUMBER);
            darkmodeBeginText.setText("Beginn des dunklen Modus:          Uhr");
            darkmodeEndText.setText("Beginn des hellen Modus:          Uhr");

            darkmodeBegin.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        a = darkmodeBegin.getText().toString();
                        int aNum = Integer.parseInt(a);
                        if((aNum > 24)||((aNum < 0))||(aNum < end)) {
                            darkmodeBegin.setText(Integer.toString(begin));
                        } else {
                            begin = aNum;
                            darkmodeBegin.setText(Integer.toString(begin));
                            editor.putInt("darkmodeBegin", begin);
                            editor.apply();
                        }
                    }
                }
            });

            darkmodeEnd.setOnFocusChangeListener(new EditText.OnFocusChangeListener() {

                public void onFocusChange(View v, boolean hasFocus) {
                    if(!hasFocus) {
                        a = darkmodeEnd.getText().toString();
                        int aNum = Integer.parseInt(a);
                        if((aNum > 24)||((aNum < 0))||(aNum > begin)) {
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

        });
        darkmodeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                darkmodeBegin.clearFocus();
                darkmodeEnd.clearFocus();
                if(darkMode == 0) {
                    darkMode = 1;
                    backgroundcolor = "#2E2E2E";
                    textcolor = "#C5C5C5";
                    buttoncolor = "#464646";
                    darkmodeSwitch.setText("Darkmode: an");
                } else {
                    if(darkMode == 1) {
                        darkMode = 2;
                        darkmodeSwitch.setText("Darkmode: auto");
                        darkmodeBegin.setText(Integer.toString(begin));
                        darkmodeEnd.setText(Integer.toString(end));
                        darkmodeBegin.setVisibility(view.VISIBLE);
                        darkmodeEnd.setVisibility(view.VISIBLE);
                        darkmodeBeginText.setVisibility(View.VISIBLE);
                        darkmodeEndText.setVisibility(View.VISIBLE);
                    } else {
                        darkMode = 0;
                        backgroundcolor = "#99B9F9"; //#B7C8EA
                        textcolor = "#3A5A9B"; //#3A5A9B
                        buttoncolor = "#B7C8EA"; //#648AD6
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
        secondsSwitch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                darkmodeBegin.clearFocus();
                darkmodeEnd.clearFocus();
                if(secondsSwitch.getText().equals("Sekunden")) {
                    secondsSwitch.setText("Minuten");
                    multiper = 60;
                } else {
                    if(secondsSwitch.getText().equals("Minuten")) {
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

        DecimalFormat dform = new DecimalFormat("#.#######");
        View layout = findViewById(R.id.Layout1);
        TimerTask tmTk1 = new TimerTask() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                //Hier wird alles geupdated
                LocalDateTime now2 = LocalDateTime.now();
                ZonedDateTime now = now2.atZone(ZoneId.of("Asia/Kolkata"));
                try {
                    x = timestamp(now.format(dr)) - timestamp("2022-08-24 07:30:00.000");
                    y = timestamp("2023-07-23 19:40:00.000") - timestamp(now.format(dr));
                    completeTime = timestamp("2023-07-23 19:40:00.000") - timestamp("2022-08-24 07:30:00.000");
                } catch (ParseException oi) {
                }
                y = y / 1000;
                z = (double) x / (double) (completeTime);
                z = z * 100;
                x = x / 1000;
                x = x / multiper;
                y = y / multiper;
                String xString = "Error";
                String yString = "Error";
                if(multiper == 1) {
                    xString = "Sekunden, seit du mich gesehen hast: " + x;
                    yString = "Sekunden, bis du mich wiedersiehst: " + y;
                }
                if(multiper == 60) {
                    xString = "Minuten, seit du mich gesehen hast: " + x;
                    yString = "Minuten, bis du mich wiedersiehst: " + y;
                }
                if(multiper == 3600) {
                    xString = "Stunden, seit du mich gesehen hast: " + x;
                    yString = "Stunden, bis du mich wiedersiehst: " + y;
                }
                String zString = dform.format(z) + " Prozent schon geschafft!";
                if(darkMode == 2)
                if ((Integer.parseInt(now.format(dz)) > begin) || (Integer.parseInt(now.format(dz)) < end)) {
                    backgroundcolor = "#2E2E2E";
                    textcolor = "#C5C5C5";
                    buttoncolor = "#464646";
                } else {
                    backgroundcolor = "#99B9F9"; //#B7C8EA
                    textcolor = "#3A5A9B"; //#3A5A9B
                    buttoncolor = "#B7C8EA"; //#648AD6
                }
                final String xString2 = xString;
                final String yString2 = yString;
                runOnUiThread(() -> {
                    // Stuff that updates the UI
                    secondsDone.setText(xString2);
                    secondsLeft.setText(yString2);
                    percent.setText(zString);
                    secondsDone.setPaddingRelative(40, 15, 40, 20);
                    darkmodeBeginText.setPaddingRelative(40, 15, 40, 20);
                    darkmodeBegin.setPaddingRelative(0, 15, 0, 20);
                    darkmodeEndText.setPaddingRelative(40, 15, 40, 20);
                    darkmodeEnd.setPaddingRelative(0, 15, 0, 20);
                    secondsLeft.setPaddingRelative(40, 15, 40, 20);
                    percent.setPaddingRelative(40, 15, 40, 20);

                    if ((percentage < Math.floor(z)) && (ran)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Noch ein Prozent geschafft! Ich liebe dich <3", Toast.LENGTH_LONG);
                        toast.show();
                    }

                    if((z>33.334)&&(z<33.34)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Ein Drittel ist schon geschafft! Wie cool <3", Toast.LENGTH_LONG);
                        toast.show();
                        builder2.setContentText("JulyTimer-Meilenstein")
                                .setContentTitle("Ein Drittel ist schon geschafft!")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(new NotificationCompat.BigTextStyle());
                        notificationManager2.notify(2, builder2.build());
                    }

                    if((z>50)&&(z<50.01)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Die Hälfte ist schon geschafft! Wie cool <3", Toast.LENGTH_LONG);
                        toast.show();
                        builder2.setContentText("JulyTimer-Meilenstein")
                                .setContentTitle("Die Hälfte ist schon geschafft!")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(new NotificationCompat.BigTextStyle());
                        notificationManager2.notify(2, builder2.build());
                    }

                    if((z>66.667)&&(z<66.67)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Zwei Drittel ist schon geschafft! Wie cool <3", Toast.LENGTH_LONG);
                        toast.show();
                        builder2.setContentText("JulyTimer-Meilenstein")
                                .setContentTitle("Zwei Drittel ist schon geschafft!")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(new NotificationCompat.BigTextStyle());
                        notificationManager2.notify(2, builder2.build());
                    }

                    if((z>99.99)&&(z<100)) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Fast alles geschafft! Bis bald :)", Toast.LENGTH_LONG);
                        toast.show();
                        builder2.setContentText("JulyTimer-Meilenstein")
                                .setContentTitle("Fast geschafft! Bis bald :)")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(new NotificationCompat.BigTextStyle());
                        notificationManager2.notify(2, builder2.build());
                    }

                    if((now.format(DateTimeFormatter.ofPattern("MM")) == "24")&&((now.format(DateTimeFormatter.ofPattern("HH:mm:ss")) == "07:30:00"))) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Noch einen Monat geschafft! Ich bin stolz auf dich <3", Toast.LENGTH_LONG);
                        toast.show();
                        builder2.setContentText("JulyTimer-Meilenstein")
                                .setContentTitle("Noch einen Monat geschafft! Ich bin stolz auf dich <3")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(new NotificationCompat.BigTextStyle());
                        notificationManager2.notify(2, builder2.build());
                    }

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

                    int width = displayMetrics.widthPixels;
                    int height = displayMetrics.heightPixels;
                    secondsDone.measure(0, 0);
                    darkmodeBeginText.measure(0, 0);
                    darkmodeEndText.measure(0, 0);
                    secondsLeft.measure(0, 0);
                    percent.measure(0, 0);
                    secondsSwitch.setX(width / 2 - 450);
                    secondsSwitch.setY(height / 4 * 3 - 550);
                    darkmodeSwitch.setX(width / 2 + 50);
                    darkmodeSwitch.setY(height / 4 * 3 - 550);
                    secondsDone.setX((width / 2) - (secondsDone.getMeasuredWidth() / 2));
                    secondsLeft.setX(width / 2 - secondsLeft.getMeasuredWidth() / 2);
                    percent.setX(width / 2 - percent.getMeasuredWidth() / 2);
                    secondsDone.setY(height / 2 - secondsDone.getMeasuredHeight() / 2 - height / 4);
                    secondsLeft.setY(height / 2 - secondsLeft.getMeasuredHeight() / 2 - height / 4 + 130);
                    percent.setY(height / 2 - percent.getMeasuredHeight() / 2 - height / 4 + 260);
                    darkmodeBeginText.setX(width / 2 - darkmodeBeginText.getMeasuredWidth() / 2);
                    darkmodeEndText.setX(width / 2 - darkmodeEndText.getMeasuredWidth() / 2);
                    darkmodeBeginText.setY(150);
                    darkmodeEndText.setY(280);
                    darkmodeBegin.setX(darkmodeBeginText.getX() + 625);
                    darkmodeEnd.setX(darkmodeEndText.getX() + 600);
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
                    darkmodeBegin.setTextColor(Color.parseColor(textcolor));
                    darkmodeEnd.setTextColor(Color.parseColor(textcolor));
                    secondsDone.setBackgroundColor(Color.parseColor(buttoncolor));
                    secondsLeft.setBackgroundColor(Color.parseColor(buttoncolor));
                    percent.setBackgroundColor(Color.parseColor(buttoncolor));
                    darkmodeBeginText.setBackgroundColor(Color.parseColor(buttoncolor));
                    darkmodeEndText.setBackgroundColor(Color.parseColor(buttoncolor));
                    secondsSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
                    darkmodeSwitch.setBackgroundColor(Color.parseColor(buttoncolor));
                    darkmodeBegin.setBackgroundColor(Color.parseColor(buttoncolor));
                    darkmodeEnd.setBackgroundColor(Color.parseColor(buttoncolor));
                });
                PROGRESS_CURRENT = (int) (x*multiper);
                builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                builder.setContentText("noch in Ghana! " + z + " Prozent schon geschafft!");
                builder.setContentTitle(y + " " +secondsSwitch.getText().toString());
                notificationManager.notify(1, builder.build());
            }
        };
        homeScreenLayout.addView(secondsDone);
        homeScreenLayout.addView(secondsLeft);
        homeScreenLayout.addView(percent);
        tm1.schedule(tmTk1, 0, 100);
    }
}