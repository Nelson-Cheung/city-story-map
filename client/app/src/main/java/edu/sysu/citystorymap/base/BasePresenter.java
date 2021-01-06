package edu.sysu.citystorymap.base;

public interface BasePresenter {
    // 规定Presenter必须要实现start方法。
    // 该方法的作用是Presenter开始获取数据并调用View的方法来刷新界面，
    // 其调用时机是在Fragment类的onResume方法中
    void start();
}
