package com.hch.businquiry.dbutils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyHelper extends SQLiteOpenHelper {
	
	private static String DB_NAME = "mydata.db";  //数据库名
	public static String TABLE_NAME = "dim"; //表名
	

	public MyHelper(Context context) {
		super(context, DB_NAME, null, 2);
	}
	public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//Create table
		String sql = "CREATE TABLE "+TABLE_NAME + "("
					  + "id integer primary key autoincrement,"
					  + "key TEXT,"
					  + "num integer);";
					 
		Log.e("table oncreate", "create table");
		db.execSQL(sql); 		//关闭
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.e("update", "update");
//		db.execSQL("ALTER TABLE "+ MyHelper.TABLE_NAME+" ADD sex TEXT"); //�޸��ֶ�
	}

}
