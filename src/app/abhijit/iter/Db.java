package app.abhijit.iter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.TreeSet;

public class Db {

  private Context context;
  private SharedPreferences sharedPreferences;

  public Db(Context context) {
    this.context = context;
    this.sharedPreferences = this.context.getSharedPreferences("bunk-v15-db", Context.MODE_PRIVATE);
  }

  public String getCurrentKey() {
    return this.sharedPreferences.getString("currentkey", null);
  }

  public void setCurrentKey(String key) {
    this.sharedPreferences.edit().putString("currentkey", key).apply();
  }

  public String[] getAllKeys() {
    TreeSet<String> keys = new TreeSet<String>();
    for (Map.Entry<String, ?> entry : this.sharedPreferences.getAll().entrySet()) {
      if (!(entry.getKey().equals("currentkey"))) {
        keys.add(entry.getKey());
      }
    }
    return keys.toArray(new String[keys.size()]);
  }

  public String getValue(String key) {
    return this.sharedPreferences.getString(key, null);
  }

  public void setValue(String key, String value) {
    this.sharedPreferences.edit().putString(key, value).apply();
  }

  public void clearAllValues() {
    this.sharedPreferences.edit().clear().apply();
  }

}
