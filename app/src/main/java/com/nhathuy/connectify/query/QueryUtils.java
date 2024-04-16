package com.nhathuy.connectify.query;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.nhathuy.connectify.MainActivity;
import com.nhathuy.connectify.dao.PostDao;
import com.nhathuy.connectify.database.PostDatabase;
import com.nhathuy.connectify.interfaces.OnFetchCommentsCountListener;
import com.nhathuy.connectify.interfaces.OnFetchUserListener;
import com.nhathuy.connectify.model.Post;
import com.nhathuy.connectify.model.User;

import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    private static final int SECOND_MILLIS=1000;
    private  static final int MINUTE_MILLIS=60*SECOND_MILLIS;
    public static final int HOUR_MILLIS=60*MINUTE_MILLIS;
    public static final int DAY_MILLIS=24*HOUR_MILLIS;
    private static FirebaseAuth mAuth;

    public static String getTimeAgo(long postTime) {
        if(postTime<1000000000000L){
            postTime*=1000;
        }

        long now=System.currentTimeMillis();
        
        if(postTime>now||postTime<=0){
            return null;
        }
        final long diff=now-postTime;
        if(diff<MINUTE_MILLIS){
            return "just now";
        }
        else if(diff<2*MINUTE_MILLIS){
            return "a minute ago";
        }
        else if(diff<50*MINUTE_MILLIS){
            return diff/MINUTE_MILLIS +" minutes ago";
        }
        else if(diff<90*MINUTE_MILLIS){
            return "an hour ago";
        }
        else if(diff<24*HOUR_MILLIS){
            return diff/HOUR_MILLIS + " hours ago";
        }
        else if(diff <48*HOUR_MILLIS){
            return "yesterday";
        }
        else {
            return diff/DAY_MILLIS + " days ago";
        }

    }

    public static void fetchCommentsCount(String postAuthName, int postNum, final OnFetchCommentsCountListener onFetchCommentsCountListener) {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        CollectionReference documentReference=db.collection("posts").document(postAuthName)
                .collection("posts").document(String.valueOf(postNum)).collection("comments");
        documentReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                onFetchCommentsCountListener.success(queryDocumentSnapshots.size());
            }
        });
    }

    public static void submitReact(boolean reactType, String postAuth, int postId, String userName) {
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        DocumentReference documentReference=db.collection("posts").document(postAuth).collection("posts").document(String.valueOf(postId));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                ArrayList<String> likeNames= (ArrayList<String>) documentSnapshot.get("likes_array");
                ArrayList<String> dislikeNames= (ArrayList<String>) documentSnapshot.get("dislikes_array");
                if(!likeNames.contains(userName) && !dislikeNames.contains(userName)){
                    if(reactType){
                        documentReference.update("likes", FieldValue.increment(1));
                        documentReference.update("liked",true);
                        ArrayList<String> newLikeName=likeNames;
                        newLikeName.add(userName);
                        documentReference.update("likes_array",newLikeName);
                    }
                    else{
                        documentReference.update("dislikes", FieldValue.increment(1));
                        documentReference.update("liked",true);
                        ArrayList<String> newLikeName=likeNames;
                        newLikeName.add(userName);
                        documentReference.update("dislikes_array",newLikeName);
                    }
                }
                else if(likeNames.contains(userName) && !dislikeNames.contains(userName)){
                    if(!reactType){
                        documentReference.update("likes",FieldValue.increment(-1));
                        documentReference.update("dislikes",FieldValue.increment(1));
                        documentReference.update("liked",false);
                        ArrayList<String>  newLikeName=likeNames;
                        newLikeName.remove(likeNames.indexOf(userName));
                        documentReference.update("likes_array",newLikeName);
                        ArrayList<String> newDislikeNams=dislikeNames;
                        newDislikeNams.add(userName);
                        documentReference.update("dislikes_array",newDislikeNams);
                    }
                }
                else if(!likeNames.contains(userName) && dislikeNames.contains(userName)){
                    if(!reactType){
                        documentReference.update("likes",FieldValue.increment(1));
                        documentReference.update("dislikes",FieldValue.increment(-1));
                        documentReference.update("liked",true);
                        ArrayList<String>  newLikeName=likeNames;
                        newLikeName.add(userName);
                        documentReference.update("likes_array",newLikeName);
                        ArrayList<String> newDislikedNames=dislikeNames;
                        newDislikedNames.remove(dislikeNames.indexOf(userName));
                        documentReference.update("dislikes_array",newDislikedNames);
                    }
                }
            }

        });
    }

    public static void fetchUserInfo(String displayName, OnFetchUserListener onFetchUserListener) {

        FirebaseFirestore db=FirebaseFirestore.getInstance();

        db.collection("users").document(displayName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String username,profilePic,firstName,lastName,bio;
                    int following,followers;
                    List<String> followingList;
                    List<String> followerList;
                    username=documentSnapshot.get("username").toString();
                    profilePic=documentSnapshot.get("profilePic").toString();
                    lastName=documentSnapshot.get("last_name").toString();
                    firstName=documentSnapshot.get("first_name").toString();
                    bio=documentSnapshot.get("bio").toString();
                    following=Integer.parseInt(documentSnapshot.get("following").toString());
                    followers=Integer.parseInt(documentSnapshot.get("followers").toString());
                    followingList= (List<String>) documentSnapshot.get("following_array");
                    followerList= (List<String>) documentSnapshot.get("followers_array");

                    User user=new User();
                    user.setBio(bio);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setUsername(username);
                    user.setFollowers(followers);
                    user.setFollowing(following);
                    user.setFollowers_list(followerList);
                    user.setFollowing_list(followingList);
                    onFetchUserListener.onSuccess(user);
                }
                else{
                    onFetchUserListener.onFailure();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Query","Error fetch user Info");
            }
        });
    }

    public static void fetchPost(Context context, String following) {
        mAuth=FirebaseAuth.getInstance();
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        PostDatabase postDatabase=PostDatabase.getInstance(context);
        CollectionReference documentReference=db.collection("posts").document(following).collection("posts");
        documentReference.addSnapshotListener(MetadataChanges.INCLUDE, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    for (int i = 0; i < value.size(); ++i) {
                        DocumentSnapshot documentSnapshot=value.getDocuments().get(i);
                        if(documentSnapshot.exists()){
                            String postAuthor=following;
                            Post post= new Post();
                            String postAuthImage=documentSnapshot.get("author_pic").toString();
                            String postDec=documentSnapshot.get("dec").toString();
                            Long postTime=documentSnapshot.getLong("time");
                            int postNum=Integer.valueOf(documentSnapshot.get("post_num").toString());
                            String postImage=documentSnapshot.get("pic").toString();
                            String postLike=documentSnapshot.get("likes").toString();
                            String postDisliked=documentSnapshot.get("dislikes").toString();
                            Boolean postLiked=documentSnapshot.getBoolean("liked");
                            Boolean hasPic=documentSnapshot.getBoolean("has_pic");
                            List<String> likesList= (List<String>) documentSnapshot.get("likes_array");
                            List<String> dislikesList= (List<String>) documentSnapshot.get("dislikes_array");

                            post.setPostId(postTime);
                            post.setPostAuthImage(postAuthImage);
                            post.setPostTime(postTime);
                            post.setPostImage(postImage);
                            post.setPostDec(postDec);
                            post.setLike(Integer.valueOf(postLike));
                            post.setDislike(Integer.valueOf(postDisliked));
                            post.setLiked(postLiked);
                            post.setHasImage(hasPic);
                            post.setDislikes_list(dislikesList);
                            post.setLikes_list(likesList);

                            postDatabase.postDao().save(post);
                        }
                    }
                }
            }
        });
    }
}
