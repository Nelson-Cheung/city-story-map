package edu.sysu.citystorymap.searchstory;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import edu.sysu.citystorymap.R;

import static android.view.View.GONE;

public class SearchStoryActivity extends AppCompatActivity {
    private Button show, search, closeAllTags;
    private LinearLayout searchBar;
    private Spinner choice;
    private EditText content;
    private RelativeLayout allTags;
    private boolean[] mark;
    private Button[] buttons;
    private int[] btnIds;
    private boolean isSearchBar, isAllTags;
    private int choicePosition;
    private InputMethodManager imm;
    private View.OnClickListener tagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int num = (int) v.getTag();
            mark[num] = !mark[num];
            String str = showChosenTags();
            System.out.println(str);
            content.setText(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_story);

        show = (Button) findViewById(R.id.show);
        search = (Button) findViewById(R.id.search);
        closeAllTags = (Button) findViewById(R.id.close_all_tags);
        searchBar = (LinearLayout) findViewById(R.id.search_bar);
        choice = (Spinner) findViewById(R.id.choice);
        content = (EditText) findViewById(R.id.content);
        allTags = (RelativeLayout) findViewById(R.id.all_tags);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        isSearchBar = false;

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearchBar == false) {
                    show.setText("关闭");
                    searchBar.setVisibility(View.VISIBLE);
                    isSearchBar = true;
                } else {
                    show.setText("搜索");
                    searchBar.setVisibility(GONE);
                    allTags.setVisibility(GONE);
                    content.setText("");
                    isSearchBar = false;
                    imm.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.text_view, new String[]{"标签", "文本"});
        choice.setAdapter(adapter);

        choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choicePosition = position;

                if (position == 0) {
                    content.setFocusableInTouchMode(false);
                    content.setText("你的标签");
                    imm.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    content.setFocusableInTouchMode(true);
                    content.setText("");
                    allTags.setVisibility(GONE);
                    show.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                searchBar.setVisibility(GONE);
                isSearchBar = false;
            }
        });

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (choicePosition == 0) {
                    allTags.setVisibility(View.VISIBLE);
                    show.setVisibility(GONE);
                }
            }
        });

        closeAllTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allTags.setVisibility(GONE);
                show.setVisibility(View.VISIBLE);
            }
        });


        //标签按钮的id
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
            buttons[i] = (Button) findViewById(btnIds[i]);
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(tagClickListener);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   StringBuffer buffer = new StringBuffer();

                for (int i = 1; i < mark.length; ++i) {
                    if (mark[i]) buffer.append(buttons[i].getText()).append(" ");
                }
                if (buffer.length() > 0) buffer.deleteCharAt(buffer.length() - 1);
                Intent intent = new Intent(getApplicationContext(), ReadStoriesActivity.class);
                intent.putExtra("get_story_type", GetStoryType.TAGS);
                intent.putExtra("tags", buffer.toString());
                startActivity(intent);*/
                finish();
            }
        });
    }

    private String showChosenTags() {
        boolean flag = false;
        StringBuffer buffer = new StringBuffer("你想看一个");

        for (int i = 0; i < mark.length; ++i) {
            if (mark[i] == true) {
                flag = true;
                buffer.append(buttons[i].getText().toString() + "的、");
            }
        }

        if (flag) {
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append("故事");
        } else {
            buffer = new StringBuffer("你想看一个什么样的故事呢？");
        }

        return buffer.toString();
    }

}
