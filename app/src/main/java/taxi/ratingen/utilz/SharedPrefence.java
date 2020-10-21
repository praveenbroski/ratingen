package taxi.ratingen.utilz;

import android.content.SharedPreferences;

import java.util.Set;

import javax.inject.Inject;

/**
 * Its a local storage class to store some sensitive information.
 */
public class SharedPrefence {
    private static final String MY_PREFS_NAME = "MY_PERFER";
    public static final String Logged = "Logged";
    public static final String INTERVALTIME = "Internaltime";
    public static final String ID = "id";
    public static final String TOKEN = "token";
    public static final String USERDETAILS = "userdetails";
    public static final String FAVLIST = "favList";
    public static final String SOSLIST = "SOSLIST";
    public static final String LANGUANGE = "LANGUANGE ";
    public static final String FCMTOKEN = "FCMTOKEN";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String PREFFERED_PAYMENT = "PREFFERED_PAYMENT";
    public static final String CURRENT_COUNTRY = "current_country";
    public static final String AR = "ar";
    public static final String LANGUAGES = "languages";
    public static final String EN = "en";
    public static final String ES = "es";
    public static final String ZH = "zh";
    public static final String JA = "ja";
    public static final String KO = "ko";
    public static final String PT = "pt";
    public static final String IS_CORPORATE_USER = "is_corporate_user";
    public static final String MAPTYPE = "MAPTYPE";
    public static final String COMPANY_KEY = "company_key";
    public static final String COMPANY_ID = "company_id";
    public static final String GetStartedScrnLoaded = "GetStartedScrnLoaded";
    public static final String DEFAULT_COUNTRY = "DEFAULT_COUNTRY";

    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    @Inject
    public SharedPrefence(SharedPreferences sharedPreferences, SharedPreferences.Editor editor) {
        prefs = sharedPreferences;
        this.editor = editor;
    }

    public void savevalue(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public int getInt(String key) {
        return prefs.getInt(key, -1);
    }

    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean GetBoolean(String key) {
        return prefs.getBoolean(key, false);
    }

    public String Getvalue(String key) {
        return prefs.getString(key, "");
    }

    public Set<String> getSet(String key) {
        return prefs.getStringSet(key, null);
    }

    public void saveCURRENT_COUNTRY(String value) {
        editor.putString(CURRENT_COUNTRY, value);
        editor.apply();
    }

    public String getCURRENT_COUNTRY() {
        return prefs.getString(CURRENT_COUNTRY, "");
    }


    public void clearAll() {
        editor.clear().commit();
    }

    public class Maptype {
    }

    public String getCompanyToken() {
        return prefs.getString(COMPANY_KEY, "");
    }

    public String getCompanyID() {
        return prefs.getString(COMPANY_ID, "");
    }
}