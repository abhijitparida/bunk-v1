package app.abhijit.iter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.LinkedList;

public class Db {

  private LinkedList<String> keys = new LinkedList<String>();
  private Context context;
  private SharedPreferences sharedPreferences;

  public Db(Context context) {
    this.context = context;
    sharedPreferences = context.getSharedPreferences("ITER", Context.MODE_PRIVATE);
    loadKeys();
  }

  public void add(String key) {
    int index = keys.indexOf(key);
    if (index > -1) {
      keys.remove(index);
    }
    keys.addFirst(key);
    saveKeys();
  }

  public void addValue(String key, String value) {
    sharedPreferences.edit().putString(key, value).apply();
  }

  public String get(int index) {
    return keys.get(index);
  }

  public String getValue(String key) {
    return sharedPreferences.getString(key, null);
  }

  public String current() {
    return keys.get(0);
  }

  public int size() {
    return keys.size();
  }

  public void clear() {
    keys.clear();
    sharedPreferences.edit().clear().apply();
  }

  private void loadKeys() {
    String data = sharedPreferences.getString("keys", null);
    if (data != null) {
      keys.addAll(Arrays.asList(data.trim().split(" ")));
    }
  }

  private void saveKeys() {
    String data = null;
    if (keys.size() > 0) {
      data = TextUtils.join(" ", keys.toArray(new String[keys.size()]));
    }
    sharedPreferences.edit().putString("keys", data).apply();
  }

}
