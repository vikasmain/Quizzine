package com.vikkyb.check.devsir;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Tedt extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "Tedt";
FirebaseAuth mauth;
FirebaseUser mcurrentuser;
    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener,mcommentlistener;
    private String mPostKey;
    private CommentAdapter mAdapter;
    private SeekBar seekBar;
    private TextView mTitleView,commen;
    private TextView mBodyView,answerscoun;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    String db;
    long comment2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tedt);
        answerscoun=(TextView)findViewById(R.id.answers);
        // Get post key from intent
        sharedPreferences=getApplicationContext().getSharedPreferences("dbname",MODE_PRIVATE);
        db=sharedPreferences.getString("dbn","NotFound");
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null)
        {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        mauth= FirebaseAuth.getInstance();

        // Initialize Database
        if(!TextUtils.isEmpty(db)) {
            mPostReference = FirebaseDatabase.getInstance().getReference().child(db).child(mPostKey);
        }
        else
        {
            Toast.makeText(this, "Not Found", Toast.LENGTH_SHORT).show();
            mPostReference = FirebaseDatabase.getInstance().getReference().child("NotFound").child(mPostKey);

        }
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(mPostKey);


               // Initialize Views
        mTitleView = (TextView) findViewById(R.id.t2);
        mBodyView = (TextView) findViewById(R.id.t);
        commen = (TextView) findViewById(R.id.answers);
        mCommentField = (EditText) findViewById(R.id.comment_edittext);
        mCommentButton = (Button) findViewById(R.id.comment_button);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.comments_recyclerview);

        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onStart() {
        super.onStart();
        ValueEventListener commentlistener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                comment2=dataSnapshot.getChildrenCount();
                commen.setText(comment2+" Answers");
                String post_uid = (String) dataSnapshot.child("uid").getValue();




            }
            // [END_EXCLUDE]


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(Tedt.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();

                String post_uid = (String) dataSnapshot.child("uid").getValue();
                if(post_desc!=null)
                {
                    mBodyView.setText(post_desc);

                }
                else
                {
                    Toast.makeText(Tedt.this, "Moet doesn't exist", Toast.LENGTH_SHORT).show();
                }
                if(post_title!=null) {
                    SpannableString content = new SpannableString(post_title);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    mTitleView.setText(content);
                }
                else
                {
                    Toast.makeText(Tedt.this, "Moet's Title doesn't exists", Toast.LENGTH_SHORT).show();
                }

            }
                // [END_EXCLUDE]


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(Tedt.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };

        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;
        mCommentsReference.addValueEventListener(commentlistener);
        mcommentlistener=commentlistener;
        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        // Clean up comments listener
        mAdapter.cleanupListener();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.comment_button) {
            postComment();
        }
    }

    private void postComment() {
        final String uid = mauth.getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information

                        String image=(String )dataSnapshot.child("image").getValue();
                        // Create new comment object
                        String username=(String)dataSnapshot.child("username").getValue();
                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, commentText,username,image);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);

                        // Clear the field
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView bodyView;
        public TextView author;
        public ImageView im1;
        public CommentViewHolder(View itemView) {
            super(itemView);

            bodyView = (TextView) itemView.findViewById(R.id.comment_body);
            author=(TextView)itemView.findViewById(R.id.comment_author);
            im1=(ImageView)itemView.findViewById(R.id.ima);
        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.bodyView.setText(comment.text);
            holder.author.setText(comment.username);
            Picasso.with(mContext).load(comment.image).into(holder.im1);
        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
    public void openBottomSheet(View v) {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        ImageView txtBackup = (ImageView) view.findViewById(R.id.txt_backup);
        ImageView txtDetail = (ImageView) view.findViewById(R.id.txt_detail);
        Button txtUninstall2 = (Button) view.findViewById(R.id.txt_color3);
        Button txtUninstall3 = (Button) view.findViewById(R.id.txt_color4);
        Button txtUninstall4 = (Button) view.findViewById(R.id.txt_color5);
        ImageView linespacing = (ImageView) view.findViewById(R.id.txt_linewidth);
        ImageView linespacing3 = (ImageView) view.findViewById(R.id.txt_linewidth3);

        ImageView linespacing2 = (ImageView) view.findViewById(R.id.txt_linecenter);

        seekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        int brightness = getScreenBrightness();
        seekBar.setProgress(brightness);

        // Set a SeekBar change listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Display the current progress of SeekBar

                // Change the screen brightness
                setScreenBrightness(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String>  categories = new ArrayList <String>();
        categories.add("Robotto");
        categories.add("Georgia");
        categories.add("Gotham");
        categories.add("Shelley");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter <String>(this, android.R.layout.simple_spinner_item, categories)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                return setCentered(super.getView(position, convertView, parent));
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                return setCentered(super.getDropDownView(position, convertView, parent));
            }

            private View setCentered(View view) {
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);




        final Dialog mBottomSheetDialog = new Dialog(Tedt.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


        txtBackup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mBodyView = (TextView) findViewById(R.id.t);

                mBodyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (mBodyView.getTextSize() + 3f));


            }
        });

        txtDetail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mBodyView = (TextView) findViewById(R.id.t);

                mBodyView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (mBodyView.getTextSize() - 3f));

            }
        });

        txtUninstall2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                mBodyView = (TextView) findViewById(R.id.t);
                mBodyView.setTextColor(Color.BLACK);
                mBottomSheetDialog.dismiss();
            }
        });

        txtUninstall3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.sepia));
                mBodyView = (TextView) findViewById(R.id.t);
                mBodyView.setTextColor(Color.BLACK);
                mBottomSheetDialog.dismiss();

            }
        });
        txtUninstall4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                mBodyView = (TextView) findViewById(R.id.t);
                mBodyView.setTextColor(Color.WHITE);
                mBottomSheetDialog.dismiss();
            }
        });
        linespacing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBodyView=(TextView)findViewById(R.id.t);
                mBodyView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f,  getResources().getDisplayMetrics()), 1.0f);
                mBottomSheetDialog.dismiss();
            }
        });
        linespacing3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBodyView=(TextView)findViewById(R.id.t);
                mBodyView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5.0f,  getResources().getDisplayMetrics()), 1.0f);
                mBottomSheetDialog.dismiss();
            }
        });
        linespacing2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBodyView=(TextView)findViewById(R.id.t);
                mBodyView.setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0f,  getResources().getDisplayMetrics()), 1.0f);
                mBottomSheetDialog.dismiss();
                // *** -30 to reduce line spacing

            }
        });
    }

    // Change the screen brightness
    public void setScreenBrightness(int brightnessValue) {
        /*
            public abstract ContentResolver getContentResolver ()
                Return a ContentResolver instance for your application's package.
        */
        /*
            Settings
                The Settings provider contains global system-level device preferences.

            Settings.System
                System settings, containing miscellaneous system preferences. This table holds
                simple name/value pairs. There are convenience functions for accessing
                individual settings entries.
        */
        /*
            public static final String SCREEN_BRIGHTNESS
                The screen backlight brightness between 0 and 255.
                Constant Value: "screen_brightness"
        */
        /*
            public static boolean putInt (ContentResolver cr, String name, int value)
                Convenience function for updating a single settings value as an integer. This will
                either create a new entry in the table if the given name does not exist, or modify
                the value of the existing row with that name. Note that internally setting values
                are always stored as strings, so this function converts the given value to a
                string before storing it.

            Parameters
                cr : The ContentResolver to access.
                name : The name of the setting to modify.
                value : The new value for the setting.
            Returns
                true : if the value was set, false on database errors
        */

        // Make sure brightness value between 0 to 255
        if (brightnessValue >= 0 && brightnessValue <= 255) {
            Settings.System.putInt(
                    getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue
            );
        }

    }

    // Get the screen current brightness
    protected int getScreenBrightness() {
        /*
            public static int getInt (ContentResolver cr, String name, int def)
                Convenience function for retrieving a single system settings value as an integer.
                Note that internally setting values are always stored as strings; this function
                converts the string to an integer for you. The default value will be returned
                if the setting is not defined or not an integer.

            Parameters
                cr : The ContentResolver to access.
                name : The name of the setting to retrieve.
                def : Value to return if the setting is not defined.
            Returns
                The setting's current value, or 'def' if it is not defined or not a valid integer.
        */
        int brightnessValue = Settings.System.getInt(
                getApplicationContext().getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS,
                0
        );
        return brightnessValue;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        if (item.equals("Georgia")) {
            mBodyView = (TextView) findViewById(R.id.t);

            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/georgia.TTF");
            mBodyView.setTypeface(face);
            // Showing selected spinner item
            Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();

        } else if (item.equals("Gotham")) {
            mBodyView = (TextView) findViewById(R.id.t);

            Typeface face = Typeface.createFromAsset(getAssets(), "fonts/gotham.ttf");
            mBodyView.setTypeface(face);
        }
        else if(item.equals("shelley"))
        {
            mBodyView = (TextView) findViewById(R.id.t);

            Typeface face= Typeface.createFromAsset(getAssets(), "fonts/shelley.ttf");
            mBodyView.setTypeface(face);
        }
        else if(item.equals("Robotto"))
        {
            mBodyView = (TextView) findViewById(R.id.t);

            Typeface face= Typeface.createFromAsset(getAssets(), "fonts/Roboto-Black.ttf");
            mBodyView.setTypeface(face);
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
