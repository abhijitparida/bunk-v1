package app.abhijit.iter;

public class Student {

  private String sectioncode;
  private String name;
  private String programcode;
  private String academicyear;
  private String enrollmentno;
  private Course[] attendance;

  // TODO: Add a constructor

  public String getName() {
    return this.name;
  }

  public String getRollNumber() {
    return this.enrollmentno;
  }

  public String getSectionCode() {
    return this.sectioncode;
  }

  public String getProgramCode() {
    return this.programcode;
  }

  public String getAcademicYear() {
    return this.academicyear;
  }

  public Course[] getAttendance() {
    return this.attendance;
  }

}
