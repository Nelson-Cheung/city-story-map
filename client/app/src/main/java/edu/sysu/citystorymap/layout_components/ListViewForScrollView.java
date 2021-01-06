package edu.sysu.citystorymap.layout_components;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 解决ScrollView嵌套ListView，ListView显示不全的问题
 * 注意：ScrollView嵌套ListView时，如果ListView很长超出了屏幕的高度，
 * 那么ScrollView会自动滚动到ListView的顶部，
 * 但是我们需要默认在整体页面顶部，所以在初始化的时候就让ScrollView获得焦点，滚动条自然就显示到顶部了。
 * scrollView.setFocusable(true);
 * scrollView.setFocusableInTouchMode(true);
 * scrollView.requestFocus();
 */
public class ListViewForScrollView extends ListView {

    public ListViewForScrollView(Context context) {
        super(context);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListViewForScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写onMeasure方法，重新计算高度，达到使ListView适应ScrollView的效果
     *
     * @param widthMeasureSpec  宽度测量规则
     * @param heightMeasureSpec 高度测量规则
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Integer.MAX_VALUE:表示int类型能够表示的最大值，值为2的31次方-1
        //>>2:右移N位相当于除以2的N的幂
        //MeasureSpec.AT_MOST：子布局可以根据自己的大小选择任意大小的模式
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }
}