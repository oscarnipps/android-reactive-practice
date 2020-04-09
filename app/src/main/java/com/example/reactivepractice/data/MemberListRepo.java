package com.example.reactivepractice.data;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;

import com.example.reactivepractice.api.ApiClient;
import com.example.reactivepractice.api.MemberApiService;
import com.example.reactivepractice.api.model.MemberGetApiBaseResponse;
import com.example.reactivepractice.data.dao.MembersDao;
import com.example.reactivepractice.data.model.Member;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Response;

public class MemberListRepo {

    private static MemberListRepo mInstance;
    private MemberApiService memberApiService;
    private MembersDao membersDao;


    public static MemberListRepo getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MemberListRepo.class) {
                if (mInstance == null) {
                    mInstance = new MemberListRepo(context.getApplicationContext());
                }
            }
        }
        return  mInstance;
    }

    private MemberListRepo(Context context) {
        //api service
        memberApiService = ApiClient.getApiClient(context).create(MemberApiService.class);
        //no need to get the entire database just get the dao needed
        membersDao = MemberDatabase.getInstance(context).getMemberDao();
    }

    public Single<Response<MemberGetApiBaseResponse>> getMembersFromServer() {
        return memberApiService.getMembers();
    }

    public LiveData<List<Member>> getMembersFromDatabase() {
        //return a live data
        return LiveDataReactiveStreams.fromPublisher(membersDao.getMembersFromDatabase());

    }

    public Completable deleteMembersFromDatabase() {
        return membersDao.deleteAllMembersFromDatabase();
    }

    public List<Long> insertMembersIntoDatabase(List<Member> members) {
        return membersDao.insertMembersIntoDatabase(members);
    }

    public Single<List<Long>> insertMembersIntoDatabaseSingle(List<Member> members) {
       return membersDao.insertMembersIntoDatabaseSingle(members);
    }
}
