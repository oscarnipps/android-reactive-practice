package com.example.reactivepractice.sync;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.reactivepractice.api.model.MemberApiPostRes;
import com.example.reactivepractice.data.MemberRepo;

import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MemberPostWorker extends Worker {
    public static final String TAG = MemberPostWorker.class.getSimpleName();
    private int statusCode;
    private String message;
    MemberRepo memberRepo;

    /**
     * @param appContext   The application {@link Context}
     * @param workerParams Parameters to setup the internal state of this worker
     */
    public MemberPostWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);


    }

    @NonNull
    @Override
    public Result doWork() {

        //repo instance
        memberRepo = MemberRepo.getInstance(getApplicationContext());

        try {

            postMembersDataToServer();

            return Result.success();

        } catch (Exception e) {
            return Result.failure();
        }


/*        return memberRepo.pushMemberDetailsToServer(context)
                .map(new Function<Response<MemberApiPostRes>, Result>() {
                    @Override
                    public Result apply(Response<MemberApiPostRes> memberApiPostResponse) throws Exception {
                        if (memberApiPostResponse.code() == 201) {
                            if (memberApiPostResponse.body() != null) {
                                //TODO: get the response , parse it and save into a database
                                MemberApiPostRes item = memberApiPostResponse.body();
                                Log.d(TAG, "name ............... " + item.getName() );
                                Log.d(TAG, "id ............... " + item.getId() );
                                Log.d(TAG, "job ............... " + item.getJob() );
                                Log.d(TAG, "created at ............... " + item.getCreatedAt() );
                            }
                            return Result.success();
                        }
                      return Result.retry();
                    }
                }).subscribeOn(Schedulers.io());*/

    }

    private void postMembersDataToServer() {
        memberRepo.pushMemberDetailsToServer()
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<MemberApiPostRes>, Result>() {
                    @Override
                    public Result apply(Response<MemberApiPostRes> memberApiPostResponse) throws Exception {
                        if (memberApiPostResponse.code() == 200) {
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
}
