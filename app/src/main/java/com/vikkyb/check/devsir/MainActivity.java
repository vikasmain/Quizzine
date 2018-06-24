package com.vikkyb.check.devsir;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private RecyclerView re;
    private FirebaseAuth mauth;
    private Boolean onprocessclikc=false;
    private Boolean ondislkieclick=false;
    private DatabaseReference d3;
    private DatabaseReference d;
    private DatabaseReference mdatabaselike;
    private static DatabaseReference mCommentsReference;
    private FirebaseAuth.AuthStateListener mauthstatelistener;
    ImageView profile;
    private Query mquery2;
    String image,current,data,flastname="";
    LinearLayoutManager linearLayoutManager;
    TextView t1;
    static long comment;
    private DatabaseReference mdatabasedislike;
    SharedPreferences sharedPreferences;
    String dbref;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences=getApplicationContext().getSharedPreferences("dbname",MODE_PRIVATE);
        dbref=sharedPreferences.getString("dbn","NotFound");
        toolbar.setTitle("Hot Questions");
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        toolbar.setTitleTextColor(getResources().getColor(R.color.blue));
        mauth=FirebaseAuth.getInstance();
        current=mauth.getCurrentUser().getUid();
        mauthstatelistener=new FirebaseAuth.AuthStateListener() {//this is for checking whether user is logged in or not.
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){//means our user is not logged in
                    Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        };

        d= FirebaseDatabase.getInstance().getReference().child(dbref);
        d.keepSynced(true);
        d3=FirebaseDatabase.getInstance().getReference().child("Users");
        d3.keepSynced(true);
        mdatabaselike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mdatabaselike.keepSynced(true);
        mdatabasedislike = FirebaseDatabase.getInstance().getReference().child("DisLikes");
        mdatabasedislike.keepSynced(true);
        mquery2=d.orderByChild("likeCount").limitToFirst(100);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments");
        re=(RecyclerView)findViewById(R.id.my_recycler_view);
        linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        if (re != null) {
            re.setHasFixedSize(true);
        }
        re.setLayoutManager(linearLayoutManager);
        FloatingActionButton floatingActionButton=(FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this,PostActivity.class);
//                intent.putExtra("dbname",dbref);
                startActivity(intent);
            }
        });
    }
    public static class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
       /* */
        mauth.addAuthStateListener(mauthstatelistener);
        FirebaseRecyclerAdapter<blog,BlogViewholder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<blog, BlogViewholder>(
                blog.class,
                R.layout.model_post,
                BlogViewholder.class,
                mquery2
        ) {
            @Override
            protected void populateViewHolder(final BlogViewholder viewHolder, final blog model, int position) {
               final String post_key=getRef(position).getKey();
                final DatabaseReference post_ref=getRef(position);


                viewHolder.setTitle(model.getTitle());
                viewHolder.setusers(model.getUsers());

                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setLikeButton(post_key);

                viewHolder.likesnum.setText(String.valueOf(model.likeCount));
                viewHolder.like.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        onprocessclikc=true;

                        mdatabaselike.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (onprocessclikc) {
                                    if (dataSnapshot.child(post_key).hasChild(mauth.getCurrentUser().getUid())) {
                                        Log.i("D Diary", "User has already Liked. So it can be considered as Unliked.");
                                        mdatabaselike.child(post_key).child(mauth.getCurrentUser().getUid()).removeValue();
                                        updateCounter(d);
                                        onprocessclikc = false;

                                    } else {
                                        Log.i("D Diary", "User Liked");
                                        mdatabaselike.child(post_key).child(mauth.getCurrentUser().getUid()).setValue("RandomValue");
                                        updateCounter(d);
                                        Log.i(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "Count");
                                        onprocessclikc = false;
                                    }
                                }
                            }

                            private void updateCounter(final DatabaseReference b) {
                                post_ref.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        blog p = mutableData.getValue(blog.class);
                                        if (p == null) {
                                            return Transaction.success(mutableData);
                                        }

                                        if (p.stars.containsKey(mauth.getCurrentUser().getUid())) {
                                            // Unstar the post and remove self from stars
                                            p.likeCount = p.likeCount - 1;
                                            p.stars.remove(mauth.getCurrentUser().getUid());
                                        } else {
                                            // Star the post and add self to stars
                                            p.likeCount = p.likeCount + 1;
                                            p.stars.put(mauth.getCurrentUser().getUid(), true);
                                        }

                                        // Set value and report transaction success
                                        mutableData.setValue(p);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                        // Transaction completed
                                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }


                });
                viewHolder.setDisLikeButton(post_key);

                viewHolder.dislikesnum.setText(String.valueOf(model.dislikeCount));
                viewHolder.dislike.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ondislkieclick=true;

                        mdatabasedislike.addValueEventListener(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (ondislkieclick) {
                                    if (dataSnapshot.child(post_key).hasChild(mauth.getCurrentUser().getUid())) {
                                        Log.i("D Diary", "User has already Liked. So it can be considered as Unliked.");
                                        mdatabasedislike.child(post_key).child(mauth.getCurrentUser().getUid()).removeValue();
                                        updateCounter(d);
                                        ondislkieclick = false;

                                    } else {
                                        Log.i("D Diary", "User Liked");
                                        mdatabasedislike.child(post_key).child(mauth.getCurrentUser().getUid()).setValue("RandomValue");
                                        updateCounter(d);
                                        Log.i(dataSnapshot.getKey(), dataSnapshot.getChildrenCount() + "Count");
                                        ondislkieclick = false;
                                    }
                                }
                            }
                            private void updateCounter(final DatabaseReference b) {
                                post_ref.runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        blog p = mutableData.getValue(blog.class);
                                        if (p == null) {
                                            return Transaction.success(mutableData);
                                        }

                                        if (p.disstars.containsKey(mauth.getCurrentUser().getUid())) {
                                            // Unstar the post and remove self from stars
                                            p.dislikeCount = p.dislikeCount - 1;
                                            p.disstars.remove(mauth.getCurrentUser().getUid());
                                        } else {
                                            // Star the post and add self to stars
                                            p.dislikeCount = p.dislikeCount + 1;
                                            p.disstars.put(mauth.getCurrentUser().getUid(), true);
                                        }

                                        // Set value and report transaction success
                                        mutableData.setValue(p);
                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b,
                                                           DataSnapshot dataSnapshot) {
                                        // Transaction completed
                                        Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                                    }
                                });

                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        })   ;
                    }


                });
                viewHolder.vi.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent newIntent=new Intent(MainActivity.this,Tedt.class);
                        newIntent.putExtra(Tedt.EXTRA_POST_KEY,post_key);
                        startActivity(newIntent);
                    }
                });
                }
            };
        re.setAdapter(firebaseRecyclerAdapter);

    }
