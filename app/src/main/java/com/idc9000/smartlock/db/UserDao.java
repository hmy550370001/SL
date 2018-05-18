package com.idc9000.smartlock.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.idc9000.smartlock.cons.DbConst;



public class UserDao {
    private DbOpenHelper mHelper;

    public UserDao(Context c) {
        mHelper = new DbOpenHelper(c);
    }

    // 保存操作
    public boolean saveUser(String name, String pwd) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbConst._NAME, name);
        values.put(DbConst._PWD, pwd);
        long insertId = db.insert(DbConst.USER_TABLE, null, values);
        return insertId != -1;
    }

    // 清空数据表
    public void clearUsers() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(DbConst.USER_TABLE, null, null);
    }

    //取用户信息
    public UserInfo aquireLastestUser() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.query(DbConst.USER_TABLE, new String[] {
                DbConst._NAME, DbConst._PWD }, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String pwd = cursor.getString(1);
            return new UserInfo(name, pwd);
        }
        return null;
    }

    public class UserInfo {
        public String name;
        public String pwd;

        UserInfo(String name, String pwd) {
            this.name = name;
            this.pwd = pwd;
        }

    }
}
