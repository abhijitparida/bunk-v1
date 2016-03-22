package app.abhijit.iter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {

  private Context context = this;
  private JsonParser jsonParser;
  private int requestId;
  private Db db;
  private Api api;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    this.jsonParser = new JsonParser();
    this.requestId = 0;
    this.db = new Db(this.context);
    this.api = new Api(this.context);
    if (this.db.getCurrentKey() == null) {
      showHint();
    } else {
      addRegistrationNumber(this.db.getCurrentKey());
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main_activity_actions, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_add:
        if (this.db.getCurrentKey() == null) {
          promptRegistrationNumber();
        } else {
          promptRegistrationNumberSelection();
        }
        return true;
      case R.id.action_about:
        startActivity(new Intent(this.context, AboutActivity.class));
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void promptRegistrationNumber() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
    alertDialog.setTitle("Add");
    final EditText editText = new EditText(this.context);
    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    alertDialog.setView(editText);
    alertDialog.setMessage("Registration Number");
    alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String registrationNumber = editText.getText().toString().trim();
        if (!registrationNumber.equals("")) {
          addRegistrationNumber(registrationNumber);
        }
      }
    });
    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    alertDialog.create().show();
  }

  private void promptRegistrationNumberSelection() {
    String[] dbKeys = this.db.getAllKeys();
    final String[] items = new String[dbKeys.length + 2];
    items[0] = "Add Registration Number";
    items[1] = "Clear All";
    for (int i = 0; i < dbKeys.length; i++) {
      items[i + 2] = dbKeys[i];
    }
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
    alertDialog.setTitle("Select");
    alertDialog.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case 0:
            promptRegistrationNumber();
            break;
          case 1:
            db.clearAllValues();
            requestId = -1;
            clearUi();
            showHint();
            Toast.makeText(context, "Registration numbers cleared successfully", Toast.LENGTH_SHORT).show();
            break;
          default:
            addRegistrationNumber(items[which]);
        }
      }
    });
    alertDialog.create().show();
  }

  private void promptUpdate(final String updateUrl, String releaseNotes) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
    alertDialog.setTitle("Update Available");
    alertDialog.setMessage(releaseNotes);
    alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)));
      }
    });
    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    alertDialog.create().show();
  }

  private void promptError(String errorMessage) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
    alertDialog.setTitle("Error");
    alertDialog.setMessage(errorMessage);
    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.cancel();
      }
    });
    alertDialog.create().show();
  }

  private void showProgressBar() {
    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
    if (this.db.getValue(this.db.getCurrentKey()) == null) {
      progressBar = (ProgressBar) findViewById(R.id.progressbar_spinner);
    }
    progressBar.setVisibility(View.VISIBLE);
  }

  private void showHint() {
    TextView textViewHint = (TextView) findViewById(R.id.textview_hint);
    textViewHint.setVisibility(View.VISIBLE);
    int lineHeight = (int) (textViewHint.getLineHeight() * 1.5);
    // TODO: getResources().getDrawable(int) is deprecated
    Drawable hintIcon = this.context.getResources().getDrawable(R.drawable.ic_hint_add_person);
    hintIcon.setBounds(0, 0, lineHeight, lineHeight);
    SpannableStringBuilder builder = new SpannableStringBuilder();
    builder.append("Use the ").append(" ");
    builder.setSpan(new ImageSpan(hintIcon), builder.length() - 1, builder.length(), 0);
    builder.append(" button to add or select your registration number");
    textViewHint.setText(builder);
  }

  private void showError(String errorMessage) {
    TextView textViewError = (TextView) findViewById(R.id.textview_error);
    textViewError.setVisibility(View.VISIBLE);
    textViewError.setText(errorMessage);
  }

  private void renderProfile(Student student) {
    if (student == null) {
      return;
    }
    TextView textViewStudentName = (TextView) findViewById(R.id.textview_student_name);
    TextView textViewStudentRollNumber = (TextView) findViewById(R.id.textview_student_roll_number);
    TextView textViewStudentExtraInfo = (TextView) findViewById(R.id.textview_student_extra_info);
    textViewStudentName.setText(student.getName());
    textViewStudentRollNumber.setText(student.getRollNumber());
    textViewStudentExtraInfo.setText(student.getSectionCode() + " " + student.getProgramCode()
        + "-" + student.getAcademicYear());
    Course[] attendance = student.getAttendance();
    if (attendance == null || attendance.length == 0) {
      showError("ATTENDANCE DATA NOT AVAILABLE");
      return;
    }
    ListView listViewCourses = (ListView) findViewById(R.id.listview_courses);
    listViewCourses.setAdapter(new CoursesAdapter(this.context, attendance));
  }

  private void clearUi() {
    TextView textViewHint = (TextView) findViewById(R.id.textview_hint);
    TextView textViewError = (TextView) findViewById(R.id.textview_error);
    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
    ProgressBar progressbarSpinner = (ProgressBar) findViewById(R.id.progressbar_spinner);
    TextView textViewStudentName = (TextView) findViewById(R.id.textview_student_name);
    TextView textViewStudentRollNumber = (TextView) findViewById(R.id.textview_student_roll_number);
    TextView textViewStudentExtraInfo = (TextView) findViewById(R.id.textview_student_extra_info);
    ListView listViewCourses = (ListView) findViewById(R.id.listview_courses);
    textViewHint.setVisibility(View.GONE);
    textViewError.setVisibility(View.GONE);
    progressBar.setVisibility(View.INVISIBLE);
    progressbarSpinner.setVisibility(View.GONE);
    textViewStudentName.setText("");
    textViewStudentRollNumber.setText("");
    textViewStudentExtraInfo.setText("");
    listViewCourses.setAdapter(new CoursesAdapter(this.context, new Course[0]));
  }

  private void addRegistrationNumber(String registrationNumber) {
    if (registrationNumber == null) {
      return;
    }
    this.db.setCurrentKey(registrationNumber);
    String studentJson = this.db.getValue(registrationNumber);
    if (studentJson == null) {
      this.db.setValue(registrationNumber, null);
    }
    clearUi();
    try {
      Student student = new Gson().fromJson(studentJson, Student.class);
      renderProfile(student);
    } catch (Exception e) {
      this.db.setValue(registrationNumber, null);
    }
    new FetchData().execute(Integer.toString(++requestId));
  }

  private void handleApiResponse(Map<String, String> apiResponse) {
    if (apiResponse == null || !apiResponse.get("requestid").equals(Integer.toString(requestId))) {
      return;
    }
    clearUi();
    String error = apiResponse.get("error");
    if (error != null) {
      promptError(error);
    }
    try {
      JsonObject updateJson = jsonParser.parse(apiResponse.get("updatejson")).getAsJsonObject();
      if (updateJson.get("updateavailable").getAsString().equals("yes")) {
        promptUpdate(updateJson.get("updateurl").getAsString(), updateJson.get("releasenotes").getAsString());
      }
    } catch (Exception e) { }
    if (error == null) {
      try {
        JsonObject studentJson = jsonParser.parse(apiResponse.get("studentjson")).getAsJsonArray().get(0).getAsJsonObject();
        JsonArray attendanceJson = jsonParser.parse(apiResponse.get("attendancejson")).getAsJsonArray();
        studentJson.add("attendance", attendanceJson);
        Student student = new Gson().fromJson(studentJson, Student.class);
        db.setValue(apiResponse.get("rollnumber"), studentJson.toString());
        renderProfile(student);
      } catch (Exception e) {
        db.setValue(apiResponse.get("rollnumber"), null);
        promptError("Bad API response");
        showHint();
      }
    } else {
      showHint();
    }
  }

  private class FetchData extends AsyncTask<String, Void, Map<String, String>> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      showProgressBar();
    }

    @Override
    protected Map<String, String> doInBackground(String... params) {
      String requestId = params[0];
      return api.makeApiRequest(db.getCurrentKey(), requestId);
    }

    @Override
    protected void onPostExecute(Map<String, String> apiResponse) {
      super.onPostExecute(apiResponse);
      handleApiResponse(apiResponse);
    }

  }

  private class CoursesAdapter extends BaseAdapter {

    private Context context;
    private Course[] attendance;

    CoursesAdapter(Context context, Course[] attendance) {
      this.context = context;
      this.attendance = attendance;
    }

    @Override
    public int getCount() {
      return this.attendance.length;
    }

    @Override
    public Course getItem(int id) {
      return this.attendance[id];
    }

    @Override
    public long getItemId(int id) {
      return id;
    }

    @Override
    public View getView(int id, View view, ViewGroup viewGroup) {
      Course course = getItem(id);
      LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = layoutInflater.inflate(R.layout.course, viewGroup, false);
      if (id % 2 != 0) {
        // TODO: getResources().getColor(int) is deprecated
        view.setBackgroundColor(this.context.getResources().getColor(R.color.white));
      }
      TextView textViewCourseName = (TextView) view.findViewById(R.id.textview_course_name);
      TextView textViewCourseAttendance = (TextView) view.findViewById(R.id.textview_course_attendance);
      TextView textViewCourseExtraInfo = (TextView) view.findViewById(R.id.textview_course_extra_info);
      textViewCourseName.setText(course.getName() + "\n" + course.getSubjectCode());
      // TODO: show leave taken if not zero
      // TODO: make things more readable
      textViewCourseAttendance.setText("Present: " + course.getTotalPresentClasses() + "/" + course.getTotalClasses() + " [" + course.getPercentPresent() + "]"
          + "\nAbsent: " + course.getTotalAbsentClasses() + (course.getTotalAbsentClasses().equals("1") ? " class" : " classes"));
      String courseExtraInfo = "";
      for (Map.Entry<String, String> bunk : course.getClassBunkStats().entrySet()) {
        String days = bunk.getKey();
        String attendance = bunk.getValue();
        courseExtraInfo += "Bunk " + days + (course.getTotalAbsentClasses().equals("0") ? "" : " more") + (days.equals("1") ? " class" : " classes") + " for " + attendance + "\n";
      }
      for (Map.Entry<String, String> need : course.getClassNeedStats().entrySet()) {
        String days = need.getKey();
        String attendance = need.getValue();
        courseExtraInfo += "Need " + days + " more" + (days.equals("1") ? " class" : " classes") + " for " + attendance + "\n";
      }
      if (!courseExtraInfo.isEmpty()) {
        textViewCourseExtraInfo.setVisibility(View.VISIBLE);
        textViewCourseExtraInfo.setText(courseExtraInfo.substring(0, courseExtraInfo.length() - 1));
      } else {
        textViewCourseExtraInfo.setVisibility(View.GONE);
      }
      return view;
    }

  }

}
