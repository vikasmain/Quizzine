package com.vikkyb.check.devsir;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
FirebaseAuth mauth;
FirebaseAuth.AuthStateListener mauthstatelistener;
DatabaseReference d3,databaseReference,d;
String first_name,last_name,dateofjoin,imagetake;
String current;
    ImageView displayuserprofile;
    RecyclerView rv;
    RecyclerViewAdapter recyclerviewadapter;
    TextView flst;
ImageView imageView;
TextView textView;
    String name[]={"Gadgets","Science and Technology","Comedy","Mathematics"};
    int image[]={R.drawable.mac,R.drawable.sciennce,R.drawable.comedy,R.drawable.pi};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View hView =  navigationView.getHeaderView(0);

        displayuserprofile=(ImageView)hView.findViewById(R.id.displayuser);
        flst=(TextView)hView.findViewById(R.id.flastname);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Quizzine");
        toolbar.setTitleTextColor(getResources().getColor(R.color.blue));
        toolbar.setBackgroundColor(getResources().getColor(R.color.white));
        imageView=(ImageView)findViewById(R.id.im1);
        textView=(TextView)findViewById(R.id.t1);
        rv=(RecyclerView) findViewById(R.id.gridview);
        mauth= FirebaseAuth.getInstance();
        mauthstatelistener=new FirebaseAuth.AuthStateListener() {//this is for checking whether user is logged in or not.
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){//means our user is not logged in
                    Intent intent=new Intent(HomeActivity.this,LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            }
        };
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        d= FirebaseDatabase.getInstance().getReference().child("Blog");
        d.keepSynced(true);
        d3=FirebaseDatabase.getInstance().getReference().child("Users");
        d3.keepSynced(true);
        if(mauth.getCurrentUser()==null)
        {

        }
        else {
            current=mauth.getCurrentUser().getUid();

            d3.child(current).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    first_name = (String) dataSnapshot.child("firstname").getValue();
                    last_name = (String) dataSnapshot.child("lastname").getValue();
                    imagetake = (String) dataSnapshot.child("image").getValue();
                    dateofjoin = (String) dataSnapshot.child("dob").getValue();


                    flst.setText(first_name + " " + last_name);
                    textView.setText(first_name + " " + last_name);
                    Picasso.with(HomeActivity.this).load(imagetake).transform(new CircleTransform()).into(displayuserprofile);
                    Picasso.with(HomeActivity.this).load(imagetake).transform(new CircleTransform()).into(imageView);


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        recyclerviewadapter = new RecyclerViewAdapter(this,name,image);

        rv.setLayoutManager(new GridLayoutManager(HomeActivity.this,2));
        rv.setAdapter(recyclerviewadapter);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        checkuserexists();

    }
    private void checkuserexists() {
        if(mauth.getCurrentUser() !=null) {
            final String uid = mauth.getCurrentUser().getUid();


            d3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(uid)) {
                        Intent mintent = new Intent(HomeActivity.this, SetupAccount.class);
                        mintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mintent);
                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mauthstatelistener);

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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id==R.id.profile)
        {
            Intent intent=new Intent(HomeActivity.this,QuizActivity.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
