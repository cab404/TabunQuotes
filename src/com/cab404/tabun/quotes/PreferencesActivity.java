package com.cab404.tabun.quotes;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cab404
 */
public class PreferencesActivity extends Activity {

    protected Map<String, String> settings;
    protected int id;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferences);

        id = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (id == -1) {
            finish();
            return;
        }

        try {
            this.settings = Settings.load(this);
        } catch (IOException | ClassNotFoundException e) {

            settings = new HashMap<>();

            getTitleSize();

            try {
                Settings.save(settings, this);
            } catch (IOException e1) {
                throw new RuntimeException("CANNOT SAVE SETTINGS!", e1);
            }

        }

        SeekBar title_size = (SeekBar) findViewById(R.id.title_size);
        title_size.setProgress(getTitleSize());
        title_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int value, boolean user) {
                if (user)
                    settings.put("title_size_" + id, value + 1 + "");
                update();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        SeekBar text_size = (SeekBar) findViewById(R.id.text_size);
        text_size.setProgress(getTextSize());
        text_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int value, boolean user) {
                if (user)
                    settings.put("text_size_" + id, value + 1 + "");
                update();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        findViewById(R.id.preview).findViewById(R.id.loading).setVisibility(View.INVISIBLE);
        update();

    }

    protected int getTitleSize() {
        String key = "title_size_" + id;
        if (settings.containsKey(key)) {
            return Integer.parseInt(settings.get(key));
        } else
            settings.put(key, "18");
        return 18;
    }


    protected int getTextSize() {
        String key = "text_size_" + id;
        if (settings.containsKey(key)) {
            return Integer.parseInt(settings.get(key));
        } else
            settings.put(key, "10");
        return 12;
    }


    protected void update() {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        TextView title = (TextView) findViewById(R.id.preview).findViewById(R.id.title);
        TextView text = (TextView) findViewById(R.id.preview).findViewById(R.id.quote);


        title.setTextSize(dm.scaledDensity * getTitleSize());
        text.setTextSize(dm.scaledDensity * getTextSize());

    }


    @Override protected void onDestroy() {
        super.onDestroy();

        if (id != -1)
            try {
                Settings.save(settings, this);
            } catch (IOException e) {
                throw new RuntimeException("CANNOT SAVE SETTINGS!", e);
            }


        DisplayMetrics dm = getResources().getDisplayMetrics();
        AppWidgetManager man = AppWidgetManager.getInstance(this);
        RemoteViews upd = new RemoteViews(getPackageName(), R.layout.widget);

        upd.setFloat(R.id.quote, "setTextSize", dm.scaledDensity * getTextSize());
        upd.setFloat(R.id.title, "setTextSize", dm.scaledDensity * getTitleSize());

        Intent update_intent = new Intent(this.getApplicationContext(), Updater.class);
        update_intent.putExtra("id", id);

        man.updateAppWidget(id, upd);

        Log.v("Preferences", "Закончили настраивать, обновляю виджет #" + id);
    }

    @Override public void onBackPressed() {
        setResult(RESULT_CANCELED, getIntent());
        finish();
    }
    @SuppressWarnings("unused")
    public void ok(View button) {
        setResult(RESULT_OK, getIntent());
        finish();
    }

}