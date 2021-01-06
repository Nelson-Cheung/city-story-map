package edu.sysu.citystorymap.main;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.model.StoryModel;
import edu.sysu.citystorymap.model.UserModel;

public class MainControlActivity extends AppCompatActivity {
    private LinearLayout homePage;
    private LinearLayout map;
    private LinearLayout user;
    private LinearLayout status;

    private MapFragment mapView;
    private UserInfoFragment userInfoView;

    private MainPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_control);

        init();
        getSupportActionBar().hide();
        mapView = new MapFragment();
        userInfoView = new UserInfoFragment();

        StoryModel model = new StoryModel();
        UserModel userModel = UserModel.getInstance();
        presenter = new MainPresenter(this, mapView, userInfoView, model, userModel);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, userInfoView)
                .add(R.id.container, mapView)
                .hide(userInfoView)
                .commit();
    }

    private void init() {
        homePage = findViewById(R.id.home_page);
        map = findViewById(R.id.map);
        status = findViewById(R.id.status);
        user = findViewById(R.id.user);

        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapView.fb.show();
                getSupportFragmentManager().beginTransaction()
                        .hide(userInfoView)
                        .show(mapView)
                        .commit();
            }
        });

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getSupportFragmentManager().beginTransaction()

            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .hide(mapView)
                        .show(userInfoView)
                        .commit();
                mapView.fb.hide();
                userInfoView.refresh();
            }
        });
    }
}
