package com.nhathuy.connectify.interfaces;

import com.nhathuy.connectify.model.User;

public interface OnFetchUserListener {
    void onSuccess(User user);
    void onFailure();
}
