package dwarsoft.learning;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import static dwarsoft.learning.QuizLoading.CATPREF;

public class QuizResultPage extends AppCompatActivity {

    Button btcontinue;
    TextView tvpoints,tvcorrect,tvwrong,tvtotal;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid,name;

    String points,correct,wrong,total,code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result_page);

        tvpoints = findViewById(R.id.tvpoints);
        tvcorrect = findViewById(R.id.tvcorrect);
        tvwrong = findViewById(R.id.tvWrong);
        tvtotal = findViewById(R.id.tvTotal);
        btcontinue = findViewById(R.id.btContinue);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    name = user.getDisplayName();

                    SharedPreferences catPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
                    total = String.valueOf(catPref.getInt("count", 0));
                    correct = String.valueOf(catPref.getInt("correct", 0));
                    wrong = String.valueOf(catPref.getInt("wrong", 0));
                    code = catPref.getString("quizcode", null);
                    points = correct;

                    tvtotal.setText("Total Questions: "+total);
                    tvcorrect.setText("Correct Answers: "+correct);
                    tvwrong.setText("Wrong Answers: "+wrong);
                    tvpoints.setText("Points Credited: "+points);

                    DatabaseReference usersReff = FirebaseDatabase.getInstance().getReference();
                    usersReff.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String exisitingpoints = dataSnapshot.child("leaderboard/"+code+"/"+name).getValue().toString();
                            int ep = Integer.parseInt(exisitingpoints);
                            int newp = Integer.parseInt(points);
                            int updatedpoints = ep + newp;
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = database.getReference();
                            databaseReference.child("leaderboard/"+code+"/"+name).setValue(updatedpoints);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                }
            }
        };

        btcontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (QuizResultPage.this, HomeScreen.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (QuizResultPage.this, HomeScreen.class);
        startActivity(intent);
        finish();
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
