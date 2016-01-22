package app.abhijit.iter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.LinkedHashMap;

public class Course {

  public String name;
  public String subjectCode;
  public String totalClasses;
  public String totalPresentClass;
  public String totalAbsentClass;
  public String totalLeaveTaken;
  public String percentPresentClass;
  public LinkedHashMap<String, String> bunk = new LinkedHashMap<String, String>();
  public LinkedHashMap<String, String> need = new LinkedHashMap<String, String>();

  public static Course[] parseJson(String json) {
    Course[] attendance;
    try {
      JSONParser parser = new JSONParser();
      JSONArray attendanceJson = (JSONArray) parser.parse(json);
      int N = attendanceJson.size();
      if (N == 0) {
        throw new RuntimeException();
      }
      attendance = new Course[N];
      for (int i = 0; i < N; i++) {
        JSONObject attendanceDetailsJson = (JSONObject) attendanceJson.get(i);

        Course course = new Course();
        course.name = String.valueOf(attendanceDetailsJson.get("subject"));
        course.subjectCode = String.valueOf(attendanceDetailsJson.get("subjectcode"));
        course.totalClasses = String.valueOf(attendanceDetailsJson.get("totalclasses"));
        course.totalPresentClass = String.valueOf(attendanceDetailsJson.get("totalpresentclass"));
        course.totalLeaveTaken = String.valueOf(attendanceDetailsJson.get("totalleavetaken"));

        int totalClasses = Integer.parseInt(course.totalClasses);
        int totalPresentClass = Integer.parseInt(course.totalPresentClass);
        int totalAbsentClass = totalClasses - totalPresentClass;
        int totalLeaveTaken = Integer.parseInt(course.totalLeaveTaken);
        float percentPresentClass = (totalPresentClass / (float) totalClasses) * 100;

        course.totalAbsentClass = Integer.toString(totalAbsentClass);
        course.percentPresentClass = String.format("%.2f", percentPresentClass) + "%";

        int p = (int) percentPresentClass;

        int nextP = (p + 4) / 5 * 5;
        if (nextP == p) {
          nextP = p + 5;
        }

        for (int n = 75; n < p; n += 5) {
          int daysBunk = (int) ((100 * totalPresentClass / (float) n) - (float) totalClasses);
          if (daysBunk > 0) {
            course.bunk.put(Integer.toString(daysBunk), Integer.toString(n) + "%");
          }
        }

        for (int n = nextP; n <= 95; n += 5) {
          int daysNeed = (int) ((n * totalClasses - 100 * totalPresentClass) / (float) (100 - n));
          if (daysNeed > 0 && (daysNeed + totalClasses <= 50)) {
            course.need.put(Integer.toString(daysNeed), Integer.toString(n) + "%");
          }
        }

        attendance[i] = course;
      }
    } catch (Exception e) {
      attendance = null;
    }
    return attendance;
  }

}
