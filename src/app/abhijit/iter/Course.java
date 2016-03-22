package app.abhijit.iter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Course {

  // TODO: Refactor: Store stuff as numbers?
  private String totalpresentclass;
  private String totalclasses;
  private String subject;
  private String subjectcode;
  private String totalleavetaken;

  // TODO: Add a constructor

  public String getName() {
    return this.subject;
  }

  public String getSubjectCode() {
    return this.subjectcode;
  }

  public String getTotalClasses() {
    return this.totalclasses;
  }

  public String getTotalPresentClasses() {
    return this.totalpresentclass;
  }

  public String getTotalAbsentClasses() {
    // TODO: Refactor: Remove repeated code
    try {
      int totalClasses = Integer.parseInt(this.totalclasses);
      int totalPresentClass = Integer.parseInt(this.totalpresentclass);
      int totalAbsentClass = totalClasses - totalPresentClass;
      return Integer.toString(totalAbsentClass);
    } catch (Exception e) {
      return null;
    }
  }

  public String getTotalLeaveTaken() {
    return this.totalleavetaken;
  }

  public String getPercentPresent() {
    // TODO: Refactor: Remove repeated code
    try {
      int totalClasses = Integer.parseInt(this.totalclasses);
      int totalPresentClass = Integer.parseInt(this.totalpresentclass);
      float percentPresent = (totalPresentClass / (float) totalClasses) * 100;
      return String.format("%.2f", percentPresent) + "%";
    } catch (Exception e) {
      return null;
    }
  }

  public Map<String, String> getClassBunkStats() {
    // TODO: Refactor: Remove repeated code
    try {
      LinkedHashMap<String, String> bunk = new LinkedHashMap<String, String>();
      int totalClasses = Integer.parseInt(this.totalclasses);
      int totalPresentClass = Integer.parseInt(this.totalpresentclass);
      float percentPresent = (totalPresentClass / (float) totalClasses) * 100;
      int p = (int) percentPresent;
      for (int n = 75; n < p; n += 5) {
        int daysBunk = (int) ((100 * totalPresentClass / (float) n) - (float) totalClasses);
        if (daysBunk > 0) {
          bunk.put(Integer.toString(daysBunk), Integer.toString(n) + "%");
        }
      }
      return bunk;
    } catch (Exception e) {
      return null;
    }
  }

  public Map<String, String> getClassNeedStats() {
    // TODO: Refactor: Remove repeated code
    try {
      LinkedHashMap<String, String> need = new LinkedHashMap<String, String>();
      int totalClasses = Integer.parseInt(this.totalclasses);
      int totalPresentClass = Integer.parseInt(this.totalpresentclass);
      float percentPresent = (totalPresentClass / (float) totalClasses) * 100;
      int p = (int) percentPresent;
      int nextP = (p + 4) / 5 * 5;
      if (nextP == p) {
        nextP = p + 5;
      }
      for (int n = nextP; n <= 95; n += 5) {
        int daysNeed = (int) ((n * totalClasses - 100 * totalPresentClass) / (float) (100 - n));
        if (daysNeed > 0 && (daysNeed + totalClasses <= 50)) {
          need.put(Integer.toString(daysNeed), Integer.toString(n) + "%");
        }
      }
      return need;
    } catch (Exception e) {
      return null;
    }
  }

}
