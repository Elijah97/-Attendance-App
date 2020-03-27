package app.ellie.assignment;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.ellie.assignment.MainActivity.fragmentManager;


public class SaveFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static final int REQUEST_CODE = 13;
    public static final int RESULT_CODE = 15;
    public static String SCAN_RESULT = "";
    private final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    DatabaseHelper mydb;
    EditText sName, password, email, id;
    private Button button, save, viewBtn, deleteBtn, scanbtn;
    private Spinner spinner1, spinner2;
    private DatabaseHelper db;
    private boolean isValid = true;
    private boolean disabled = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_save, container, false);


        mydb = new DatabaseHelper(getContext());
        button = view.findViewById(R.id.save);
        viewBtn = view.findViewById(R.id.readBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        scanbtn = view.findViewById(R.id.scanbtn);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity().getApplicationContext(), QrScannerActivity.class), REQUEST_CODE);
            }
        });
        ReadData();

        sName = view.findViewById(R.id.name);
        id = view.findViewById(R.id.id);
        password = view.findViewById(R.id.password);
        email = view.findViewById(R.id.email);
        db = new DatabaseHelper(getContext());
        disableViews();

        DeleteData();


        Spinner session = view.findViewById(R.id.session_spinner);
        session.setOnItemSelectedListener(this);
        List<String> sessions = new ArrayList<String>();
        sessions.add("Session 1");
        sessions.add("Session 2");
        sessions.add("Session 3");

        ArrayAdapter<String> adapterSession = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, sessions);
        adapterSession.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        session.setAdapter(adapterSession);

        Spinner dept = view.findViewById(R.id.dept_spinner);
        dept.setOnItemSelectedListener(this);
        List<String> deptList = new ArrayList<String>();
        deptList.add("Department 1");
        deptList.add("Department 2");
        deptList.add("Department 3");

        ArrayAdapter<String> deptAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, deptList);
        deptAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dept.setAdapter(deptAdapter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFieldsAndSave();
            }
        });
        return view;
    }


    private void validateFieldsAndSave() {
        final String name = sName.getText().toString();
        final String password = this.password.getText().toString();
        final String email = this.email.getText().toString();
        final String id = this.id.getText().toString();

        if (id.trim().isEmpty()) {
            this.id.setError("Please add the student id");
            isValid = false;
        }
        if (name.trim().isEmpty()) {
            sName.setError("Name should not be empty");
            isValid = false;
        }
        if (!email.trim().matches(EMAIL_PATTERN) ||
                email.trim().isEmpty()) {
            this.email.setError("Invalid email");
            isValid = false;
        }
        if (password.trim().isEmpty()) {
            this.password.setError("Invalid password");
            isValid = false;
        }

        if (!isValid) return;
        if (db.updateData(this.id.getText().toString(), name, password, email) != -1) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            SuccessFragment fragment = new SuccessFragment();
            final Bundle args = new Bundle();
            args.putString(SuccessFragment.NAME_PARAM, name);
            args.putString(SuccessFragment.EMAIL_PARAM, email);
            args.putString(SuccessFragment.PASSWORD_PARAM, password);
            fragment.setArguments(args);
            ft.replace(R.id.FragmentContainer, fragment, null)
                    .addToBackStack(null)
                    .commit();
            Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
            cleanViews();
            disabled = true;
            disableViews();
        } else {
            Toast.makeText(getContext(), "they was an issue", Toast.LENGTH_SHORT).show();
            return;
        }


    }

    private void cleanViews() {
        sName.setText("");
        id.setText("");
        password.setText("");
        email.setText("");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void ReadData() {
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor results = mydb.ReadData();
                if (results.getCount() == 0) {
                    showMessage("Error", "Nothing to show");
                }
                StringBuffer sb = new StringBuffer();
                while (results.moveToNext()) {
                    sb.append("ReadNo : " + results.getString(results.getColumnIndex(DatabaseHelper.COL0)) + "\n");
                    sb.append("Student id: " + results.getString(results.getColumnIndex(DatabaseHelper.COL4)) + "\n");
                    sb.append("Name : " + results.getString(results.getColumnIndex(DatabaseHelper.COL1)) + "\n");
                    sb.append("Password : " + results.getString(results.getColumnIndex(DatabaseHelper.COL2)) + "\n");
                    sb.append("Email : " + results.getString(results.getColumnIndex(DatabaseHelper.COL3)) + "\n\n");
                }
                showMessage("Students Record", sb.toString());
            }
        });
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder((getActivity()));
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    public void DeleteData() {
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor results = mydb.deleteRow(id.getText().toString());
                Toast.makeText(getContext(), "Data delete successfully", Toast.LENGTH_SHORT).show();
                cleanViews();
                disabled = true;
                disableViews();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            String scanResult = data.getStringExtra(SCAN_RESULT);
            id.setText(scanResult);
            getStudentInfo(scanResult);
        }
    }

    private void getStudentInfo(String scanResult) {
        final Map<String, String> student = db.getStudentById(scanResult);
        if (student != null) {
            sName.setText(student.get(DatabaseHelper.COL1));
            password.setText(student.get(DatabaseHelper.COL2));
            email.setText(student.get(DatabaseHelper.COL3));
        } else {
            Toast.makeText(getContext(), "Student does not exists please create him/her", Toast.LENGTH_SHORT).show();
        }
        disabled = false;
        disableViews();
    }

    private void disableViews() {
        button.setEnabled(!disabled);
        deleteBtn.setEnabled(!disabled);
        sName.setEnabled(!disabled);
        id.setEnabled(!disabled);
        password.setEnabled(!disabled);
        email.setEnabled(!disabled);
    }

}
