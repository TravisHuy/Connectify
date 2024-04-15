package com.nhathuy.connectify.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.nhathuy.connectify.dao.PostDao;
import com.nhathuy.connectify.database.PostDatabase;
import com.nhathuy.connectify.model.Post;

import java.util.List;


public class PostRepository {
    private PostDao postDao;
    private LiveData<List<Post>> allPost;
    private PostDatabase db;
    public PostRepository(Application application){
        db=PostDatabase.getInstance(application);
        postDao=db.postDao();
        allPost=postDao.findLiveData();
    }

    public PostDao getPostDao() {
        return postDao;
    }

    public LiveData<List<Post>> getAllPost() {
        return allPost;
    }
    public void insert(Post post){
        new insertAsyncTask(postDao).doInBackground(post);
    }
    public static class insertAsyncTask extends AsyncTask<Post,Void,Void>{
        private PostDao dao;
        insertAsyncTask(PostDao dao){
            dao=dao;
        }
        @Override
        protected Void doInBackground(Post... posts) {
            dao.save(posts[0]);
            return null;
        }
    }

}
