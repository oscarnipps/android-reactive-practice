package com.example.reactivepractice.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.reactivepractice.api.ApiClient;
import com.example.reactivepractice.api.MemberApiService;
import com.example.reactivepractice.api.model.MemberApiPostRes;
import com.example.reactivepractice.api.model.MemberGetApiBaseResponse;
import com.example.reactivepractice.api.model.MemberPostSchema;
import com.example.reactivepractice.data.dao.MembersDao;
import com.example.reactivepractice.data.model.Member;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.Response;

/*
 * using singleton pattern to initialize the repo
 */

public class MemberRepo {

    public static final String TAG = MemberRepo.class.getSimpleName();
    private MembersDao membersDao;
    private MemberApiService memberApiService;
    private LiveData<String> status;
    private static MemberRepo mInstance;


    public static MemberRepo getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MemberRepo.class) {
                if (mInstance == null) {
                    mInstance = new MemberRepo(context.getApplicationContext());
                }
            }
        }

        return mInstance;
    }


    private MemberRepo(Context applicationContext) {
        MemberDatabase membersDatabase = MemberDatabase.getInstance(applicationContext);
        membersDao = membersDatabase.getMemberDao();
        //get api service
        memberApiService = ApiClient.getApiClient(applicationContext).create(MemberApiService.class);

    }

    public Flowable<List<Member>> getMembersFromDatabase() {
        return membersDao.getMembersFromDatabase();

    }

    public LiveData<String> getStatus() {
        return status;
    }

    public Single<Response<MemberGetApiBaseResponse>> getMembersFromServer() {
        return memberApiService.getMembers();
    }

    public Single<Response<MemberApiPostRes>> pushMemberDetailsToServer() {
        return memberApiService.pushMemberToServer(new MemberPostSchema("morpheus", "leader"));
    }

    public Single<Response<MemberApiPostRes>> loginInUser() {
        return memberApiService.logInUser(new MemberPostSchema("morpheus", "leader"));
    }




/*    public LiveData<MemberApiPostRes> logInUser() {
        return LiveDataReactiveStreams
                .fromPublisher(memberApiService.logInUser(new MemberPostSchema("morpheus", "leader"))
                        .toFlowable()
                        .map(new Function<Response<MemberApiPostRes>, MemberApiPostRes>() {
                            @Override
                            public MemberApiPostRes apply(Response<MemberApiPostRes> memberApiPostResponse) throws Exception {
                                if (memberApiPostResponse.code() == 201) {
                                    if (memberApiPostResponse.body() != null) {
                                        //TODO: get the response , parse it and save into a database
                                        MemberApiPostRes item = memberApiPostResponse.body();
                                        Log.d(TAG, "status code ............... " + memberApiPostResponse.code());
                                        return item;
                                    }
                                    return null;
                                }
                                return null;
                            }
                        })
                        .onErrorReturn(new Function<Throwable, MemberApiPostRes>() {
                            @Override
                            public MemberApiPostRes apply(Throwable throwable) throws Exception {
                                return new MemberApiPostRes();
                            }
                        })
                        .subscribeOn(Schedulers.io()));

    }*/


    public Completable deleteMembersFromDatabase() {
        return membersDao.deleteAllMembersFromDatabase();
    }

    public List<Long> insertMembersToDatabase(List<Member> members) {
        return membersDao.insertMembersIntoDatabase(members);
    }

    public Single<List<Long>> insertMembersToDatabaseSingle(List<Member> members) {
        return membersDao.insertMembersIntoDatabaseSingle(members);
    }


}
