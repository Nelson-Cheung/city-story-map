package edu.sysu.citystorymap.entrance;


import android.content.Intent;

import edu.sysu.citystorymap.main.MainControlActivity;
import edu.sysu.citystorymap.model.UserModel;

public class EntrancePresenter implements EntranceContract.EntrancePresenter {
    private EntranceActivity mContext;
    private LoginFrag mLoginView;
    private ForgetPasswordFrag mForgetPasswordView;
    private RegisterFrag mRegisterView;
    private UserModel mModel;
    private int mContainer;

    public EntrancePresenter(EntranceActivity context, int container, LoginFrag loginView, ForgetPasswordFrag forgetPasswordView, RegisterFrag registerView, UserModel userModel) {
        mContext = context;
        mContainer = container;
        mLoginView = loginView;
        mForgetPasswordView = forgetPasswordView;
        mRegisterView = registerView;
        mModel = userModel;

        mForgetPasswordView.setPresenter(this);
        mLoginView.setPresenter(this);
        mRegisterView.setPresenter(this);
    }

    @Override
    public boolean isLogin() {
        //cookie登录
        //return true;
        return mModel.isLogin();
    }

    @Override
    public void login(String account, String password, EntranceContract.Result result) {
        int res = mModel.login(account, password);
        if(res == 0) {
            result.onSuccess();
        } else {
            result.onFailure(res);
        }
    }

    @Override
    public void finishLogin() {
        //跳转
        Intent intent = new Intent(mContext, MainControlActivity.class);
        mContext.startActivity(intent);
    }

    @Override
    public void startForgetPassword() {
        mContext.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(mContainer, mForgetPasswordView)
                .commit();
    }

    @Override
    public void startRegister() {
        mContext.getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(mContainer, mRegisterView)
                .commit();
    }

    @Override
    public void sendVerificationCode(String address) {
        mModel.sendVerificationCode(address);
    }

    @Override
    public void resetPassword(String account, String password, String code, EntranceContract.Result result) {
        int res = mModel.resetPasswordWithCode(account, password, code);
        if(res == 2) {
            result.onSuccess();
        } else {
            result.onFailure(res);
        }
    }

    @Override
    public void register(String account, String password, String code, EntranceContract.Result result) {
        int res = mModel.register(account, password, code);
        if(res == 0) {
            result.onSuccess();
        } else {
            result.onFailure(res);
        }
    }

    @Override
    public void start() {

    }
}
