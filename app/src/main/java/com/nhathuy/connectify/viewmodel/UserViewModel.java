package com.nhathuy.connectify.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nhathuy.connectify.model.User;
import com.nhathuy.connectify.repo.UserRepository;

import java.util.List;

public class UserViewModel extends AndroidViewModel {
    private UserRepository userRepository;
    private LiveData<List<User>> allUsers;
    private User user;
    public UserViewModel(@NonNull Application application,String userName) {
        super(application);
        userRepository=new UserRepository(application,userName);
        allUsers= userRepository.getAllUser();
        user= userRepository.getByUserName();
    }

    public User getUser() {
        return user;
    }

    public LiveData<List<User>> getAllUsers() {
        return allUsers;
    }
    public void saveUser(final User user){
        userRepository.insert(user);
    }
}
