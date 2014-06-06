package com.cab404.tabun.quotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.Map;

/**
 * @author cab404
 */
public class QuoteWidget extends AppWidgetProvider {

    static String UPDATE = "com.cab404.tabun.quotes.UPDATE";

    @Override public void onUpdate(final Context context, final AppWidgetManager man, int[] ids) {
        super.onUpdate(context, man, ids);

        for (final int id : ids) {
            /* Просим обновлялку обновить нас. */
            Intent update_intent = new Intent(context, Updater.class);
            update_intent.putExtra("id", id);

            RemoteViews upd = new RemoteViews(context.getPackageName(), R.layout.widget);

            Intent intent = new Intent();
            intent.setClass(context, Updater.class);
            intent.putExtra("id", id);

            upd.setOnClickPendingIntent(
                    R.id.root,
                    PendingIntent.getBroadcast(context, id, intent, 0)
            );

            man.updateAppWidget(id, upd);
            context.sendBroadcast(update_intent);
        }

    }

    @Override public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        /* Загружаем настройки */
        Map<String, String> settings;
        try {
            settings = Settings.load(context);
        } catch (Exception e) {
            throw new RuntimeException("Не могу загрузить параметры!", e);
        }

        /* Удаляем уничтоженные виджеты */
        for (int id : appWidgetIds) {
            settings.remove("text_size_" + id);
            settings.remove("title_size_" + id);
        }

        /* Сохраняем настройки */
        try {
            Settings.save(settings, context);
        } catch (IOException e) {
            throw new RuntimeException("Не могу сохранить параметры!", e);
        }
    }
}
