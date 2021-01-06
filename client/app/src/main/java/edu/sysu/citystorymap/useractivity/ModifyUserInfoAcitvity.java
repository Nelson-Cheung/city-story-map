//package edu.sysu.citystorymap.useractivity;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.os.Bundle;
//import android.text.SpannableString;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import edu.sysu.citystorymap.R;
//import edu.sysu.citystorymap.server.ResetPasswordResult;
//import edu.sysu.citystorymap.server.Server;
//
//public class ModifyUserInfoAcitvity extends AppCompatActivity {
//    private Button btnBack, btnSave;
//    private ImageView userHeadPortrait;
//    private ListView itemList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_modify_user_info_acitvity);
//        btnBack = (Button) findViewById(R.id.back);
//        userHeadPortrait = (ImageView) findViewById(R.id.head_portrait);
//        btnSave = (Button) findViewById(R.id.save);
//        itemList = (ListView) findViewById(R.id.item_list);
//
//        getSupportActionBar().hide();
//
//        /*
//         * 回退键
//         */
//
//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        /*换头像
//         */
//        Bitmap bitmap = BitmapFactory.decodeByteArray(Server.getUser().getHeadPortrait(),
//                0, Server.getUser().getHeadPortrait().length);
//        userHeadPortrait.setImageBitmap(bitmap);
//        userHeadPortrait.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//             //   Server.getUser().setHeadPortraitTemp(getResources().getDrawable(R.drawable.test));
//            }
//        });
//
//        /*
//        保存信息
//         */
//        btnSave.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String res = null;
//                try {
//                    res = Server.updateUser();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                if( res.equals(UserResult.UPATE_SUCCESS) ) Toast.makeText(getApplicationContext(), "修改成功",Toast.LENGTH_SHORT);
//                else Toast.makeText(getApplicationContext(), "修改失败",Toast.LENGTH_SHORT);
//                finish();
//            }
//        });
//
//        /*
//         *修改昵称，密码，个人介绍
//         */
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.user_info_item_list,
//                new String[]{"修改昵称", "修改密码", "修改个人介绍"});
//        itemList.setAdapter(adapter);
//
//        //Dialog的环境一定是Acivity
//        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                View myDialog;
//                switch (position) {
//                    case 0:
//                        myDialog = LayoutInflater.from(ModifyUserInfoAcitvity.this).inflate(R.layout.modify_username, null);
//                        final EditText new_name_editor = (EditText) myDialog.findViewById(R.id.new_user_name_editor);
//
//                        TextView oldNameView = (TextView)myDialog.findViewById(R.id.old_user_name);
//                        oldNameView.setText(Server.getUser().getName());
//
//                        new AlertDialog.Builder(ModifyUserInfoAcitvity.this)
//                                .setTitle("修改昵称")
//                                .setView(myDialog)
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Server.getUser().setNameTemp(new_name_editor.getText().toString());
//                                    }
//                                })
//                                .setCancelable(true)
//                                .create()
//                                .show();
//                        break;
//                    case 1:
//                        myDialog = LayoutInflater.from(ModifyUserInfoAcitvity.this).inflate(R.layout.modify_password, null);
//                        final EditText new_password_editor = (EditText) myDialog.findViewById(R.id.new_user_password_editor);
//                        final EditText old_password_editor = (EditText) myDialog.findViewById(R.id.old_user_password_editor);
//
//                        new AlertDialog.Builder(ModifyUserInfoAcitvity.this)
//                                .setTitle("修改密码")
//                                .setView(myDialog)
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        String res = null;
//                                        try {
//                                            res = Server.resetPassword(old_password_editor.getText().toString(),
//                                                    new_password_editor.getText().toString());
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                        if( res.equals(ResetPasswordResult.SUCCESS) ) Toast.makeText(getApplicationContext(), "修改成功",Toast.LENGTH_SHORT);
//                                        else Toast.makeText(getApplicationContext(), "修改失败",Toast.LENGTH_SHORT);
//                                    }
//                                })
//                                .setCancelable(true)
//                                .create()
//                                .show();
//                        break;
//                    case 2:
//                        myDialog = LayoutInflater.from(ModifyUserInfoAcitvity.this).inflate(R.layout.modify_introduction, null);
//                        final EditText new_introduction_editor = (EditText) myDialog.findViewById(R.id.new_user_introduction_editor);
//
//                        new_introduction_editor.setText(Server.getUser().getIntroduction());
//
//                        new AlertDialog.Builder(ModifyUserInfoAcitvity.this)
//                                .setTitle("修改个人介绍")
//                                .setView(myDialog)
//                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        Server.getUser().setIntroductionTemp(new SpannableString(new_introduction_editor.getText()));
//                                    }
//                                })
//                                .setCancelable(true)
//                                .create()
//                                .show();
//                        break;
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Server.getUser().setHeadPortraitChanged(false);
//        Server.getUser().setIntroductionChanged(false);
//        Server.getUser().setNameChanged(false);
//    }
//}
