package app.abhijit.iter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.TreeSet;

public class Db {

  private Context context;
  private SharedPreferences sharedPreferences;
  private TreeSet<String> keys;
  private String currentkey;

  public Db(Context context) {
    this.context = context;
    this.sharedPreferences = this.context.getSharedPreferences("bunk-v15-db", Context.MODE_PRIVATE);
    this.keys = getkeys();
    this.currentkey = getcurrentkey();
  }

  public String getCurrentKey() {
    return this.currentkey;
  }

  public void setCurrentKey(String key) {
    this.currentkey = key;
    this.sharedPreferences.edit().putString("currentkey", key).apply();
  }

  public String[] getAllKeys() {
    return this.keys.toArray(new String[this.keys.size()]);
  }

  public String getValue(String key) {
    return this.sharedPreferences.getString(key, null);
  }

  public void setValue(String key, String value) {
    this.keys.add(key);
    this.sharedPreferences.edit().putString(key, value).apply();
  }

  public void clearDb() {
    this.keys.clear();
    this.sharedPreferences.edit().clear().apply();
  }

  private TreeSet<String> getkeys() {
    TreeSet<String> keys = new TreeSet<String>();
    Map<String, ?> allentries = this.sharedPreferences.getAll();
    for (Map.Entry<String, ?> entry : allentries.entrySet()) {
      if (!(entry.getKey().equals("currentkey"))) {
        keys.add(entry.getKey());
      }
    }
    return keys;
  }

  private String getcurrentkey() {
    return this.sharedPreferences.getString("currentkey", null);
  }

}
