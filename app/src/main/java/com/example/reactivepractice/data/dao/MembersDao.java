package com.example.reactivepractice.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.reactivepractice.data.model.Member;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface MembersDao {

    @Query("SELECT * FROM members")
    Flowable<List<Member>> getMembersFromDatabase();

    @Insert(onConflict = REPLACE)
    List<Long> insertMembersIntoDatabase(List<Member> memberItems);

    @Insert(onConflict = REPLACE)
    Single<List<Long>> insertMembersIntoDatabaseSingle(List<Member> memberItems);

    @Query("DELETE FROM members")
    Completable deleteAllMembersFromDatabase();
}
