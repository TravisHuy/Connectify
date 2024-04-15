package com.nhathuy.connectify.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.nhathuy.connectify.model.User;

import java.util.List;

import javax.inject.Inject;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<User> users);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void save(User user);
    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("select * from user where uid in (:userId)")
    List<User> findAllByIds(int[] userId);

    @Query("select *from user where username in(:userName)")
    User findAllByUserName(String userName);

    @Query("select * from user")
    List<User> findAll();

    @Query("select * from user order by username asc")
    LiveData<List<User>> findLiveData();


    @Query("delete from user")
    void deleteAll();
}
