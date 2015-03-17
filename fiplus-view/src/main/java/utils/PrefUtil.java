package utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefUtil {
    static public final class Prefs {
        public static SharedPreferences get(Context context) {
            return context.getSharedPreferences("_dreamf_pref", 0);
        }
    } 

    static public String getString(Context context, String key) {
        SharedPreferences settings = Prefs.get(context);
        return settings.getString(key, "");
    }
    
    static public String getString(Context context, String key, String defaultString) {
        SharedPreferences settings = Prefs.get(context);
        return settings.getString(key, defaultString);
    }

    static public boolean getBoolean(Context context, String key) {
        SharedPreferences settings = Prefs.get(context);
        return settings.getBoolean(key, false);
    }

    static public boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = Prefs.get(context);
        return settings.getBoolean(key, defaultValue);
    }

    static public long getLong(Context context, String key) {
        SharedPreferences settings = Prefs.get(context);
        return settings.getLong(key, 0);
    }

    static public long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = Prefs.get(context);
        return settings.getLong(key, defaultValue);
    }

    static public synchronized void putString(Context context, String key,
            String value) {
        SharedPreferences settings = Prefs.get(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static public synchronized void putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = Prefs.get(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    static public synchronized void putLong(Context context, String key, long value) {
        SharedPreferences settings = Prefs.get(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.commit();
    }
}

