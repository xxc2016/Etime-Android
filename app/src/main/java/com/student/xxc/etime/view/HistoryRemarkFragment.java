package com.student.xxc.etime.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.student.xxc.etime.R;
import com.student.xxc.etime.bean.UserBean;
import com.student.xxc.etime.impl.DealUserBean;

public class HistoryRemarkFragment extends Fragment implements DealUserBean{

    public static HistoryRemarkFragment newInstance(String data) {
        Bundle args = new Bundle();
        args.putString("key", data);
        HistoryRemarkFragment fragment = new HistoryRemarkFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public View createFragment(LayoutInflater inflater, ViewGroup container)
    {
        View view = inflater.inflate(R.layout.fragment_history_remark, container, false);

        return view;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String pageTitle=getArguments().getString("key");
        Log.i(pageTitle, "History Remark onCreateView: _____________________");
        View view=null;
        view = createFragment(inflater,container);
        return view;
    }

    public void updateUserBean(UserBean userBean)
    {

    }
}
