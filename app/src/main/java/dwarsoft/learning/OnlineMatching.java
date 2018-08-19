package dwarsoft.learning;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
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

public class OnlineMatching extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid,name;
    TextView tvOnlinePlayers;

    Button btSearch,btJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_matching);

        tvOnlinePlayers = findViewById(R.id.tvOnlinePlayers);

        btSearch = findViewById(R.id.btSearch);
        btJoin = findViewById(R.id.btJoin);

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OnlineMatching.this, "Please wait...", Toast.LENGTH_SHORT).show();
            }
        });

        btJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OnlineMatching.this, "Please wait...", Toast.LENGTH_SHORT).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    name = user.getDisplayName();
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference databaseReference = database.getReference();
                    databaseReference.child("online/"+uid+"/uid").setValue(uid);
                    databaseReference.child("online/"+uid+"/name").setValue(name);
                    databaseReference.child("online/"+uid+"/playing").setValue("0");
                    databaseReference.child("online/"+uid+"/match").setValue("0");
                    databaseReference.child("online/"+uid+"/looking").setValue("0");



                    DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();
                    databaseref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long childcount = dataSnapshot.child("online").getChildrenCount();
                            tvOnlinePlayers.setText("Players Online: "+String.valueOf(childcount));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    btSearch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            search();
                        }
                    });

                    btJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            join();
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("online/"+uid).setValue(null);
        mAuth.removeAuthStateListener(mAuthListener);
    }


    public void search(){
        //check if child is there in online. else check in playing. if yes, take the current to quiz screen
        // else nothing

        Toast.makeText(this, "Finding users", Toast.LENGTH_SHORT).show();
        DatabaseReference databaseref = FirebaseDatabase.getInstance().getReference();
        databaseref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        Long childcount = dataSnapshot.child("online").getChildrenCount();

                            if (childcount==1)
                            {
                            }
                            else
                            {
                                for (DataSnapshot postSnapshot : dataSnapshot.child("online").getChildren()) {
                                    if (uid.equals(postSnapshot.getKey()))
                                    {

                                    }
                                    else
                                    {
                                        //get a random quiz code. assign both same.
                                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                        DatabaseReference databaseReference = database.getReference();
                                        String uidd = postSnapshot.getKey();
                                        String looking = postSnapshot.child("looking").getValue().toString();
                                        if (looking.equals("1"))
                                        {
                                            String newname = postSnapshot.child("name").getValue().toString();
                                            databaseReference.child("playing/"+uid+"/uid").setValue(uid);
                                            databaseReference.child("playing/"+uid+"/playinguid").setValue(uidd);
                                            databaseReference.child("playing/"+uid+"/playingwith").setValue(newname);
                                            databaseReference.child("playing/"+uid+"/points").setValue("0");
                                            databaseReference.child("playing/"+uid+"/quizcode").setValue("Basics of DBMS");
                                            databaseReference.child("playing/"+uid+"/ready").setValue("0");
                                            databaseReference.child("playing/"+uid+"/question").setValue("0");

                                            databaseReference.child("playing/"+uidd+"/uid").setValue(uidd);
                                            databaseReference.child("playing/"+uidd+"/playinguid").setValue(uid);
                                            databaseReference.child("playing/"+uidd+"/playingwith").setValue(name);
                                            databaseReference.child("playing/"+uidd+"/points").setValue("0");
                                            databaseReference.child("playing/"+uidd+"/quizcode").setValue("Basics of DBMS");
                                            databaseReference.child("playing/"+uidd+"/ready").setValue("0");
                                            databaseReference.child("playing/"+uidd+"/question").setValue("0");

                                            databaseReference.child("online/"+uid+"/match").setValue("1");
                                            databaseReference.child("online/"+uidd+"/match").setValue("1");
                                            
                                            Intent intent = new Intent (OnlineMatching.this, OnQuiz.class);
                                            startActivity(intent);
                                            finish();

                                            //make playing 1
                                            //take to next screen and ask it to wait
                                        }
                                        else
                                        {
                                        }
                                    }
                                }

                            }
                    }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void join(){
        Toast.makeText(this, "Finding users", Toast.LENGTH_SHORT).show();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        databaseReference.child("online/"+uid+"/looking").setValue("1");
        DatabaseReference databasereff = FirebaseDatabase.getInstance().getReference("online");
        databasereff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(uid))
                {
                    String match = dataSnapshot.child(uid+"/match").getValue().toString();
                    if (match.equals("1"))
                    {
                        Intent intent = new Intent (OnlineMatching.this, OnQuiz.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                {
                    //nothing
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
