package edu.sysu.citystorymap.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.sysu.citystorymap.R;
import edu.sysu.citystorymap.myposition.MyPosition;
import edu.sysu.citystorymap.navigatebutton.FloatingButton;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.GONE;

public class MapFragment extends Fragment implements MainContract.MapView {
    boolean isTransferBarShowed, isResultShowed;
    private BaiduMap mBaiduMap;
    private MapView mMapView;
    private Marker myPostionMarker = null;
    //转移到指定地点所需变量
    private LinearLayout transferBar;
    private EditText transferLocationInput;
    private Button resultShow;
    private Button transferLocation;
    private InputMethodManager imm;
    private TextView tv;
    private List<SuggestionResult.SuggestionInfo> keywordList;
    private SimpleAdapter adapter;
    private ListView locationList;

    //故事搜索
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

    //悬浮导航
    public FloatingButton fb;

    //屏幕内故事的显示
    private boolean isShowed = false;
    private List<Marker> storyPointMarkerList = new ArrayList<Marker>();

    private MainContract.Presenter mPresenter;

    private View.OnClickListener tagClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int num = (int) v.getTag();
            mark[num] = !mark[num];
            String str = showChosenTags();
            content.setText(str);
            if (mark[num])
                ((Button) v).setTextColor(getResources().getColor(R.color.chosenColor));
            else
                ((Button) v).setTextColor(getResources().getColor(R.color.unChosenColor));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        init(view);
        //获取定位并转移
        mPresenter.locate();
        return view;
    }

    private void init(View view) {
        //获取地图控件引用
        mMapView = view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        mMapView.setLogoPosition(LogoPosition.logoPostionRightTop);

        mPresenter.setMapView(mMapView);
        mPresenter.setMap(mBaiduMap);

        //悬浮导航按钮
        fb = new FloatingButton(getActivity());
        fb.getMenu().getLocateBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.locate();
                fb.getMenu().hide();
            }
        });

        fb.getMenu().getSearchBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSearchBar == false) {
                    fb.getMenu().getSearchBtn().setText("关闭");
                    searchBar.setVisibility(View.VISIBLE);
                    isSearchBar = true;
                } else {
                    fb.getMenu().getSearchBtn().setText("搜索");
                    searchBar.setVisibility(GONE);
                    allTags.setVisibility(GONE);
                    content.setText("");
                    isSearchBar = false;
                    imm.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    for (int i = 1; i < mark.length; ++i) {
                        mark[i] = false;
                        buttons[i].setTextColor(getResources().getColor(R.color.unChosenColor));
                    }
                }

                fb.getMenu().hide();
            }
        });

        fb.getMenu().getTransferBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTransferBarShowed) {
                    transferBar.setVisibility(GONE);
                    locationList.setVisibility(GONE);
                    isResultShowed = false;
                    imm.hideSoftInputFromWindow(transferLocationInput.getWindowToken(), 0);
                } else {
                    transferBar.setVisibility(View.VISIBLE);
                }
                isTransferBarShowed = !isTransferBarShowed;

                fb.getMenu().hide();
            }
        });

        fb.getMenu().getUserBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(getActivity(), UserActivity.class);
                startActivity(intent);*/

                fb.getMenu().hide();
            }
        });

        //点击覆盖物的时间处理：添加故事或删除故事
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final LatLng position = marker.getPosition();
                new AlertDialog.Builder(getActivity())
                        .setItems(new String[]{"添加故事", "查看故事"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                if (which == 0) {
                                    mPresenter.editStoryAtPosition(position);
                                } else if (which == 1) {
                                    mPresenter.showStoryAtPosition(position);
                                }
                            }
                        })
                        .create()
                        .show();
                return false;
            }
        });

        //地图长按事件处理：添加故事
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                final LatLng position = latLng;

                new AlertDialog.Builder(getActivity())
                        .setItems(new String[]{"添加故事"}, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.editStoryAtPosition(position);
                            }
                        })
                        .create()
                        .show();
            }
        });

        //转移到指定地点
        transferBar = view.findViewById(R.id.transfer_bar);
        resultShow = view.findViewById(R.id.resultShow);
        transferLocationInput = view.findViewById(R.id.transfer_location_input);
        transferLocation = view.findViewById(R.id.transfer_location);
        locationList = view.findViewById(R.id.location_list);

        isTransferBarShowed = isResultShowed = false;
        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        tv = view.findViewById(R.id.test);

        //监听输入变化
        transferLocationInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.doSuggestionSearch(s.toString(), new OnGetSuggestionResultListener() {
                    @Override
                    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                        try {
                            keywordList = suggestionResult.getAllSuggestions();
                            showKeywordList();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
        //联想跳转位置点击处理
        locationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    LatLng location = keywordList.get(position).getPt();
                    imm.hideSoftInputFromWindow(transferLocationInput.getWindowToken(), 0);
                    jumpTo(location);
                    locationList.setVisibility(GONE);
                    isResultShowed = false;
                    fb.getMenu().getTransferBtn().callOnClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        //是否显示联想结果列表
        resultShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isResultShowed) {
                    locationList.setVisibility(GONE);
                } else {
                    locationList.setVisibility(View.VISIBLE);
                }
                isResultShowed = !isResultShowed;
            }
        });
        //打开用户信息

        //故事搜索
        show = view.findViewById(R.id.show);
        search = view.findViewById(R.id.search);
        closeAllTags = view.findViewById(R.id.close_all_tags);
        searchBar = view.findViewById(R.id.search_bar);
        choice = view.findViewById(R.id.choice);
        content = view.findViewById(R.id.content);
        allTags = view.findViewById(R.id.all_tags);
        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        isSearchBar = false;

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.text_view, new String[]{"标签", "文本"});
        choice.setAdapter(adapter);

        choice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                choicePosition = position;

                if (position == 0) {
                    content.setFocusable(false);
                    content.setText("你的标签");
                    imm.hideSoftInputFromWindow(content.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                } else {
                    content.setFocusable(true);
                    content.setText("");
                    allTags.setVisibility(GONE);
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
                }
            }
        });

        closeAllTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 1; i < mark.length; ++i) {
                    mark[i] = false;
                    buttons[i].setTextColor(getResources().getColor(R.color.unChosenColor));
                }
                content.setText("选择你的标签");
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
            buttons[i] = view.findViewById(btnIds[i]);
            buttons[i].setTag(i);
            buttons[i].setOnClickListener(tagClickListener);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuffer buffer = new StringBuffer();

                for (int i = 1; i < mark.length; ++i) {
                    if (mark[i])
                        buffer.append(buttons[i].getText()).append(" ");
                }

                if (buffer.length() > 0) {
                    buffer.deleteCharAt(buffer.length() - 1);
                    mPresenter.showStoryWithTags(buffer.toString());
                } else {
                    new android.app.AlertDialog.Builder(getActivity()) // 在Fragment中调用对话框的方法
                            .setMessage("请输入或选择内容")
                            .setCancelable(true)
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .create()
                            .show();
                }

                fb.getMenu().getSearchBtn().callOnClick();
            }
        });

        //屏幕内的故事显示
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                if (isShowed) {
                    if (mapStatus.zoom < 19.0f) {
                        MapStatus.Builder builder = new MapStatus.Builder().zoom(19.0f);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }

                    removeStoryPoint();

                    MapStatus status = mBaiduMap.getMapStatus();

                    MyPosition p1 = new MyPosition();
                    p1.setLatitude(status.bound.northeast.latitude);
                    p1.setLongitude(status.bound.northeast.longitude);

                    MyPosition p2 = new MyPosition();
                    p2.setLatitude(status.bound.southwest.latitude);
                    p2.setLongitude(status.bound.southwest.longitude);

                    mPresenter.getStoryWithBound(p1, p2, new OnGetStoryPointListListener() {
                        @Override
                        public void show(List<MyPosition> list) {
                            storyPointMarkerList = new ArrayList<Marker>();

                            for (int i = 0; i < list.size(); ++i) {
                                LatLng position = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
                                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.flag);
                                MarkerOptions option = new MarkerOptions().position(position).icon(bitmap);
                                Marker marker = (Marker) mBaiduMap.addOverlay(option);
                                storyPointMarkerList.add(marker);
                            }
                        }
                    });
                }
            }
        });

        // Button showStory = findViewById(R.id.show_story);
        fb.getMenu().getStoryBtn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowed)
                    removeStoryPoint();
                isShowed = !isShowed;
                fb.getMenu().hide();
            }
        });
    }

    private void removeStoryPoint() {
        for (int i = 0; i < storyPointMarkerList.size(); ++i) {
            storyPointMarkerList.get(i).remove();
        }

        storyPointMarkerList = new ArrayList<Marker>();
    }

    @Override
    public void jumpTo(LatLng position) {
        if (myPostionMarker != null)
            myPostionMarker.remove();
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.dot);
        MarkerOptions option = new MarkerOptions().position(position).icon(bitmap);
        MapStatus.Builder builder = new MapStatus.Builder().target(position).zoom(19.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        myPostionMarker = (Marker) mBaiduMap.addOverlay(option);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        fb.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        fb.hide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
    }

    private void showKeywordList() {
        try {
            ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < keywordList.size(); ++i) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("key", keywordList.get(i).getKey());
                map.put("address", keywordList.get(i).getAddress());
                items.add(map);
            }

            adapter = new SimpleAdapter(getActivity(), items, R.layout.location_list_item,
                    new String[]{"key", "address"}, new int[]{R.id.key, R.id.address});
            locationList.setAdapter(adapter);
            locationList.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void setPresenter(MainContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public interface OnGetStoryPointListListener {
        void show(List<MyPosition> list);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fb.destory();
    }


}
