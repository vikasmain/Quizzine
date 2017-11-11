package com.vikkyb.check.devsir;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("deprecation")
public class SetupAccount extends AppCompatActivity {
    String email="";
    String name="";
    private EditText e1,e5,e7,e8;
    private Button b1;
    EditText changeDate;
    private DatePicker datePicker;
    private Calendar calendar;
    private AwesomeValidation awesomeValidation;
    private ProgressDialog progress;
    String s3;
    private ImageView imageView;
    private StorageReference mstorage;
    private DatabaseReference d4;
    private FirebaseAuth mauth;
    private static final int Gallery_Request_Code = 1;
    public static String uri;
    String dob="";
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_account);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        changeDate = (EditText) findViewById(R.id.change_date_button);

        showDate(year, month+1, day);


        mauth = FirebaseAuth.getInstance();
        mstorage = FirebaseStorage.getInstance().getReference().child("profile_images");
        d4 = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser currentUser = mauth.getCurrentUser();

        progress = new ProgressDialog(this);
        Format formatter3 = new SimpleDateFormat("MMMM yyyy dd");
        s3 = formatter3.format(new Date());
        e1 = (EditText) findViewById(R.id.editText3);
        e5 = (EditText) findViewById(R.id.editText9);
        e7 = (EditText) findViewById(R.id.editText10);
        e8=(EditText)findViewById(R.id.editText7);
        awesomeValidation.addValidation(this, R.id.editText10, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.editText9, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.editText7, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.editText3, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);

        if ( currentUser== null) {
            // No user is signed in
        } else {
            // User logged in
            email = currentUser.getEmail();
            name = (currentUser.getDisplayName());

            uri = currentUser.getPhotoUrl().toString();
        }

        String lastName = "";
        String firstName= "";
        if(name.split("\\w+").length>1){
//Getting first name and last name
            firstName = name.substring(0, name.lastIndexOf(' '));
            firstName=firstName.substring(0,1).toUpperCase()+firstName.substring(1);

            lastName = name.substring(name.lastIndexOf(" ")+1);
            lastName=lastName.substring(0,1).toUpperCase()+lastName.substring(1);

        }
        else{
            firstName=name;
        }

        e1.setText(firstName, TextView.BufferType.EDITABLE);
        e8.setText(lastName, TextView.BufferType.EDITABLE);


        b1 = (Button) findViewById(R.id.button2);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                startsetupaccount();
            }
        });

    }
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "choose date",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };
    private void showDate(int year, int i, int day) {
        changeDate.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
/*
    public String currentDate() {
        StringBuilder mcurrentDate = new StringBuilder();
        month = datePicker.getMonth() + 1;
        mcurrentDate.append("Date: " + month + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear());
        return mcurrentDate.toString();
    }
    */
    private void startsetupaccount() {
        if (awesomeValidation.validate()) {


            //process the data further
            final String dob=changeDate.getText().toString();
            final String phone = e5.getText().toString().trim();
            final String signature = e7.getText().toString().trim();
            final String firstnamedisplay = e1.getText().toString().trim();
            final String lastnamedisplay = e8.getText().toString().trim();
            final String uid = mauth.getCurrentUser().getUid();
            if (!TextUtils.isEmpty(firstnamedisplay) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(signature) && uri != null && !uri.isEmpty()
                    && !TextUtils.isEmpty(lastnamedisplay) && !TextUtils.isEmpty(s3)&&!TextUtils.isEmpty(dob)) {
                progress.setMessage("Finishing Setup...");
                progress.show();

                d4.child(uid).child("firstname").setValue(firstnamedisplay);
                d4.child(uid).child("lastname").setValue(lastnamedisplay);
                d4.child(uid).child("phone").setValue(phone);
                d4.child(uid).child("username").setValue(signature);
                d4.child(uid).child("image").setValue(uri);
                d4.child(uid).child("joinigdate").setValue(s3);
                d4.child(uid).child("dob").setValue(dob);
                d4.child(uid).child("uid").setValue(mauth.getCurrentUser().getUid());
                progress.dismiss();
                Intent mintent2 = new Intent(SetupAccount.this, HomeActivity.class);
                startActivity(mintent2);
                finish();
            }




              /*

            */


        }

    }
}

