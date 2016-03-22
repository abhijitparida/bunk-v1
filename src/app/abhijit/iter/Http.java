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
  private String userAgent;

  public Http(Context context) {
    this.context = context;
    this.userAgent = WebSettings.getDefaultUserAgent(this.context);
  }

  public String makePostRequest(String url, String data) throws Exception {
    URL endPoint = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) endPoint.openConnection();
    conn.setRequestMethod("POST");
    conn.setConnectTimeout(5000);
    conn.setReadTimeout(5000);
    conn.setRequestProperty("User-Agent", this.userAgent);
    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    conn.setDoOutput(true);
    DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
    outStream.writeBytes(data);
    outStream.flush();
    outStream.close();
    BufferedReader inStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String inStreamLine;
    StringBuffer response = new StringBuffer();
    while ((inStreamLine = inStream.readLine()) != null) {
      response.append(inStreamLine);
    }
    inStream.close();
    return response.toString();
  }

  public String makeGetRequest(String url) throws Exception {
    URL endPoint = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) endPoint.openConnection();
    conn.setRequestMethod("GET");
    conn.setConnectTimeout(5000);
    conn.setReadTimeout(5000);
    conn.setRequestProperty("User-Agent", this.userAgent);
    conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
    BufferedReader inStream = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String inStreamLine;
    StringBuffer response = new StringBuffer();
    while ((inStreamLine = inStream.readLine()) != null) {
      response.append(inStreamLine);
    }
    inStream.close();
    return response.toString();
  }

}
