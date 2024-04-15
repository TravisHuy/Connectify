package com.nhathuy.connectify.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nhathuy.connectify.model.Post;

import java.util.List;

@Dao
public interface PostDao {

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     void saveAll(List<Post> posts);

     @Insert(onConflict = OnConflictStrategy.REPLACE)
     void save(Post post);

     @Update
     void update(Post post);

     @Delete
     void delete(Post post);

     @Query("select *from post where postId in (:postIds)")
     List<Post> findAllByIds(long[] postIds);

     @Query("select *from post where postId in (:postId)")
     List<Post> findAllByPostId(int postId);

     @Query("select *from post order by postId asc")
     LiveData<Post> findLiveData();
     @Query("select * from post")
     List<Post> findAll();

     @Query("delete from post")
     void deleteAll();
}
