package com.yihai.caotang.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.GroundOverlayOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.orhanobut.logger.Logger;
import com.yihai.caotang.AppContext;
import com.yihai.caotang.AppController;
import com.yihai.caotang.R;
import com.yihai.caotang.adapter.AbstractListAdapter;
import com.yihai.caotang.adapter.OnItemClickLisener;
import com.yihai.caotang.adapter.landscape.SimulatorListAdapter;
import com.yihai.caotang.data.Constants;
import com.yihai.caotang.data.landscape.LandScape;
import com.yihai.caotang.data.landscape.LandScapeManager;
import com.yihai.caotang.data.session.SessionManager;
import com.yihai.caotang.event.EntryLandscapeEvent;
import com.yihai.caotang.event.MapEvent;
import com.yihai.caotang.service.SoundTrackService;
import com.yihai.caotang.ui.base.BaseActivity;
import com.yihai.caotang.ui.dialog.ConfirmDialog;
import com.yihai.caotang.ui.landscape.CoinCollectionActivity;
import com.yihai.caotang.ui.manage.ConfigUserActivity;
import com.yihai.caotang.ui.repository.RepositoryListActivity;
import com.yihai.caotang.ui.task.TaskListActivity;
import com.yihai.caotang.utils.CoordsUtils;
import com.yihai.caotang.utils.PermissionUtils;
import com.yihai.caotang.utils.SysInfoUtils;
import com.yihai.caotang.utils.ToastUtils;
import com.yihai.caotang.widgets.NoGlowRecyclerView;
import com.yihai.caotang.widgets.SoundTrackMenuView;
import com.yihai.caotang.widgets.ToggleMenuView;
import com.yihai.caotang.widgets.guideview.Guide;
import com.yihai.caotang.widgets.guideview.GuideBuilder;
import com.yihai.caotang.widgets.guideview.components.SimpleComponent;
import com.yihai.caotang.widgets.overlay.WalkRouteOverlay;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import xiaofei.library.hermeseventbus.HermesEventBus;

import static com.amap.api.services.route.RouteSearch.WALK_DEFAULT;

