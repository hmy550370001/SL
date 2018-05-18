package com.idc9000.smartlock.listener;

/**
 * Created by 赵先生 on 2017/10/12.
 */

public interface IModeSmsListener {
    /**
     */
     void onModeChanged(int event, int result, Object data);
}
