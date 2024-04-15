package com.nhathuy.connectify.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.nhathuy.connectify.dao.UserDao;
import com.nhathuy.connectify.model.Converters;
import com.nhathuy.connectify.model.User;

@Database(entities = {User.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class UserDatabase extends RoomDatabase {
    public static UserDatabase INSTANCE;

    public abstract UserDao userDao();
    public static final Object  object=new Object();

    public static UserDatabase getInstance(Context context){
        synchronized (object){
            if(INSTANCE==null){
                INSTANCE= Room.databaseBuilder(context.getApplicationContext(),
                        UserDatabase.class,"user.db").allowMainThreadQueries()
                        .build();
            }
        }
        return INSTANCE;
    }
}
