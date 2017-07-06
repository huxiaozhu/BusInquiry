package com.hch.businquiry;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.busline.BusLineResult;
import com.baidu.mapapi.search.busline.BusLineSearch;
import com.baidu.mapapi.search.busline.BusLineSearchOption;
import com.baidu.mapapi.search.busline.OnGetBusLineSearchResultListener;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteLine;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.hch.businquiry.adapter.GroupAdapter;
import com.hch.businquiry.adapter.RouteLineAdapter;
import com.hch.businquiry.adapter.listadapter.CommonAdapter;
import com.hch.businquiry.adapter.listadapter.viewHolder.BaseViewHolder;
import com.hch.businquiry.bean.DimBean;
import com.hch.businquiry.dbutils.DatabaseUtil;
import com.hch.businquiry.overlayutil.BikingRouteOverlay;
import com.hch.businquiry.overlayutil.BusLineOverlay;
import com.hch.businquiry.overlayutil.DrivingRouteOverlay;
import com.hch.businquiry.overlayutil.MassTransitRouteOverlay;
import com.hch.businquiry.overlayutil.OverlayManager;
import com.hch.businquiry.overlayutil.PoiOverlay;
import com.hch.businquiry.overlayutil.TransitRouteOverlay;
import com.hch.businquiry.overlayutil.WalkingRouteOverlay;
import com.hch.businquiry.provider.Provider;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.SENSOR_SERVICE;

public class FragmentCategory extends Fragment implements SensorEventListener, OnGetPoiSearchResultListener, OnGetSuggestionResultListener, BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener, OnGetBusLineSearchResultListener, View.OnClickListener {
    private static final int DIM_SEARCH = 1;
    private static final int ROUTE_SEARCH = 2;
    private static final int PATH_SEARCH = 3;
    private static final int STATION_SEARCH = 4;
    //用来区分当前mapview处于哪一状态   1：收索
    //2：换乘 3：线路  4站点
    private int type = 1;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;

    RouteLine route = null;
    MassTransitRouteLine massroute = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null; // 泡泡view
    // 搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    WalkingRouteResult nowResultwalk = null;
    BikingRouteResult nowResultbike = null;
    TransitRouteResult nowResultransit = null;
    DrivingRouteResult nowResultdrive = null;
    //公交查询返回的结果
    MassTransitRouteResult nowResultmass = null;
    int nowSearchType = -1; // 当前进行的检索，供判断浏览节点时结果使用。
    //公交列表是否显示 默认不显示
    boolean hasShownDialogue = false;

    private SuggestionSearch mSuggestionSearch = null;
    BitmapDescriptor mCurrentMarker;
    private static final int accuracyCircleFillColor = 0xAAFFFF88;
    private static final int accuracyCircleStrokeColor = 0xAA00FF00;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    //路线查询的起始位置与终点位置
    private String start_location = "黄河科技学院";
    private String end_location = "郑州火车站";
    int nodeIndex = -2; // 节点索引,供浏览节点时使用
    //private Button mBtnPre = null; // 上一个节点
    //private Button mBtnNext = null; // 下一个节点
    private BusLineResult bRoute = null; // 保存驾车/步行路线数据的变量，供浏览节点时使用


    //公交Id集合
    private List<String> busLineIDList = null;
    private int busLineIndex = 0;
    // 搜索相关
    private PoiSearch mPoiSearch = null;// 搜索模块，也可去掉地图模块独立使用
    private BusLineSearch mBusLineSearch = null;
    BusLineOverlay overlay; // 公交路线绘制对象
    // 线路查询值
    private String pathNum = "101";

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    //搜索关键字窗口
    private AutoCompleteTextView keyWorldsView = null;
    // 公交路线查询
    private RelativeLayout bus_route_rl;
    // 站点查询
    private LinearLayout search_dim_ll;
    // 站点起始位置
    private AutoCompleteTextView start_location_ac;
    // 站点终点位置
    private AutoCompleteTextView end_location_ac;
    //公交线路查询
    private LinearLayout search_path_ll;
    //公交线路值
    private AutoCompleteTextView search_path_ac;
    //公交线路站点查询
    private LinearLayout search_path_station_ll;

