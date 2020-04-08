package com.example.reactivepractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.reactivepractice.api.model.MemberApiPostRes;
import com.example.reactivepractice.data.MemberViewModel;
import com.example.reactivepractice.data.Resource;
import com.example.reactivepractice.databinding.ActivityMemberLoginBinding;
import com.example.reactivepractice.util.UserLoginUtil;

public class MemberLoginActivity extends AppCompatActivity {

    private static final String TAG = MemberLoginActivity.class.getSimpleName();
    ActivityMemberLoginBinding memberLoginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memberLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_member_login);

        //get the viewmodel
        MemberViewModel memberViewModel = new ViewModelProvider(this).get(MemberViewModel.class);

        //bind the viewmodel to the layout
        memberLoginBinding.setMemberViewModel(memberViewModel);

        //when the login button is clicked
        memberLoginBinding.memberLogin.setOnClickListener(view -> memberViewModel.logInUser());

        //set the lifecycle owner
        memberLoginBinding.setLifecycleOwner(this);

        //bind the viewmodel
        memberLoginBinding.setMemberViewModel(memberViewModel);

        //bind the integer value of the resource enum which would be used to toggle the progress bar visibility
        memberLoginBinding.setStatus(Resource.Status.LOADING.ordinal());

        //subscribe to the log in result
        memberViewModel.getLoggedUserResponse().observe(this, response -> {
            UserLoginUtil.handleResponse(response,MemberLoginActivity.this);
        });
    }

}
