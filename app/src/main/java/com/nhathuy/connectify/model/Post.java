package com.nhathuy.connectify.model;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.palette.graphics.Palette;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.nhathuy.connectify.R;


@Entity
public class Post extends BaseObservable {
    @PrimaryKey
    private long postId;
    private String postAuthName;
    private String postAuthImage;
    private long postTime;
    private int postNum;
    private int postCommentsNum;
    private String postImage;
    private String postDec;
    private boolean like;
    private boolean dislike;
    private boolean hasImage;


    public static void loadPostAuthImage(ImageView authPic, String imageUrl) {
        Glide.with(authPic.getContext())
                .load(imageUrl)
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(authPic);
    }
    @BindingAdapter("postImage")
    public static void loadPostImage(ImageView authPic, String imageUrl) {
        Glide.with(authPic.getContext())
                .asBitmap()
                .load(imageUrl)
                .override(Target.SIZE_ORIGINAL)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(@NonNull Bitmap resource, @NonNull Object model, Target<Bitmap> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                        if(resource!=null){
                            Palette p=Palette.from(resource).generate();
                            int mutedColor =p.getMutedColor(authPic.getContext().getColor(R.color.colorPrimaryDark));
                            authPic.setBackgroundColor(mutedColor);
                        }

                        return false;
                    }
                }).into(authPic);
    }


    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getPostAuthName() {
        return postAuthName;
    }

    public void setPostAuthName(String postAuthName) {
        this.postAuthName = postAuthName;
    }

    public String getPostAuthImage() {
        return postAuthImage;
    }

    public void setPostAuthImage(String postAuthImage) {
        this.postAuthImage = postAuthImage;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public int getPostNum() {
        return postNum;
    }

    public void setPostNum(int postNum) {
        this.postNum = postNum;
    }

    public int getPostCommentsNum() {
        return postCommentsNum;
    }

    public void setPostCommentsNum(int postCommentsNum) {
        this.postCommentsNum = postCommentsNum;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostDec() {
        return postDec;
    }

    public void setPostDec(String postDec) {
        this.postDec = postDec;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isDislike() {
        return dislike;
    }

    public void setDislike(boolean dislike) {
        this.dislike = dislike;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }
}
