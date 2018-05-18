package com.idc9000.smartlock.cons;

import android.provider.BaseColumns;

public class DbConst implements BaseColumns {
	//SqlLite数据库
	public static final String DB_NAME = "smartlock.db";
	public static final int DB_VERSION = 1;
	
	public static final String USER_TABLE = "user";
	public static final String _NAME = "name";
	public static final String _PWD = "pwd";
	
}
