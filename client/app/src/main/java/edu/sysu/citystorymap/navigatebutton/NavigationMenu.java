package edu.sysu.citystorymap.navigatebutton;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableLayout;

import edu.sysu.citystorymap.R;

public class NavigationMenu extends TableLayout {
    private int screenHeight;
    private int screenWidth;
    private WindowManager.LayoutParams wmParams;
    private WindowManager wm;

    private Button user, transfer, search, locate, story;

    public NavigationMenu(Activity activity) {
        super(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.navigation_menu, this);

        wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        //屏宽
        screenWidth = wm.getDefaultDisplay().getWidth();
        //屏高
        screenHeight = wm.getDefaultDisplay().getHeight();
        //布局设置
        wmParams = new WindowManager.LayoutParams();
        // 设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 设置Window flag
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //添加项时修改此处
        wmParams.width = 200 * dm.densityDpi / 160;
        wmParams.height = 150 * dm.densityDpi / 160;
        wmParams.y = (screenHeight - wmParams.height) / 3;
        wmParams.x = (screenWidth - wmParams.width) / 2;
        wm.addView(this, wmParams);
        hide();

        user = view.findViewById(R.id.user);
        transfer = view.findViewById(R.id.transfer);
        search = view.findViewById(R.id.search);
        locate = view.findViewById(R.id.locate);
        story = view.findViewById(R.id.story);

    }

    public void show() {
        if (isShown()) {
            return;
        }
        setVisibility(View.VISIBLE);
    }


    public void hide() {
        setVisibility(View.GONE);
    }

    public void destory() {
        hide();
        wm.removeViewImmediate(this);
    }

    public Button getUserBtn() {
        return user;
    }

    public Button getTransferBtn() {
        return transfer;
    }

    public Button getSearchBtn() {
        return search;
    }

    public Button getLocateBtn() {
        return locate;
    }

    public Button getStoryBtn() {
        return story;
    }
}
