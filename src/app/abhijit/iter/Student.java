package app.abhijit.iter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URLDecoder;

public class Student {

  public String studentRollNumber;
  public String instituteId;
  public String studentId;
  public String registrationId;
  public String lastRefreshed;
  public String sectionCode;
  public String name;
  public String programCode;
  public String academicYear;
  public Course[] attendance;

  public static Student parseJson(String json) {
    Student student;
    try {
      JSONParser parser = new JSONParser();
      JSONObject studentJson = (JSONObject) parser.parse(json);
      JSONObject studentDetailsJson = (JSONObject) ((JSONArray) parser.parse(URLDecoder.decode(String.valueOf(studentJson.get("studentDetailsJson")), "UTF-8"))).get(0);
      String attendanceJson = URLDecoder.decode(String.valueOf(studentJson.get("attendanceJson")), "UTF-8");
      student = new Student();
      student.studentRollNumber = String.valueOf(studentJson.get("studentRollNumber"));
      student.instituteId = String.valueOf(studentJson.get("instituteId"));
      student.studentId = String.valueOf(studentJson.get("studentId"));
      student.registrationId = String.valueOf(studentJson.get("registrationId"));
      student.lastRefreshed = String.valueOf(studentJson.get("lastRefreshed"));
      student.sectionCode = String.valueOf(studentDetailsJson.get("sectioncode"));
      student.name = String.valueOf(studentDetailsJson.get("name"));
      student.programCode = String.valueOf(studentDetailsJson.get("programcode"));
      student.academicYear = String.valueOf(studentDetailsJson.get("academicyear"));
      student.attendance = Course.parseJson(attendanceJson);
    } catch (Exception e) {
      student = null;
    }
    return student;
  }

}
