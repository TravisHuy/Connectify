package com.nhathuy.connectify.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nhathuy.connectify.model.Post;
import com.nhathuy.connectify.repo.PostRepository;

import java.util.List;

public class PostViewModel extends AndroidViewModel {
    private PostRepository repository;
    private LiveData<List<Post>> allPosts;
    public PostViewModel(@NonNull Application application) {
        super(application);
        repository=new PostRepository(application);
        allPosts=repository.getAllPost();
    }

    public LiveData<List<Post>> getAllPosts() {
        return allPosts;
    }
    public void savePost(final Post post){
        repository.insert(post);
    }

}
