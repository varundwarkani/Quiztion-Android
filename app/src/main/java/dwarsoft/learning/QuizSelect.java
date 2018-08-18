package dwarsoft.learning;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
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

public class QuizSelect extends AppCompatActivity {

    private SharedPreferences categoriesPref;
    private SharedPreferences.Editor editor;
    String quizref;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button btQuizSelect;
    String uid;

    RecyclerView rvquiz;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private QuizAdapter quizAdapter;
    private ArrayList<QuizModel> quizModelArrayList;
    private ArrayList<String> quizlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_select);

        btQuizSelect = findViewById(R.id.btQuizSelect);
        quizlist.clear();
        rvquiz = findViewById(R.id.rvQuiz);
        rvquiz.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,1);
        rvquiz.setLayoutManager(staggeredGridLayoutManager);
        quizAdapter = new QuizAdapter(QuizSelect.this,getListData());
        rvquiz.setAdapter(quizAdapter);

        categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
        quizref = categoriesPref.getString("quizref",null);

        btQuizSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                final String selectedcode = categoriesPref.getString("selectedquiz",null);

                if (selectedcode.equals("0"))
                {
                    Toast.makeText(QuizSelect.this, "Please select a quiz", Toast.LENGTH_SHORT).show();
                }
                else {

                    new AlertDialog.Builder(QuizSelect.this)
                            .setMessage("Are you sure you want to join "+selectedcode+" quiz?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    //add to firebase ref
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference = database.getReference();
                                    databaseReference.child("profile/"+uid+"/quiz/"+selectedcode+"/code").setValue(selectedcode);
                                    Toast.makeText(QuizSelect.this, "Quiz added!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
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
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference(quizref);
                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                quizlist.add(postSnapshot.getKey());
                            }
                            rvquiz.setHasFixedSize(true);
                            staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,1);
                            rvquiz.setLayoutManager(staggeredGridLayoutManager);
                            quizAdapter = new QuizAdapter(QuizSelect.this,getListData());
                            rvquiz.setAdapter(quizAdapter);
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

    private ArrayList<QuizModel> getListData(){
        quizModelArrayList = new ArrayList<>();
        for (int i = 0; i < quizlist.size(); i++){
            quizModelArrayList.add(new QuizModel(quizlist.get(i)));
        }
        return quizModelArrayList;
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