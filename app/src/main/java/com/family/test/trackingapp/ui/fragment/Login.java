package com.family.test.trackingapp.ui.fragment;

import android.annotation.SuppressLint;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.family.test.trackingapp.R;
import com.family.test.trackingapp.data.model.User;
import com.family.test.trackingapp.databinding.FragmentLoginBinding;
import com.family.test.trackingapp.init.TrackingApp;
import com.family.test.trackingapp.init.UserSession;
import com.family.test.trackingapp.ui.viewmodel.ChildTrackViewModel;
import com.family.test.trackingapp.util.Utility;
import com.family.test.trackingapp.ui.activity.ChildrenActivity;
import com.family.test.trackingapp.ui.activity.TrackingActivity;

/**
 * Created by Nagaraj Mulgund on 27-09-2018.
 */
@SuppressLint("ValidFragment")
public class Login extends Fragment {
    //region Variable Declaration
    private ChildTrackViewModel childTrackViewModel;
    private UserSession session;
    private FragmentLoginBinding mBinding;
    private Intent intent;
    //endregion

    //region Fragment Lifecycle component
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login,
                container, false);
        // User Session Manager
        session = new UserSession(TrackingApp.getAppContext());
        childTrackViewModel = ViewModelProviders.of(getActivity()).get(ChildTrackViewModel.class);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeView();
        setButtonClickListner();
    }
    //endregion

    //region Intializing View
    private void initializeView() {
        Utility.textEventListner(mBinding.username, mBinding.login);
        Utility.textEventListner(mBinding.password, mBinding.login);
    }

    private void showToastMessge(String s) {
        Toast.makeText(getContext(), s, Toast.LENGTH_LONG).show();
    }
    //endregion

    //region Login button to submit request
    private void setButtonClickListner() {
        mBinding.login.setOnClickListener(view -> {
            String username = mBinding.username.getText().toString();
            String password = mBinding.password.getText().toString();
            LiveData<User> liveData = childTrackViewModel.getUserLiveData(username);
            intent = new Intent(getActivity(), TrackingActivity.class);
            liveData.observe(getActivity(), (User mEntities) -> {
                if (mEntities.getPassword() != null) {
                    if (!mEntities.getPassword().equals(password)) {
                        showToastMessge(Utility.INVALID_PASSWORD);
                        return;
                    } else if (mEntities.getUserRole().equals(Utility.CHILD_CONSTANT)) {
                        intent = new Intent(getContext(), ChildrenActivity.class);
                    }
                    intent.putExtra(Utility.KEY_USER_NAME, username);
                    intent.putExtra(Utility.KEY_NAME, mEntities.getFirstName());
                    session.createUserLoginSession(username, password, mEntities.getUserRole(), mEntities.getFirstName());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    showToastMessge(Utility.USER_NOT_FOUND);
                }
            });
        });
    }
    //endregion

}
