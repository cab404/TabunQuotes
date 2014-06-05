package com.cab404.tabun.quotes;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RemoteViews;
import com.cab404.libtabun.pages.MainPage;
import com.cab404.libtabun.util.TabunAccessProfile;

/**
 * @author cab404
 */
public class Updater extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        final AppWidgetManager man = AppWidgetManager.getInstance(context);

        new AsyncTask<Void, Void, RemoteViews>() {
            @Override protected RemoteViews doInBackground(Void... voids) {
                RemoteViews update = new RemoteViews(context.getPackageName(), R.layout.widget);

                MainPage mainPage = new MainPage();
                mainPage.fetch(new TabunAccessProfile());

                update.setTextViewText(R.id.quote, TextEscaper.simpleEscape(mainPage.quote));
                update.setViewVisibility(R.id.loading, View.GONE);

                return update;
            }

            @Override protected void onPostExecute(RemoteViews remoteViews) {
                super.onPostExecute(remoteViews);
                man.updateAppWidget(intent.getIntExtra("id", -1), remoteViews);
            }

        }.execute();

    }
}
