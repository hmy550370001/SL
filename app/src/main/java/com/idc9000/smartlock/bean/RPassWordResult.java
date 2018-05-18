package com.idc9000.smartlock.bean;

import java.util.List;

/**
 * 锁具密码指纹设置列表
 */

public class RPassWordResult {


    /**
     * result : {"passwords":[{"id":22,"orderNumber":0,"name":"123456","type":"密码和指纹","userNumber":0},{"id":23,"orderNumber":1,"name":"123456","type":"密码和指纹","userNumber":0},{"id":null,"orderNumber":2,"name":"未设置","type":"","userNumber":0},{"id":null,"orderNumber":3,"name":"未设置","type":"","userNumber":0},{"id":null,"orderNumber":4,"name":"未设置","type":"","userNumber":0},{"id":null,"orderNumber":5,"name":"未设置","type":"","userNumber":0},{"id":null,"orderNumber":6,"name":"未设置","type":"","userNumber":0},{"id":null,"orderNumber":7,"name":"未设置","type":"","userNumber":0},{"id":null,"orderNumber":8,"name":"未设置","type":"","userNumber":0},{"id":null,"orderNumber":9,"name":"未设置","type":"","userNumber":0}]}
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
        private List<PasswordsBean> passwords;

        public List<PasswordsBean> getPasswords() {
            return passwords;
        }

        public void setPasswords(List<PasswordsBean> passwords) {
            this.passwords = passwords;
        }

        public static class PasswordsBean {
            /**
             * id : 22
             * orderNumber : 0
             * name : 123456
             * type : 密码和指纹
             * userNumber : 0
             */

            private int id;
            private int orderNumber;
            private String name;
            private String type;
            private int userNumber;
            public boolean isDeleteState;//是否删除状态
            public boolean isSelect; //选中状态

            public boolean isDeleteState() {
                return isDeleteState;
            }

            public void setDeleteState(boolean deleteState) {
                isDeleteState = deleteState;
            }

            public boolean isSelect() {
                return isSelect;
            }

            public void setSelect(boolean select) {
                isSelect = select;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getOrderNumber() {
                return orderNumber;
            }

            public void setOrderNumber(int orderNumber) {
                this.orderNumber = orderNumber;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public int getUserNumber() {
                return userNumber;
            }

            public void setUserNumber(int userNumber) {
                this.userNumber = userNumber;
            }
        }
    }
}
