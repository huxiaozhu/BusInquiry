package com.hch.businquiry.dbutils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hch.businquiry.bean.DimBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/19.
 */

public class DatabaseUtil {
    private MyHelper helper;
    private static Context mContext;
    private String mTag = getClass().getName();

    private DatabaseUtil() {
        super();
        helper = new MyHelper(mContext);
    }
    /**
     * 使用静态内部类来实现单例
     */
    private static class SingletonHolder{
        private static DatabaseUtil mDao = new DatabaseUtil();
    }

    /**
     * 获取当前类的实例
     *
     * @return
     */
    public static DatabaseUtil instance(Context context) {
        mContext = context;
        return SingletonHolder.mDao;
    }


    /**
     * 插入
     * @param dim
     * @return
     */
    public boolean Insert(DimBean dim){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "insert into "+MyHelper.TABLE_NAME
                +"(key,num) values ("
                + "'"+dim.getKey()
                + "' ," + "'"+ dim.getNum() + "'" + ")";
        try {
            db.execSQL(sql);
            return true;
        } catch (SQLException e){
            Log.e("err", "insert failed");
            return false;
        }finally{
            db.close();
        }

    }

    /**
     * 查询
     * @return
     */
    public List<DimBean> queryAll(){
        SQLiteDatabase db = helper.getReadableDatabase();
        List<DimBean> list = new ArrayList<DimBean>();
        Cursor cursor = db.query(MyHelper.TABLE_NAME, null, null,null, null, null, "id desc");
        while(cursor.moveToNext()){
            DimBean dim = new DimBean();
            dim.setId(cursor.getInt(cursor.getColumnIndex("id")));
            dim.setKey(cursor.getString(cursor.getColumnIndex("key")));
            dim.setNum(cursor.getInt(cursor.getColumnIndex("num")));
            list.add(dim);
        }
        db.close();
        return list;
    }
    /**
     * 删除数据
     *
     * @return ： 成功 true，否则false
     */
    public boolean delete() {
        String deleteSql = "DELETE FROM "+MyHelper.TABLE_NAME;
        SQLiteDatabase mDb = helper.getReadableDatabase();
        //db 提供的删除的方法
        if (mDb == null) {
            Log.e(mTag, "mDb is null !!!");
            return false;
        }
//        mDb.delete("Student", "_id=? AND name=?", new String[]{"s13", "张三"});
        boolean result;
        try {
            mDb.execSQL(deleteSql);
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

}
