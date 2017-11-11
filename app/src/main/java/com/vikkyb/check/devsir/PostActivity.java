package com.vikkyb.check.devsir;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by dell on 04-04-2017.
 */
@SuppressWarnings("deprecation")
public class PostActivity extends Activity
{
    private ImageButton imageButton;
    private EditText e1,e3;
    private Button b1;
    static EditText e2;
    private ProgressDialog progressDialog;
    String[] tags ={"C","C++","Java","Programming","iPhone","android","ASP.NET","PHP","JavaScript","Angularjs","Tech"
            ,"Social","History","Geography","Ecommerce","Commerce","Accounts"};
    String imagetake;
    String user;
    Button b;
    private StorageReference storageReference;
    private static final int galleryrequest = 1;
    private FirebaseUser mcurrentuser;
    private DatabaseReference mDatabaseUsers;
    private static final int PERMISSIONS_REQUEST_READ_STORAGE = 100;
    private DatabaseReference databaseReference;
    private ImageView im1, im2;
    private FirebaseAuth mauth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mauth = FirebaseAuth.getInstance();

        mcurrentuser = mauth.getCurrentUser();
        Intent intent=getIntent();
        storageReference = FirebaseStorage.getInstance().getReference();//root directory
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        //root directory
        progressDialog=new ProgressDialog(this);

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(mcurrentuser.getUid());
        e1 = (EditText) findViewById(R.id.editText);


        e3=(EditText)findViewById(R.id.editTextnakoli);
        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imagetake = (String) dataSnapshot.child("image").getValue();
                user=(String)dataSnapshot.child("username").getValue();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        b1 = (Button) findViewById(R.id.save);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,tags);
        //Getting the instance of AutoCompleteTextView
        AutoCompleteTextView actv= (AutoCompleteTextView)findViewById(R.id.tags);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startposting();
            }
        });

    }
private void startposting()
{
   final String title=e1.getText().toString().trim();
    final String desc=e3.getText().toString().trim();
    progressDialog.setTitle("Wait");
progressDialog.setMessage("Saving Your Moet");
    if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(desc))
    {

                    progressDialog.show();



                    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                    final DatabaseReference newpost=databaseReference.push();

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newpost.child("title").setValue(title);
                        newpost.child("desc").setValue(desc);
                        newpost.child("image").setValue(imagetake);
                        newpost.child("users").setValue(user);
                        newpost.child("uid").setValue(mcurrentuser.getUid());
                        newpost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener
                                (new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    startActivity(new Intent(PostActivity.this,MainActivity.class));
                                }
                                else
                                {
                                }
                            }
                        });//datasnapshot returns everything inside random id(0dkm003kmd39iok) object in firebase

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                progressDialog.dismiss();

            }


    else {
        Toast.makeText(PostActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();

    }
}


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == galleryrequest && resultCode == RESULT_OK) {
//            Uri imageri = data.getData();
//            CropImage.activity(imageri)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(1,1)
//                    .start(this);
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                imageuri=result.getUri();
//                imageButton.setImageURI(imageuri);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//    }
}

