package com.cab404.tabun.quotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import com.cab404.libtabun.pages.MainPage;
import com.cab404.libtabun.util.TabunAccessProfile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cab404
 */
public class Updater extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final AppWidgetManager man = AppWidgetManager.getInstance(context);
        final int id = intent.getIntExtra("id", AppWidgetManager.INVALID_APPWIDGET_ID);

        Log.v("Updater", "Обновляю " + id);

        Map<String, String> settings;
        try {
            settings = Settings.load(context);
        } catch (IOException | ClassNotFoundException e) {
            settings = new HashMap<>();
        }

        RemoteViews update = new RemoteViews(context.getPackageName(), R.layout.widget);
        update.setViewVisibility(R.id.loading, View.VISIBLE);
        /* Обновляем размеры */
        if (settings.containsKey("text_size_" + id)) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            update.setFloat(
                    R.id.quote,
                    "setTextSize",
                    dm.scaledDensity * Integer.valueOf(settings.get("text_size_" + id)
                    )
            );
            update.setFloat(
                    R.id.title,
                    "setTextSize",
                    dm.scaledDensity * Integer.valueOf(settings.get("title_size_" + id)
                    )
            );
        }

        /* Шлем обновление */
        man.updateAppWidget(id, update);

        /* Запускаем обновление текста */
        new AsyncTask<Void, Void, RemoteViews>() {

            @Override protected RemoteViews doInBackground(Void... voids) {
                RemoteViews update = new RemoteViews(context.getPackageName(), R.layout.widget);

                MainPage mainPage = new MainPage();
                mainPage.fetch(new TabunAccessProfile());

                update.setTextViewText(R.id.quote, TextEscaper.simpleEscape(mainPage.quote));
                update.setViewVisibility(R.id.loading, View.GONE);

                Intent intent = new Intent();
                intent.setClass(context, Updater.class);
                intent.putExtra("id", id);

                update.setOnClickPendingIntent(
                        R.id.root,
                        PendingIntent.getBroadcast(context, id, intent, 0)
                );

                return update;
            }

            @Override protected void onPostExecute(RemoteViews remoteViews) {
                super.onPostExecute(remoteViews);
                man.updateAppWidget(id, remoteViews);
            }

        }.execute();

    }
}