public  static class BlogViewholder extends RecyclerView.ViewHolder {
        View vi;
        String current;
        ImageView like,dislike;
        TextView likesnum,dislikesnum;
        DatabaseReference mdatabaselike,mdatabasedislike, d3;
        FirebaseAuth mauth;
        TextView textView;
        ImageView profile;
        EditText e1;
        public BlogViewholder(View itemView) {
            super(itemView);
            vi = itemView;
            mauth = FirebaseAuth.getInstance();
            current = mauth.getCurrentUser().getUid();
            mdatabaselike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mdatabasedislike = FirebaseDatabase.getInstance().getReference().child("DisLikes");
            d3 = FirebaseDatabase.getInstance().getReference().child("Likes");
            mdatabaselike.keepSynced(true);
            like = (ImageView) itemView.findViewById(R.id.like);
            likesnum = (TextView) itemView.findViewById(R.id.nofolikes);
            dislike = (ImageView) itemView.findViewById(R.id.dislike);
            dislikesnum = (TextView) itemView.findViewById(R.id.textView3);
            textView=(TextView)itemView.findViewById(R.id.textview);
         }

        public void setImage(final Context context, String ima1) {
            profile = (ImageView) vi.findViewById(R.id.profile_image);
            Picasso.with(context).load(ima1).transform(new CircleTransform()).into(profile);
       }
        public void setusers(String users) {
            TextView textView = (TextView) vi.findViewById(R.id.textView1);
            textView.setText(users);
        }
        public void setTitle(String title) {
            TextView textView = (TextView) vi.findViewById(R.id.post);
            textView.setText(title);
        }
        public void setLikeButton(final String post_key) {
            mdatabaselike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mauth.getCurrentUser().getUid())) {//we are looking inside the like folder in firebase like exists or not
                        like.setImageResource(R.mipmap.like_colored);
                    } else {
                        like.setImageResource(R.mipmap.like_gray);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        public void setDisLikeButton(final String post_key) {
            mdatabasedislike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).hasChild(mauth.getCurrentUser().getUid())) {//we are looking inside the like folder in firebase like exists or not
                        dislike.setImageResource(R.drawable.dislikegreen);
                    } else {
                        dislike.setImageResource(R.drawable.downgrey);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public void onBackPressed() { super.onBackPressed();}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_add)
{
    Intent intent=new Intent(MainActivity.this,PostActivity.class);
//    intent.putExtra("dbname",dbref);
    startActivity(intent);
}
else if(id==R.id.action_settings)
        {
            signOut();
        }
return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id==R.id.action_logout)
        { signOut();}
        return true;
    }
    public void signOut() {
        // Firebase sign out
        AuthUI.getInstance()
                .signOut(MainActivity.this)
                .addOnCompleteListener(new OnCompleteListener<Void>(){

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        // do something here

                    }
                });
    }
}
