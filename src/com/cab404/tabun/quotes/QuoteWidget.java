package com.cab404.tabun.quotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

/**
 * @author cab404
 */
public class QuoteWidget extends AppWidgetProvider {

    String UPDATE = "com.cab404.tabun.quotes.UPDATE";

    @Override public void onUpdate(final Context context, final AppWidgetManager manager, int[] ids) {
        super.onUpdate(context, manager, ids);

        for (final int id : ids) {

            final Intent intent = new Intent(UPDATE);
            intent.putExtra("id", id);
            context.sendBroadcast(intent);

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
