package in.assignmentsolutions.momskart.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HP on 8/12/2019.
 */

public class SharedPreferencesHelper {
    private SharedPreferencesHelper(Context context){
    }

    private static void ensureNotNull(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context is null.");
        }
    }

    public static boolean isLogged(Context context, String prefs){
        ensureNotNull(context);
        return context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .getBoolean("LOGGED",false);
    }

    public static void setLogged(Context context, String prefs, JSONObject jsonObject){
        ensureNotNull(context);
        String id = null, email = null, name = null, profile_pic = null;
        try {
            id = jsonObject.getString("id");
            email = jsonObject.getString("email");
            name = jsonObject.getString("name");
            profile_pic = jsonObject.getString("profile_pic");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .edit().putString("id", id).apply();
        context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .edit().putString("email",email).apply();
        context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .edit().putString("name",name).apply();
        context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .edit().putString("profile",profile_pic).apply();
        context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .edit().putBoolean("LOGGED",true).apply();
    }

    public static void setAttb(Context context, String prefs,String name, String key) {
        context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .edit().putString(key,name).apply();
    }

    public static String getAttb(Context context, String prefs, String key) {
        return context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .getString(key, "-1");
    }


    public static String getUserInfo(Context context, String prefs) {
        return context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .getString("id","-1");
    }

    public static void setLogout(Context context, String prefs) {
        context.getSharedPreferences(prefs,Context.MODE_PRIVATE)
                .edit().putBoolean("LOGGED",false).apply();
    }
}
