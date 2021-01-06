//package edu.sysu.citystorymap.useractivity;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import edu.sysu.citystorymap.R;
//
//
///*
// * 用户信息模块
// */
//public class UserActivity extends AppCompatActivity {
//
//    private Button btnBack, btnManager, btnUserStories, btnCollectStories, btnPenpals, btnPenpalStories;
//    private ImageView userHeadPortrait;
//    private TextView userName, userIntroduction, userAccount;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user);
//
//
//        //使用自定义action bar
//        getSupportActionBar().hide();
//
//        btnBack = (Button) findViewById(R.id.back);
//        btnManager = (Button) findViewById(R.id.manager);
//        btnUserStories = (Button) findViewById(R.id.user_stories);
//        btnCollectStories = (Button) findViewById(R.id.collect_stories);
//        btnPenpals = (Button) findViewById(R.id.user_penpals);
//        btnPenpalStories = (Button) findViewById(R.id.penpal_stories);
//        userHeadPortrait = (ImageView) findViewById(R.id.head_portrait);
//        userName = (TextView) findViewById(R.id.user_name);
//        userIntroduction = (TextView) findViewById(R.id.user_introduction);
//        userAccount = (TextView)findViewById(R.id.user_account);
//        /*
//         * 对返回按钮点击事件的监听
//         */
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        /*
//         * 对用户信息管理事件的监听
//         */
//        btnManager.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //启动信息管理的Activity
//                Intent intent = new Intent(getApplicationContext(), ModifyUserInfoAcitvity.class);
//                intent.putExtra("user_account",Server.getUser().getAccount());
//                startActivity(intent);
//           }
//        });
//
//        /*
//         * 展示用户写过的故事
//         */
//        btnUserStories.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //user.getStories().showInfoSet(getApplicationContext());
//                Intent intent = new Intent(getApplicationContext(), ReadStoriesActivity.class);
//                intent.putExtra(GetStoryType.TYPE_KEY, GetStoryType.ACCOUNT);
//                intent.putExtra("account", Server.getUser().getAccount());
//                intent.putExtra("is_deletable", true);
//                intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
//                getApplicationContext().startActivity(intent);
//            }
//        });
//
//        /*
//         *展示用户收藏的故事
//         */
//        btnCollectStories.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // user.getCollectStories().showInfoSet(getApplicationContext());
//            }
//        });
//
//        btnPenpals.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  user.getPenpals().showInfoSet(getApplicationContext());
//            }
//        });
//
//        /*
//         * 展示笔友动态
//         */
//        btnPenpalStories.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              //  user.getPenpalsStories().showInfoSet(getApplicationContext());
//            }
//        });
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //设置用户头像
//        Bitmap bitmap = BitmapFactory.decodeByteArray(Server.getUser().getHeadPortrait(),
//                0, Server.getUser().getHeadPortrait().length);
//        userHeadPortrait.setImageBitmap(bitmap);
//
//        //设置用户介绍
//        userIntroduction.setText(Server.getUser().getIntroduction());
//
//        //设置用户名
//        userName.setText(Server.getUser().getName());
//
//        //设置用户账户
//        userAccount.setText(Server.getUser().getAccount());
//    }
//}
