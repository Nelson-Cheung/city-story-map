package edu.sysu.citystorymap.main;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;

import edu.sysu.citystorymap.base.BasePresenter;
import edu.sysu.citystorymap.base.BaseView;
import edu.sysu.citystorymap.myposition.MyPosition;

public interface MainContract {
    interface Presenter extends BasePresenter {
        // MapView
        void setMapView(com.baidu.mapapi.map.MapView mapView);
        void setMap(BaiduMap baiduMap);
        void locate();
        void doSuggestionSearch(String content, OnGetSuggestionResultListener listener);
        void showStoryWithTags(String tags);
        void showStoryAtPosition(LatLng position);
        void editStoryAtPosition(LatLng position);
        void getStoryWithBound(MyPosition p1, MyPosition p2, MapFragment.OnGetStoryPointListListener listener);

        // User Info
        void showAllStory();
        void getUserInfo(UserInfoFragment.OnGetUserInfo onGetUserInfo);
    }

    interface MapView extends BaseView<Presenter> {
        void jumpTo(LatLng position);
    }

    interface UserInfoView extends BaseView<Presenter> {
        void refresh();
    }
}