    // 公交线路站点查询，上一站、下一站
    private Button path_station_pre;
    private Button path_station_next;
    //公交上下行
    private Button nextline;
    //公交站点查询
    private LinearLayout search_station_ll;
    //公交区域值
    private AutoCompleteTextView search_station_ac;

    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    // 当前所在的城市
    private String city = "郑州市";

    private List<String> suggest;
    private List<String> history;
    // LatLng地理坐标基本数据结构
    private List<LatLng> latLngs;
    private ArrayAdapter<String> sugAdapter = null;

    private int loadIndex = 0;

    //	LatLng center = new LatLng(39.92235, 116.380338);
    int radius = 1000;
    LatLng southwest = new LatLng(39.92235, 116.380338);
    LatLng northeast = new LatLng(39.947246, 116.414977);
    LatLngBounds searchbound = new LatLngBounds.Builder().include(southwest).include(northeast).build();
    private DatabaseUtil mDBUtil;
    private DimBean dim;
    private List<DimBean> dimBeanList;
    private int count = 0;
    private PopupWindow popupWindow;

    private LinearLayout mBus, mSearchs;
    private PopupWindow popupWindows;
    private ListView mListView;
    private CommonAdapter<String> adapter;
    private TextView mTitles, mHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBUtil = DatabaseUtil.instance(getContext());
        dimBeanList = new ArrayList<>();
        dimBeanList = mDBUtil.queryAll();
        count = dimBeanList.size();
        // 初始化搜索模块，注册搜索事件监听
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);

        mBusLineSearch = BusLineSearch.newInstance();
        mBusLineSearch.setOnGetBusLineSearchResultListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
//        初始化视图
        initView(view);
        //初始化数据
        setData();
        //设置监听
        setLisiniter();
        return view;
    }

    private void setData() {
        mTitles.setText("搜索");
        mHistory.setVisibility(View.VISIBLE);
        setMapView();
        sugAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        keyWorldsView.setThreshold(1);
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }

                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(city));
            }
        });

        search_station_ac.setAdapter(sugAdapter);
        search_station_ac.setThreshold(1);
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        search_station_ac.addTextChangedListener(myTextWatcher);

        start_location_ac.setAdapter(sugAdapter);
        start_location_ac.setThreshold(1);
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        start_location_ac.addTextChangedListener(myTextWatcher);
        end_location_ac.setAdapter(sugAdapter);
        end_location_ac.setThreshold(1);
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        end_location_ac.addTextChangedListener(myTextWatcher);

        search_path_ac.setAdapter(sugAdapter);
        search_path_ac.setThreshold(1);
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        search_path_ac.addTextChangedListener(myTextWatcher);
        changeModel(DIM_SEARCH);
    }

    /**
     * 初始化与地图相关的数据
     */
    private void setMapView() {
        mBaiduMap = mMapView.getMap();
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);// 位置，一定要设置，否则后面得不到地址
        option.setScanSpan(1000);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 高精度
        mLocClient.setLocOption(option);
        mLocClient.start();
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        busLineIDList = new ArrayList<>();
        overlay = new BusLineOverlay(mBaiduMap);
    }

    /**
     * 设置监听
     */
    private void setLisiniter() {
        mBus.setOnClickListener(this);
        mSearchs.setOnClickListener(this);
        mHistory.setOnClickListener(this);
        path_station_pre.setOnClickListener(this);
        path_station_next.setOnClickListener(this);
        nextline.setOnClickListener(this);
        // 地图点击事件处理
        mBaiduMap.setOnMapClickListener(this);
        mBaiduMap.setOnMarkerClickListener(overlay);
    }

    /**
     * 初始化视图
     *
     * @param view
     */
    private void initView(View view) {
        mTitles = (TextView) view.findViewById(R.id.titles_title);
        mHistory = (TextView) view.findViewById(R.id.titles_history);
        // 地图初始化
        mMapView = (MapView) view.findViewById(R.id.bmapView);
        search_dim_ll = (LinearLayout) view.findViewById(R.id.search_dim_ll);
        keyWorldsView = (AutoCompleteTextView) view.findViewById(R.id.searchkey);
        mBus = (LinearLayout) view.findViewById(R.id.search_bus);
        mSearchs = (LinearLayout) view.findViewById(R.id.search_condition);
        bus_route_rl = (RelativeLayout) view.findViewById(R.id.bus_route_rl);
        start_location_ac = (AutoCompleteTextView) view.findViewById(R.id.start_location_ac);
        end_location_ac = (AutoCompleteTextView) view.findViewById(R.id.end_location_ac);
        search_path_ll = (LinearLayout) view.findViewById(R.id.search_path_ll);
        search_path_station_ll = (LinearLayout) view.findViewById(R.id.search_path_station_ll);
        search_path_ac = (AutoCompleteTextView) view.findViewById(R.id.search_path_ac);
        path_station_pre = (Button) view.findViewById(R.id.path_station_pre);
        path_station_next = (Button) view.findViewById(R.id.path_station_next);
        nextline = (Button) view.findViewById(R.id.nextline);
        search_station_ll = (LinearLayout) view.findViewById(R.id.search_station_ll);
        search_station_ac = (AutoCompleteTextView) view.findViewById(R.id.search_station_ac);
    }

    TextWatcher myTextWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1,
                                      int arg2, int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                  int arg3) {
            if (cs.length() <= 0) {
                return;
            }

            /**
             * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
             */
            mSuggestionSearch
                    .requestSuggestion((new SuggestionSearchOption())
                            .keyword(cs.toString()).city(city));
        }
    };

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_bus:
                /**
                 * 选择出行方式
                 */
                showPopupWindows();
                setListViewLinstener();
                break;
            case R.id.search_condition:
                /**
                 * 搜索
                 */
                setSerachEvent(v);
                break;
            case R.id.path_station_pre:
