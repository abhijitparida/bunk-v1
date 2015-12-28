package app.abhijit.iter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.URLDecoder;

public class Student {

  public String sudentRollNumber;
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
    JSONParser parser = new JSONParser();
    JSONObject studentJson;
    JSONObject studentDetailsJson;
    String attendanceJson;
    try {
      studentJson = (JSONObject) parser.parse(json);
      studentDetailsJson = (JSONObject) ((JSONArray) parser.parse(URLDecoder.decode(String.valueOf(studentJson.get("studentDetailsJson")), "UTF-8"))).get(0);
      attendanceJson = URLDecoder.decode(String.valueOf(studentJson.get("attendanceJson")), "UTF-8");
    } catch (Exception e) {
      return null;
    }
    Student student = new Student();
    student.sudentRollNumber = String.valueOf(studentJson.get("sudentRollNumber"));
    student.instituteId = String.valueOf(studentJson.get("instituteId"));
    student.studentId = String.valueOf(studentJson.get("studentId"));
    student.registrationId = String.valueOf(studentJson.get("registrationId"));
    student.lastRefreshed = String.valueOf(studentJson.get("lastRefreshed"));
    student.sectionCode = String.valueOf(studentDetailsJson.get("sectioncode"));
    student.name = String.valueOf(studentDetailsJson.get("name"));
    student.programCode = String.valueOf(studentDetailsJson.get("programcode"));
    student.academicYear = String.valueOf(studentDetailsJson.get("academicyear"));
    if (student.sudentRollNumber == null
        || student.instituteId == null
        || student.studentId == null
        || student.registrationId == null
        || student.lastRefreshed == null
        || student.sectionCode == null
        || student.name == null
        || student.programCode == null
        || student.academicYear == null) {
      return null;
    }
    student.attendance = Course.parseJson(attendanceJson);
    return student;
  }

}
