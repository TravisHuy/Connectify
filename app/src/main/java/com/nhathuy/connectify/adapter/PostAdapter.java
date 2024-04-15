package com.nhathuy.connectify.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.nhathuy.connectify.R;
import com.nhathuy.connectify.activity.CommentsActivity;
import com.nhathuy.connectify.activity.ProfileActivity;
import com.nhathuy.connectify.callback.PostDiffCallback;
import com.nhathuy.connectify.databinding.PostListBinding;
import com.nhathuy.connectify.interfaces.OnFetchCommentsCountListener;
import com.nhathuy.connectify.model.Post;
import com.nhathuy.connectify.query.QueryUtils;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private List<Post> postList;
    private FirebaseAuth mAuth;
    private LayoutInflater layoutInflater;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
        setHasStableIds(true);
        notifyDataSetChanged();
    }

    public void setData(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }
    public void clear(){
        this.postList.clear();
        notifyDataSetChanged();
    }
    public void updateData(List<Post> newData){
        final PostDiffCallback diffCallback=new PostDiffCallback(this.postList,newData);
        final DiffUtil.DiffResult diffResult=DiffUtil.calculateDiff(diffCallback);
        this.postList.clear();
        this.postList.addAll(newData);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(layoutInflater==null){
            layoutInflater=LayoutInflater.from(parent.getContext());
        }
        PostListBinding binding= DataBindingUtil.inflate(layoutInflater, R.layout.post_list,parent,false);
        return new PostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        holder.binding.setPost(postList.get(position));

        setFadeAnimation(holder.itemView);
        this.mAuth=FirebaseAuth.getInstance();
        final Post post=postList.get(position);

        Post.loadPostAuthImage(holder.binding.authPic,post.getPostAuthImage());
        holder.binding.authPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(holder.itemView.getContext(), ProfileActivity.class);
                intent.putExtra("username",post.getPostAuthName());
                View sharedView=holder.binding.authPicCircle;
                String transName="splash_anim";
                Activity activity=(Activity) holder.itemView.getContext();
                ActivityOptions transitionActivityOptions =ActivityOptions.makeSceneTransitionAnimation(activity,sharedView,transName);
                holder.itemView.getContext().startActivity(intent,transitionActivityOptions.toBundle());
            }
        });

        holder.binding.postAuthUsername.setText(post.getPostAuthName());
        holder.binding.postTime.setText(QueryUtils.getTimeAgo(post.getPostTime()));
        holder.binding.postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] resource=new String[]{post.getPostImage()};

                new StfalconImageViewer.Builder<>(holder.itemView.getContext(), resource, new ImageLoader<String>() {
                    @Override
                    public void loadImage(ImageView imageView, String image) {
                        Glide.with(holder.itemView.getContext())
                                .load(post.getPostImage())
                                .into(imageView);
                    }
                }).withTransitionFrom(holder.binding.postImage).show();
            }
        });
        if(post.isHasImage()){
            Post.loadPostImage(holder.binding.postImage,post.getPostImage());
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P){
                Glide.with(holder.itemView.getContext())
                        .asBitmap()
                        .load(post.getPostImage())
                        .listener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, @Nullable Object model, @NonNull Target<Bitmap> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Bitmap resource, @NonNull Object model, Target<Bitmap> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                               if(resource!=null){
                                   Palette p=Palette.from(resource).generate();
                                   int mutedColor=p.getDarkMutedColor(holder.itemView.getContext().getColor(R.color.colorPrimary));
                                   holder.binding.postCard.setOutlineSpotShadowColor(mutedColor);
                                   holder.binding.postCard.setOutlineAmbientShadowColor(mutedColor);
                               }

                                return false;
                            }
                        }).submit();
            }

        }
        holder.binding.btnIdPostMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu=new PopupMenu(holder.itemView.getContext(),holder.binding.btnIdPostMenu);
                popupMenu.getMenuInflater().inflate(R.menu.post_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.post_download:
                                Glide.with(holder.itemView.getContext())
                                        .asBitmap().load(post.getPostImage())
                                        .into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                saveImage(resource,String.valueOf(post.getPostId()),holder.itemView.getContext());
                             }
                         });
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        holder.binding.descText.setText(post.getPostDec());

        holder.binding.btnLike.setText(String.valueOf(post.getLike()));
        holder.binding.btnDislike.setText(String.valueOf(post.getDislike()));
        QueryUtils.fetchCommentsCount(post.getPostAuthName(),post.getPostNum(), new OnFetchCommentsCountListener() {
            @Override
            public void success(int count) {
                holder.binding.btnIdComment.setText(String.valueOf(count));
            }
        });
        String userName=mAuth.getCurrentUser().getDisplayName();

        if(post.getLikes_list().contains(userName) && !post.getDislikes_list().contains(userName)){
            holder.binding.btnLike.setIconTintResource(R.color.colorAccentDark);
            holder.binding.btnLike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
            holder.binding.btnDislike.setIconTintResource(R.color.colorAccentBlueDark_50);
            holder.binding.btnDislike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));

            holder.binding.btnDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.binding.btnDislike.setIconTintResource(R.color.colorAccentDark);
                    holder.binding.btnDislike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
                    holder.binding.btnLike.setIconTintResource(R.color.colorAccentBlueDark_50);
                    holder.binding.btnLike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
                    holder.binding.btnLike.setText(String.valueOf(post.getLike()-1));
                    holder.binding.btnDislike.setText(String.valueOf(post.getLike()+1));
                    QueryUtils.submitReact(false,post.getPostAuthName(),post.getPostNum(),mAuth.getCurrentUser().getDisplayName());
                }
            });
            holder.binding.btnLike.setOnClickListener(null);
        }
        else if(!post.getLikes_list().contains(userName) && post.getDislikes_list().contains(userName)){
            holder.binding.btnDislike.setIconTintResource(R.color.colorAccentBlueDark_50);
            holder.binding.btnDislike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
            holder.binding.btnLike.setIconTintResource(R.color.colorAccentBlueDark_50);
            holder.binding.btnLike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));

            holder.binding.btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.binding.btnDislike.setIconTintResource(R.color.colorAccentDark);
                    holder.binding.btnDislike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
                    holder.binding.btnLike.setIconTintResource(R.color.colorAccentBlueDark_50);
                    holder.binding.btnLike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
                    holder.binding.btnLike.setText(String.valueOf(post.getLike()+1));
                    QueryUtils.submitReact(true,post.getPostAuthName(),post.getPostNum(),mAuth.getCurrentUser().getDisplayName());
                }
            });
            holder.binding.btnDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.binding.btnDislike.setIconTintResource(R.color.colorAccentDark);
                    holder.binding.btnDislike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentDark));
                    holder.binding.btnLike.setIconTintResource(R.color.colorAccentBlueDark_50);
                    holder.binding.btnLike.setTextColor(holder.itemView.getContext().getColor(R.color.colorAccentBlueDark_50));
                    holder.binding.btnDislike.setText(String.valueOf(post.getLike()+1));
                    QueryUtils.submitReact(false,post.getPostAuthName(),post.getPostNum(),mAuth.getCurrentUser().getDisplayName());
                }
            });
        }
        holder.binding.btnIdComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), CommentsActivity.class);
                intent.putExtra("postAuthor", post.getPostAuthName());
                intent.putExtra("postNum",post.getPostNum());
                intent.putExtra("postAuthorPic",post.getPostAuthImage());
                holder.itemView.getContext().startActivity(intent);
            }
        });
        holder.binding.setPost(post);
    }

    private void saveImage(Bitmap image, String imageView, Context context) {
        String savedImagePath=null;
        String imageFileName= "CONNECTIFY_IMG_"+imageView+".jpg";

        File storageDir=new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)+"/Huy");

        boolean success=true;
        if(!storageDir.exists()){
            success=storageDir.mkdir();
        }
        if(success){
            File imageFile=new File(storageDir,imageFileName);
            savedImagePath=imageFile.getAbsolutePath();
            try {
                OutputStream out=new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG,100,out);
                out.close();
            }catch (Exception e){
                e.printStackTrace();
            }

            galleryAddPic(savedImagePath,context);
            Toast.makeText(context, "Image downloaded", Toast.LENGTH_SHORT).show();
        }
    }

    private void galleryAddPic(String savedImagePath, Context context) {
        Intent mediaScanIntent=new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file=new File(savedImagePath);
        Uri contentUri=Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    private void setFadeAnimation(View itemView) {
        AlphaAnimation anim =new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(500);
        itemView.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    // get item id
    @Override
    public long getItemId(int position) {
        Post post=postList.get(position);
        return post.getPostId();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull PostViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.clearAnimation();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        private final PostListBinding binding;
        public PostViewHolder(final PostListBinding postListBinding) {
            super(postListBinding.getRoot());
            this.binding=postListBinding;
        }
    }
}
