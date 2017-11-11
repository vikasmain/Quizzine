package com.vikkyb.check.devsir;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dell on 06-04-2017.
 */
public class blog {
    private String users;
    private String number;
    private String title;

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    private String desc;
    private String image,image2;
    private String uid;
    private String deeplinks;
    private long noofshares;
    private String jokeKey;
    private long timeCreated;
    private long commentcount=0;
    public String getGenre() {
        return genre;
    }

    public long getCommentcount() {
        return commentcount;
    }

    public void setCommentcount(long commentcount) {
        this.commentcount = commentcount;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    private String genre;

    public String getDeeplinks() {
        return deeplinks;
    }

    public void setDeeplinks(String deeplinks)
    {
        this.deeplinks = deeplinks;
    }

    public long getNoofshares()
    {
        return noofshares;
    }

    public void setNoofshares(long noofshares)
    {
        this.noofshares = noofshares;
    }

    public Map<String, Boolean> stars = new HashMap<>();
    public Map<String, Boolean> disstars = new HashMap<>();

    public Long timecreated;
    public int likeCount = 0;
    public int dislikeCount=0;
    public int sharecount=0;

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public blog(String title, String desc, String image, String uid, String deeplinks, long noofshares) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.uid = uid;
        this.deeplinks=deeplinks;
        this.noofshares=noofshares;
    }



/*
    @Exclude
        public Map<String, Object> toMap() {
            HashMap<String, Object> result = new HashMap<>();
            result.put("uid", uid);
            result.put("title", title);

            result.put("desc", desc);
            result.put("image", image);
            result.put("jokeKey", jokeKey);
            result.put("likeCount", likeCount);
            result.put("like",stars);

            return result;
        }
  */  public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }




    public Long getTimecreated() {
        return timecreated;
    }

    public void setTimecreated(Long timecreated) {
        this.timecreated = timecreated;
    }


    public String getTitle() {
        return title;
    }
public blog(){

}

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
