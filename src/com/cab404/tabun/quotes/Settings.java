package com.cab404.tabun.quotes;

import android.content.Context;

import java.io.*;
import java.util.Map;

/**
 * @author cab404
 */
public class Settings {

    protected final static Object settings_lock = new Object();

    public static void save(Map<String, String> settings, Context context)
    throws IOException {
        synchronized (settings_lock) {
            File file = new File(context.getFilesDir(), "settings.bin");
            if (file.exists() && !file.delete()) throw new RuntimeException("Cannot delete file");

            ObjectOutputStream saver = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
            saver.writeObject(settings);
            saver.close();
        }
    }

    @SuppressWarnings("unchecked")
    protected static Map<String, String> load(Context context)
    throws IOException, ClassNotFoundException {

        synchronized (settings_lock) {
            File file = new File(context.getFilesDir(), "settings.bin");
            if (!file.exists()) throw new FileNotFoundException("No file detected");

            ObjectInputStream loader = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
            return (Map<String, String>) loader.readObject();

        }
    }

}
