package edu.sysu.citystorymap.editstory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.editor.common.EssFile;
import edu.sysu.citystorymap.editor.common.FilesUtils;
import edu.sysu.citystorymap.editor.view.RichEditorNew;

import static android.view.View.GONE;

public class EditStoryFragment extends Fragment implements EditStoryContract.View {
    public final static int RESULT_CHOOSE = 123;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final int WHOLE_STORY = 0;
    private final int SUMMAY_AND_TAGS = 1;
    private final int MAX_NUMBER_LIMIT = 15;

    private boolean FIRST = true;
    private EditStoryContract.Presenter mPresenter;
    private ImageView btnback, btnNext;
    private RichEditorNew richEditor;
    private GridView bottomBar, fontChoice;
    private int[] bottomBarItems = {R.drawable.font, R.drawable.file, R.drawable.justify_left,
            R.drawable.justify_center, R.drawable.justify_right};
    private int[] fontChoiceItems = {R.drawable.color, R.drawable.bold, R.drawable.italic,
            R.drawable.strikethrough, R.drawable.underline, R.drawable.font_size};
    private int step;
    private boolean isFontChoice = false;
    private LinearLayout summaryInput;
    private RelativeLayout tagsLayout;
    private EditText summary;
    private TextView numberLimit;
    private ImageView cancelTags;
    private boolean[] mark;
    private Button[] buttons;
    private int[] btnIds;
    private ProgressDialog uploadDialog;

