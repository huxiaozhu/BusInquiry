package com.hch.businquiry.adapter.listadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hch.businquiry.adapter.listadapter.viewHolder.BaseViewHolder;
import com.hch.businquiry.callback.OnClickBack;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ChenHui on 2016/12/23.
 */

public abstract class CommonAdapter<T> extends BaseAdapter implements OnClickBack {

    private List<T> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private int mLayoutId;

    public CommonAdapter(Context context,List<T> data, int mLayoutId) {
        mInflater = LayoutInflater.from(context);
        this.mLayoutId = mLayoutId;
        if (data != null) {
            mData.addAll(data);
        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int i) {
        if (i >= 0 && i < getCount()) {
            return mData.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        BaseViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(mLayoutId, viewGroup, false);
            holder = new BaseViewHolder(view, this);
            setListener(i, holder);
        } else {
            holder = (BaseViewHolder) view.getTag();
        }
        holder.setmPosition(i);
        setViewData(getItem(i), holder);
        return view;
    }

    /**
     * 设置item数据
     * @param item
     * @param holder
     */
    protected abstract void setViewData(T item, BaseViewHolder holder);

    /**
     * 设置item控件监听
     * @param i
     * @param holder
     */
    protected abstract void setListener(int i, BaseViewHolder holder) ;

    /**
     * 点击事件的回调
     * @param position
     * @param view
     * @param holder
     */
    protected abstract void onClick(int position, View view, BaseViewHolder holder);

    @Override
    public void onClickBack(int position, View view, BaseViewHolder holder) {
        onClick(position,view,holder);
    }

    /**
     * 该方法处理刷新数据
     *
     * @param data:
     */
    public void update(List<T> data) {
        mData.clear(); //如果data是null，则清空原有的数据
        if (data != null) { //防止空指针
            mData.addAll(data); //否则，把data中的数据添加进来
        }
        //该方法会通知adapter数据改变了，需要刷新界面
        notifyDataSetChanged();
    }
    public void update() {
        notifyDataSetChanged();
    }

    /**
     *向集合中追加多条数据
     * @param data ：新添加的数据
     */
    public void append(List<T> data) {
        if (data != null) { //防止空指针
            mData.addAll(data); //否则，把data中的数据添加进来
            //该方法会通知adapter数据改变了，需要刷新界面
            notifyDataSetChanged();
        }
    }
}
