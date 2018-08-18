package dwarsoft.learning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class OnQuiz extends AppCompatActivity {

    TextView tvOnlineQuizName,tvOnlinePlayerName,tvOnlineQuizQuestions;
    Button btOnlineReady;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    int yes = 0;

    private SharedPreferences categoriesPref;
    private SharedPreferences.Editor editor;
    public static final String CATPREF = "catpref";

    ArrayList<String> questions = new ArrayList<String>();
    ArrayList<String> correctoption = new ArrayList<String>();
    ArrayList<String> option1 = new ArrayList<String>();
    ArrayList<String> option2 = new ArrayList<String>();
    ArrayList<String> option3 = new ArrayList<String>();
    ArrayList<String> option4 = new ArrayList<String>();
    ArrayList<String> explanation = new ArrayList<String>();
    String correct = "0",wrong = "0";
    int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_quiz);

        questions.clear();
        correctoption.clear();
        option1.clear();
        option2.clear();
        option3.clear();
        option4.clear();
        explanation.clear();

        tvOnlineQuizName = findViewById(R.id.tvOnlineQuizName);
        tvOnlinePlayerName = findViewById(R.id.tvOnlinePlayerName);
        tvOnlineQuizQuestions = findViewById(R.id.tvOnlineQuizQuestions);

        btOnlineReady = findViewById(R.id.btOnlineReady);
        btOnlineReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OnQuiz.this, "Please wait...", Toast.LENGTH_SHORT).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();

                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("playing/"+uid);
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            final String playingwith,quizcode,playinguid,points;

                            quizcode = dataSnapshot.child("quizcode").getValue().toString();
                            playinguid = dataSnapshot.child("playinguid").getValue().toString();
                            playingwith = dataSnapshot.child("playingwith").getValue().toString();
                            points = dataSnapshot.child("points").getValue().toString();

                            DatabaseReference usersReff = FirebaseDatabase.getInstance().getReference("playing");
                            usersReff.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //check the ready status
                                    String ready = dataSnapshot.child(playinguid+"/ready").getValue().toString();
                                    if (ready.equals("1"))
                                    {
                                        if (yes==1)
                                        {
                                            //can proceed both are ready
                                            Toast.makeText(OnQuiz.this, "Both are ready", Toast.LENGTH_SHORT).show();

                                            //set ready to 2 for both
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference databaseReference = database.getReference();
                                            databaseReference.child("playing/"+uid+"/ready").setValue("2");
                                            databaseReference.child("playing/"+playinguid+"/ready").setValue("2");



                                            //intent here
                                            Intent intent = new Intent (OnQuiz.this, OnlineQuizQuestions.class);
                                            startActivity(intent);
                                            finish();


                                        }
                                        else
                                        {
                                            Toast.makeText(OnQuiz.this, "Opponent is ready. Please click on Ready!", Toast.LENGTH_SHORT).show();
                                        }
                                    } else if (ready.equals("0"))
                                    {
                                        Toast.makeText(OnQuiz.this, "Opponent is not ready...", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            DatabaseReference userref = FirebaseDatabase.getInstance().getReference();
                            userref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    Long noofques = dataSnapshot.child("quiz/"+quizcode).getChildrenCount();
                                    count = Math.round(noofques);

                                    tvOnlinePlayerName.setText("Playing with: "+playingwith);
                                    tvOnlineQuizName.setText(quizcode);
                                    tvOnlineQuizQuestions.setText("No. of ques "+String.valueOf(noofques));

                                    for (DataSnapshot postSnapshot : dataSnapshot.child("quiz/"+quizcode).getChildren()) {
                                        // store list of correct answers, questions, options - 6 different lists
                                        String question = postSnapshot.getKey();
                                        questions.add(question);
                                        option1.add(postSnapshot.child("option1").getValue().toString());
                                        option2.add(postSnapshot.child("option2").getValue().toString());
                                        option3.add(postSnapshot.child("option3").getValue().toString());
                                        option4.add(postSnapshot.child("option4").getValue().toString());
                                        explanation.add(postSnapshot.child("explanation").getValue().toString());
                                        correctoption.add(postSnapshot.child("correct").getValue().toString());
                                    }

                                    btOnlineReady.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (questions.size()!=count)
                                            {
                                                Toast.makeText(OnQuiz.this, "Please wait...", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {

                                                //add here in sharedpref
                                                categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                                                editor = categoriesPref.edit();

                                                editor.putInt("onlineanswered",0);

                                                editor.putInt("onlinecorrect",0);
                                                editor.putInt("onlinewrong",0);

                                                editor.putString("onlineuid",playinguid);
                                                editor.putString("onlinename",playingwith);

                                                editor.putInt("onlinecount", count);

                                                Log.i("COUNT",String.valueOf(count));
                                                for(int i=0;i<questions.size();i++)
                                                {

                                                    editor.remove("onlinequestion" + i);
                                                    editor.putString("onlinequestion" + i, questions.get(i));

                                                    editor.remove("onlinecorrect" + i);
                                                    editor.putString("onlinecorrect" + i, correctoption.get(i));

                                                    editor.remove("onlineoption1" + i);
                                                    editor.putString("onlineoption1" + i, option1.get(i));

                                                    editor.remove("onlineoption2" + i);
                                                    editor.putString("onlineoption2" + i, option2.get(i));

                                                    editor.remove("onlineoption3" + i);
                                                    editor.putString("onlineoption3" + i, option3.get(i));

                                                    editor.remove("onlineoption4" + i);
                                                    editor.putString("onlineoption4" + i, option4.get(i));

                                                    editor.remove("onlineexplanation" + i);
                                                    editor.putString("onlineexplanation" + i, explanation.get(i));
                                                }
                                                editor.commit();

                                                yes = 1;
                                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                DatabaseReference databaseReference = database.getReference();
                                                databaseReference.child("playing/"+uid+"/ready").setValue("1");

                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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
