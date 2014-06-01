package com.cab404.tabun.quotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
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
public class QuoteWidget extends AppWidgetProvider {

    @Override public void onUpdate(final Context context, final AppWidgetManager manager, int[] ids) {
        super.onUpdate(context, manager, ids);

        for (final int id : ids) {

            final Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra("appWidgetIds", new int[]{id});

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
                    manager.updateAppWidget(id, remoteViews);
                }

            }.execute();


            RemoteViews update = new RemoteViews(context.getPackageName(), R.layout.widget);
            update.setViewVisibility(R.id.loading, View.VISIBLE);
            update.setOnClickPendingIntent(
                    R.id.root,
                    PendingIntent.getBroadcast(context, id, intent, 0)
            );

            manager.updateAppWidget(id, update);

        }

    }

    @Override public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }
}
