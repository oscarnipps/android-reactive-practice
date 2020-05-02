package com.example.reactivepractice.sync;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.RxWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.reactivepractice.R;
import com.example.reactivepractice.api.model.MemberApiPostRes;
import com.example.reactivepractice.api.model.MemberGetApiBaseResponse;
import com.example.reactivepractice.data.MemberRepo;
import com.example.reactivepractice.data.model.Member;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class MemberWorker extends RxWorker {

    public static final String TAG = MemberWorker.class.getSimpleName();
    private int statusCode;
    private String message;
    MemberRepo memberRepo;
    Data.Builder outputData = new Data.Builder();

    public MemberWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        //repo instance
        memberRepo = MemberRepo.getInstance(getApplicationContext());

        //post data from local database to server
        /*postMembersDataToServer();*/

        return getMembersDataFromServer();
    }

    private Single<Result> getMembersDataFromServer() {
     return memberRepo.getMembersFromServer().subscribeOn(Schedulers.io())

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
                            outputData.putString("member_result","success from server ====================== ");
                            return Result.success(outputData.build());
                        }
                        outputData.putString("member_result","error from server......................");
                        return Result.failure(outputData.build());
                    }
                })
               .onErrorReturn(new Function<Throwable, Result>() {
                   @Override
                   public Result apply(Throwable throwable) throws Exception {
                       //TODO: put error inside message data so as to show in UI
                       message = throwable.getLocalizedMessage();
                       Log.d(TAG, "error getting memeber -------------> " + throwable.getLocalizedMessage());
                       /*Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();*/
                       outputData.putString("member_result",message);
                       return Result.failure(outputData.build());
                   }
               });

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

    private void showNotification(String message, String description) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "task_channel";
        String channelName = "task_name";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setContentTitle(message)
                .setContentText(description)
                .setSmallIcon(R.mipmap.ic_launcher);
        manager.notify(1, builder.build());
    }

}

