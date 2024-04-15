package com.nhathuy.connectify.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.nhathuy.connectify.R;
import com.nhathuy.connectify.activity.ProfileActivity;
import com.nhathuy.connectify.databinding.PostListBinding;
import com.nhathuy.connectify.model.Post;

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
        holder.binding.postTime.setText(QueyUtils);
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
