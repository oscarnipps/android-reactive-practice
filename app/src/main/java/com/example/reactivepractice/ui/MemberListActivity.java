package com.example.reactivepractice.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.reactivepractice.R;
import com.example.reactivepractice.api.model.MemberGetApiBaseResponse;
import com.example.reactivepractice.data.Constants;
import com.example.reactivepractice.data.MemberAdapter;
import com.example.reactivepractice.data.MemberListRepo;
import com.example.reactivepractice.data.Resource;
import com.example.reactivepractice.data.model.Member;
import com.example.reactivepractice.databinding.ActivityMembersListBinding;
import com.example.reactivepractice.sync.MemberPostWorker;
import com.example.reactivepractice.sync.MemberSyncManager;
import com.example.reactivepractice.sync.MemberWorker;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;


public class MemberListActivity extends AppCompatActivity {

    public static final String TAG = MemberListActivity.class.getSimpleName();

    private ActivityMembersListBinding membersListBinding;
    private List<Member> mItems = new ArrayList<>();
    private MemberListViewModel memberListViewModel;
    private MemberAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private MemberSyncManager memberSyncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        membersListBinding = DataBindingUtil.setContentView(this, R.layout.activity_members_list);

        //the list view model
        memberListViewModel = new ViewModelProvider(this).get(MemberListViewModel.class);

        //set the lifecycle owner to this activity
        membersListBinding.setLifecycleOwner(this);

        //bind the view model
        membersListBinding.setViewModel(memberListViewModel);

        initializeRecyclerViewAndAdapter();

        //work manager to sync data from the server
        /*initializeSyncManager();*/

        //bind the integer value of the resource enum which would be used to toggle the progress bar visibility
        membersListBinding.setStatus(Resource.Status.LOADING.ordinal());

        //observe the members data from the database
        memberListViewModel.getMembers().observe(this,members -> {
            Log.d(TAG, " members list ------> " + members.size());
            mAdapter.setItems(members);
        });

    }

    /*private void initializeSyncManager() {
        memberSyncManager = new MemberSyncManager(getApplicationContext());
        memberSyncManager.initializeOneTimeWorkRequestsToSyncData(MemberWorker.class);

        WorkManager.getInstance(this).getWorkInfosByTagLiveData(Constants.MEMBERS_WORK_PUSH_REQUEST_TAG).observe(this,new androidx.lifecycle.Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                } else {

                }
            }
        });
    }*/

    private void initializeRecyclerViewAndAdapter() {
        //adapter
        mAdapter = new MemberAdapter(this, mItems);
        //set up recycler view
        mRecyclerView = membersListBinding.memberRecylerview;
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.member_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            //delete all
            memberListViewModel.deleteMembersFromDatabase();
        } else {
            memberListViewModel.getMembersFromServer();
            /*memberSyncManager.initializeOneTimeWorkRequestsToSyncData(MemberPostWorker.class,MemberWorker.class);*/
        }
        return super.onOptionsItemSelected(item);
    }
}
