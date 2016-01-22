package app.abhijit.iter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Api {

  private static final String API_ENDPOINT_URL = "http://111.93.164.202:8282/CampusLynxSOA/CounsellingRequest?refor=StudentOnlineDetailService";
  private static final String UPDATE_ENDPOINT_URL = "http://iter-update-server.herokuapp.com";

  public static ApiResponse getApiResponse(String studentRollNumber) {
    ApiResponse apiResponse = new ApiResponse();
    apiResponse.updateAvailable = checkUpdate(studentRollNumber);
    String studentJson;
    try {
      String instituteId = fetchInstituteId();
      String studentId = fetchStudentId(instituteId, studentRollNumber);
      if (studentId.equals("0")) {
        apiResponse.error = true;
        apiResponse.errorMessage = "Invalid registration number";
        return apiResponse;
      }
      String registrationId = fetchRegistrationId(instituteId);
      String lastRefreshed = new SimpleDateFormat("MMMM dd, yyyy").format(new Date());
      String studentDetailsJson = fetchStudentDetailsJson(instituteId, studentId);
      String attendanceJson = fetchAttendanceJson(instituteId, registrationId, studentId);
      studentJson = "{\"studentRollNumber\": \"" + studentRollNumber + "\", \"instituteId\": \"" + instituteId + "\", \"studentId\": \"" + studentId + "\", \"registrationId\": \"" + registrationId + "\", \"lastRefreshed\": \"" + lastRefreshed + "\", \"studentDetailsJson\": \"" + URLEncoder.encode(studentDetailsJson, "UTF-8") + "\", \"attendanceJson\": \"" + URLEncoder.encode(attendanceJson, "UTF-8") + "\"}";
    } catch (Exception e) {
      apiResponse.error = true;
      apiResponse.errorMessage = "Could not connect to the server";
      return apiResponse;
    }
    Student student = Student.parseJson(studentJson);
    if (student == null) {
      apiResponse.error = true;
      apiResponse.errorMessage = "Bad API response";
      return apiResponse;
    }
    apiResponse.studentJson = studentJson;
    apiResponse.student = student;
    return apiResponse;
  }

  private static String makeHttpPostRequest(String url, String data) throws Exception {
    URL endPoint = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) endPoint.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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

  private static String makeHttpGetRequest(String url) throws Exception {
    URL endPoint = new URL(url);
    HttpURLConnection conn = (HttpURLConnection) endPoint.openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
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

  private static String getCurrentVersion() {
    // implement this
    return "1.0.0";
  }

  private static boolean checkUpdate(String studentRollNumber) {
    String updateAvailable;
    String currentVersion = getCurrentVersion();
    try {
      updateAvailable = makeHttpGetRequest(UPDATE_ENDPOINT_URL + "/check/" + studentRollNumber + "/" + currentVersion);
    } catch (Exception e) {
      return false;
    }
    if (updateAvailable.equals("yes")) {
      return true;
    }
    return false;
  }

  private static String fetchInstituteId() throws Exception {
    String instituteId = "SOAUINSD1312A0000002";
    return instituteId;
  }

  private static String fetchStudentId(String instituteId, String studentRollNumber) throws Exception {
    String requestData = "jdata=%7B%22sid%22%3A%22validate%22%2C%22instituteID%22%3A%22" + instituteId + "%22%2C%22studentrollno%22%3A%22" + studentRollNumber + "%22%7D";
    String studentId = makeHttpPostRequest(API_ENDPOINT_URL, requestData);
    return studentId;
  }

  private static String fetchRegistrationId(String instituteId) throws Exception {
    String requestData = "jdata=%7B%22sid%22%3A%22registrationcode%22%2C%22labelname%22%3A%22Select%20Registration%20Code%22%2C%22instituteID%22%3A%22" + instituteId + "%22%7D";
    String registrationId = makeHttpPostRequest(API_ENDPOINT_URL, requestData).substring(16, 36);
    return registrationId;
  }

  private static String fetchStudentDetailsJson(String instituteId, String studentId) throws Exception {
    String requestData = "jdata=%7B%22sid%22%3A%22studentdetails%22%2C%22instituteid%22%3A%22" + instituteId + "%22%2C%22studentid%22%3A%22" + studentId + "%22%7D";
    String studentDetailsJson = makeHttpPostRequest(API_ENDPOINT_URL, requestData);
    return studentDetailsJson;
  }

  private static String fetchAttendanceJson(String instituteId, String registrationId, String studentId) throws Exception {
    String requestData = "jdata=%7B%22sid%22%3A%22attendance%22%2C%22instituteid%22%3A%22" + instituteId + "%22%2C%22registrationid%22%3A%22" + registrationId + "%22%2C%22studentid%22%3A%22" + studentId + "%22%7D";
    String attendanceDetails = makeHttpPostRequest(API_ENDPOINT_URL, requestData);
    return attendanceDetails;
  }

}
