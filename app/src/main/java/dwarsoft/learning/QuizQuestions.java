package dwarsoft.learning;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import static dwarsoft.learning.QuizLoading.CATPREF;

public class QuizQuestions extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    private SharedPreferences categoriesPref;
    private SharedPreferences.Editor editor;

    int last = 0;
    Button btQuiz;

    int answered;
    int correct,wrong;

    String correctanswer,explanation;
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
        correct = catPref.getInt("correct", 0);
        wrong = catPref.getInt("wrong", 0);
        final int answered = catPref.getInt("answered", 0);

        if (answered<size)
        {
            //fetch from sharedpref. set to display
            tvQuizQuestions.setText(catPref.getString("question"+answered,null));
            rbQuizOption1.setText(catPref.getString("option1"+answered,null));
            rbQuizOption2.setText(catPref.getString("option2"+answered,null));
            rbQuizOption3.setText(catPref.getString("option3"+answered,null));
            rbQuizOption4.setText(catPref.getString("option4"+answered,null));
            correctanswer = catPref.getString("correct"+answered,null);
            explanation = catPref.getString("explanation"+answered,null);
            
        }
        if (answered==size-1)
        {
            last = 1;
        }

        //set the values here


        btQuiz = findViewById(R.id.btQuiz);

        btQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int radioButtonID = rgQuizOptions.getCheckedRadioButtonId();
                View radioButton = rgQuizOptions.findViewById(radioButtonID);
                int idx = rgQuizOptions.indexOfChild(radioButton);
                RadioButton r = (RadioButton)  rgQuizOptions.getChildAt(idx);
                String selectedtext = r.getText().toString();

                    categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                    editor = categoriesPref.edit();
                    editor.putInt("answered",answered+1);
                    editor.commit();

                    if (selectedtext.equals(correctanswer))
                    {

                        //credit points to the user
                        correct = correct + 1;
                        editor.putInt("correct",correct);
                        editor.commit();

                        new AlertDialog.Builder(QuizQuestions.this)
                                .setMessage("Correct answer! "+explanation+" .")
                                .setCancelable(false)
                                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                .show();
                    }
                    else
                    {
                        wrong = wrong + 1;
                        editor.putInt("wrong",wrong);
                        editor.commit();

                        new AlertDialog.Builder(QuizQuestions.this)
                                .setMessage("Wrong answer! "+explanation+" .")
                                .setCancelable(false)
                                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                .show();
                    }
                if (last==0)
                {

                }
                else
                {
                    new AlertDialog.Builder(QuizQuestions.this)
                            .setMessage("You have finished the quiz. Results: Right Answers: "+String.valueOf(correct)+", Wrong answer: "+String.valueOf(wrong))
                            .setCancelable(false)
                            .setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(QuizQuestions.this, "Finished", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent (QuizQuestions.this, HomeScreen.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                    //last ques answered.
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
