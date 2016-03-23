package app.abhijit.iter;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class Api {

  private static final String API_ENDPOINT_URL = "http://111.93.164.202:8282/CampusLynxSOA/CounsellingRequest?refor=StudentOnlineDetailService";
  private static final String UPDATE_ENDPOINT_URL = "http://iter-update-server.herokuapp.com";
  private Context context;
  private Http http;

  // TODO: implement Http interface and pass Http in constructor
  public Api(Context context) {
    this.context = context;
    this.http = new Http(context);
  }

  public Map<String, String> makeApiRequest(String rollNumber, String requestId) {
    HashMap<String, String> apiResponse = new HashMap<String, String>();
    apiResponse.put("requestid", requestId);
    apiResponse.put("rollnumber", rollNumber);
    apiResponse.put("updatejson", fetchUpdateInfo(rollNumber));
    try {
      String instituteId = fetchInstituteId();
      String registrationId = fetchRegistrationId();
      String studentId = fetchStudentId(instituteId, rollNumber);
      if (studentId.equals("0")) {
        apiResponse.put("error", "Invalid registration number");
        return apiResponse;
      }
      apiResponse.put("studentjson", fetchStudentJson(instituteId, studentId));
      apiResponse.put("attendancejson", fetchAttendanceJson(instituteId, registrationId,
          studentId));
    } catch (Exception e) {
      apiResponse.put("error", "Could not connect to the server");
    }
    return apiResponse;
  }

  private String fetchUpdateInfo(String rollNumber) {
    String currentVersion = this.context.getString(R.string.app_version);
    try {
      return this.http.makeGetRequest(this.UPDATE_ENDPOINT_URL + "/check/" + rollNumber + "/"
          + currentVersion);
    } catch (Exception e) {
      return null;
    }
  }

  private String fetchInstituteId() {
    return "SOAUINSD1312A0000002";
  }

  private String fetchRegistrationId() {
    // TODO: Implement xml parsing to fetch and parse registration id automatically
    return "ITERRETD1511A0000001";
  }

  private String fetchStudentId(String instituteId, String rollNumber) throws Exception {
    // TODO: Make request data readable
    String requestData = "jdata=%7B%22sid%22%3A%22validate%22%2C%22instituteID%22%3A%22"
        + instituteId + "%22%2C%22studentrollno%22%3A%22" + rollNumber + "%22%7D";
    return this.http.makePostRequest(this.API_ENDPOINT_URL, requestData);
  }

  private String fetchStudentJson(String instituteId, String studentId) throws Exception {
    // TODO: Make request data readable
    String requestData = "jdata=%7B%22sid%22%3A%22studentdetails%22%2C%22instituteid%22%3A%22"
        + instituteId + "%22%2C%22studentid%22%3A%22" + studentId + "%22%7D";
    return this.http.makePostRequest(this.API_ENDPOINT_URL, requestData);
  }

  private String fetchAttendanceJson(String instituteId, String registrationId, String studentId)
      throws Exception {
    // TODO: Make request data readable
    String requestData = "jdata=%7B%22sid%22%3A%22attendance%22%2C%22instituteid%22%3A%22"
        + instituteId + "%22%2C%22registrationid%22%3A%22" + registrationId
        + "%22%2C%22studentid%22%3A%22" + studentId + "%22%7D";
    return this.http.makePostRequest(this.API_ENDPOINT_URL, requestData);
  }

}
