package edu.sysu.citystorymap.entrance;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.sysu.citystorymap.R;


public class ForgetPasswordFrag extends Fragment implements EntranceContract.ForgetPasswordView {
    private EntranceContract.EntrancePresenter mPresenter;
    private EditText account;
    private Button sendCode, btnBack;
    private Button resetPassword;
    private EditText password, verificationCode, passwordConfirm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forget_password, null);
        init(view);
        return view;
    }

    private void init(View view) {
        account = view.findViewById(R.id.account);
        sendCode = view.findViewById(R.id.send_code);
        btnBack = view.findViewById(R.id.back);
        password = view.findViewById(R.id.password);
        passwordConfirm = view.findViewById(R.id.password_confirm);
        verificationCode = view.findViewById(R.id.verification_code);
        resetPassword = view.findViewById(R.id.reset_password);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        sendCode.setOnClickListener(new View.OnClickListener() {
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
                } else {
                    mPresenter.sendVerificationCode(account.getText().toString());
                }
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("密码不可为空")
                            .setCancelable(true)
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                } else if (verificationCode.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("验证码不可为空")
                            .setCancelable(true)
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                } else if (!password.getText().toString().equals(passwordConfirm.getText().toString())) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("两次密码输入不一致")
                            .setCancelable(true)
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                } else {
                    mPresenter.resetPassword(account.getText().toString(), password.getText().toString(),
                            verificationCode.getText().toString(), new EntranceContract.Result() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getActivity(), "修改密码成功", Toast.LENGTH_SHORT);
                            btnBack.callOnClick();
                        }

                        @Override
                        public void onFailure(int answer) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("修改密码失败，错误码: " + answer)
                                    .setCancelable(true)
                                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();
                            btnBack.callOnClick();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void setPresenter(EntranceContract.EntrancePresenter presenter) {
        mPresenter = presenter;
    }
}
