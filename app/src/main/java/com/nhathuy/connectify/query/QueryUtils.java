package com.nhathuy.connectify.query;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nhathuy.connectify.interfaces.OnFetchCommentsCountListener;

import java.util.ArrayList;

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
}