//                公交站点查询上一站
                nodeClick(v);
                break;
            case R.id.path_station_next:
//                公交站点查询下一站
                nodeClick(v);
                break;
            case R.id.nextline:
                searchNextBusline();
                break;
            case R.id.titles_history:
                showHistory();
                break;
        }
    }

    /**
     * 显示搜索历史
     */
    private void showHistory() {
        switch (type) {
            case 1:
                showPopupWindow(keyWorldsView);
                break;
            case 2:
                Toast.makeText(getContext(), "没有换乘历史消息", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getContext(), "没有线路历史消息", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(getContext(), "没有站点历史消息", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * 设置搜索点击事件
     *
     * @param v
     */
    private void setSerachEvent(View v) {
        switch (type) {
            case 1:
                //搜索查询
                searchButtonProcess(v);
                break;
            case 2:
                //换乘查询
                start_location = start_location_ac.getText().toString();
                end_location = end_location_ac.getText().toString();
                searchRouteProcess();
                break;
            case 3:
                //线路查询
                pathNum = search_path_ac.getText().toString();
                searchPath();
                break;
            case 4:
//                站点查询(输入后字节得到结果)
                searchNearbyProcess();
                break;
        }
    }

    /**
     * 使用popupwindow实现下拉菜单
     */
    private void showPopupWindows() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popuplayout, null);
        mListView = (ListView) view.findViewById(R.id.popup_listview);
        List<String> list = new ArrayList<>();
        list.add("搜索");
        list.add("换乘");
        list.add("线路");
        list.add("站点");
        adapter = new CommonAdapter<String>(getContext(), list, R.layout.list_bus) {

            @Override
            protected void setViewData(String item, BaseViewHolder holder) {
                holder.setText(R.id.bus_item, item);
            }

            @Override
            protected void setListener(int i, BaseViewHolder holder) {

            }

            @Override
            protected void onClick(int position, View view, BaseViewHolder holder) {

            }
        };
        mListView.setAdapter(adapter);
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int popWith = manager.getDefaultDisplay().getWidth() / 2;
        popupWindows = new PopupWindow(view, popWith - 30, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindows.showAsDropDown(mSearchs, popWith + 30, 0);
    }

    /**
     * 设置出行方式ListView的点击监听事件
     */
    private void setListViewLinstener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                popupWindows.dismiss();
                mBaiduMap.clear();//清除图层
                switch (position) {
                    case 0:
//                        收索
                        type = DIM_SEARCH;
                        changeModel(DIM_SEARCH);
                        break;
                    case 1:
//                        换乘
                        type = ROUTE_SEARCH;
                        changeModel(ROUTE_SEARCH);
                        break;
                    case 2:
//                        线路
                        type = PATH_SEARCH;
                        changeModel(PATH_SEARCH);
                        break;
                    case 3:
//                        站点
                        type = STATION_SEARCH;
                        changeModel(STATION_SEARCH);
                        //自动检索附近的公交站
                        searchNearbyProcess();
                        break;
                }
            }
        });
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            Provider.center = new LatLng(mCurrentLat, mCurrentLon);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                city = location.getCity();
//				start_location_ac.setText(location.get,false);
//				keyWorldsView.setText(city);
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {
        isFirstLoc = true;
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onStop() {
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        super.onDestroy();
    }

    /**
     * 响应城市内搜索按钮点击事件
     *
     * @param v
     */
    public void searchButtonProcess(View v) {
        String keystr = keyWorldsView.getText().toString();
        dimBeanList = mDBUtil.queryAll();
        count = dimBeanList.size();
        boolean isAdd = true;
        for (DimBean dim : dimBeanList) {
            if (keystr != null && keystr.equals(dim.getKey())) {
                isAdd = false;
                break;
            }
        }
        if (isAdd&&keystr!=null) {
            dim = new DimBean();
            dim.setKey(keystr);
            dim.setNum(count + 1);
            mDBUtil.Insert(dim);
        }
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(city).keyword(keystr).pageNum(loadIndex));
    }

    /**
     * 响应周边搜索按钮点击事件
     * 2
     */
    public void searchNearbyProcess() {
//		keyWorldsView.getText().toString()
        PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption().keyword("公交站")
                .sortType(PoiSortType.distance_from_near_to_far).location(Provider.center)
                .radius(radius).pageNum(loadIndex);
        mPoiSearch.searchNearby(nearbySearchOption);
    }


    /**
     * 响应区域搜索按钮点击事件
     * 3
     *
     * @param v
     */
    public void searchBoundProcess(View v) {
        mPoiSearch.searchInBound(new PoiBoundSearchOption().bound(searchbound)
                .keyword(keyWorldsView.getText().toString()));

    }

    /**
     * 获取POI搜索结果，包括searchInCity，searchNearby，searchInBound返回的搜索结果
     *
     * @param result
     */
    public void onGetPoiResult(PoiResult result) {
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (type == 3) {
            // 遍历所有poi，找到类型为公交线路的poi
            busLineIDList.clear();
            for (PoiInfo poi : result.getAllPoi()) {
                if (poi.type == PoiInfo.POITYPE.BUS_LINE
                        || poi.type == PoiInfo.POITYPE.SUBWAY_LINE) {
                    busLineIDList.add(poi.uid);
                }
            }
            searchNextBusline();
            bRoute = null;
        } else {
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                mBaiduMap.clear();
                PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result);
                overlay.addToMap();
                overlay.zoomToSpan();
                switch (type) {
                    case 2:
                        showNearbyArea(Provider.center, radius);
                        break;
                    case 3:
                        showBound(searchbound);
                        break;
                    default:
                        break;
                }

                return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

                // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                String strInfo = "在";
                for (CityInfo cityInfo : result.getSuggestCityList()) {
                    strInfo += cityInfo.city;
                    strInfo += ",";
                }
                strInfo += "找到结果";
                Toast.makeText(getActivity(), strInfo, Toast.LENGTH_LONG)
                        .show();
            }
        }

    }

    /**
     * 获取POI详情搜索结果，得到searchPoiDetail返回的搜索结
     * @param result
     */
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {

            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(getActivity(), result.getName() + ": " + result.getAddress(), Toast.LENGTH_SHORT)
                    .show();
            //创建InfoWindow展示的view
//            Button button = new Button(getContext());
//            button.setBackgroundResource(R.drawable.popup);
////定义用于显示该InfoWindow的坐标点
//            LatLng pt = new LatLng(result.getLocation().latitude, result.getLocation().longitude);
////创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
//            InfoWindow mInfoWindow = new InfoWindow(button, pt, -47);
////显示InfoWindow
//            mBaiduMap.showInfoWindow(mInfoWindow);
        }
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    /**
     * 获取在线建议搜索结果，得到requestSuggestion返回的搜索结果
     * @param res
     */
    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        suggest = new ArrayList<>();
        latLngs = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info :
                res.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
                latLngs.add(info.pt);
            }
        }
        sugAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, suggest);
        if (type == DIM_SEARCH) {
            keyWorldsView.setAdapter(sugAdapter);
            sugAdapter.notifyDataSetChanged();
            keyWorldsView.setOnItemClickListener(autoAVOnItemClickListener);
        } else if (type == ROUTE_SEARCH) {
            start_location_ac.setAdapter(sugAdapter);
            sugAdapter.notifyDataSetChanged();
            start_location_ac.setOnItemClickListener(autoAVOnItemClickListener);

            end_location_ac.setAdapter(sugAdapter);
            sugAdapter.notifyDataSetChanged();
            end_location_ac.setOnItemClickListener(autoAVOnItemClickListener);

        } else if (type == PATH_SEARCH) {
            search_path_ac.setAdapter(sugAdapter);
            sugAdapter.notifyDataSetChanged();
            search_path_ac.setOnItemClickListener(autoAVOnItemClickListener);
        } else if (type == STATION_SEARCH) {
            search_station_ac.setAdapter(sugAdapter);
            sugAdapter.notifyDataSetChanged();
            search_station_ac.setOnItemClickListener(autoAVOnItemClickListener);

        }

    }
    /**
     * aoutTextview点击Item监听
     */
    AdapterView.OnItemClickListener autoAVOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            mPoiSearch.searchInCity((new PoiCitySearchOption())
//                    .city(city).keyword(suggest.get(position)).pageNum(loadIndex));
            //此处自动检索站台信息
            if (type == STATION_SEARCH) {
                Provider.center = latLngs.get(position);
            }
        }
    };

    @Override
    public void onGetBusLineResult(BusLineResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果",
                    Toast.LENGTH_LONG).show();
            return;
        }
        mBaiduMap.clear();
        bRoute = result;
        nodeIndex = -1;
        overlay.removeFromMap();
        overlay.setData(result);
        overlay.addToMap();
        overlay.zoomToSpan();
        path_station_pre.setVisibility(View.VISIBLE);
        path_station_next.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), result.getBusLineName(),
                Toast.LENGTH_SHORT).show();
    }


    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            PoiInfo poi = getPoiResult().getAllPoi().get(index);
            // if (poi.hasCaterDetails) {
            mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
                    .poiUid(poi.uid));
            // }
            return true;
        }
    }

    /**
     * 对周边检索的范围进行绘制
     *
     * @param center
     * @param radius
     */
    public void showNearbyArea(LatLng center, int radius) {
        BitmapDescriptor centerBitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        MarkerOptions ooMarker = new MarkerOptions().position(center).icon(centerBitmap);
        mBaiduMap.addOverlay(ooMarker);
        OverlayOptions ooCircle = new CircleOptions().fillColor(0x55808080)
                .center(center).stroke(new Stroke(5, 0xFFFF00FF))
                .radius(radius);
        mBaiduMap.addOverlay(ooCircle);
    }

    /**
     * 对区域检索的范围进行绘制
     *
     * @param bounds
     */
    public void showBound(LatLngBounds bounds) {
        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.drawable.ground_overlay);

        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds).image(bdGround).transparency(0.1f);
        mBaiduMap.addOverlay(ooGround);
        MapStatusUpdate u = MapStatusUpdateFactory
                .newLatLng(bounds.getCenter());
        mBaiduMap.setMapStatus(u);

        bdGround.recycle();
    }

    /**
     * 变更显示查询条件
     *
     * @param type
     */
    private void changeModel(int type) {
        switch (type) {
            case DIM_SEARCH:
                search_dim_ll.setVisibility(View.VISIBLE);
                bus_route_rl.setVisibility(View.GONE);
                search_path_ll.setVisibility(View.GONE);
                search_station_ll.setVisibility(View.GONE);
                search_path_station_ll.setVisibility(View.GONE);
                break;

            case ROUTE_SEARCH:
                search_dim_ll.setVisibility(View.GONE);
                bus_route_rl.setVisibility(View.VISIBLE);
                search_path_ll.setVisibility(View.GONE);
                search_station_ll.setVisibility(View.GONE);
                search_path_station_ll.setVisibility(View.GONE);
                break;
            case PATH_SEARCH:
                search_dim_ll.setVisibility(View.GONE);
                bus_route_rl.setVisibility(View.GONE);
                search_path_ll.setVisibility(View.VISIBLE);
                search_station_ll.setVisibility(View.GONE);
                search_path_station_ll.setVisibility(View.VISIBLE);
                break;
            case STATION_SEARCH:
                search_dim_ll.setVisibility(View.GONE);
                bus_route_rl.setVisibility(View.GONE);
                search_path_ll.setVisibility(View.GONE);
                search_station_ll.setVisibility(View.VISIBLE);
                search_path_station_ll.setVisibility(View.GONE);
                break;
        }
    }


    /**
     * 发起路线规划搜索示例
     */
    public void searchRouteProcess() {
        if (start_location == null || end_location == null || "".equals(start_location) || "".equals(end_location)) {
            Toast.makeText(getActivity(), "请输入起始位置与终点位置", Toast.LENGTH_SHORT).show();
        }
        // 重置浏览节点的路线数据
        route = null;

        mBaiduMap.clear();
        // 处理搜索按钮响应
        // 设置起终点信息，对于tranist search 来说，城市名无意义
        PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start_location);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end_location);
        mSearch.transitSearch((new TransitRoutePlanOption())
                .from(stNode).city(city).to(enNode));
        nowSearchType = 2;
    }

    /**
     * 获取步行路线规划
     * @param result
     */
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        Log.d("result", "WalkingRouteResult--" + result.toString());
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            //nodeIndex = -1;
            //mBtnPre.setVisibility(View.VISIBLE);
            //mBtnNext.setVisibility(View.VISIBLE);

            if (result.getRouteLines().size() > 1) {
                nowResultwalk = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(getActivity(),
                            result.getRouteLines(),
                            RouteLineAdapter.Type.WALKING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    /**
                     * 点击公交item
                     */
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultwalk.getRouteLines().get(position);
                            //
                            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultwalk.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                route = result.getRouteLines().get(0);
                WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        Log.d("result", "onGetTransitRouteResult--" + result.toString());
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            //mBtnPre.setVisibility(View.VISIBLE);
            //mBtnNext.setVisibility(View.VISIBLE);


            if (result.getRouteLines().size() > 1) {
                nowResultransit = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(getActivity(),
                            result.getRouteLines(),
                            RouteLineAdapter.Type.TRANSIT_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {

                            route = nowResultransit.getRouteLines().get(position);
                            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultransit.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                // 直接显示
                route = result.getRouteLines().get(0);
                TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaiduMap);
                mBaiduMap.setOnMarkerClickListener(overlay);
                routeOverlay = overlay;
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();

            } else {
                Log.d("route result", "结果数<0");
                return;
            }


        }
    }

    /**
     * 公交查询的返回结果
     * @param result
     */
    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        Log.d("result", "MassTransitRouteResult--" + result.toString());
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点模糊，获取建议列表
            result.getSuggestAddrInfo();
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nowResultmass = result;
            nodeIndex = -1;
            //mBtnPre.setVisibility(View.VISIBLE);
            //mBtnNext.setVisibility(View.VISIBLE);

            if (!hasShownDialogue) {
                // 列表选择
                MyTransitDlg myTransitDlg = new MyTransitDlg(getActivity(),
                        result.getRouteLines(),
                        RouteLineAdapter.Type.MASS_TRANSIT_ROUTE);
                nowResultmass = result;
                myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        hasShownDialogue = false;
                    }
                });
                myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                    public void onItemClick(int position) {

                        MyMassTransitRouteOverlay overlay = new MyMassTransitRouteOverlay(mBaiduMap);
                        mBaiduMap.setOnMarkerClickListener(overlay);
                        routeOverlay = overlay;
                        massroute = nowResultmass.getRouteLines().get(position);
                        overlay.setData(nowResultmass.getRouteLines().get(position));

                        MassTransitRouteLine line = nowResultmass.getRouteLines().get(position);
                        overlay.setData(line);
                        if (nowResultmass.getOrigin().getCityId() == nowResultmass.getDestination().getCityId()) {
                            // 同城
                            overlay.setSameCity(true);
                        } else {
                            // 跨城
                            overlay.setSameCity(false);

                        }
                        mBaiduMap.clear();
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    }

                });
                myTransitDlg.show();
                hasShownDialogue = true;
            }
        }
    }

    /**
     *公交查询驾车的结果
     * @param result
     */
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        Log.d("result", "DrivingRouteResult--" + result.toString());
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            if (result.getRouteLines().size() > 1) {
                nowResultdrive = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(getActivity(),
                            result.getRouteLines(),
                            RouteLineAdapter.Type.DRIVING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultdrive.getRouteLines().get(position);
                            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultdrive.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
                routeOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                //mBtnPre.setVisibility(View.VISIBLE);
                //mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
        Log.d("result", "IndoorRouteResult--" + indoorRouteResult.toString());
    }

    /**
     * 骑车
     * @param result
     */
    @Override
    public void onGetBikingRouteResult(BikingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getActivity(), "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        Log.d("result", "BikingRouteResult--" + result.toString());
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            //mBtnPre.setVisibility(View.VISIBLE);
            //mBtnNext.setVisibility(View.VISIBLE);

            if (result.getRouteLines().size() > 1) {
                nowResultbike = result;
                if (!hasShownDialogue) {
                    MyTransitDlg myTransitDlg = new MyTransitDlg(getActivity(),
                            result.getRouteLines(),
                            RouteLineAdapter.Type.DRIVING_ROUTE);
                    myTransitDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            hasShownDialogue = false;
                        }
                    });
                    myTransitDlg.setOnItemInDlgClickLinster(new OnItemInDlgClickListener() {
                        public void onItemClick(int position) {
                            route = nowResultbike.getRouteLines().get(position);
                            BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaiduMap);
                            mBaiduMap.setOnMarkerClickListener(overlay);
                            routeOverlay = overlay;
                            overlay.setData(nowResultbike.getRouteLines().get(position));
                            overlay.addToMap();
                            overlay.zoomToSpan();
                        }

                    });
                    myTransitDlg.show();
                    hasShownDialogue = true;
                }
            } else if (result.getRouteLines().size() == 1) {
                route = result.getRouteLines().get(0);
                BikingRouteOverlay overlay = new MyBikingRouteOverlay(mBaiduMap);
                routeOverlay = overlay;
                mBaiduMap.setOnMarkerClickListener(overlay);
                overlay.setData(result.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
                //mBtnPre.setVisibility(View.VISIBLE);
                //mBtnNext.setVisibility(View.VISIBLE);
            } else {
                Log.d("route result", "结果数<0");
                return;
            }

        }
    }

    /**
     * 定制RouteOverly（公交）
     */
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    /**
     * 绘制公交步行
     */
    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }
    /**
     * 绘制公交驾车
     */
    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyBikingRouteOverlay extends BikingRouteOverlay {
        public MyBikingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }

    private class MyMassTransitRouteOverlay extends MassTransitRouteOverlay {
        public MyMassTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }


    }

    /**
     * map点击事件
     *
     * @param point
     */
    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
        return false;
    }

    // 响应DLg中的List item 点击
    interface OnItemInDlgClickListener {
        public void onItemClick(int position);
    }

    // 供路线选择的Dialog
    class MyTransitDlg extends Dialog {

        private List<? extends RouteLine> mtransitRouteLines;
        private ListView transitRouteList;
        private RouteLineAdapter mTransitAdapter;

        OnItemInDlgClickListener onItemInDlgClickListener;

        public MyTransitDlg(Context context, int theme) {
            super(context, theme);
        }

        public MyTransitDlg(Context context, List<? extends RouteLine> transitRouteLines, RouteLineAdapter.Type
                type) {
            this(context, 0);
            mtransitRouteLines = transitRouteLines;
            mTransitAdapter = new RouteLineAdapter(context, mtransitRouteLines, type);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        @Override
        public void setOnDismissListener(OnDismissListener listener) {
            super.setOnDismissListener(listener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_transit_dialog);

            transitRouteList = (ListView) findViewById(R.id.transitList);
            transitRouteList.setAdapter(mTransitAdapter);

            transitRouteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onItemInDlgClickListener.onItemClick(position);
                    //mBtnPre.setVisibility(View.VISIBLE);
                    //mBtnNext.setVisibility(View.VISIBLE);
                    dismiss();
                    hasShownDialogue = false;
                }
            });
        }

        public void setOnItemInDlgClickLinster(OnItemInDlgClickListener itemListener) {
            onItemInDlgClickListener = itemListener;
        }

    }

    /**
     * 发起检索
     */
    public void searchPath() {
        if (pathNum == null || "".equals(pathNum)) {
            Toast.makeText(getActivity(), "请输入公交路线", Toast.LENGTH_SHORT).show();
        }
        busLineIDList.clear();
        busLineIndex = 0;
        path_station_pre.setVisibility(View.INVISIBLE);
        path_station_next.setVisibility(View.INVISIBLE);


        // 发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
        mPoiSearch.searchInCity((new PoiCitySearchOption()).city(
                city)
                .keyword(pathNum));
    }

    public void searchNextBusline() {
        if (busLineIndex >= busLineIDList.size()) {
            busLineIndex = 0;
        }
        if (busLineIndex >= 0 && busLineIndex < busLineIDList.size()
                && busLineIDList.size() > 0) {
            mBusLineSearch.searchBusLine((new BusLineSearchOption()
                    .city(city).uid(busLineIDList.get(busLineIndex))));

            busLineIndex++;
        }

    }

    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {

        if (nodeIndex < -1 || bRoute == null
                || nodeIndex >= bRoute.getStations().size()) {
            return;
        }
        TextView popupText = new TextView(getActivity());
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xff000000);
        // 上一个节点
        if (path_station_pre.equals(v) && nodeIndex > 0) {
            // 索引减
            nodeIndex--;
        }
        // 下一个节点
        if (path_station_next.equals(v) && nodeIndex < (bRoute.getStations().size() - 1)) {
            // 索引加
            nodeIndex++;
        }
        if (nodeIndex >= 0) {
            // 移动到指定索引的坐标
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(bRoute
                    .getStations().get(nodeIndex).getLocation()));
            // 弹出泡泡
            popupText.setText(bRoute.getStations().get(nodeIndex).getTitle());
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, bRoute.getStations()
                    .get(nodeIndex).getLocation(), 10));
        }
    }

    /**
     * 收索历史
     * @param parent
     */
    private void showPopupWindow(View parent) {
        //从数据库中拿出收索历史
        history = new ArrayList<>();
        dimBeanList = mDBUtil.queryAll();
        for (DimBean dim : dimBeanList) {
            history.add(dim.getKey());
        }
//        如历史为空返回
        if (history.size() == 0) {
            Toast.makeText(getContext(), "收索历史为空", Toast.LENGTH_SHORT).show();
            return;
        }
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.group_list, null);
        ListView historyList = (ListView) view.findViewById(R.id.history_list);
        View view1 = LayoutInflater.from(getContext()).inflate(R.layout.item_head, null);
        historyList.addHeaderView(view1);
        GroupAdapter groupAdapter = new GroupAdapter(getActivity(), history);
        historyList.setAdapter(groupAdapter);
        WindowManager manager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int popWith = manager.getDefaultDisplay().getWidth() / 2;
        popupWindow = new PopupWindow(view, popWith, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(parent, 40, 10);
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,
                                    int position, long id) {
                if (position != 0) {
                    keyWorldsView.setText(history.get(position-1));
                    /**
                     * 定位到输入地理信息处
                     */
                    mSuggestionSearch
                            .requestSuggestion((new SuggestionSearchOption())
                                    .keyword(history.get(position-1)).city(city));
                }
                if (popupWindow != null)
                    popupWindow.dismiss();
            }
        });
    }
}
