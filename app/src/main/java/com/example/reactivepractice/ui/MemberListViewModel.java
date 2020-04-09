package com.example.reactivepractice.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.reactivepractice.api.model.MemberGetApiBaseResponse;
import com.example.reactivepractice.data.MemberListRepo;
import com.example.reactivepractice.data.Resource;
import com.example.reactivepractice.data.model.Member;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MemberListViewModel extends AndroidViewModel {

    private static final String TAG = MemberListViewModel.class.getSimpleName();
    private MemberListRepo memberListRepo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LiveData<List<Member>> members;
    private MutableLiveData<Resource<List<Member>>> membersResults = new MutableLiveData<>();

    public MemberListViewModel(@NonNull Application application) {
        super(application);
        //get the repository
        memberListRepo = MemberListRepo.getInstance(application);
        members = memberListRepo.getMembersFromDatabase();
    }

    public LiveData<List<Member>> getMembers() {
        return members;
    }

    public LiveData<Resource<List<Member>>> getMembersServerResponse() {
        return membersResults;
    }


    public void deleteMembersFromDatabase() {
        compositeDisposable.add(memberListRepo.deleteMembersFromDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "items deleted successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error deleting items");
                    }
                }));
    }

    public List<Long> insertMembersToDatabase(List<Member> members) {
        return memberListRepo.insertMembersIntoDatabase(members);
    }

    public void insertMembersToDatabaseSingle(List<Member> members) {
        compositeDisposable.add( memberListRepo.insertMembersIntoDatabaseSingle(members)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Long>>() {

                    @Override
                    public void onSuccess(List<Long> insertIds) {
                        Log.d(TAG,"insert items of size ... "+ insertIds);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG,"error .............. "+ e.getLocalizedMessage());
                    }
                })
        );
    }

    public void getMembersFromServer() {
        membersResults.postValue(Resource.loading(null));
        compositeDisposable.add(memberListRepo.getMembersFromServer()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<Response<MemberGetApiBaseResponse>, Single<Resource<List<Member>>>>) memberGetApiBaseResponse -> {
                    List<Member> members = new ArrayList<>();
                    //check the status code
                    if (memberGetApiBaseResponse.code() == 200) {
                        //if the data in the response body is null
                        if (memberGetApiBaseResponse.body() != null) {
                            //TODO: parse the response with the help of a utility class / method
                            List<MemberGetApiBaseResponse.MemberApiRes> items = memberGetApiBaseResponse.body().data;
                            for (MemberGetApiBaseResponse.MemberApiRes memberRes : items) {
                                members.add(new Member(memberRes.id, memberRes.email, memberRes.firstName, memberRes.firstName));
                            }
                        }
                        //TODO: use utility class / method to set error message
                        membersResults.postValue(Resource.error("could not get members from server", null));
                        return Single.just(Resource.success(members));
                    }

                    //TODO: use utility class / method to return appropriate error message based on the status code returned by the server
                    membersResults.postValue(Resource.error("could not get members from server", null));

                    return Single.just(Resource.error("could not get members from server", null));
                })
                .map(new Function<Resource<List<Member>>, List<Member>>() {
                    @Override
                    public List<Member> apply(Resource<List<Member>> listResource) throws Exception {
                        if (listResource.data != null) {

                            /*//add items to database
                            List<Long> insertIds = insertMembersToDatabase(listResource.data);
                            insertIds.size();
                            Log.d(TAG,"insert id's .......... "+ insertIds);*/
                            return listResource.data;
                        }
                        Log.d(TAG,"insert data .......... "+ listResource.data);
                       return listResource.data;
                    }
                })
                .subscribeWith(new DisposableSingleObserver<List<Member>>() {
                    @Override
                    public void onSuccess(List<Member> members) {
                        Log.d(TAG,"items of size ..."+ members.size());
                        //set a success resource status
                        membersResults.postValue(Resource.success(members));
                        insertMembersToDatabaseSingle(members);
                    }

                    @Override
                    public void onError(Throwable e) {
                        //set an error resource status
                        membersResults.postValue(Resource.error("error .................." + e.getLocalizedMessage(), null));
                    }
                })
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }
}
