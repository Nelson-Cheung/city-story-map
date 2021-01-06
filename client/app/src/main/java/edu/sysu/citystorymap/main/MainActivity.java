//package edu.sysu.citystorymap.main;
//
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.SimpleAdapter;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.baidu.location.BDAbstractLocationListener;
//import com.baidu.location.BDLocation;
//import com.baidu.location.LocationClient;
//import com.baidu.location.LocationClientOption;
//import com.baidu.mapapi.map.BaiduMap;
//import com.baidu.mapapi.map.BitmapDescriptor;
//import com.baidu.mapapi.map.BitmapDescriptorFactory;
//import com.baidu.mapapi.map.LogoPosition;
//import com.baidu.mapapi.map.MapStatus;
//import com.baidu.mapapi.map.MapStatusUpdateFactory;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.map.Marker;
//import com.baidu.mapapi.map.MarkerOptions;
//import com.baidu.mapapi.map.MyLocationData;
//import com.baidu.mapapi.model.LatLng;
//import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
//import com.baidu.mapapi.search.sug.SuggestionResult;
//import com.baidu.mapapi.search.sug.SuggestionSearch;
//import com.baidu.mapapi.search.sug.SuggestionSearchOption;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import edu.sysu.citystorymap.R;
//import edu.sysu.citystorymap.editstory.EditStoryActivity;
//import edu.sysu.citystorymap.model.StoryModel;
//import edu.sysu.citystorymap.myposition.MyPosition;
//import edu.sysu.citystorymap.navigatebutton.FloatingButton;
//import edu.sysu.citystorymap.showstories.ShowStoryActivity;
//import edu.sysu.citystorymap.useractivity.UserActivity;
//import edu.sysu.citystorymap.userstory.UserStory;
//
//import static android.view.View.GONE;
//
//public class MainActivity extends AppCompatActivity {
//    boolean isTransferBarShowed, isResultShowed;
//    private MapView mMapView;
//    private BaiduMap mBaiduMap;
//    private Button btnLocate;
//    private LocationClient mLocationClient;
//    private Marker myPostionMarker = null;
//    private BDLocation mlocation;
//    //转移到指定地点所需变量
//    private LinearLayout transferBar;
//    private EditText transferLocationInput;
//    private Button resultShow;
//    private Button transferLocation;
//    private InputMethodManager imm;
//    private TextView tv;
//    private List<SuggestionResult.SuggestionInfo> keywordList;
//    private SimpleAdapter adapter;
//    private ListView locationList;
//
//    //打开用户信息
//    private Button btnUser;
//
//    //打开故事搜索框
//    private Button btnSearchStory;
//
//    //故事搜索
//    private Button show, search, closeAllTags;
//    private LinearLayout searchBar;
//    private Spinner choice;
//    private EditText content;
//    private RelativeLayout allTags;
//    private boolean[] mark;
//    private Button[] buttons;
//    private int[] btnIds;
//    private boolean isSearchBar, isAllTags;
//    private int choicePosition;
//
//    //悬浮导航
//    private FloatingButton fb;
//
//    //屏幕内故事的显示
//    private boolean isShowed = false;
//    private List<Marker> storyPointMarkerList = new ArrayList<Marker>();
//
//    private StoryModel mStoryModel;
//
//    private View.OnClickListener tagClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            int num = (int) v.getTag();
//            mark[num] = !mark[num];
//            String str = showChosenTags();
//            content.setText(str);
//            if (mark[num])
//                ((Button) v).setTextColor(getResources().getColor(R.color.chosenColor));
//            else
//                ((Button) v).setTextColor(getResources().getColor(R.color.unChosenColor));
//        }
//    };
//
//    private View.OnClickListener locateBtnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            mBaiduMap.setMyLocationEnabled(true);
//            mLocationClient = new LocationClient(mMapView.getContext());
//            LocationClientOption option = new LocationClientOption();
//            option.setCoorType("bd09ll");
//            option.setOpenGps(true);
//            option.setScanSpan(1000);
//            option.setIsNeedAddress(true);
//
//            mLocationClient.setLocOption(option);
//            MyLocationListener listener = new MyLocationListener();
//            mLocationClient.registerLocationListener(listener);
//            mLocationClient.start();
//            mBaiduMap.setMyLocationEnabled(false);
//
//            fb.getMenu().hide();
//        }
//    };
//
//    private BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
//        @Override
//        public boolean onMarkerClick(Marker marker) {
//            final LatLng position = marker.getPosition();
//            new AlertDialog.Builder(MainActivity.this)
//                    .setItems(new String[]{"添加故事", "查看故事"}, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent();
//                            if (which == 0) {
//                                intent.setClass(getApplicationContext(), EditStoryActivity.class);
//                                intent.putExtra("latitude", position.latitude);
//                                intent.putExtra("longitude", position.longitude);
//                                intent.putExtra("type", UserStory.INSERT);
//                            } else if (which == 1) {
//                                intent.setClass(getApplicationContext(), ShowStoryActivity.class);
//                                intent.putExtra("type", ShowStoryActivity.LOCATION);
//                                intent.putExtra("latitude", position.latitude);
//                                intent.putExtra("longitude", position.longitude);
//                            }
//                            startActivity(intent);
//                        }
//                    })
//                    .create()
//                    .show();
//            return false;
//        }
//    };
//    private BaiduMap.OnMapLongClickListener onMapLongClickListener = new BaiduMap.OnMapLongClickListener() {
//        @Override
//        public void onMapLongClick(LatLng latLng) {
//            final LatLng position = latLng;
//
//            new AlertDialog.Builder(MainActivity.this)
//                    .setItems(new String[]{"添加故事"}, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent();
//                            intent.setClass(getApplicationContext(), EditStoryActivity.class);
//                            intent.putExtra("latitude", position.latitude);
//                            intent.putExtra("longitude", position.longitude);
//                            intent.putExtra("type", UserStory.INSERT);
//                            startActivity(intent);
//                        }
//                    })
//                    .create()
//                    .show();
//        }
//    };
//    private View.OnClickListener transferLocationBtnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (isTransferBarShowed) {
//                transferBar.setVisibility(GONE);
//                locationList.setVisibility(GONE);
//                isResultShowed = false;
//                imm.hideSoftInputFromWindow(transferLocationInput.getWindowToken(), 0);
//            } else {
//                transferBar.setVisibility(View.VISIBLE);
//            }
//            isTransferBarShowed = !isTransferBarShowed;
//
//            fb.getMenu().hide();
//        }
//    };
//    private TextWatcher transferLocationInputWatcher = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            doSuggestionSearch(s.toString());
//        }
//    };
//    private AdapterView.OnItemClickListener locationListItemClickListener = new AdapterView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            try {
//                LatLng location = keywordList.get(position).getPt();
//                imm.hideSoftInputFromWindow(transferLocationInput.getWindowToken(), 0);
//                jumpToPostition(location);
//                locationList.setVisibility(GONE);
//                isResultShowed = false;
//                fb.getMenu().getTransferBtn().callOnClick();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
//    private View.OnClickListener locationListShowBtnClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (isResultShowed) {
//                locationList.setVisibility(GONE);
//            } else {
//                locationList.setVisibility(View.VISIBLE);
//            }
//            isResultShowed = !isResultShowed;
//        }
//    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        mStoryModel = new StoryModel();
//
//        //获取地图控件引用
//        mMapView = findViewById(R.id.bmapView);
//        btnLocate = findViewById(R.id.btnLocate);
//        mBaiduMap = mMapView.getMap();
//
//        mMapView.showScaleControl(false);
//        mMapView.showZoomControls(false);
//        mMapView.setLogoPosition(LogoPosition.logoPostionRightTop);
//
//        //悬浮导航按钮
//        fb = new FloatingButton(this);
//        fb.getMenu().getLocateBtn().setOnClickListener(locateBtnClickListener);
//        fb.getMenu().getSearchBtn().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isSearchBar == false) {
//                    fb.getMenu().getSearchBtn().setText("关闭");
//                    searchBar.setVisibility(View.VISIBLE);
//                    isSearchBar = true;
//                } else {
//                    fb.getMenu().getSearchBtn().setText("搜索");
//                    searchBar.setVisibility(GONE);
//                    allTags.setVisibility(GONE);
//                    content.setText("");
//                    isSearchBar = false;
//                    imm.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//
//                    for (int i = 1; i < mark.length; ++i) {
//                        mark[i] = false;
//                        buttons[i].setTextColor(getResources().getColor(R.color.unChosenColor));
//                    }
//                }
//
//                fb.getMenu().hide();
//            }
//        });
//
//        fb.getMenu().getTransferBtn().setOnClickListener(transferLocationBtnClickListener);
//        fb.getMenu().getUserBtn().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
//                startActivity(intent);
//
//                fb.getMenu().hide();
//            }
//        });
//
//        //获取定位并转移
//        // btnLocate.setOnClickListener(locateBtnClickListener);
//        mBaiduMap.setMyLocationEnabled(true);
//        mLocationClient = new LocationClient(mMapView.getContext());
//        LocationClientOption option = new LocationClientOption();
//        option.setCoorType("bd09ll");
//        option.setOpenGps(true);
//        option.setScanSpan(1000);
//        option.setIsNeedAddress(true);
//
//        mLocationClient.setLocOption(option);
//        MyLocationListener listener = new MyLocationListener();
//        mLocationClient.registerLocationListener(listener);
//        mLocationClient.start();
//        mBaiduMap.setMyLocationEnabled(false);
//
//        //点击覆盖物的时间处理：添加故事或删除故事
//        mBaiduMap.setOnMarkerClickListener(markerClickListener);
//        //地图长按事件处理：添加故事
//        mBaiduMap.setOnMapLongClickListener(onMapLongClickListener);
//
//        //转移到指定地点
//        transferBar = findViewById(R.id.transfer_bar);
//        resultShow = findViewById(R.id.resultShow);
//        transferLocationInput = findViewById(R.id.transfer_location_input);
//        transferLocation = findViewById(R.id.transfer_location);
//        locationList = findViewById(R.id.location_list);
//
//        isTransferBarShowed = isResultShowed = false;
//        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//
//        tv = findViewById(R.id.test);
//        //搜索地点并转移
//        // transferLocation.setOnClickListener(transferLocationBtnClickListener);
//
//        //监听输入变化
//        transferLocationInput.addTextChangedListener(transferLocationInputWatcher);
//        //联想跳转位置点击处理
//        locationList.setOnItemClickListener(locationListItemClickListener);
//        //是否显示联想结果列表
//        resultShow.setOnClickListener(locationListShowBtnClickListener);
//
//        //打开用户信息
//    /*    btnUser = findViewById(R.id.user);
//        btnUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
//                startActivity(intent);
//            }
//        });*/
//
//        //故事搜索
//        show = findViewById(R.id.show);
//        search = findViewById(R.id.search);
//        closeAllTags = findViewById(R.id.close_all_tags);
//        searchBar = findViewById(R.id.search_bar);
//        choice = findViewById(R.id.choice);
//        content = findViewById(R.id.content);
//        allTags = findViewById(R.id.all_tags);
//        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//
//        isSearchBar = false;
//
//      /*  show.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isSearchBar == false) {
//                    show.setText("关闭");
//                    searchBar.setVisibility(View.VISIBLE);
//                    isSearchBar = true;
//                } else {
//                    show.setText("搜索");
//                    searchBar.setVisibility(GONE);
//                    allTags.setVisibility(GONE);
//                    content.setText("");
//                    isSearchBar = false;
//                    imm.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                }
//            }
//        });*/
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.text_view, new String[]{"标签", "文本"});
//        choice.setAdapter(adapter);
//
//        choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                choicePosition = position;
//
//                if (position == 0) {
//                    //content.setFocusableInTouchMode(false);
//                    content.setFocusable(false);
//                    content.setText("你的标签");
//                    imm.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                } else {
//                    content.setFocusable(true);
//                    content.setText("");
//                    allTags.setVisibility(GONE);
//                    // show.setVisibility(View.VISIBLE)
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                searchBar.setVisibility(GONE);
//                isSearchBar = false;
//            }
//        });
//
//        content.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (choicePosition == 0) {
//                    allTags.setVisibility(View.VISIBLE);
//                    //   show.setVisibility(GONE);
//                }
//            }
//        });
//
//        closeAllTags.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                for (int i = 1; i < mark.length; ++i) {
//                    mark[i] = false;
//                    buttons[i].setTextColor(getResources().getColor(R.color.unChosenColor));
//                }
//                content.setText("选择你的标签");
//            }
//        });
//
//
//        //标签按钮的id
//        btnIds = new int[]{0,
//                R.id.t01, R.id.t02, R.id.t03, R.id.t04, R.id.t05, R.id.t06,
//                R.id.t07, R.id.t08, R.id.t09, R.id.t10, R.id.t11,
//                R.id.t12, R.id.t13, R.id.t14, R.id.t15, R.id.t16,
//                R.id.t17, R.id.t18, R.id.t19, R.id.t20, R.id.t21,
//                R.id.t22, R.id.t23, R.id.t24, R.id.t25, R.id.t26,
//                R.id.t27, R.id.t28, R.id.t29, R.id.t30, R.id.t31,
//                R.id.t32, R.id.t33, R.id.t34, R.id.t35, R.id.t36,
//                R.id.t37, R.id.t38, R.id.t39, R.id.t40, R.id.t41,
//                R.id.t42, R.id.t43, R.id.t44, R.id.t45, R.id.t46,
//                R.id.t47, R.id.t48};
//
//        mark = new boolean[49];
//        buttons = new Button[49];
//
//
//        for (int i = 1; i < mark.length; ++i) {
//            mark[i] = false;
//            buttons[i] = findViewById(btnIds[i]);
//            buttons[i].setTag(i);
//            buttons[i].setOnClickListener(tagClickListener);
//        }
//
//        search.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                StringBuffer buffer = new StringBuffer();
//
//                for (int i = 1; i < mark.length; ++i) {
//                    if (mark[i])
//                        buffer.append(buttons[i].getText()).append(" ");
//                }
//
//                if (buffer.length() > 0) {
//                    buffer.deleteCharAt(buffer.length() - 1);
//                    Intent intent = new Intent(getApplicationContext(), ShowStoryActivity.class);
//                    intent.putExtra("type", ShowStoryActivity.TAGS);
//                    intent.putExtra("tags", buffer.toString());
//                    startActivity(intent);
//                } else {
//                    new android.app.AlertDialog.Builder(MainActivity.this) // 在Fragment中调用对话框的方法
//                            .setMessage("请输入或选择内容")
//                            .setCancelable(true)
//                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .create()
//                            .show();
//                }
//
//                fb.getMenu().getSearchBtn().callOnClick();
//            }
//        });
//
//        //屏幕内的故事显示
//        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
//            @Override
//            public void onMapStatusChangeStart(MapStatus mapStatus) {
//
//            }
//
//            @Override
//            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
//
//            }
//
//            @Override
//            public void onMapStatusChange(MapStatus mapStatus) {
//
//            }
//
//            @Override
//            public void onMapStatusChangeFinish(MapStatus mapStatus) {
//                if (isShowed) {
//                    if (mapStatus.zoom < 19.0f) {
//                        MapStatus.Builder builder = new MapStatus.Builder().zoom(19.0f);
//                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//                    }
//
//                    removeStoryPoint();
//
//                    MapStatus status = mBaiduMap.getMapStatus();
//
//                    MyPosition p1 = new MyPosition();
//                    p1.setLatitude(status.bound.northeast.latitude);
//                    p1.setLongitude(status.bound.northeast.longitude);
//
//                    MyPosition p2 = new MyPosition();
//                    p2.setLatitude(status.bound.southwest.latitude);
//                    p2.setLongitude(status.bound.southwest.longitude);
//
//                    List<MyPosition> storyPointList = null;
//
//                    storyPointList = mStoryModel.getStoryWithBound(p1, p2);
//
//                    showStoryPoint(storyPointList);
//                }
//            }
//        });
//
//        // Button showStory = findViewById(R.id.show_story);
//        fb.getMenu().getStoryBtn().setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isShowed)
//                    removeStoryPoint();
//                isShowed = !isShowed;
//                fb.getMenu().hide();
//            }
//        });
//
//    }
//
//    private void removeStoryPoint() {
//        for (int i = 0; i < storyPointMarkerList.size(); ++i) {
//            storyPointMarkerList.get(i).remove();
//        }
//
//        storyPointMarkerList = new ArrayList<Marker>();
//    }
//
//    private void showStoryPoint(List<MyPosition> list) {
//        storyPointMarkerList = new ArrayList<Marker>();
//
//        for (int i = 0; i < list.size(); ++i) {
//            LatLng position = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
//            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.flag);
//            MarkerOptions option = new MarkerOptions().position(position).icon(bitmap);
//            Marker marker = (Marker) mBaiduMap.addOverlay(option);
//            storyPointMarkerList.add(marker);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        mMapView.onResume();
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        mMapView.onPause();
//        super.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        mBaiduMap.setMyLocationEnabled(false);
//        mMapView.onDestroy();
//        mMapView = null;
//        super.onDestroy();
//    }
//
//    private void doSuggestionSearch(String keyword) {
//        SuggestionSearch suggestionSearch = SuggestionSearch.newInstance();
//
//        suggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
//            @Override
//            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
//                try {
//                    keywordList = suggestionResult.getAllSuggestions();
//                    showKeywordList();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//
//        SuggestionSearchOption option = new SuggestionSearchOption();
//        option.city(mlocation.getCity());
//        option.keyword(keyword);
//
//        suggestionSearch.requestSuggestion(option);
//
//        suggestionSearch.destroy();
//    }
//
//    private void showKeywordList() {
//        try {
//            ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
//            for (int i = 0; i < keywordList.size(); ++i) {
//                HashMap<String, Object> map = new HashMap<String, Object>();
//                map.put("key", keywordList.get(i).getKey());
//                map.put("address", keywordList.get(i).getAddress());
//                items.add(map);
//            }
//
//            adapter = new SimpleAdapter(this, items, R.layout.location_list_item,
//                    new String[]{"key", "address"}, new int[]{R.id.key, R.id.address});
//            locationList.setAdapter(adapter);
//            locationList.setVisibility(View.VISIBLE);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void jumpToPostition(LatLng position) {
//        if (myPostionMarker != null)
//            myPostionMarker.remove();
//        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.dot);
//        MarkerOptions option = new MarkerOptions().position(position).icon(bitmap);
//        MapStatus.Builder builder = new MapStatus.Builder().target(position).zoom(19.0f);
//        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
//        myPostionMarker = (Marker) mBaiduMap.addOverlay(option);
//    }
//
//    private String showChosenTags() {
//        boolean flag = false;
//        StringBuffer buffer = new StringBuffer("你想看一个");
//
//        for (int i = 0; i < mark.length; ++i) {
//            if (mark[i] == true) {
//                flag = true;
//                buffer.append(buttons[i].getText().toString() + "的、");
//            }
//        }
//
//        if (flag) {
//            buffer.deleteCharAt(buffer.length() - 1);
//            buffer.append("故事");
//        } else {
//            buffer = new StringBuffer("你想看一个什么样的故事呢？");
//        }
//
//        return buffer.toString();
//    }
//
//    public class MyLocationListener extends BDAbstractLocationListener {
//        @Override
//        public void onReceiveLocation(BDLocation bdLocation) {
//            if (bdLocation == null && mMapView == null) {
//                mlocation = null;
//                return;
//            }
//
//            mlocation = bdLocation;
//
//            System.out.println(bdLocation.getLatitude()
//                    + " "
//                    + bdLocation.getLongitude());
//
//            MyLocationData locationData = new MyLocationData.Builder().accuracy(bdLocation.getRadius())
//                    .direction(bdLocation.getDirection())
//                    .latitude(bdLocation.getLatitude())
//                    .longitude(bdLocation.getLongitude())
//                    .build();
//
//            mBaiduMap.setMyLocationData(locationData);
//
//            LatLng position = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
//            jumpToPostition(position);
//            mLocationClient.stop();
//        }
//    }
//}
//