    private View.OnClickListener onTagBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (int) v.getTag();
            mark[index] = !mark[index];
            if (mark[index]) {
                buttons[index].setTextColor(Color.BLUE);
            } else {
                buttons[index].setTextColor(Color.BLACK);
            }
        }
    };

    /**
     * 在对sd卡进行读写操作之前调用这个方法
     * Checks if the app has permission to write to device storage
     * If the app does not has permission then the user will be prompted to grant permissions
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        step = WHOLE_STORY;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_story_fragment, container, false);
        btnback = view.findViewById(R.id.back);
        btnNext = view.findViewById(R.id.next);
        richEditor = view.findViewById(R.id.whole_story);
        fontChoice = view.findViewById(R.id.font_choice);
        bottomBar = view.findViewById(R.id.bottom_bar);
        summary = view.findViewById(R.id.summary);
        numberLimit = view.findViewById(R.id.number);
        cancelTags = view.findViewById(R.id.cancel_tags);
        summaryInput = view.findViewById(R.id.summary_input);
        tagsLayout = view.findViewById(R.id.tags_layout);

        bottomBar.setAdapter(new BottomBarAdapter());
        fontChoice.setAdapter(new FontChoiceAdapter());

        fontChoice.setVisibility(GONE);

       bottomBar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (!isFontChoice)
                            fontChoice.setVisibility(View.VISIBLE);
                        else
                            fontChoice.setVisibility(GONE);
                        isFontChoice = !isFontChoice;
                        break;
                    case 1:
                        insertFile();
                        break;
                    case 2:
                        richEditor.setAlignLeft();
                        break;
                    case 3:
                        richEditor.setAlignCenter();
                        break;
                    case 4:
                        richEditor.setAlignRight();
                        break;
                    default:
                        break;
                }

            }
        });

        fontChoice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fontChoice.setVisibility(GONE);
                isFontChoice = !isFontChoice;

                AlertDialog.Builder builder;

                switch (position) {
                    case 0:
                        String[] colorNames = new String[]{"黑色", "红色", "灰色", "蓝色", "黄色", "青色"};
                        final int[] colorValues = new int[]{Color.BLACK, Color.RED, Color.GRAY, Color.BLUE, Color.YELLOW, Color.CYAN,};
                        builder = new AlertDialog.Builder(getActivity(), R.style.show_notice);
                        builder.setTitle("选择字体大小")
                                .setItems(colorNames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {
                                        richEditor.setTextColor(colorValues[position]);
                                    }
                                }).show();
                        break;
                    case 1:
                        richEditor.setBold();
                        break;
                    case 2:
                        richEditor.setItalic();
                        break;
                    case 3:
                        richEditor.setStrikeThrough();
                        break;
                    case 4:
                        richEditor.setUnderline();
                        break;
                    case 5:
                        String[] fontSizeNames = new String[]{"非常小", "较小", "默认", "中等", "大", "较大", "非常大"};
                        builder = new AlertDialog.Builder(getActivity(), R.style.show_notice);
                        builder.setTitle("选择颜色")
                                .setItems(fontSizeNames, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int position) {
                                        richEditor.setFontSize(position + 1);
                                    }
                                }).show();
                        break;
                }
            }
        });

        summary.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > MAX_NUMBER_LIMIT) {
                    s.delete(MAX_NUMBER_LIMIT, s.length());
                    Toast.makeText(getActivity(), "字数已达上限", Toast.LENGTH_LONG);

                }
                numberLimit.setText(s.length() + "/" + MAX_NUMBER_LIMIT);
            }
        });

        numberLimit.setText("0/" + MAX_NUMBER_LIMIT);

        btnIds = new int[]{0,
                R.id.t01, R.id.t02, R.id.t03, R.id.t04, R.id.t05, R.id.t06,
                R.id.t07, R.id.t08, R.id.t09, R.id.t10, R.id.t11,
                R.id.t12, R.id.t13, R.id.t14, R.id.t15, R.id.t16,
                R.id.t17, R.id.t18, R.id.t19, R.id.t20, R.id.t21,
                R.id.t22, R.id.t23, R.id.t24, R.id.t25, R.id.t26,
                R.id.t27, R.id.t28, R.id.t29, R.id.t30, R.id.t31,
                R.id.t32, R.id.t33, R.id.t34, R.id.t35, R.id.t36,
                R.id.t37, R.id.t38, R.id.t39, R.id.t40, R.id.t41,
                R.id.t42, R.id.t43, R.id.t44, R.id.t45, R.id.t46,
                R.id.t47, R.id.t48};

        mark = new boolean[49];
        buttons = new Button[49];

        for (int i = 1; i < mark.length; ++i) {
            mark[i] = false;
            buttons[i] = view.findViewById(btnIds[i]);
            buttons[i].setTag(i);
        }

        //为每一个标签按钮设置监听
        for (int i = 1; i < buttons.length; ++i) {
            buttons[i].setOnClickListener(onTagBtnClickListener);
        }

        cancelTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i < mark.length; ++i) {
                    if (mark[i]) {
                        buttons[i].setTextColor(Color.BLACK);
                        mark[i] = false;
                    }
                }
            }
        });

        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (step) {
                    case WHOLE_STORY:
                        getActivity().finish();
                        break;
                    case SUMMAY_AND_TAGS:
                        summaryInput.setVisibility(GONE);
                        tagsLayout.setVisibility(GONE);
                        richEditor.setVisibility(View.VISIBLE);
                        bottomBar.setVisibility(View.VISIBLE);
                        step = WHOLE_STORY;
                        break;
                    default:
                        break;

                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (step) {
                    case WHOLE_STORY:
                        step = SUMMAY_AND_TAGS;
                        summaryInput.setVisibility(View.VISIBLE);
                        tagsLayout.setVisibility(View.VISIBLE);
                        richEditor.setVisibility(GONE);
                        bottomBar.setVisibility(GONE);
                        break;
                    case SUMMAY_AND_TAGS:
                        mPresenter.upload(richEditor.getAllSrcAndHref(), richEditor.getHtml(), summary.getText().toString(), getAllTags(), new EditStoryContract.Result() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError() {
                            }
                        });
                        Toast.makeText(getActivity(), "故事已提交", Toast.LENGTH_SHORT);
                        getActivity().finish();
                        break;
                    default:
                        break;
                }
            }
        });

        return view;
    }

    private String getAllTags() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 1; i < mark.length; ++i) {
            if (mark[i]) {
                buffer.append(buttons[i].getText().toString()).append(" ");
            }
        }
        if (buffer.length() > 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (FIRST) {
            mPresenter.start();
            FIRST = false;
        }
    }

    @Override
    public void setWholeStory(String text) {
        richEditor.setHtml(text);
    }

    @Override
    public void setSummary(String text) {
        summary.setText(text);
    }

    @Override
    public void setTag() {

    }

    @Override
    public void setPresenter(EditStoryContract.Presenter Presenter) {
        mPresenter = Presenter;
    }

    /**
     * 根据返回选择的文件，来进行上传操作
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == RESULT_CHOOSE) {
            Uri uri = data.getData();
            if (uri != null) {
                String abUrl = FilesUtils.getPath(getActivity(), uri);

                Log.i("rex", "abUrl:" + abUrl);

                EssFile essFile = new EssFile(abUrl);

                if (essFile.isImage() || essFile.isGif()) {
                    richEditor.insertImage(essFile.getAbsolutePath());
                } else if (essFile.isVideo()) {
                    richEditor.insertVideo(essFile.getAbsolutePath());
                } else if (essFile.isAudio()) {
                    richEditor.insertAudio(essFile.getAbsolutePath());
                } else {
                    richEditor.insertFileWithDown(essFile.getAbsolutePath(), "file:");
                }

            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 这里采用系统自带方法，可替换为你更方便的自定义文件选择器
     */
    private void insertFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        //   intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);//多选
        startActivityForResult(intent, RESULT_CHOOSE);
    }

    private class BottomBarAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return bottomBarItems.length;
        }

        @Override
        public Object getItem(int position) {
            return bottomBarItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = View.inflate(getActivity(), R.layout.item_gv_edit, null);
            } else {
                view = convertView;
            }

            ImageView icon = view.findViewById(R.id.ivIcon);
            icon.setImageResource(bottomBarItems[position]);

            return view;
        }
    }

    private class FontChoiceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fontChoiceItems.length;
        }

        @Override
        public Object getItem(int position) {
            return fontChoiceItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;

            if (convertView == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.item_gv_edit, null);
            } else {
                view = convertView;
            }

            ImageView icon = view.findViewById(R.id.ivIcon);
            icon.setImageResource(fontChoiceItems[position]);

            return view;
        }
    }
}
