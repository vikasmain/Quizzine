package com.vikkyb.check.devsir.quizfragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vikkyb.check.devsir.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Youranswers extends Fragment {

TextView textView,getTextView;
    public Youranswers() {
        // Required empty public constructor
    }
FirebaseAuth mauth;
    String current;
DatabaseReference databaseReference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_youranswers, container, false);
        mauth=FirebaseAuth.getInstance();
        textView=(TextView)v.findViewById(R.id.answres);
        getTextView=(TextView)v.findViewById(R.id.t1);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username=(String)dataSnapshot.child("username").getValue();
                getTextView.setText("Dear "+username);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Integer integer=getArguments().getInt("score");
        textView.setText("Your Score is "+integer);
        return v;
    }

}
