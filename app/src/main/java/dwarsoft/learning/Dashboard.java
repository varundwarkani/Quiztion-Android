package dwarsoft.learning;

import android.content.DialogInterface;
import android.content.Intent;
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

public class Dashboard extends AppCompatActivity {

    TextView tvDashboardPoints;
    Button btDashboard;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        tvDashboardPoints = findViewById(R.id.tvDashboardPoints);
        btDashboard = findViewById(R.id.btDashboard);

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
                            final String points = dataSnapshot.child("profile/"+uid+"/points").getValue().toString();
                            tvDashboardPoints.setText("Total Points: "+points);
                            final int p = Integer.parseInt(points);
                            btDashboard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (p<100)
                                    {
                                        Toast.makeText(Dashboard.this, "Sorry your balance is low.", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        new AlertDialog.Builder(Dashboard.this)
                                                .setMessage("Are you sure you want to redeem "+points+" ?")
                                                .setCancelable(false)
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        Toast.makeText(Dashboard.this, "Successfully submitted.", Toast.LENGTH_SHORT).show();
                                                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                        DatabaseReference databaseReference = database.getReference();
                                                        databaseReference.child("profile/"+uid+"/points").setValue("0");
                                                        Intent intent = new Intent (Dashboard.this, HomeScreen.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .setNegativeButton("No", null)
                                                .show();
                                    }
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
