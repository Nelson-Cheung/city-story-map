package edu.sysu.citystorymap.entrance;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.model.UserModel;


//app入口：登录，忘记密码，注册
public class EntranceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrance);

        getSupportActionBar().hide();

        LoginFrag loginView = new LoginFrag();
        RegisterFrag registerView = new RegisterFrag();
        ForgetPasswordFrag forgetPasswordView = new ForgetPasswordFrag();
        UserModel model = UserModel.getInstance();
        EntrancePresenter presenter = new EntrancePresenter(this, R.id.layout_container, loginView, forgetPasswordView, registerView, model);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_container, loginView)
                .commit();


    }
}
