package com.idc9000.smartlock.controller;


import android.content.Context;

import com.idc9000.smartlock.cons.IdiyMessage;
import com.idc9000.smartlock.db.UserDao;
import com.idc9000.smartlock.utils.AESUtils;


public class UserController extends BaseController {


    private UserDao mUserDao;

    public UserController(Context c) {
        super(c);
        mUserDao = new UserDao(mContext);
    }

    @Override
    protected void handleMessage(int action, Object... values) {

        //登陆的请求
        switch (action) {
            case IdiyMessage.SAVE_USERTODB:
                boolean saveUser2Db = saveUser2Db((String) values[0], (String) values[1]);
                mListener.onModeChanged(IdiyMessage.SAVE_USERTODB_RESULT, saveUser2Db);
                break;
            case IdiyMessage.GET_USER_ACTION:
                UserDao.UserInfo userInfo = aquireUser();
                mListener.onModeChanged(IdiyMessage.GET_USER_ACTION_RESULT, userInfo);
                break;
        }
    }

    private boolean saveUser2Db(String name, String pwd) {

        clearUser();
//		可逆性加密
        try {
            name = AESUtils.encrypt(name);
            pwd = AESUtils.encrypt(pwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mUserDao.saveUser(name, pwd);
    }

    private UserDao.UserInfo aquireUser() {
        UserDao.UserInfo userInfo = mUserDao.aquireLastestUser();
        if (userInfo != null) {
            try {
                userInfo.name = AESUtils.decrypt(userInfo.name);
                userInfo.pwd = AESUtils.decrypt(userInfo.pwd);
                userInfo.name = AESUtils.decrypt(userInfo.name);
                userInfo.pwd = AESUtils.decrypt(userInfo.pwd);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userInfo;
        }
        return null;
    }

    private void clearUser() {
        mUserDao.clearUsers();
    }

}
