package edu.sysu.citystorymap.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.user.User;

public class UserInfoFragment extends Fragment implements MainContract.UserInfoView{
    private TextView userName;
    private TextView storyAmount;
    private LinearLayout allStory;
    private LinearLayout aboutUs;
    private LinearLayout settings;
    private MainContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, null);
        init(view);
        return view;
    }

    private void init(View view) {
        userName = view.findViewById(R.id.user_name);
        storyAmount = view.findViewById(R.id.story_amount);
        allStory = view.findViewById(R.id.all_story);
        aboutUs = view.findViewById(R.id.about_us);
        settings = view.findViewById(R.id.settings);

        mPresenter.getUserInfo(new OnGetUserInfo() {
            @Override
            public void show(String name, int amount) {
                userName.setText(name);
                storyAmount.setText("已写下"+amount+"篇故事");
            }
        });

        allStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.showAllStory();
            }
        });

        aboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setCancelable(true)
                        .setNeutralButton("我知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setMessage("故事地图\n" +
                                "开发者: 姜智瀚，张洪宾，张景润\n" +

                                "联系方式:zhanghb55@mail2.sysu.edu.cn\n" +
                                "感谢您的使用")
                        .create()
                        .show();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void refresh() {
        mPresenter.getUserInfo(new OnGetUserInfo() {
            @Override
            public void show(String name, int amount) {
                userName.setText(name);
                storyAmount.setText("已写下"+amount+"篇故事");
            }
        });
    }

    public interface OnGetUserInfo {
        void show(String name, int amount);
    }
}
