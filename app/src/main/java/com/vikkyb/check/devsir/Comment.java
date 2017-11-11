package com.vikkyb.check.devsir;

import com.google.firebase.database.IgnoreExtraProperties;

// [START comment_class]
@IgnoreExtraProperties
public class Comment {

    public String uid;
    public String text;
   public String username;
   public String image;
    public Comment() {
        // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    }

    public Comment(String uid, String text,String username,String image) {
        this.uid = uid;
        this.text = text;
        this.username=username;
        this.image=image;
    }

}
// [END comment_class]
