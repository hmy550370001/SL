package com.idc9000.smartlock.bean;

import java.util.List;

/**
 * 消息
 */

public class RMessage {

    /**
     * result : {"infos":[{"content":"123的开门密码:12345678","createTime":"2017-12-26 09:23"},{"content":"666的开门密码:12345678","createTime":"2017-12-26 09:33"}]}
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
        private List<InfosBean> infos;

        public List<InfosBean> getInfos() {
            return infos;
        }

        public void setInfos(List<InfosBean> infos) {
            this.infos = infos;
        }

        public static class InfosBean {
            /**
             * content : 123的开门密码:12345678
             * createTime : 2017-12-26 09:23
             */

            private String content;
            private String createTime;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }
        }
    }
}
