package app.abhijit.iter;

import android.content.Context;
import android.webkit.WebSettings;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {

  private Context context;
  private String useragent;

  public Http(Context context) {
    this.context = context;
    this.useragent = WebSettings.getDefaultUserAgent(this.context);
  }

  public String makePostRequest(String url, String data) throws Exception {
    URL endpoint = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("User-Agent", this.useragent);
    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    conn.setDoOutput(true);
    DataOutputStream outstream = new DataOutputStream(conn.getOutputStream());
    outstream.writeBytes(data);
    outstream.flush();
    outstream.close();
    BufferedReader instream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String instreamline;
    StringBuffer response = new StringBuffer();
    while ((instreamline = instream.readLine()) != null) {
      response.append(instreamline);
    }
    instream.close();
    return response.toString();
  }

  public String makeGetRequest(String url) throws Exception {
    URL endPoint = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) endPoint.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("User-Agent", this.useragent);
    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    BufferedReader instream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String instreamline;
    StringBuffer response = new StringBuffer();
    while ((instreamline = instream.readLine()) != null) {
      response.append(instreamline);
    }
    instream.close();
    return response.toString();
  }

}
