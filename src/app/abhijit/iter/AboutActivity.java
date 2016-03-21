package app.abhijit.iter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class AboutActivity extends PreferenceActivity implements OnPreferenceClickListener {

  private Context context = this;
  private int easteregg;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.about);
    findPreference("pref_version").setOnPreferenceClickListener(this);
    findPreference("pref_feedback").setOnPreferenceClickListener(this);
    findPreference("pref_contribute").setOnPreferenceClickListener(this);
    findPreference("pref_rate").setOnPreferenceClickListener(this);
  }

  @Override
  public boolean onPreferenceClick(Preference preference) {
    if (preference.getKey().equals("pref_version") && ++easteregg == 5) {
      Toast.makeText(context, "GRG", Toast.LENGTH_LONG).show();
    } else if (preference.getKey().equals("pref_feedback")) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://goo.gl/forms/MsrY3JciZD")));
    } else if (preference.getKey().equals("pref_contribute")) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/abhijitparida/ITER/")));
    } else if (preference.getKey().equals("pref_rate")) {
      // TODO: implement this
    }
    return false;
  }

}
