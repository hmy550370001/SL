package com.idc9000.smartlock.listener;

/**
 * Created by 赵先生 on 2017/10/10.
 */

public interface IModeChangeListener {
    /**
     *
     * @param action 返回处理不同的action
     */
    public void onModeChanged(int action,Object...values);
}
