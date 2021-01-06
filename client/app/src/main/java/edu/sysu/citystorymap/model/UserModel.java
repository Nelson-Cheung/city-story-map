package edu.sysu.citystorymap.model;

import net.sf.json.JSONObject;

import edu.sysu.citystorymap.user.User;
import edu.sysu.citystorymap.utils.HttpUtils;

public class UserModel {
    private String LOGIN_PATH = "login";
    private String SEND_CODE_PATH = "send_verification_code";
    private String RESET_PASSWORD_PATH = "reset_password";
    private String REGISTER_PATH = "register";

    private boolean isLoginFlag = false;
    private User user = new User();
    private String account;

    // 单例模式
    private UserModel() {
        user = new User();
        account = "";
    }

    public static UserModel getInstance() {
        return UserModelHolder.sInstance;
    }

    public User getUser() {
        if (isLoginFlag) {
            if(user.getAccount().isEmpty()) {
                try {
                    GetUserTask task = new GetUserTask(account);
                    task.start();
                    task.join();
                    user = task.user;
                    return user;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return new User();
            } else {
                return user;
            }
        } else {
            return new User();
        }
    }

    // 判断是否已经登录
    public boolean isLogin() {
        return isLoginFlag;
    }

    // 登录
    public int login(String account, String password) {
        try {
            LoginTask task = new LoginTask(account, password);
            task.start();
            task.join();
            if (task.answer == 0) {
                isLoginFlag = true;
                this.account = account;
                user = getUser();
            }
            return task.answer;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // 发送验证码
    public void sendVerificationCode(String address) {
        try {
            SendCodeTask task = new SendCodeTask(address);
            task.start();
            task.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    // 使用验证码重设密码
    public int resetPasswordWithCode(String account, String password, String code) {
        try {
            ResetPasswordWithCodeTask task = new ResetPasswordWithCodeTask(account, password, code);
            task.start();
            task.join();
            return task.answer;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // 注册
    public int register(String account, String password, String code) {
        try {
            RegisterTask task = new RegisterTask(account, password, code);
            task.start();
            task.join();
            return task.answer;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private static class UserModelHolder {
        private static final UserModel sInstance = new UserModel();
    }

    private class LoginTask extends Thread {
        private String account;
        private String password;
        private int answer;

        public LoginTask(String account, String password) {
            this.account = account;
            this.password = password;
            answer = -1;
        }

        @Override
        public void run() {
            JSONObject params = new JSONObject();
            params.put("account", account);
            params.put("password", password);
            JSONObject res = HttpUtils.post(LOGIN_PATH, params);
            answer = Integer.parseInt(res.get("answer").toString());
            System.out.println("------user login-----");
            System.out.println("login result: " + answer);
        }
    }

    private class SendCodeTask extends Thread {
        private String address;

        public SendCodeTask(String address) {
            this.address = address;
        }

        @Override
        public void run() {
            JSONObject param = new JSONObject();
            param.put("account", address);
            JSONObject res = HttpUtils.post(SEND_CODE_PATH, param);
            System.out.println("-----Send Verification Code-----");
        }
    }

    private class ResetPasswordWithCodeTask extends Thread {
        private String account;
        private String password;
        private String code;
        private int answer;

        public ResetPasswordWithCodeTask(String account, String password, String code) {
            this.account = account;
            this.password = password;
            this.code = code;
            answer = -1;
        }

        @Override
        public void run() {
            JSONObject params = new JSONObject();
            params.put("action_flag", "verification_code");
            params.put("account", account);
            params.put("new_password", password);
            params.put("verification_code", code);
            JSONObject res = HttpUtils.post(RESET_PASSWORD_PATH, params);
            System.out.println("-----Reset Password-----");
            answer = Integer.parseInt(res.get("answer").toString());
            System.out.println("Reset Password Result: " + answer);
        }
    }

    private class RegisterTask extends Thread {
        private String account;
        private String password;
        private String code;
        private int answer;

        public RegisterTask(String account, String password, String code) {
            this.account = account;
            this.password = password;
            this.code = code;
            answer = -1;
        }

        @Override
        public void run() {
            JSONObject params = new JSONObject();
            params.put("account", account);
            params.put("password", password);
            params.put("verification_code", code);
            JSONObject res = HttpUtils.post(REGISTER_PATH, params);
            answer = Integer.parseInt(res.get("answer").toString());
            System.out.println("-----Register-----");
            System.out.println("Register Result: " + answer);
        }
    }

    private class GetUserTask extends Thread {
        private String account;
        private User user;

        public GetUserTask(String account) {
            this.account = account;
            user = new User();
        }

        @Override
        public void run() {
            System.out.println("----Get User-----\n" + "account: " + account);
            JSONObject param = new JSONObject();
            param.put("account", account);
            JSONObject ans = HttpUtils.post("get_user", param);
            int result = Integer.parseInt(ans.get("result").toString());
            if (result == 0) {
                user.fromJSONObject(ans.getJSONObject("user"));
            }
        }
    }
}
