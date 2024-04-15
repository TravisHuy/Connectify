package com.nhathuy.connectify.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.nhathuy.connectify.dao.PostDao;
import com.nhathuy.connectify.model.Converters;
import com.nhathuy.connectify.model.Post;

@Database(entities = {Post.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PostDatabase extends RoomDatabase {
    private static PostDatabase INSTANCE;

    public static final Object object=new Object();

    public abstract PostDao postDao();

    public static PostDatabase getInstance(Context context){
        synchronized (object){
            if(object==null){
                INSTANCE= Room.databaseBuilder(context.getApplicationContext(),
                        PostDatabase.class,"post.db").allowMainThreadQueries().build();
            }
        }

        return INSTANCE;
    }
}
