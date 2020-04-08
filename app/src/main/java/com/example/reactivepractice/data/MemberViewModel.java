package com.example.reactivepractice.data;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.WorkManager;

import com.example.reactivepractice.api.model.MemberApiPostRes;
import com.example.reactivepractice.data.model.Member;

import java.util.List;
import java.util.Objects;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MemberViewModel extends AndroidViewModel {

    private static final String TAG = MemberViewModel.class.getSimpleName();
    private MemberRepo memberRepo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private WorkManager mWorkManager;
    private MutableLiveData<Resource<MemberApiPostRes>> loggedUserResponse = new MutableLiveData<>();

    public MemberViewModel(@NonNull Application application) {
        super(application);
        memberRepo = MemberRepo.getInstance(application);
        mWorkManager = WorkManager.getInstance(application);
    }

    public Flowable<List<Member>> getMembersFromDatabase() {
        return memberRepo.getMembersFromDatabase();

    }

    public LiveData<Resource<MemberApiPostRes>> getLoggedUserResponse() {
        return loggedUserResponse;

    }

    public void deleteMembersFromDatabase() {
        compositeDisposable.add(memberRepo.deleteMembersFromDatabase()
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


    public void logInUser() {
        //show the loading status
        loggedUserResponse.postValue(Resource.loading(null));
        compositeDisposable.add(memberRepo.loginInUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map((Function<Response<MemberApiPostRes>, Resource<MemberApiPostRes>>) memberApiPostResponse -> {
                    if (memberApiPostResponse.code() == 201) {
                        if (memberApiPostResponse.body() != null) {
                            //TODO: get the response , parse it and save into a database
                            MemberApiPostRes item = memberApiPostResponse.body();
                            Log.d(TAG, "status code ............... " + memberApiPostResponse.code());
                            return Resource.success(item);
                        }
                    }
                    //TODO: use utility class to return appropriate error message based on the status code returned by the server
                    loggedUserResponse.postValue(Resource.error("could not get member from server", null));

                    //set error message with data from server
                    return Resource.error("could not get member from server", null);
                })
                .subscribeWith(new DisposableSingleObserver<Resource>() {

                    @SuppressWarnings("unchecked")
                    @Override
                    public void onSuccess(Resource resource) {
                        //set the live data value which in the process updates the status
                        loggedUserResponse.postValue(resource);
                    }

                    @Override
                    public void onError(Throwable e) {
                        loggedUserResponse.postValue(Resource.error("no internet connection", null));
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
