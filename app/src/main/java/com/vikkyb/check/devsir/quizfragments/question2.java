package com.vikkyb.check.devsir.quizfragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vikkyb.check.devsir.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class question2 extends Fragment {
    RadioGroup grp;
    TextView tvquestion, bl;
    private RadioButton rbtnA, rbtnB, rbtnC, rbtnD;
    private Button btnNext,quit;
    private int obtainedScore = 0;
    FirebaseAuth mauth;
    Integer integer;
    FirebaseUser mcurrentuser;
    private int questionId = 0;
    private String question, option1, option2, option3, option4;
    String firebaseanswer;
    private int answeredQsNo = 0;
    DatabaseReference d3, databaserefere, mDatabaseUsers;
    View v;
    RelativeLayout relativeLayout;
    public question2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_question1, container, false);
        initview();
        integer=getArguments().getInt("score");
        mauth = FirebaseAuth.getInstance();

        mcurrentuser = mauth.getCurrentUser();
        d3 = FirebaseDatabase.getInstance().getReference().child("Quiz");
        databaserefere = FirebaseDatabase.getInstance().getReference().child("Leaderboard");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mcurrentuser.getUid());

        d3.keepSynced(true);
        questionview();


        return v;
    }
    public void initview()
    {
        relativeLayout=(RelativeLayout)v.findViewById(R.id.r1);
        tvquestion = (TextView) v.findViewById(R.id.tvQuestion);
        bl = (TextView) v.findViewById(R.id.tvNumberOfQuestions);

        rbtnA = (RadioButton) v.findViewById(R.id.radio0);
        rbtnB = (RadioButton) v.findViewById(R.id.radio1);
        rbtnC = (RadioButton) v.findViewById(R.id.radio2);
        rbtnD = (RadioButton) v.findViewById(R.id.radio3);
        rbtnA.setChecked(false);
        rbtnB.setChecked(false);
        rbtnC.setChecked(false);
        rbtnD.setChecked(false);
        grp = (RadioGroup) v.findViewById(R.id.radioGroup1);
        btnNext=(Button)v.findViewById(R.id.btnNext);
        quit=(Button)v.findViewById(R.id.quit);

    }
    public void questionview()
    {
        d3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                question = (String) dataSnapshot.child("question2").getValue();
                option1 = (String) dataSnapshot.child("options").child("question2").child("option1").getValue();
                option2 = (String) dataSnapshot.child("options").child("question2").child("option2").getValue();
                option3 = (String) dataSnapshot.child("options").child("question2").child("option3").getValue();
                option4 = (String) dataSnapshot.child("options").child("question2").child("option4").getValue();
                firebaseanswer = (String) dataSnapshot.child("options").child("question2").child("answers").getValue();

                tvquestion.setText(question);
                rbtnA.setText(option1);
                rbtnB.setText(option2);
                rbtnC.setText(option3);
                rbtnD.setText(option4);

                grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton checkedRadioButton = (RadioButton) v.findViewById(checkedId);
                        String text = checkedRadioButton.getText().toString();
                        if (text.equals(firebaseanswer))
                        {
                            integer++;
                            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                                final DatabaseReference newpost = databaserefere.push();

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    newpost.child("score").setValue(integer);
                                    newpost.child("uid").setValue(mcurrentuser.getUid());

                                    newpost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener
                                            (new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Snackbar.make(relativeLayout,"Right Answer",Snackbar.LENGTH_SHORT).show();
                                                    } else {
                                                        Snackbar.make(relativeLayout,"Wrong Answer",Snackbar.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });//datasnapshot returns everything inside random id(0dkm003kmd39iok) object in firebase

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                question3 fr=new question3();
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("score", integer);

                fr.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame_container,fr);
                fragmentTransaction.commit();
            }
        });
    }

}