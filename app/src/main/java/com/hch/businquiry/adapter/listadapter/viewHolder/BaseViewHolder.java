package com.hch.businquiry.adapter.listadapter.viewHolder;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.businquiry.callback.OnClickBack;


/**
 * Created by ChenHui on 2017/5/10.
 * ViewHodler
 */

public class BaseViewHolder implements View.OnClickListener{
    /**
     * 存储item转换后的view控件的集合
     */
    private SparseArray<View> mSparseArray = new SparseArray<>();
    /**
     * 用来接收getview（）中的ConvertView
     */
    private View mConvertView;
    /**
     * 事件上抛
     */
    private OnClickBack mOnClickBack;
    /**
     * 接收position
     */
    private int mPosition;

    public BaseViewHolder(View convertView, OnClickBack mOnClickBack) {
        this.mConvertView = convertView;
        if (mConvertView != null) {
            mConvertView.setTag(this);
        }
        this.mOnClickBack = mOnClickBack;
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    /**
     * 从convertView中找到viewId对应的控件
     * @param viewId
     * @param <T>
     * @return
     */
    private <T extends View> T findView(int viewId) {
        View view = null;
        if (mConvertView != null) {
            view = mConvertView.findViewById(viewId);
        }
        if (view != null) {
            mSparseArray.put(viewId,view);
        }
        return (T) view;
    }

    public <T extends View> T getView(int viewId) {
        View view = mSparseArray.get(viewId);
        if (view != null) {
                return (T) view;
        }
        view = findView(viewId);
        return (T) view;
    }

    /**设置控件文本
     * @param viewId ：控件id
     * @param sequence ：显示的文本内容
     * @return ：当前类自己
     */
    public BaseViewHolder setText(int viewId,CharSequence sequence) {
        TextView view = getView(viewId);
        if (view != null) { //防御式编程，判断非null
            view.setText(sequence);
        }

        return this;
    }

    /**设置控件图片内容
     * @param viewId ：
     * @param imgResId ：图片的资源id
     * @return ：
     */
    public BaseViewHolder setImageRes(int viewId,int imgResId) {
        ImageView view = getView(viewId);
        if (view != null) {
            view.setImageResource(imgResId);
        }

        return this;
    }

    public void setImgVisiblility(int viewId, boolean isVisility) {
        ImageView view = getView(viewId);
        if (isVisility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**给一个view设置点击事件监听
     * @param viewId ：
     * @return ：
     */
    public BaseViewHolder setOnClickListener(int viewId) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(this);
        }

        return this;
    }
    @Override
    public void onClick(View view) {
        /**
         * 上抛点击事件
         */
        if (mOnClickBack != null) {
            mOnClickBack.onClickBack(mPosition,view,this);
        }
    }
}
