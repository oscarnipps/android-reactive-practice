package com.example.reactivepractice.sync;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.reactivepractice.api.model.MemberApiPostRes;
import com.example.reactivepractice.api.model.MemberGetApiBaseResponse;
import com.example.reactivepractice.data.MemberRepo;
import com.example.reactivepractice.data.model.Member;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MemberWorker extends Worker {

    public static final String TAG = MemberWorker.class.getSimpleName();
    private int statusCode;
    private String message;
    MemberRepo memberRepo;

    public MemberWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //repo instance
        memberRepo = MemberRepo.getInstance(getApplicationContext());

        //gets current data from server parse the data
        getMembersDataFromServer();

        postMembersDataToServer();

        /*pushMembersToDb(context);*/
        if (statusCode == 200) {
            return Result.success();
        } else {
            return Result.failure();
        }
    }

    private void postMembersDataToServer() {
        memberRepo.pushMemberDetailsToServer()
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<MemberApiPostRes>, Result>() {
                    @Override
                    public Result apply(Response<MemberApiPostRes> memberApiPostResponse) throws Exception {
                        if (memberApiPostResponse.code() == 201) {
                            if (memberApiPostResponse.body() != null) {
                                //TODO: get the response , parse it and save into a database
                                MemberApiPostRes item = memberApiPostResponse.body();
                                Log.d(TAG, "name ............... " + item.getName());
                                Log.d(TAG, "id ............... " + item.getId());
                                Log.d(TAG, "job ............... " + item.getJob());
                                Log.d(TAG, "created at ............... " + item.getCreatedAt());
                                statusCode = memberApiPostResponse.code();
                                message = memberApiPostResponse.message();
                                Log.d(TAG, "status code ............... " + statusCode);
                                Log.d(TAG, "message ............... " + message);
                                return Result.success();
                            }
                        }
                        return Result.failure();
                    }
                })
                .subscribe();
    }


    private void getMembersDataFromServer() {
       memberRepo.getMembersFromServer().subscribeOn(Schedulers.io())
                .flatMap(new Function<Response<MemberGetApiBaseResponse>, Single<List<Member>>>() {
                    @Override
                    public Single<List<Member>> apply(Response<MemberGetApiBaseResponse> memberGetApiBaseResponse) throws Exception {
                        List<Member> members = new ArrayList<>();
                        //check the status code for success
                        if (memberGetApiBaseResponse.code() == 200) {
                            if (memberGetApiBaseResponse.body() != null) {
                                List<MemberGetApiBaseResponse.MemberApiRes> items = memberGetApiBaseResponse.body().data;
                                for (MemberGetApiBaseResponse.MemberApiRes memberRes : items) {
                                    members.add(new Member(memberRes.id, memberRes.email, memberRes.firstName, memberRes.firstName));
                                }
                                statusCode = memberGetApiBaseResponse.code();
                                message = memberGetApiBaseResponse.message();
                                Log.d(TAG, "status code ............... " + statusCode);
                                Log.d(TAG, "message ............... " + message);
                                return Single.just(members);
                            }
                        }
                        return null;
                    }
                })
                .map(new Function<List<Member>, Result>() {
                    @Override
                    public Result apply(List<Member> members) throws Exception {
                        if (members != null) {
                            Log.d(TAG, "member items to insert to database from background  thread ............... " + members.size());
                            List<Long> id = memberRepo.insertMembersToDatabase(members);
                            Log.d(TAG, "id's -------------> " + id);
                            return Result.success();
                        }
                        return Result.retry();

                    }
                }).subscribe();
    }

}

