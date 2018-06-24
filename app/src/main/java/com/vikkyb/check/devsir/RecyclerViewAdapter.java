package com.vikkyb.check.devsir;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    String[] values;
     private Context context1;
    int[] images;
    public RecyclerViewAdapter(Context context1,String[] values2,int[] images){

        values = values2;

        this.context1 = context1;
        this.images=images;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        public ImageView imageView;
        public ViewHolder(View v){

            super(v);

            textView = (TextView) v.findViewById(R.id.textview);
            imageView=(ImageView)v.findViewById(R.id.imageview);
            v.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            if(getPosition()==0)
            {
                Intent intent=new Intent(context1,MainActivity.class);
SharedPreferences sharedPreferences=context1.getSharedPreferences("dbname",Context.MODE_PRIVATE);
SharedPreferences.Editor editor=sharedPreferences.edit();
editor.putString("dbn","Blog");
editor.commit();
context1.startActivity(intent);

            }
            else if(getPosition()==1)
            {
                Intent intent=new Intent(context1,MainActivity.class);
                SharedPreferences sharedPreferences=context1.getSharedPreferences("dbname",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("dbn","Science");
                editor.commit();

                context1.startActivity(intent);

            }
            else if(getPosition()==2)
            {
                Intent intent=new Intent(context1,MainActivity.class);
                SharedPreferences sharedPreferences=context1.getSharedPreferences("dbname",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("dbn","Comedy");
                editor.commit();
                context1.startActivity(intent);

            }
            else if(getPosition()==3)
            {
                Intent intent=new Intent(context1,MainActivity.class);
                SharedPreferences sharedPreferences=context1.getSharedPreferences("dbname",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("dbn","Maths");
                editor.commit();
                context1.startActivity(intent);

            }
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view1 = LayoutInflater.from(context1).inflate(R.layout.recycler_view_items,parent,false);

        ViewHolder viewHolder1 = new ViewHolder(view1);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position){

        Vholder.textView.setText(values[position]);

        Vholder.imageView.setImageResource(images[position]);

    }

    @Override
    public int getItemCount(){

        return values.length;
    }


}