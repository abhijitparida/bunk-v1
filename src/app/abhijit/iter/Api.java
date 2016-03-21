package app.abhijit.iter;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class Api {

  private final String API_ENDPOINT_URL = "http://111.93.164.202:8282/CampusLynxSOA/CounsellingRequest?refor=StudentOnlineDetailService";
  private final String UPDATE_ENDPOINT_URL = "http://iter-update-server.herokuapp.com";
  private Context context;
  private Http http;

  // TODO: implement Http interface and pass Http in constructor
  public Api(Context context) {
    this.context = context;
    this.http = new Http(context);
  }

  public Map<String, String> makeApiRequest(String rollnumber, String requestid) {
    HashMap<String, String> apiresponse = new HashMap<String, String>();
    apiresponse.put("requestid", requestid);
    apiresponse.put("rollnumber", rollnumber);
    apiresponse.put("update", fetchupdateinfo(rollnumber));
    try {
      String instituteid = fetchinstituteid();
      String registrationid = fetchregistrationid(instituteid);
      String studentid = fetchstudentid(instituteid, rollnumber);
      if (studentid.equals("0")) {
        apiresponse.put("error", "Invalid registration number");
        return apiresponse;
      }
      apiresponse.put("studentjson", fetchstudentjson(instituteid, studentid));
      apiresponse.put("attendancejson", fetchattendancejson(instituteid, registrationid, studentid));
    } catch (Exception e) {
      apiresponse.put("error", "Could not connect to the server");
    }
    return apiresponse;
  }

  private String fetchupdateinfo(String rollnumber) {
    String currentversion = this.context.getString(R.string.app_version);
    try {
      return this.http.makeGetRequest(this.UPDATE_ENDPOINT_URL + "/check/" + rollnumber + "/" + currentversion);
    } catch (Exception e) {
      return null;
    }
  }

  private String fetchinstituteid() {
    return "SOAUINSD1312A0000002";
  }

  private String fetchregistrationid(String instituteid) {
    // TODO: Implement xml parsing to fetch and parse registration id automatically
    return "ITERRETD1511A0000001";
  }

  private String fetchstudentid(String instituteid, String rollnumber) throws Exception {
    // TODO: Make request data readable
    String requestdata = "jdata=%7B%22sid%22%3A%22validate%22%2C%22instituteID%22%3A%22" + instituteid + "%22%2C%22studentrollno%22%3A%22" + rollnumber + "%22%7D";
    return this.http.makePostRequest(this.API_ENDPOINT_URL, requestdata);
  }

  private String fetchstudentjson(String instituteid, String studentid) throws Exception {
    // TODO: Make request data readable
    String requestdata = "jdata=%7B%22sid%22%3A%22studentdetails%22%2C%22instituteid%22%3A%22" + instituteid + "%22%2C%22studentid%22%3A%22" + studentid + "%22%7D";
    return this.http.makePostRequest(this.API_ENDPOINT_URL, requestdata);
  }

  private String fetchattendancejson(String instituteid, String registrationid, String studentid) throws Exception {
    // TODO: Make request data readable
    String requestdata = "jdata=%7B%22sid%22%3A%22attendance%22%2C%22instituteid%22%3A%22" + instituteid + "%22%2C%22registrationid%22%3A%22" + registrationid + "%22%2C%22studentid%22%3A%22" + studentid + "%22%7D";
    return this.http.makePostRequest(this.API_ENDPOINT_URL, requestdata);
  }

}
