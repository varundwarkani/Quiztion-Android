package dwarsoft.learning;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
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
    int i;
    Button btQuiz;

    int answered;
    int correct,wrong;

    String correctanswer,explanation;
    TextView tvQuizQuestions;
    RadioGroup rgQuizOptions;
    RadioButton rbQuizOption1,rbQuizOption2,rbQuizOption3,rbQuizOption4;

    Button btoption1,btoption2,btoption3,btoption4;

    TextView tvRemaining;

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

        btoption1 = findViewById(R.id.btoption1);
        btoption2 = findViewById(R.id.btoption2);
        btoption3 = findViewById(R.id.btoption3);
        btoption4 = findViewById(R.id.btoption4);

        SharedPreferences catPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
        int size = catPref.getInt("count", 0);
        correct = catPref.getInt("correct", 0);
        wrong = catPref.getInt("wrong", 0);
        final int answered = catPref.getInt("answered", 0);

        tvRemaining = findViewById(R.id.tvRemaining);
        tvRemaining.setText("Question "+String.valueOf(answered+1)+"/"+String.valueOf(size));

        if (answered<size)
        {
            //fetch from sharedpref. set to display
            tvQuizQuestions.setText(catPref.getString("question"+answered,null));
            btoption1.setText(catPref.getString("option1"+answered,null));
            btoption2.setText(catPref.getString("option2"+answered,null));
            btoption3.setText(catPref.getString("option3"+answered,null));
            btoption4.setText(catPref.getString("option4"+answered,null));
            correctanswer = catPref.getString("correct"+answered,null);
            explanation = catPref.getString("explanation"+answered,null);
            
        }
        if (answered==size-1)
        {
            last = 1;
        }

        //set the values here
        btQuiz = findViewById(R.id.btQuiz);


        btoption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedtext = btoption1.getText().toString();

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

                    i = 0;

                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if (i==0)
                            {
                                i = 1;
                                btoption1.setBackgroundResource(R.drawable.blinkingborder);
                            }
                            else
                            {
                                btoption1.setBackgroundResource(R.drawable.whiteanswer);
                                i = 0;
                            }
                        }

                        public void onFinish() {
                            if (last==0)
                            {
                                finish();
                                startActivity(getIntent());
                            }
                            else
                            {
                                new AlertDialog.Builder(QuizQuestions.this)
                                        .setMessage("You have finished the quiz. Results: Right Answers: "+String.valueOf(correct)+", Wrong answer: "+String.valueOf(wrong))
                                        .setCancelable(false)
                                        .setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                                //last ques answered.
                            }
                        }
                    }.start();
                }
                else
                {
                    wrong = wrong + 1;
                    editor.putInt("wrong",wrong);
                    editor.commit();

                    i = 0;

                    if (correctanswer.equals(btoption2.getText().toString()))
                    {
                        btoption2.setBackgroundResource(R.drawable.blinkingborder);
                    }
                    else
                    {
                        if (correctanswer.equals(btoption3.getText().toString()))
                        {
                            btoption3.setBackgroundResource(R.drawable.blinkingborder);
                        }
                        else
                        {
                            btoption4.setBackgroundResource(R.drawable.blinkingborder);
                        }
                    }

                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if (i==0)
                            {
                                i = 1;
                                btoption1.setBackgroundResource(R.drawable.wronganswer);
                            }
                            else
                            {
                                btoption1.setBackgroundResource(R.drawable.whiteanswer);
                                i = 0;
                            }
                        }

                        public void onFinish() {
                            if (last==0)
                            {
                                finish();
                                startActivity(getIntent());
                            }
                            else
                            {
                                new AlertDialog.Builder(QuizQuestions.this)
                                        .setMessage("You have finished the quiz. Results: Right Answers: "+String.valueOf(correct)+", Wrong answer: "+String.valueOf(wrong))
                                        .setCancelable(false)
                                        .setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                                //last ques answered.
                            }
                        }
                    }.start();
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
                                    Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                    //last ques answered.
                }
            }
        });

        btoption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedtext = btoption2.getText().toString();

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

                    i = 0;

                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if (i==0)
                            {
                                i = 1;
                                btoption2.setBackgroundResource(R.drawable.blinkingborder);
                            }
                            else
                            {
                                btoption2.setBackgroundResource(R.drawable.whiteanswer);
                                i = 0;
                            }
                        }

                        public void onFinish() {
                            if (last==0)
                            {
                                finish();
                                startActivity(getIntent());
                            }
                            else
                            {
                                new AlertDialog.Builder(QuizQuestions.this)
                                        .setMessage("You have finished the quiz. Results: Right Answers: "+String.valueOf(correct)+", Wrong answer: "+String.valueOf(wrong))
                                        .setCancelable(false)
                                        .setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                                //last ques answered.
                            }
                        }
                    }.start();
                }
                else
                {
                    wrong = wrong + 1;
                    editor.putInt("wrong",wrong);
                    editor.commit();

                    i = 0;

                    if (correctanswer.equals(btoption1.getText().toString()))
                    {
                        btoption1.setBackgroundResource(R.drawable.blinkingborder);
                    }
                    else
                    {
                        if (correctanswer.equals(btoption3.getText().toString()))
                        {
                            btoption3.setBackgroundResource(R.drawable.blinkingborder);
                        }
                        else
                        {
                            btoption4.setBackgroundResource(R.drawable.blinkingborder);
                        }
                    }


                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if (i==0)
                            {
                                i = 1;
                                btoption2.setBackgroundResource(R.drawable.wronganswer);
                            }
                            else
                            {
                                btoption2.setBackgroundResource(R.drawable.whiteanswer);
                                i = 0;
                            }
                        }

                        public void onFinish() {
                            if (last==0)
                            {
                                finish();
                                startActivity(getIntent());
                            }
                            else
                            {
                                new AlertDialog.Builder(QuizQuestions.this)
                                        .setMessage("You have finished the quiz. Results: Right Answers: "+String.valueOf(correct)+", Wrong answer: "+String.valueOf(wrong))
                                        .setCancelable(false)
                                        .setPositiveButton("FINISH", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                                //last ques answered.
                            }
                        }
                    }.start();
                }

            }
        });

        btoption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedtext = btoption3.getText().toString();

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

                    i = 0;

                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if (i==0)
                            {
                                i = 1;
                                btoption3.setBackgroundResource(R.drawable.blinkingborder);
                            }
                            else
                            {
                                btoption3.setBackgroundResource(R.drawable.whiteanswer);
                                i = 0;
                            }
                        }

                        public void onFinish() {
                            if (last==1)
                            {
                                Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                startActivity(intent);
                            }
                            else
                            {
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    }.start();
                }
                else
                {
                    wrong = wrong + 1;
                    editor.putInt("wrong",wrong);
                    editor.commit();

                    i = 0;

                    if (correctanswer.equals(btoption1.getText().toString()))
                    {
                        btoption1.setBackgroundResource(R.drawable.blinkingborder);
                    }
                    else
                    {
                        if (correctanswer.equals(btoption2.getText().toString()))
                        {
                            btoption2.setBackgroundResource(R.drawable.blinkingborder);
                        }
                        else
                        {
                            btoption4.setBackgroundResource(R.drawable.blinkingborder);
                        }
                    }

                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if (i==0)
                            {
                                i = 1;
                                btoption3.setBackgroundResource(R.drawable.wronganswer);
                            }
                            else
                            {
                                btoption3.setBackgroundResource(R.drawable.whiteanswer);
                                i = 0;
                            }
                        }

                        public void onFinish() {
                            if (last==1)
                            {
                                Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                startActivity(intent);
                            }
                            else
                            {
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    }.start();
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
                                    Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                    startActivity(intent);
                                }
                            })
                            .show();
                    //last ques answered.
                }
            }
        });

        btoption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedtext = btoption4.getText().toString();

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

                    i = 0;

                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if (i==0)
                            {
                                i = 1;
                                btoption4.setBackgroundResource(R.drawable.blinkingborder);
                            }
                            else
                            {
                                btoption4.setBackgroundResource(R.drawable.whiteanswer);
                                i = 0;
                            }
                        }

                        public void onFinish() {
                            if (last==1)
                            {
                                Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                startActivity(intent);
                            }
                            else
                            {
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    }.start();
                }
                else
                {
                    wrong = wrong + 1;
                    editor.putInt("wrong",wrong);
                    editor.commit();

                    i = 0;

                    if (correctanswer.equals(btoption1.getText().toString()))
                    {
                        btoption1.setBackgroundResource(R.drawable.blinkingborder);
                    }
                    else
                    {
                        if (correctanswer.equals(btoption2.getText().toString()))
                        {
                            btoption2.setBackgroundResource(R.drawable.blinkingborder);
                        }
                        else
                        {
                            btoption3.setBackgroundResource(R.drawable.blinkingborder);
                        }
                    }

                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                            if (i==0)
                            {
                                i = 1;
                                btoption4.setBackgroundResource(R.drawable.wronganswer);
                            }
                            else
                            {
                                btoption4.setBackgroundResource(R.drawable.whiteanswer);
                                i = 0;
                            }
                        }

                        public void onFinish() {
                            if (last==1)
                            {
                                Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
                                startActivity(intent);
                            }
                            else
                            {
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    }.start();
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
                                    Intent intent = new Intent (QuizQuestions.this, QuizResultPage.class);
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
