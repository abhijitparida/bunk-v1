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
  private int easterEgg;

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
    String feedbackUrl = "https://goo.gl/forms/MsrY3JciZD";
    String contributeUrl = "https://github.com/abhijitparida/ITER/";
    String storeUrl = "https://play.google.com/store/apps/details?id=app.abhijit.iter";
    if (preference.getKey().equals("pref_version") && ++this.easterEgg == 5) {
      Toast.makeText(this.context, "GRG", Toast.LENGTH_LONG).show();
    } else if (preference.getKey().equals("pref_feedback")) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(feedbackUrl)));
    } else if (preference.getKey().equals("pref_contribute")) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(contributeUrl)));
    } else if (preference.getKey().equals("pref_rate")) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl)));
    }
    return false;
  }

}
