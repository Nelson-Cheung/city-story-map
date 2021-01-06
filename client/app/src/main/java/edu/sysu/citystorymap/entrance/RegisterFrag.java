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


public class RegisterFrag extends Fragment implements EntranceContract.RegisterView {
    private EntranceContract.EntrancePresenter mPresenter;
    private EditText account, password, againPassword, verificationCode;
    private Button register, btnBack, btnSendCode;
    private boolean flag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, null);
        init(view);
        return view;
    }

    private void init(View view) {
        account = view.findViewById(R.id.account);
        password = view.findViewById(R.id.password);
        register = view.findViewById(R.id.register);
        btnBack = view.findViewById(R.id.back);
        againPassword = view.findViewById(R.id.password_confirm);
        btnSendCode = view.findViewById(R.id.send_code);
        verificationCode = view.findViewById(R.id.verification_code);

        flag = false;

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        btnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!account.getText().toString().isEmpty()) {
                    mPresenter.sendVerificationCode(account.getText().toString());
                    Toast.makeText(getActivity(), " 已发送验证码", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("请输入邮箱")
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setCancelable(true)
                            .create()
                            .show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (account.getText().toString().isEmpty()) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("邮箱不可为空")
                            .setCancelable(true)
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                } else if (password.getText().toString().isEmpty()) {
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
                } else if (password.getText().toString().equals(againPassword.getText().toString())) {
                    mPresenter.register(account.getText().toString(), password.getText().toString(),
                            verificationCode.getText().toString(), new EntranceContract.Result() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getActivity(), "注册成功", Toast.LENGTH_SHORT).show();
                            btnBack.callOnClick();
                        }

                        @Override
                        public void onFailure(int answer) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("注册失败，错误码: " + answer)
                                    .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setCancelable(true)
                                    .create()
                                    .show();
                        }
                    });
                } else {
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
                }

            }
        });
    }

    @Override
    public void setPresenter(EntranceContract.EntrancePresenter presenter) {
        mPresenter = presenter;
    }
}
