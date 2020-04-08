package com.example.reactivepractice.background;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.reactivepractice.sync.MemberWorker;

import java.util.List;
import java.util.UUID;

public class MemberWorkManager {
    public static final String TAG = MemberWorkManager.class.getSimpleName();
    private Context context;
    private LiveData<List<WorkInfo>> workInfo;

    public MemberWorkManager(Context context) {
        this.context = context;
    }

    public static WorkManager getInstance(Context context) {
        return WorkManager.getInstance(context);
    }

    public void dispatchRequest() {
        WorkManager workManager = WorkManager.getInstance(context);
        //dispatch work
        workManager.beginUniqueWork("unique-member-worker", ExistingWorkPolicy.KEEP,createOneTimeWorkRequest()).enqueue();
    }

    private OneTimeWorkRequest createOneTimeWorkRequest() {
        return new OneTimeWorkRequest.Builder(MemberWorker.class)
                .addTag("member-worker")
                .build();
    }

    public LiveData<List<WorkInfo>> getWorkInfo(String s) {
        return WorkManager.getInstance(context).getWorkInfosByTagLiveData(s);
    }

/*    private Constraints setConstraintsForTheWorkRequest() {
        Constraints constraints = new Constraints.Builder()

    }*/

}
