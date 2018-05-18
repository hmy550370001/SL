package com.idc9000.smartlock.bean;

/**
 * Created by Administrator on 2017/12/18.
 */

public class RUserInfo {
    /**
     * result : {"user":{"username":"李四","phone":"18722378112","familyPhone":"18222933832"}}
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
        /**
         * user : {"username":"李四","phone":"18722378112","familyPhone":"18222933832"}
         */

        private UserBean user;

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {
            /**
             * username : 李四
             * phone : 18722378112
             * familyPhone : 18222933832
             */

            private String username;
            private String phone;
            private String familyPhone;

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public String getPhone() {
                return phone;
            }

            public void setPhone(String phone) {
                this.phone = phone;
            }

            public String getFamilyPhone() {
                return familyPhone;
            }

            public void setFamilyPhone(String familyPhone) {
                this.familyPhone = familyPhone;
            }
        }
    }
}
