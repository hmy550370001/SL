package com.idc9000.smartlock.event;

/**
 * Created by Administrator on 2018/1/12.
 */

public class RefreshData {
    boolean refreshData;

    public RefreshData(boolean refreshData) {
        this.refreshData = refreshData;
    }

    public boolean isRefreshData() {
        return refreshData;
    }

    public void setRefreshData(boolean refreshData) {
        this.refreshData = refreshData;
    }
}
