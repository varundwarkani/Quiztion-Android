package dwarsoft.learning;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuizQuestions extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    private SharedPreferences categoriesPref;
    private SharedPreferences.Editor editor;
    public static final String CATPREF = "catpref";

    int last = 0;
    Button btQuiz;

    int answered;
    String correct = "0",wrong = "0";

    TextView tvQuizQuestions;
    RadioGroup rgQuizOptions;
    RadioButton rbQuizOption1,rbQuizOption2,rbQuizOption3,rbQuizOption4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions);

        tvQuizQuestions = findViewById(R.id.tvQuizQuestions);
        rgQuizOptions = findViewById(R.id.rgQuizOptions);
        rbQuizOption1 = findViewById(R.id.rbQuizOption1);
        rbQuizOption2 = findViewById(R.id.rbQuizOption2);
        rbQuizOption3 = findViewById(R.id.rbQuizOption3);
        rbQuizOption4 = findViewById(R.id.rbQuizOption4);

        SharedPreferences catPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
        int size = catPref.getInt("count", 0);
        final int answered = catPref.getInt("answered", 0);

        if (answered<size)
        {
            //fetch from sharedpref. set to display
            tvQuizQuestions.setText(catPref.getString("question"+answered,null));
            rbQuizOption1.setText(catPref.getString("option1"+answered,null));
            rbQuizOption2.setText(catPref.getString("option2"+answered,null));
            rbQuizOption3.setText(catPref.getString("option3"+answered,null));
            rbQuizOption4.setText(catPref.getString("option4"+answered,null));
        }
        if (answered==size)
        {
            last = 1;
        }

        //set the values here


        btQuiz = findViewById(R.id.btQuiz);

        btQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (last==0)
                {
                    categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                    editor = categoriesPref.edit();
                    editor.putInt("answered",answered+1);
                    editor.commit();

                    finish();
                    startActivity(getIntent());
                }
                else {
                    Toast.makeText(QuizQuestions.this, "Last question answered", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    //get the questions and other details. setthem to shared pref
                } else {
                    Toast.makeText(QuizQuestions.this, "Session lost...", Toast.LENGTH_SHORT).show();
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
