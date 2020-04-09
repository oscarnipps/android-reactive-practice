package com.example.reactivepractice.sync;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.ListenableWorker;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import com.example.reactivepractice.data.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MemberSyncManager {
    public static final String TAG = MemberSyncManager.class.getSimpleName();
    private Context mContext;
    private WorkManager mWorkManager;

    public MemberSyncManager(Context mContext) {
        this.mContext = mContext;
        this.mWorkManager = WorkManager.getInstance(mContext);
    }


    public final void initializeOneTimeWorkRequestsToSyncData(Class<?> ... workers) {
        for (Class<?> worker : workers) {

            Log.d(TAG, " worker tag is ------> "  + worker.getSimpleName());
            Log.d(TAG, " worker unique name is ------> "  + worker.getSimpleName());
            //the constraints for the work
            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();
            OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(worker.asSubclass(Worker.class))
                    .setConstraints(constraints)
                    //use the class name as the tag which would be usd to observe the work's progress
                    .addTag(worker.getSimpleName())
                    .build();
            //use the worker class name as the unique work name
            mWorkManager.enqueueUniqueWork(worker.getSimpleName(), ExistingWorkPolicy.KEEP, request);
        }
    }

    public void intializePeriodicWorkRequestsToSyncData() {
    }






/*    public void initiateOneTimeWorkerToGetMembersFromServer() {
        //set the constraints
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        //create work request , you can optionally pass inputs to the worker ,set backoff retry policy
        OneTimeWorkRequest membersOneTimeWorRequest = new OneTimeWorkRequest.Builder(MemberWorker.class)
                .setConstraints(constraints)
                //set the backoff delay to 10 seconds(min backoff) i.e time to wait before retrying the work if Result.retry was returned from the database
                .setBackoffCriteria(BackoffPolicy.LINEAR , OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .addTag(Constants.MEMBERS_WORK_GET_REQUEST_TAG)
                .build();
        //enqueue the work
        mWorkManager.beginUniqueWork(Constants.MEMBERS_WORK_REQUEST_NAME , ExistingWorkPolicy.KEEP,membersOneTimeWorRequest).enqueue();
    }

    public void initiateOneTimeWorkerToPushMembersToServer() {
        //set the constraints
        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        //create work request , you can optionally pass inputs to the worker ,set backoff retry policy
        OneTimeWorkRequest membersOneTimeWorRequest = new OneTimeWorkRequest.Builder(MemberPostWorker.class)
                .setConstraints(constraints)
                //set the backoff delay to 10 seconds(min backoff) i.e time to wait before retrying the work if Result.retry was returned from the database
                .setBackoffCriteria(BackoffPolicy.LINEAR , OneTimeWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .addTag(Constants.MEMBERS_WORK_PUSH_REQUEST_TAG)
                .build();
        //enqueue the work
        mWorkManager.beginUniqueWork(Constants.MEMBERS_WORK_REQUEST_NAME , ExistingWorkPolicy.KEEP,membersOneTimeWorRequest).enqueue();
    }*/
}
