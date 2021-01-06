package edu.sysu.citystorymap.entrance;


import edu.sysu.citystorymap.base.BasePresenter;
import edu.sysu.citystorymap.base.BaseView;

public interface EntranceContract {
    interface EntrancePresenter extends BasePresenter {
        boolean isLogin();
        void login(String account, String password, Result result);
        void finishLogin();
        void startForgetPassword();
        void startRegister();
        void sendVerificationCode(String address);
        void resetPassword(String account, String password, String code, Result result);
        void register(String account, String password, String code, Result result);
    }

    interface LoginView extends BaseView<EntrancePresenter> {

    }

    interface ForgetPasswordView extends BaseView<EntrancePresenter> {

    }

    interface RegisterView extends BaseView<EntrancePresenter> {

    }

    interface Result {
        void onSuccess();
        void onFailure(int answer);
    }

}