public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getName();

    @Bind(R.id.toggle_menu)
    ToggleMenuView toggleMenu;

    @Bind(R.id.soundtrack_menu)
    SoundTrackMenuView soundTrackMenu;

    @Bind(R.id.mapview)
    MapView mapView;

    @Bind(R.id.listuv)
    public NoGlowRecyclerView listuv;

    AMap mMap;
    RouteSearch mRouteSearch;
    Marker mUserLocMarker;
    Map<Integer, Marker> landscapeMarkers;
    WalkRouteOverlay mWalkRouteOverlay;

    private AbstractListAdapter mAdapter;
    private SoundTrackBroadcast mSoundTrackBroadcast;
    private PermissionUtils mPermUtil;

    public static Intent newIntent() {
        Intent intent = new Intent(AppContext.getInstance(),
                MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        landscapeMarkers = new HashMap<>();

        initPermission();
        initMap(savedInstanceState);
        initRoute();
        initMenu();
        initTestMenu();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        mSoundTrackBroadcast = new SoundTrackBroadcast();
        registerReceiver(mSoundTrackBroadcast, new IntentFilter(SoundTrackService.PLAYSTATE_COMPLETE));
        updateLandscapeMarkers();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWalkRouteOverlay.removeFromMap();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        unregisterReceiver(mSoundTrackBroadcast);
        mSoundTrackBroadcast = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.d("result triggered");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMapEvent(MapEvent event) {
        mUserLocMarker.setPosition(SessionManager.getInstance().getLocationLatLng());
    }


    @Override
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEntryLandscapeEvevt(final EntryLandscapeEvent event) {
        if (event.getType() == EntryLandscapeEvent.ENTRY) {
            if (!AppController.getInstance().isNearGate(event.getLandScape())) {
                ConfirmDialog dialog = new ConfirmDialog(MainActivity.this)
                        .setText("已靠近" + event.getLandScape().getName() + "，是否进入浏览模式")
                        .setCancelButton("放弃进入", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
//                                AppController.getInstance().quitLandscape(event.getLandScape());
                            }
                        })
                        .setConfirmButton("开始浏览", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                AppController.getInstance().enterLandscape(event.getLandScape());
                                updateLandscapeMarkers();
                                mUserLocMarker.setPosition(SessionManager.getInstance().getLocationLatLng());
                            }
                        });
                cleanDialog();
                dialog.show();
                confirmDialogs.add(dialog);
            } else {
                ConfirmDialog dialog = new ConfirmDialog(MainActivity.this)
                        .setText("已靠近大门，是否结束游览？")
                        .setConfirmButton("继续使用", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelButton("结束游览", new ConfirmDialog.OnDialogBtnClickListener() {
                            @Override
                            public void onClick(View v, Dialog dialog) {
                                dialog.dismiss();
                                if (!AppController.DEBUG) {
                                    postNotKeepLend();
                                }
                            }
                        });
                cleanDialog();
                dialog.show();
                confirmDialogs.add(dialog);
            }
        } else if (event.getType() == EntryLandscapeEvent.LEAVE) {
            /**
             * unity退出返回后接收
             * 1.退出景点
             * 2.停止音频播放
             */
            if (AppController.DEBUG) {
                ToastUtils.show(MainActivity.this, "onEntryLandscapeEvevt:quite" + event.getLandScape().getName());
            }
            AppContext.getInstance().stopSoundTrack();
            AppController.getInstance().quitLandscape(event.getLandScape());
            HermesEventBus.getDefault().removeAllStickyEvents();
        } else if (event.getType() == EntryLandscapeEvent.UNITY_TURN_BACK) {
            /**
             * 考虑unity切换的情况
             * 1.需要播放音乐
             * 2.需要检查是否靠近门
             */
            //播放介绍音频
            AppContext.getInstance().stopSoundTrack();
            AppContext.getInstance().playSoundTrack(event.getLandScape().getRealSoundTrackPath());
            HermesEventBus.getDefault().removeAllStickyEvents();
        }
    }

    /**
     * 初始化权限
     */
    private void initPermission() {
        mPermUtil = new PermissionUtils(MainActivity.this, new PermissionUtils.PermissionModel[]{
                new PermissionUtils.PermissionModel("存储空间", Manifest.permission.ACCESS_COARSE_LOCATION, "定位权限",
                        PermissionUtils.WRITE_EXTERNAL_STORAGE_CODE)
        });
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        toggleMenu.setOnMenuItemClick(new ToggleMenuView.OnMenuItemClickListener() {
            @Override
            public void onClick(View view, int type) {
                switch (type) {
                    case ToggleMenuView.CLICK_COLLECTION:
                        startActivity(CoinCollectionActivity.newIntent(MainActivity.this));
                        break;
                    case ToggleMenuView.CLICK_TASK:
                        startActivity(TaskListActivity.newIntent(MainActivity.this));
                        break;
                    case ToggleMenuView.CLICK_REPOSITORY:
                        startActivity(RepositoryListActivity.newIntent());
                        break;
                    case ToggleMenuView.CLICK_CONFIG:
                        startActivity(ConfigUserActivity.newIntent());
                        break;
                }
            }
        });
    }

    /**
     * 初始化amap
     */
    private void initMap(Bundle savedInstanceState) {

        //初始化
        mapView.onCreate(savedInstanceState);
        if (mMap != null) {
            return;
        }
        mMap = mapView.getMap();

        //地图样式
        mMap.setMapType(AMap.MAP_TYPE_NORMAL);
        mMap.showBuildings(false);
        mMap.showMapText(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setScaleControlsEnabled(false);

        //地图缩放大小
        mMap.setMinZoomLevel(Constants.MAP_ZOOM_LEVEL_MIN);
        mMap.setMaxZoomLevel(Constants.MAP_ZOOM_LEVEL_MAX);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(Constants.MAP_ZOOM_LEVEL_DEFAULT));
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(Constants.MAP_CENTER, Constants.MAP_ZOOM_LEVEL_DEFAULT, 0, 0)));

        //地图边界
        LatLngBounds limitBounds = new LatLngBounds.Builder()
                .include(Constants.MAP_BOUND_NORTH_EAST)
                .include(Constants.MAP_BOUND_SOUTH_WEST)
                .build();
        mMap.setMapStatusLimits(limitBounds);

        //手绘地图叠加
        LatLngBounds overlayBounds = new LatLngBounds.Builder()
                .include(Constants.OVERLAY_BOUND_NORTH_EAST)
                .include(Constants.OVERLAY_BOUND_SOUTH_WEST)
                .build();
        mMap.addGroundOverlay(new GroundOverlayOptions()
                .bearing(0)
                .anchor(0.0f, 0.0f)
                .image(BitmapDescriptorFactory.fromResource(R.drawable.bg_map))
                .positionFromBounds(overlayBounds));

        //地图事件
        mMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (TextUtils.isEmpty(marker.getSnippet())) {
                    return false;
                }

                final LandScape selLandScape = LandScapeManager.getInstance().getLandscape(Integer.valueOf(marker.getSnippet()));
                LandScape curLandscape = SessionManager.getInstance().getSession().getCurLandscape();

                if (AppController.DEBUG) {
                    int resId = SysInfoUtils.getResId(MainActivity.this, selLandScape.getId(), 1);
                    landscapeMarkers.get(selLandScape.getId()).setIcon(BitmapDescriptorFactory.fromResource(resId));
                    mUserLocMarker.setPosition(SessionManager.getInstance().getLocationLatLng());

                    //refresh
                    SessionManager.getInstance().getSession().setCurLandscape(selLandScape);
                    SessionManager.getInstance().getSession().setLastLandscape(selLandScape);
                    AppController.getInstance().enterLandscape(selLandScape);

//                    //线路测试
//                    new ConfirmDialog(MainActivity.this)
//                            .setCancelButton("取消", new ConfirmDialog.OnDialogBtnClickListener() {
//                                @Override
//                                public void onClick(View v, Dialog dialog) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setConfirmButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
//                                @Override
//                                public void onClick(View v, Dialog dialog) {
//                                    dialog.dismiss();
//                                    calculateRoute(selLandScape);
//                                }
//                            })
//                            .setText("是否请前往景点" + selLandScape.getName() + "浏览")
//                            .show();
//                    return false;
                }else {
                    if (curLandscape != null && selLandScape.equals(curLandscape)) {
                        int resId = SysInfoUtils.getResId(MainActivity.this, selLandScape.getId(), 1);
                        landscapeMarkers.get(selLandScape.getId()).setIcon(BitmapDescriptorFactory.fromResource(resId));
                        mUserLocMarker.setPosition(SessionManager.getInstance().getLocationLatLng());
                        //refresh
                        SessionManager.getInstance().getSession().setCurLandscape(selLandScape);
                        SessionManager.getInstance().getSession().setLastLandscape(selLandScape);
                        AppController.getInstance().enterLandscape(selLandScape);
                    } else {
                        new ConfirmDialog(MainActivity.this)
                                .setConfirmButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
                                    @Override
                                    public void onClick(View v, Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .setText("请前往景点" + selLandScape.getName() + "浏览")
                                .show();

                        //线路测试
//                    new ConfirmDialog(MainActivity.this)
//                            .setCancelButton("取消", new ConfirmDialog.OnDialogBtnClickListener() {
//                                @Override
//                                public void onClick(View v, Dialog dialog) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setConfirmButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
//                                @Override
//                                public void onClick(View v, Dialog dialog) {
//                                    calculateRoute(selLandScape);
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setText("是否请前往景点" + selLandScape.getName() + "浏览")
//                            .show();
                    }

                    //                if (curLandscape != null) {
//                    new ConfirmDialog(MainActivity.this)
//                            .setCancelButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
//                                @Override
//                                public void onClick(View v, Dialog dialog) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setText("您当前正在浏览景点" + curLandscape.getName())
//                            .show();
//                } else if (clickLandScape.equals(curLandscape)) {
//                    new ConfirmDialog(MainActivity.this)
//                            .setCancelButton("确定", new ConfirmDialog.OnDialogBtnClickListener() {
//                                @Override
//                                public void onClick(View v, Dialog dialog) {
//                                    dialog.dismiss();
//                                }
//                            })
//                            .setText("您当前正在浏览该景点")
//                            .show();
//                } else {
//                    int resId = SysInfoUtils.getResId(MainActivity.this, clickLandScape.getId(), 1);
//                    landscapeMarkers.get(clickLandScape.getId()).setIcon(BitmapDescriptorFactory.fromResource(resId));
//                    mUserLocMarker.setPosition(SessionManager.getInstance().getLocationLatLng());
//                    AppController.getInstance().enterLandscape(clickLandScape);
//                }
                }

                return false;
            }
        });

        mMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        //marker
        drawLandscapeMarkers();
        drawLocationMarker();
