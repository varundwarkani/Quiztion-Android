package dwarsoft.learning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static dwarsoft.learning.QuizLoading.CATPREF;

public class list extends AppCompatActivity {

    private Spinner spCollegename,spDept,spYear,spRoom;
    private ArrayList<String> spinner_college = new ArrayList<>();
    private ArrayList<String> spinner_dept = new ArrayList<>();
    private ArrayList<String> spinner_year = new ArrayList<>();
    private ArrayList<String> spinner_room = new ArrayList<>();
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String collegename, deptname, year,room;

    Button btContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        spCollegename = findViewById(R.id.spinnerCollege);
        spDept = findViewById(R.id.spinnerDept);
        spYear = findViewById(R.id.spinnerYear);
        spRoom = findViewById(R.id.spinnerRoom);
        btContinue = findViewById(R.id.btContinue);

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //store db reference.
                SharedPreferences categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = categoriesPref.edit();
                editor.putString("quizref","colleges/"+collegename+"/"+deptname+"/"+year+"/"+room);
                editor.commit();
                Intent intent = new Intent (list.this, QuizSelect.class);
                startActivity(intent);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(list.this,R.layout.spinnerfield,spinner_college);
        spCollegename.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(list.this,R.layout.spinnerfield,spinner_dept);
        spDept.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(list.this,R.layout.spinnerfield,spinner_year);
        spYear.setAdapter(adapter2);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(list.this,R.layout.spinnerfield,spinner_room);
        spRoom.setAdapter(adapter3);

        spCollegename.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                spinner_dept.clear();

                Object item = adapterView.getItemAtPosition(i);
                collegename = (String) item;
                spCollegename.setSelection(i);

                //for college name, load the dept now
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("colleges/"+collegename);
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            spinner_dept.add(postSnapshot.getKey());
                        }
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(list.this,R.layout.spinnerfield,spinner_dept);
                        spDept.setAdapter(adapter1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spDept.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                spinner_year.clear();

                Object item = adapterView.getItemAtPosition(i);
                deptname = (String) item;
                spDept.setSelection(i);

                //for college name, load the dept now
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("colleges/"+collegename+"/"+deptname);
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            spinner_year.add(postSnapshot.getKey());
                        }
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(list.this,R.layout.spinnerfield,spinner_year);
                        spYear.setAdapter(adapter1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                spinner_room.clear();

                Object item = adapterView.getItemAtPosition(i);
                year = (String) item;
                spYear.setSelection(i);

                //for college name, load the dept now
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("colleges/"+collegename+"/"+deptname+"/"+year);
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            spinner_room.add(postSnapshot.getKey());
                        }
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(list.this,R.layout.spinnerfield,spinner_room);
                        spRoom.setAdapter(adapter1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        spRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Object item = adapterView.getItemAtPosition(i);
                room = (String) item;
                spRoom.setSelection(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("colleges");
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            spinner_college.clear();

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                spinner_college.add(postSnapshot.getKey());
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(list.this,R.layout.spinnerfield,spinner_college);
                            spCollegename.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}
