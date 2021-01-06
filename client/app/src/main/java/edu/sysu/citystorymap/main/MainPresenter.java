package edu.sysu.citystorymap.main;

import android.content.Intent;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.List;

import edu.sysu.citystorymap.editstory.EditStoryActivity;
import edu.sysu.citystorymap.model.StoryModel;
import edu.sysu.citystorymap.model.UserModel;
import edu.sysu.citystorymap.myposition.MyPosition;
import edu.sysu.citystorymap.showstories.ShowStoryActivity;
import edu.sysu.citystorymap.userstory.UserStory;

public class MainPresenter implements MainContract.Presenter {
    private MainControlActivity mContext;
    private MapFragment mMapFragment;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private LocationClient mLocationClient;
    private BDLocation mlocation;

    private UserInfoFragment mUserInfoFragment;

    private StoryModel mModel;
    private UserModel mUserModel;

    public MainPresenter(MainControlActivity context, MapFragment mapFragment,
                         UserInfoFragment UserInfoView, StoryModel model, UserModel userModel){
        mContext = context;
        mMapFragment = mapFragment;
        mModel = model;

        mUserModel = userModel;
        mUserInfoFragment = UserInfoView;

        mMapFragment.setPresenter(this);
        mUserInfoFragment.setPresenter(this);
    }

    @Override
    public void setMapView(MapView mapView) {
        mMapView = mapView;
    }

    @Override
    public void setMap(BaiduMap baiduMap) {
        mBaiduMap = baiduMap;
    }


    @Override
    public void locate() {
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient = new LocationClient(mMapView.getContext());
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setOpenGps(true);
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);

        mLocationClient.setLocOption(option);
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start();
        mBaiduMap.setMyLocationEnabled(false);
    }

    @Override
    public void doSuggestionSearch(String content, OnGetSuggestionResultListener listener) {
        SuggestionSearch suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(listener);
        SuggestionSearchOption option = new SuggestionSearchOption();
        option.city(mlocation.getCity());
        option.keyword(content);
        suggestionSearch.requestSuggestion(option);
        suggestionSearch.destroy();
    }

    @Override
    public void showStoryWithTags(String tags) {
        Intent intent = new Intent(mContext, ShowStoryActivity.class);
        intent.putExtra("type", ShowStoryActivity.TAGS);
        intent.putExtra("tags", tags);
        mContext.startActivity(intent);
    }

    @Override
    public void showStoryAtPosition(LatLng position) {
        Intent intent = new Intent();
        intent.setClass(mContext, ShowStoryActivity.class);
        intent.putExtra("type", ShowStoryActivity.LOCATION);
        intent.putExtra("latitude", position.latitude);
        intent.putExtra("longitude", position.longitude);
        mContext.startActivity(intent);
    }

    @Override
    public void editStoryAtPosition(LatLng position) {
        Intent intent = new Intent();
        intent.setClass(mContext, EditStoryActivity.class);
        intent.putExtra("latitude", position.latitude);
        intent.putExtra("longitude", position.longitude);
        intent.putExtra("type", UserStory.INSERT);
        mContext.startActivity(intent);
    }

    @Override
    public void getStoryWithBound(MyPosition p1, MyPosition p2, MapFragment.OnGetStoryPointListListener listener) {
       List<MyPosition> list = mModel.getStoryWithBound(p1,p2);
        listener.show(list);
    }

    @Override
    public void showAllStory() {
        Intent intent = new Intent();
        intent.setClass(mContext, ShowStoryActivity.class);
        intent.putExtra("account", mUserModel.getUser().getAccount());
        intent.putExtra("type", ShowStoryActivity.ACCOUNT);
        mContext.startActivity(intent);
    }

    @Override
    public void getUserInfo(UserInfoFragment.OnGetUserInfo onGetUserInfo) {
        String account = mUserModel.getUser().getAccount();
        int amount = mModel.getStoryWithAccount(account).size();
        onGetUserInfo.show(account, amount);
    }

    @Override
    public void start() {

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null && mMapView == null) {
                mlocation = null;
                return;
            }

            mlocation = bdLocation;

            System.out.println(bdLocation.getLatitude()
                    + " "
                    + bdLocation.getLongitude());

            MyLocationData locationData = new MyLocationData.Builder().accuracy(bdLocation.getRadius())
                    .direction(bdLocation.getDirection())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();

            mBaiduMap.setMyLocationData(locationData);

            LatLng position = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            mMapFragment.jumpTo(position);
            mLocationClient.stop();
        }
    }
}