//        drawLandscape();
    }

    private void initRoute() {
        mRouteSearch = new RouteSearch(MainActivity.this);
        mRouteSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {

            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    if (result != null && result.getPaths() != null) {
                        if (result.getPaths().size() > 0) {
                            final WalkPath walkPath = result.getPaths().get(0);
                            mWalkRouteOverlay.removeFromMap();
//                            mWalkRouteOverlay = new WalkRouteOverlay(
//                                    MainActivity.this, mMap, walkPath,
//                                    result.getStartPos(),
//                                    result.getTargetPos());
                            mWalkRouteOverlay.setPath(walkPath, result.getStartPos(), result.getTargetPos());
                            mWalkRouteOverlay.addToMap();
                            mWalkRouteOverlay.zoomToSpan();
                        } else if (result != null && result.getPaths() == null) {

                        }
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }

        });

        mWalkRouteOverlay = new WalkRouteOverlay(
                MainActivity.this, mMap);
    }

    private void calculateRoute(LandScape landScape) {
        //104.028553,30.660076
//        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(SessionManager.getInstance().getSession().getLat(), SessionManager.getInstance().getSession().getLng()), CoordsUtils.Gps2LatLonPoint(landScape.getLat(), landScape.getLng()));

        //TODO test only
        RouteSearch.FromAndTo test = new RouteSearch.FromAndTo(new LatLonPoint(30.660076, 104.028553), CoordsUtils.Gps2LatLonPoint(landScape.getLat(), landScape.getLng()));
        RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(test, WALK_DEFAULT);
        mRouteSearch.calculateWalkRouteAsyn(query);
    }

    /**
     * 绘制景点边界
     */
    private void drawLandscape() {
        for (LandScape landScape : LandScapeManager.getInstance().getLandscapes()) {
            if (landScape.getBorders().size() != 0) {
                mMap.addPolygon(new PolygonOptions()
                        .addAll(landScape.getBorderLatLng())
                        .fillColor(Color.RED)
                        .strokeColor(Color.YELLOW)
                        .strokeWidth(1));
            }
        }
    }

    /**
     * 绘制位置marker
     */
    private void drawLocationMarker() {
        MarkerOptions option = new MarkerOptions()
                .infoWindowEnable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
                .draggable(false);
        mUserLocMarker = mMap.addMarker(option);
    }

    /**
     * 绘制景点marker
     */
    private void drawLandscapeMarkers() {
        Map<Integer, Integer> logs = SessionManager.getInstance().getSession().getLandScapeLog();
        for (LandScape landScape : LandScapeManager.getInstance().getLandscapes()) {
            //没有坐标不显示
            if (landScape.getLng() == 0) {
                continue;
            }
            //如果不是推荐路线，不显示
            List<Integer> recomendTours = Constants.TOURS.get(SessionManager.getInstance().getSession().getSelTour());
            if (!recomendTours.contains(landScape.getId())) {
                continue;
            }

            LatLng markerCoord = CoordsUtils.Gps2LatLng(landScape.getLat(), landScape.getLng());
            int resId = SysInfoUtils.getResId(MainActivity.this, landScape.getId(), logs.get(landScape.getId()) == 0 ? 0 : 1);
            MarkerOptions option = new MarkerOptions()
                    .snippet(String.valueOf(landScape.getId()))
                    .position(markerCoord)
                    .infoWindowEnable(false)
                    .draggable(false)
                    .icon(BitmapDescriptorFactory.fromResource(resId));
            Marker marker = mMap.addMarker(option);
            landscapeMarkers.put(landScape.getId(), marker);
        }
    }

    private void updateLandscapeMarkers() {
        Iterator iter = landscapeMarkers.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            int landscapeId = (int) entry.getKey();
            int times = SessionManager.getInstance().getSession().getLandScapeLog().get(landscapeId);
            if (times > 0) {
                int resId = SysInfoUtils.getResId(MainActivity.this, landscapeId, 1);
                ((Marker) entry.getValue()).setIcon(BitmapDescriptorFactory.fromResource(resId));
            }
        }
    }

    /*******************************
     * 测试用 0 GBCVR/ 1 GJVR/2 TEXTAR/3 3DAR
     ********************************/
    private class SoundTrackBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            soundTrackMenu.reset();
        }
    }

    /*******************************
     * 测试用 0 GBCVR/ 1 GJVR/2 TEXTAR/3 3DAR
     ********************************/
    private void initTestMenu() {
        mAdapter = new SimulatorListAdapter(new ArrayList<LandScape>());
        mAdapter.setOnItemClickLisener(new OnItemClickLisener() {
            @Override
            public void OnItemClick(View view, int position) {
                LandScape landscape = (LandScape) mAdapter.getEntity(position);
                AppController.getInstance().enterLandscape(landscape);
            }

            @Override
            public void OnFunctionClick(View view, int position, int functionFlag) {

            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listuv.setLayoutManager(manager);
        listuv.setHasFixedSize(true);
        listuv.setClipToPadding(true);
        listuv.setEmptyView(R.layout.include_empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);
        listuv.setAdapter(mAdapter);
        mAdapter.replace(LandScapeManager.getInstance().getLandscapes());
    }


}
