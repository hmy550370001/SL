package com.idc9000.smartlock.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 */

public class RLockRecord {

    /**
     * result : {"locklogs":[{"content":"张三领取了美年美的钥匙","id":2,"time":"2017/12/26 15:54","username":"张三"},{"content":"美年美已更新租期为日租,从2017-12-29到2017-12-30","id":3,"time":"2017/12/26 15:55","username":null}]}
     * errorMsg :
     * success : true
     */

    private ResultBean result;
    private String errorMsg;
    private boolean success;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public static class ResultBean {
        private List<LocklogsBean> locklogs;

        public List<LocklogsBean> getLocklogs() {
            return locklogs;
        }

        public void setLocklogs(List<LocklogsBean> locklogs) {
            this.locklogs = locklogs;
        }

        public static class LocklogsBean {
            /**
             * content : 张三领取了美年美的钥匙
             * id : 2
             * time : 2017/12/26 15:54
             * username : 张三
             */

            private String content;
            private int id;
            private String time;
            private String username;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }
    }
}
