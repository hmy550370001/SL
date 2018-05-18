package com.idc9000.smartlock.bean;

import java.io.Serializable;

/**
 * Created by 赵先生 on 2017/10/26.
 */

public class RecordItemInfo implements Serializable {
    public static final int RECORD_LEFT = 1001;
    public static final int RECORD_RIGHT = 1002;
    public int type;
    public Object object;

    public RecordItemInfo(int type, Object object) {
        this.type = type;
        this.object = object;
    }
}
