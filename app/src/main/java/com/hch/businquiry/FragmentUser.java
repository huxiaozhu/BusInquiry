package com.hch.businquiry;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hch.businquiry.dbutils.DatabaseUtil;

public class FragmentUser extends Fragment implements View.OnClickListener {
    private TextView mTitle;
    private LinearLayout mClean;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        initView(view);
        setData();
        setLisiter();
        return view;
    }

    private void setLisiter() {
        mClean.setOnClickListener(this);
    }


    private void setData() {
        mTitle.setText("我的");
    }

    private void initView(View view) {
        mTitle = (TextView) view.findViewById(R.id.titles_title);
        mClean = (LinearLayout) view.findViewById(R.id.clean_data);
    }

    @Override
    public void onClick(View v) {
//        DatabaseUtil.instance(getContext()).delete();
//        Toast.makeText(getContext(), "数据清除中", Toast.LENGTH_SHORT).show();
    }

}
