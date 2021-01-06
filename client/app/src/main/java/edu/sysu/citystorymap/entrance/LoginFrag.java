package edu.sysu.citystorymap.entrance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.sysu.citystorymap.R;


/*
 *登录界面Fragment
 */
public class LoginFrag extends Fragment implements EntranceContract.LoginView {
    private EntranceContract.EntrancePresenter mPresenter;
    private EditText account, password;
    private ImageView btnLogin;
    private TextView forgetPassword, userRegister;
    private String loginResult;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mPresenter.isLogin()) {
            mPresenter.finishLogin();
        }
        View view = inflater.inflate(R.layout.fragment_login, null);
        init(view);
        return view;
    }

    private void init(View view) {
        account = view.findViewById(R.id.account);
        password = view.findViewById(R.id.password);
        btnLogin = view.findViewById(R.id.login);
        forgetPassword = view.findViewById(R.id.forget_password);
        userRegister = view.findViewById(R.id.register);

        //登录按钮事件监听
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity()) // 在Fragment中调用对话框的方法
                            .setMessage("账号不可为空")
                            .setCancelable(true)
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                } else if (password.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity()) // 在Fragment中调用对话框的方法
                            .setMessage("密码不可为空")
                            .setCancelable(true)
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                } else {
                    mPresenter.login(account.getText().toString(), password.getText().toString(), new EntranceContract.Result() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getActivity(), "登陆成功", Toast.LENGTH_SHORT);
                            mPresenter.finishLogin();
                        }

                        @Override
                        public void onFailure(int answer) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("用户名或密码错误，错误码: " + answer)
                                    .setCancelable(true)
                                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    });
                }
            }
        });

        // 忘记密码按钮监听
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.startForgetPassword();
            }
        });

        //用户注册按钮监听
        userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.startRegister();
            }
        });
    }

    @Override
    public void setPresenter(EntranceContract.EntrancePresenter presenter) {
        mPresenter = presenter;
    }
}
