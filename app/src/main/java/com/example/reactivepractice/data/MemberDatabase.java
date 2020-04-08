package com.example.reactivepractice.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.reactivepractice.data.dao.MembersDao;
import com.example.reactivepractice.data.model.Member;


@Database(entities = {Member.class},version = 1 ,exportSchema = false)
public abstract class MemberDatabase extends RoomDatabase {

    private static volatile MemberDatabase mInstance;
    private static final String DATABASE_NAME = "member_database.db";
    public static final String TAG = MemberDatabase.class.getSimpleName();
    public abstract MembersDao getMemberDao();

    public static MemberDatabase getInstance(Context context) {
        if (mInstance == null) {
            return createDatabaseInstance(context);
        }
        return mInstance;
    }

    private static MemberDatabase createDatabaseInstance(Context context) {
        mInstance = Room.databaseBuilder(
                context,
                MemberDatabase.class,
                DATABASE_NAME
        ).build();

        return mInstance;
    }
}
