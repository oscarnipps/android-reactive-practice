package com.example.reactivepractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.reactivepractice.api.model.MemberGetApiBaseResponse;
import com.example.reactivepractice.data.MemberAdapter;
import com.example.reactivepractice.data.MemberRepo;
import com.example.reactivepractice.data.MemberViewModel;
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
    private Observer<Member> taskModelsObserver;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MemberViewModel memberViewModel;
    private MemberAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private MemberRepo memberRepo;
    private List<MemberGetApiBaseResponse.MemberApiRes> memberResponseList = new ArrayList<>();
    private MemberSyncManager memberSyncManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        membersListBinding = DataBindingUtil.setContentView(this, R.layout.activity_members_list);
        mAdapter = new MemberAdapter(this, mItems);
        memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);
        memberRepo = MemberRepo.getInstance(getApplication());
        initializeRecyclerView();

        memberSyncManager = new MemberSyncManager(getApplicationContext());

        /*memberSyncManager.initializeOneTimeWorkRequestsToSyncData(MemberWorker.class);*/

/*        WorkManager.getInstance(this).getWorkInfosByTagLiveData(Constants.MEMBERS_WORK_PUSH_REQUEST_TAG).observe(this,new androidx.lifecycle.Observer<List<WorkInfo>>() {
            @Override
            public void onChanged(List<WorkInfo> workInfos) {
                if (workInfos == null || workInfos.isEmpty()) {
                    return;
                } else {

                }
            }
        });*/

        getMemebersFromDataBase();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void getMemebersFromDataBase() {
        compositeDisposable.add(memberViewModel.getMembersFromDatabase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSubscriber<List<Member>>() {

                    @Override
                    public void onNext(List<Member> members) {
                        Log.d(TAG, " members list ------> " + members.size());
                        displayItems(members);
                        mAdapter.setItems(members);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, " error ------> " + e.getLocalizedMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                })
        );
    }

    private void initializeRecyclerView() {
        mRecyclerView = membersListBinding.memberRecylerview;
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }


    private void displayItems(List<Member> members) {
        if (members.size() == 0) {
            //show the empty view and hide the recycler view
            membersListBinding.emptyMemberList.setVisibility(View.VISIBLE);
            membersListBinding.memberRecylerview.setVisibility(View.GONE);

        } else {
            //show the recycler and refresh adapter with new data
            membersListBinding.emptyMemberList.setVisibility(View.GONE);
            membersListBinding.memberRecylerview.setVisibility(View.VISIBLE);
            mAdapter.setItems(mItems);
        }
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
            memberViewModel.deleteMembersFromDatabase();
        } else {
            Toast.makeText(this, "getting members from server...", Toast.LENGTH_SHORT).show();
            memberSyncManager.initializeOneTimeWorkRequestsToSyncData(MemberPostWorker.class,MemberWorker.class);
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void getMembersFromServer() {
       compositeDisposable.add( memberRepo.getMembersFromApi(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Response<MemberGetApiBaseResponse>, List<Member>>() {
                    @Override
                    public List<Member> apply(Response<MemberGetApiBaseResponse> memberGetApiBaseResponse) throws Exception {
                        List<Member> members = new ArrayList<>();
                        if (memberGetApiBaseResponse.body() != null) {
                            List<MemberGetApiBaseResponse.MemberApiRes> items = memberGetApiBaseResponse.body().data;
                            for (MemberGetApiBaseResponse.MemberApiRes memberRes: items) {
                                members.add(new Member(memberRes.id,memberRes.email, memberRes.firstName,memberRes.firstName));
                            }
                            Log.d(TAG, "member items............... " +members.size());
                            return members;
                        }
                        return null;
                    }
                })
               .subscribeWith(new DisposableSingleObserver<List<Member>>() {
                   @Override
                   public void onSuccess(List<Member> members) {
                       if (members != null) {
                           //insert items in database
                           memberViewModel.insertMembersToDatabase(members);
                       } else {
                           Log.d(TAG, "members null............... " );
                       }
                   }

                   @Override
                   public void onError(Throwable e) {
                       Log.d(TAG, "error............... " + e.getLocalizedMessage() );
                   }
               }));
    }
*/


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
            compositeDisposable = null;
        }
    }
}
