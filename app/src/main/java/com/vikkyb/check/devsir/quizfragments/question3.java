package com.vikkyb.check.devsir.quizfragments;


import android.os.Bundle;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vikkyb.check.devsir.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class question3 extends Fragment {
    RadioGroup grp;
    TextView tvquestion, bl;
    private RadioButton rbtnA, rbtnB, rbtnC, rbtnD;
    private Button btnNext;
    private int obtainedScore = 0;
    int get;
    Query nakoli;
    FirebaseAuth mauth;
    FirebaseUser mcurrentuser;
    private int questionId = 0;
    private String question, option1, option2, option3, option4;
    String firebaseanswer;
    private int answeredQsNo = 0;
    DatabaseReference d3,  mDatabaseUsers;
    View v;
    RelativeLayout relativeLayout;
    public question3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment3, container, false);
        initview();
        mauth = FirebaseAuth.getInstance();

        mcurrentuser = mauth.getCurrentUser();
        get=getArguments().getInt("score");
        d3 = FirebaseDatabase.getInstance().getReference().child("Quiz");
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
        btnNext=(Button)v.findViewById(R.id.btnsubmit);

    }
    public void questionview()
    {
        d3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                question = (String) dataSnapshot.child("question3").getValue();
                option1 = (String) dataSnapshot.child("options").child("question3").child("option1").getValue();
                option2 = (String) dataSnapshot.child("options").child("question3").child("option2").getValue();
                option3 = (String) dataSnapshot.child("options").child("question3").child("option3").getValue();
                option4 = (String) dataSnapshot.child("options").child("question3").child("option4").getValue();
                firebaseanswer = (String) dataSnapshot.child("options").child("question1").child("answers").getValue();

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

                            get++;

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

                Youranswers fr=new Youranswers();
                Bundle bundle=new Bundle();
                bundle.putInt("score",get);
                fr.setArguments(bundle);
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container,fr);
                fragmentTransaction.commit();
            }
        });

    }
}