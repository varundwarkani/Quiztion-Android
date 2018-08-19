package dwarsoft.learning;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static dwarsoft.learning.QuizLoading.CATPREF;

public class ResultPage extends AppCompatActivity {

    TextView tvOwnCorrect,tvOwnWrong,tvOppName,tvPoints,tvResult;
    Button btFinish;
    private SharedPreferences categoriesPref;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;

    String oc,ow,opc,opw,p,r,ouid,oppname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_page);

        categoriesPref = getSharedPreferences(CATPREF, Context.MODE_PRIVATE);
        ouid = categoriesPref.getString("onlineuid",null);
        oppname = categoriesPref.getString("onlinename",null);
        oc = String.valueOf(categoriesPref.getInt("onlinecorrect",0));
        ow = String.valueOf(categoriesPref.getInt("onlinewrong",0));
        int ooc = Integer.parseInt(oc);
        ooc = ooc * 20;
        p = String.valueOf(ooc);

        tvOwnCorrect = findViewById(R.id.tvOwnCorrect);
        tvOwnWrong = findViewById(R.id.tvOwnWrong);
        tvOppName = findViewById(R.id.tvOppName);
        tvPoints = findViewById(R.id.tvPoints);
        tvResult = findViewById(R.id.tvResult);
        btFinish = findViewById(R.id.btFinish);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();
                    databaseref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            String oppname = dataSnapshot.child("playing/"+uid+"/playingwith").getValue().toString();
                            String opppoint = dataSnapshot.child("playing/"+ouid+"/points").getValue().toString();

                            tvOppName.setText("Opposion Name: "+ oppname);
                            tvOwnCorrect.setText("Correct Answers: "+oc);
                            tvOwnWrong.setText("Wrong Answers: "+ow);
                            tvPoints.setText("Points Credited: "+p);

                            int oppo = Integer.parseInt(opppoint);
                            int currpo = Integer.parseInt(p);


                            String currentdbpoints = dataSnapshot.child("profile/"+uid+"/points").getValue().toString();
                            int newpoints = Integer.parseInt(p);
                            int currp = Integer.parseInt(currentdbpoints);
                            int updated = currp + newpoints;
                            String updatedcoins = String.valueOf(updated);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference databaseReference = database.getReference();
                            databaseReference.child("profile/"+uid+"/points").setValue(updatedcoins);

                            btFinish.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent (ResultPage.this, HomeScreen.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            if (currpo>oppo)
                            {
                                //you win
                                tvResult.setText("You Win!");
                                tvResult.setTextColor(Color.parseColor("#32CD32"));
                            }
                            else if (currpo<oppo)
                            {
                                //you lose
                                tvResult.setText("You Lose!");
                                tvResult.setTextColor(Color.parseColor("#FF0000"));
                            }
                            else
                            {
                                //tie
                                tvResult.setText("Tie!");
                                tvResult.setTextColor(Color.parseColor("#FF8000"));
                            }
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent (ResultPage.this, HomeScreen.class);
        startActivity(intent);
        finish();
    }
}
