package com.nhathuy.connectify.repo;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.nhathuy.connectify.dao.UserDao;
import com.nhathuy.connectify.database.UserDatabase;
import com.nhathuy.connectify.model.User;

import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private LiveData<List<User>> allUser;
    private User byUserName;


    public UserRepository(Application application){
        UserDatabase db=UserDatabase.getInstance(application);
        userDao=db.userDao()
                ;
        allUser=userDao.findLiveData();
    }

    public UserRepository(Application application,String name){
        UserDatabase db=UserDatabase.getInstance(application);
        userDao=db.userDao()
        ;
        byUserName=userDao.findAllByUserName(name);
    }



    public LiveData<List<User>> getAllUser() {
        return allUser;
    }

    public void setAllUser(LiveData<List<User>> allUser) {
        this.allUser = allUser;
    }

    public User getByUserName() {
        return byUserName;
    }

    public void setByUserName(User byUserName) {
        this.byUserName = byUserName;
    }

    public void insert(User user){
        new InsertAsyncTask(userDao).doInBackground(user);
    }
    private static class InsertAsyncTask extends AsyncTask<User,Void,Void>{
        private UserDao mUserDao;

        InsertAsyncTask(UserDao dao){
            mUserDao=dao;
        }
        @Override
        protected Void doInBackground(User... users) {
            mUserDao.save(users[0]);
            return null;
        }
    }
}
