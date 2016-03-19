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
      int totalclasses = Integer.parseInt(this.totalclasses);
      int totalpresentclass = Integer.parseInt(this.totalpresentclass);
      int totalabsentclass = totalclasses - totalpresentclass;
      return Integer.toString(totalabsentclass);
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
      int totalclasses = Integer.parseInt(this.totalclasses);
      int totalpresentclass = Integer.parseInt(this.totalpresentclass);
      float percentpresent = (totalpresentclass / (float) totalclasses) * 100;
      return String.format("%.2f", percentpresent) + "%";
    } catch (Exception e) {
      return null;
    }
  }

  public Map<String, String> getClassBunkStats() {
    // TODO: Refactor: Remove repeated code
    try {
      int totalclasses = Integer.parseInt(this.totalclasses);
      int totalpresentclass = Integer.parseInt(this.totalpresentclass);
      float percentpresent = (totalpresentclass / (float) totalclasses) * 100;

      LinkedHashMap<String, String> bunk = new LinkedHashMap<String, String>();
      int p = (int) percentpresent;

      for (int n = 75; n < p; n += 5) {
        int daysbunk = (int) ((100 * totalpresentclass / (float) n) - (float) totalclasses);
        if (daysbunk > 0) {
          bunk.put(Integer.toString(daysbunk), Integer.toString(n) + "%");
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
      int totalclasses = Integer.parseInt(this.totalclasses);
      int totalpresentclass = Integer.parseInt(this.totalpresentclass);
      float percentpresent = (totalpresentclass / (float) totalclasses) * 100;

      LinkedHashMap<String, String> need = new LinkedHashMap<String, String>();
      int p = (int) percentpresent;
      int nextp = (p + 4) / 5 * 5;
      if (nextp == p) {
        nextp = p + 5;
      }

      for (int n = nextp; n <= 95; n += 5) {
        int daysneed = (int) ((n * totalclasses - 100 * totalpresentclass) / (float) (100 - n));
        if (daysneed > 0 && (daysneed + totalclasses <= 50)) {
          need.put(Integer.toString(daysneed), Integer.toString(n) + "%");
        }
      }

      return need;
    } catch (Exception e) {
      return null;
    }
  }

}
