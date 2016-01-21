package app.abhijit.iter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class MainActivity extends Activity {

  private Context context = this;
  private Db db;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    db = new Db(context);
    if (db.size() == 0) {
      promptRegistrationNumber();
    } else {
      addRegistrationNumber(db.current());
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
      case R.id.action_refresh:
        if (db.size() == 0) {
          promptRegistrationNumber();
        } else {
          addRegistrationNumber(db.current());
        }
        return true;
      case R.id.action_add:
        if (db.size() == 0) {
          promptRegistrationNumber();
        } else {
          promptRegistrationNumberSelection();
        }
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void promptRegistrationNumber() {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
    alertDialog.setTitle("ITER");
    final EditText editText = new EditText(context);
    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    alertDialog.setView(editText);
    alertDialog.setMessage("Registration Number");
    alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String registrationNumber = editText.getText().toString().trim();
        if (registrationNumber.equals("")) {
          promptRegistrationNumber();
          return;
        }
        addRegistrationNumber(registrationNumber);
      }
    });
    alertDialog.create().show();
  }

  private void promptRegistrationNumberSelection() {
    final String[] items = new String[db.size() + 2];
    items[0] = "Add Registration Number";
    items[1] = "Clear All";
    for (int i = 0; i < db.size(); i++) {
      items[i + 2] = db.get(i);
    }
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
    alertDialog.setTitle("ITER");
    alertDialog.setItems(items, new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        switch (which) {
          case 0:
            promptRegistrationNumber();
            break;
          case 1:
            db.clear();
            renderProfile(null);
            Toast.makeText(context, "Registration numbers cleared successfully", Toast.LENGTH_SHORT).show();
            promptRegistrationNumber();
            break;
          default:
            addRegistrationNumber(items[which]);
        }
      }
    });
    alertDialog.create().show();
  }

  private void promptUpdate(String updateMessage) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
    alertDialog.setTitle("Update Available");
    alertDialog.setMessage(updateMessage);
    alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://ninjadoge24.github.io")));
      }
    });
    alertDialog.create().show();
  }

  private void promptError(String errorMessage) {
    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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
    if (db.getValue(db.current()) == null) {
      progressBar = (ProgressBar) findViewById(R.id.progressbar_spinner);
    }
    progressBar.setVisibility(View.VISIBLE);
  }

  private void hideProgressBar() {
    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
    ProgressBar progressBarSpinner = (ProgressBar) findViewById(R.id.progressbar_spinner);
    progressBar.setVisibility(View.INVISIBLE);
    progressBarSpinner.setVisibility(View.GONE);
  }

  private void addRegistrationNumber(String registrationNumber) {
    String studentJson = db.getValue(registrationNumber);
    Student student = Student.parseJson(studentJson);
    renderProfile(student);
    db.add(registrationNumber);
    new fetchData().execute();
  }

  private void handleApiResponse(ApiResponse apiResponse) {
    if (apiResponse.updateAvailable) {
      promptUpdate("A new version of this app is available!");
    }
    if (apiResponse.error) {
      promptError(apiResponse.errorMessage);
      return;
    }
    db.addValue(apiResponse.student.studentRollNumber, apiResponse.studentJson);
    renderProfile(apiResponse.student);
  }

  private void renderProfile(Student student) {
    TextView studentName = (TextView) findViewById(R.id.textview_student_name);
    TextView studentRollNumber = (TextView) findViewById(R.id.textview_student_roll_number);
    TextView studentExtraInfo = (TextView) findViewById(R.id.textview_student_extra_info);
    TextView lastRefreshed = (TextView) findViewById(R.id.textview_last_refreshed);
    TextView info = (TextView) findViewById(R.id.textview_info);
    ListView courses = (ListView) findViewById(R.id.listview_courses);
    studentName.setText("");
    studentRollNumber.setText("");
    studentExtraInfo.setText("");
    lastRefreshed.setVisibility(View.GONE);
    info.setVisibility(View.GONE);
    courses.setAdapter(new CoursesAdapter(context, new Course[0]));
    if (student == null) {
      return;
    }
    studentName.setText(student.name);
    studentRollNumber.setText(student.studentRollNumber);
    studentExtraInfo.setText(student.sectionCode + " " + student.programCode + "-" + student.academicYear);
    String today = new SimpleDateFormat("MMMM dd, yyyy").format(new Date());
    if (!today.equals(student.lastRefreshed)) {
      lastRefreshed.setVisibility(View.VISIBLE);
      lastRefreshed.setText("Last refreshed: " + student.lastRefreshed);
    }
    if (student.attendance == null) {
      info.setVisibility(View.VISIBLE);
      info.setText("NO ATTENDANCE DATA AVAILABLE");
      return;
    }
    courses.setAdapter(new CoursesAdapter(context, student.attendance));
  }

  private class fetchData extends AsyncTask<Void, Void, ApiResponse> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      TextView info = (TextView) findViewById(R.id.textview_info);
      showProgressBar();
    }

    @Override
    protected ApiResponse doInBackground(Void... params) {
      return Api.getApiResponse(db.current());
    }

    @Override
    protected void onPostExecute(ApiResponse result) {
      super.onPostExecute(result);
      hideProgressBar();
      handleApiResponse(result);
    }

  }

  private class CoursesAdapter extends BaseAdapter {

    private Context context;
    private Course[] courses;

    CoursesAdapter(Context context, Course[] courses) {
      this.context = context;
      this.courses = courses;
    }

    @Override
    public int getCount() {
      return courses.length;
    }

    @Override
    public Course getItem(int id) {
      return courses[id];
    }

    @Override
    public long getItemId(int id) {
      return id;
    }

    @Override
    public View getView(int id, View view, ViewGroup viewGroup) {
      Course course = getItem(id);
      LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      view = inflater.inflate(R.layout.course, viewGroup, false);
      if (id % 2 != 0) {
        view.setBackgroundColor(context.getResources().getColor(R.color.white));
      }
      TextView courseName = (TextView) view.findViewById(R.id.textview_course_name);
      TextView courseAttendance = (TextView) view.findViewById(R.id.textview_course_attendance);
      TextView courseExtraInfo = (TextView) view.findViewById(R.id.textview_course_extra_info);
      courseName.setText(course.name + "\n" + course.subjectCode);
      courseAttendance.setText("Present: " + course.totalPresentClass + "/" + course.totalClasses + " [" + course.percentPresentClass + "]\nAbsent: " + course.totalAbsentClass + " Leave Taken: " + course.totalLeaveTaken);
      String extraInfo = "";
      for (Map.Entry<String, String> bunk : course.bunk.entrySet()) {
        String days = bunk.getKey();
        String attendance = bunk.getValue();
        extraInfo += "Bunk " + days + " more" + (days.equals("1") ? " class" : " classes") + " for " + attendance + "\n";
      }
      for (Map.Entry<String, String> need : course.need.entrySet()) {
        String days = need.getKey();
        String attendance = need.getValue();
        extraInfo += "Need " + days + " more" + (days.equals("1") ? " class" : " classes") + " for " + attendance + "\n";
      }
      courseExtraInfo.setText(extraInfo.substring(0, extraInfo.length() - 1));
      return view;
    }

  }

}
