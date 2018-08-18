package dwarsoft.learning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class QuizLoading extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    private SharedPreferences categoriesPref;
    private SharedPreferences.Editor editor;
    public static final String CATPREF = "catpref";

    String code;

    Button btStartQuiz;
    int i = 0;

    ArrayList<String> questions = new ArrayList<String>();
    ArrayList<String> correctoption = new ArrayList<String>();
    ArrayList<String> option1 = new ArrayList<String>();
    ArrayList<String> option2 = new ArrayList<String>();
    ArrayList<String> option3 = new ArrayList<String>();
    ArrayList<String> option4 = new ArrayList<String>();
    ArrayList<String> explanation = new ArrayList<String>();
    String correct = "0",wrong = "0";
    int count = -1;

    RecyclerView rvLeaderboard;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private LeaderboardAdapter quizAdapter;
    private ArrayList<LeaderboardModel> quizModelArrayList;
    private ArrayList<String> namelist = new ArrayList<>();
    private ArrayList<String> scorelist= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_loading);
        questions.clear();
        correctoption.clear();
        option1.clear();
        option2.clear();
        option3.clear();
        option4.clear();
        explanation.clear();

        categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
        code = categoriesPref.getString("quizcode", null);

        rvLeaderboard = findViewById(R.id.rvLeaderboard);
        rvLeaderboard.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, 1);
        rvLeaderboard.setLayoutManager(staggeredGridLayoutManager);
        quizAdapter = new LeaderboardAdapter(QuizLoading.this, getListData());
        rvLeaderboard.setAdapter(quizAdapter);

        btStartQuiz = findViewById(R.id.btStartQuiz);

        btStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count==-1)
                {
                    Toast.makeText(QuizLoading.this, "Please wait for data to load...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (questions.size()==count)
                    {
                        //store in sharedpref here and take the user to quiz display screen

                        categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                        editor = categoriesPref.edit();

                        editor.putInt("answered",0);

                        editor.putInt("correct",0);
                        editor.putInt("wrong",0);

                        editor.putInt("count", count);
                        for(int i=0;i<questions.size();i++)
                        {
                            editor.remove("question" + i);
                            editor.putString("question" + i, questions.get(i));

                            editor.remove("question" + i);
                            editor.putString("question" + i, questions.get(i));

                            editor.remove("correct" + i);
                            editor.putString("correct" + i, correctoption.get(i));

                            editor.remove("option1" + i);
                            editor.putString("option1" + i, option1.get(i));

                            editor.remove("option2" + i);
                            editor.putString("option2" + i, option2.get(i));

                            editor.remove("option3" + i);
                            editor.putString("option3" + i, option3.get(i));

                            editor.remove("option4" + i);
                            editor.putString("option4" + i, option4.get(i));

                            editor.remove("explanation" + i);
                            editor.putString("explanation" + i, explanation.get(i));
                        }
                        editor.commit();

                    Intent intent = new Intent (QuizLoading.this, QuizQuestions.class);
                    startActivity(intent);

                    }
                    else
                    {
                        Toast.makeText(QuizLoading.this, "Please wait for questions to load...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        new CountDownTimer(300000000, 200) {

            public void onTick(long millisUntilFinished) {
                if (i==0)
                {
                    i = 1;
                    btStartQuiz.setBackgroundResource(R.drawable.blinkingborder);
                }
                else
                {
                    btStartQuiz.setBackgroundResource(R.drawable.blinkingborder2);
                    i = 0;
                }
            }

            public void onFinish() {
            }
        }.start();

        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    //for leaderboard
                    final HashMap<String, Integer> map = new HashMap<String, Integer>();
                    ValueComparator bvc = new ValueComparator(map);
                    final TreeMap<String, Integer> sorted_map = new TreeMap<String, Integer>(bvc);

                    DatabaseReference usersReff = FirebaseDatabase.getInstance().getReference("leaderboard/"+code);
                    usersReff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            namelist.clear();
                            scorelist.clear();

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                            {
                                namelist.add(postSnapshot.getKey());
                                scorelist.add(postSnapshot.getValue().toString());
                                map.put(postSnapshot.getKey(),Integer.parseInt(postSnapshot.getValue().toString()));
                            }


                            //get from sorted and store again in the list
                            //namelist.clear();
                            //scorelist.clear();


                            rvLeaderboard.setHasFixedSize(true);
                            staggeredGridLayoutManager = new StaggeredGridLayoutManager(1, 1);
                            rvLeaderboard.setLayoutManager(staggeredGridLayoutManager);
                            quizAdapter = new LeaderboardAdapter(QuizLoading.this, getListData());
                            rvLeaderboard.setAdapter(quizAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    //for getting quiz details

                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("quiz/"+code);
                    usersRef.orderByChild("option1").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Long c = dataSnapshot.getChildrenCount();
                            count = Math.round(c);

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                // store list of correct answers, questions, options - 6 different lists
                                String question = postSnapshot.getKey();
                                    questions.add(question);
                                    option1.add(postSnapshot.child("option1").getValue().toString());
                                    option2.add(postSnapshot.child("option2").getValue().toString());
                                    option3.add(postSnapshot.child("option3").getValue().toString());
                                    option4.add(postSnapshot.child("option4").getValue().toString());
                                    explanation.add(postSnapshot.child("explanation").getValue().toString());
                                    correctoption.add(postSnapshot.child("correct").getValue().toString());


                                //      categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                                //    editor = categoriesPref.edit();
                                //        editor.putString("question",question);
                                //      editor.commit();
                                //      postSnapshot.child("values/coins").getValue().toString();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    //get the questions and other details. setthem to shared pref
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

    private ArrayList<LeaderboardModel> getListData() {
        quizModelArrayList = new ArrayList<>();
        for (int i = 0; i < namelist.size(); i++) {
            quizModelArrayList.add(new LeaderboardModel(namelist.get(i),scorelist.get(i)));
        }
        return quizModelArrayList;
    }
}


class ValueComparator implements Comparator<String> {
    Map<String, Integer> base;

    public ValueComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with
    // equals.
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}