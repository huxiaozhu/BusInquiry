package com.hch.businquiry.callback;

import android.view.View;

import com.hch.businquiry.adapter.listadapter.viewHolder.BaseViewHolder;


/**
 * Created by ChenHui on 2016/12/23.
 */

public interface OnClickBack {
    void onClickBack(int position, View view, BaseViewHolder holder);
}
