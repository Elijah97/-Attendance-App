package app.ellie.assignment;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static app.ellie.assignment.MainActivity.fragmentManager;


public class SaveFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    DatabaseHelper mydb;
    EditText sName, password, email, deleteText, id;
    private Button button, save, viewBtn, deleteBtn;
    private Spinner spinner1, spinner2;
    private DatabaseHelper db;
    private boolean isValid = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_save, container, false);


        mydb = new DatabaseHelper(getContext());
        button = view.findViewById(R.id.save);
        viewBtn = view.findViewById(R.id.readBtn);
        deleteBtn = view.findViewById(R.id.deleteBtn);
        ReadData();

        sName = view.findViewById(R.id.name);
        id = view.findViewById(R.id.id);
        password = view.findViewById(R.id.password);
        email = view.findViewById(R.id.email);
        deleteText = view.findViewById(R.id.idNum);
        db = new DatabaseHelper(getContext());


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

        //SAVE IN THE DATABASE
        if (id.getText().toString().isEmpty()) {
            if (db.insertData(name, password, email) != -1) {
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
                Toast.makeText(getContext(), "saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "they was an issue", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Integer id = Integer.valueOf(this.id.getText().toString());

            db.updateData(id, name, password, email);
            Toast.makeText(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
        }


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
                    sb.append("ReadNo : " + results.getString(0) + "\n");
                    sb.append("Name : " + results.getString(1) + "\n");
                    sb.append("Password : " + results.getString(2) + "\n");
                    sb.append("Email : " + results.getString(3) + "\n\n");
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
                Cursor results = mydb.deleteRow(Integer.parseInt(deleteText.getText().toString()));
                Toast.makeText(getContext(), "Data delete successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
