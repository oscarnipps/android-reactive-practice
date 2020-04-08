package com.example.reactivepractice.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.reactivepractice.MemberListActivity;
import com.example.reactivepractice.MemberLoginActivity;
import com.example.reactivepractice.api.model.MemberApiPostRes;
import com.example.reactivepractice.data.Resource;

public class UserLoginUtil {

    public static final String TAG = UserLoginUtil.class.getSimpleName();

    public static void handleResponse(Resource<MemberApiPostRes> response, Context context) {
        Log.d(TAG, " status ........" + response.status);

        switch (response.status) {

            case SUCCESS:
                Toast.makeText(context, "member retrieved with name : " + response.data.getName(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, " name is ........" + response.data.getName());
                Log.d(TAG, " job is ........" + response.data.getJob());
                Log.d(TAG, " id is ........" + response.data.getId());
                //start new activity
                Intent intent = new Intent(context, MemberListActivity.class);
                context.startActivity(intent);
                break;

            case ERROR:
                Toast.makeText(context, " " + response.message, Toast.LENGTH_SHORT).show();
                break;

            case LOADING:

        }
        /*}*/
        /*Log.d(TAG, " data : ........" + response.data);*/

        /*Log.d(TAG, " response is null : ........" + response);*/
    }
}
