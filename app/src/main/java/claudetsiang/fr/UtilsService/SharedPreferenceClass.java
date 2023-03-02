package claudetsiang.fr.UtilsService;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

//SharedPreferences ou store
public class SharedPreferenceClass {
    private static final String USER_PREF = "current_user";
    private SharedPreferences appShared;
    private SharedPreferences.Editor prefsEditor;

    // configuration du store
    public SharedPreferenceClass(Context context) {
        appShared = context.getSharedPreferences(USER_PREF, Activity.MODE_PRIVATE);
        this.prefsEditor = appShared.edit();
    }

    //int***** Methods sur les entiers pour le store
    public int getValue_int(String key){
        return appShared.getInt(key, 0);
    }
    public void setValue_int(String key, int value){
        prefsEditor.putInt(key, value).commit();
    }

    // String***** Methods sur les string pour le store
    public String getValue_string(String key){
        return appShared.getString(key, "");
    }
    public void setValue_string(String key, String value){
        prefsEditor.putString(key, value).commit();
    }

    //Boolean****** Methods sur les booleans pour le store
    public boolean getValue_boolean(String key){
        return appShared.getBoolean(key, false);
    }
    public void setValue_boolean(String key, Boolean value){
        prefsEditor.putBoolean(key, value).commit();
    }

    //
    public void clear(){
        prefsEditor.clear().commit();
    }
}
