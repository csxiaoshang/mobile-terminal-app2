package co.herxun.impp.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMap.OnPOIClickListener;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.NaviPara;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.Text;
import com.amap.api.maps.model.TextOptions;
import com.arrownock.exception.ArrownockException;
import com.arrownock.social.AnSocial;
import com.arrownock.social.AnSocialMethod;
import com.arrownock.social.IAnSocialCallback;

import co.herxun.impp.IMppApp;
import co.herxun.impp.R;
import co.herxun.impp.R.layout;
import co.herxun.impp.adapter.EventListAdapter;
import co.herxun.impp.controller.UserManager;
import co.herxun.impp.controller.EventManager;
import co.herxun.impp.controller.UserManager.FetchUserCallback;

import co.herxun.impp.im.controller.IMManager;
import co.herxun.impp.im.model.Message;
import co.herxun.impp.imageloader.ImageLoader;
import co.herxun.impp.model.Post;
import co.herxun.impp.model.User;
import co.herxun.impp.utils.Constant;
import co.herxun.impp.utils.DBug;
import co.herxun.impp.utils.Utils;
import co.herxun.impp.view.AppBar;
import co.herxun.impp.view.EventView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class EventActivity extends BaseActivity implements OnClickListener,
OnPOIClickListener,OnInfoWindowClickListener,OnMapLongClickListener {
	private AppBar appbar;
	private EventView mEventView;
	private FrameLayout header;
	private MapView mapView;
	private AMap aMap;
	private Button basicmap;
	private Button rsmap;
	private Button nightmap;
	private Button navimap;
	private MarkerOptions markerOption;
	private CheckBox mStyleCheckbox;
	private Marker marker2;// marker对象2
	private Marker marker3;// marker对象3
	String Latlon; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event);
		
		initView();
	    initData();
	    mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写

		init();
	}
	
	private void initView(){
		appbar = (AppBar) findViewById(R.id.wall_app_bar);
		appbar.getLogoView().setImageResource(R.drawable.menu_back);
		appbar.getLogoView().setLayoutParams(new RelativeLayout.LayoutParams(Utils.px2Dp(this, 56),Utils.px2Dp(this, 56)));
		appbar.getLogoView().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		appbar.getTextView().setVisibility(View.VISIBLE);
		appbar.getTextView().setText(R.string.event_title);
		
	
	}
	private void initData(){
		
		
	
	}
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.slide_out_right);
	}
	
	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
	
		//定位相关
		MyLocationStyle myLocationStyle;
		myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
		myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//定位一次，且将视角移动到地图中心点。
		myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
		aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
		//aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
		aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
		aMap.setOnMapLongClickListener(this);// 对amap添加长按地图事件监听器
		//POI
		aMap.setOnPOIClickListener(this);
		//aMap.setOnMarkerClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		//事件marker
		//aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
		//aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
		//aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		//aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		//aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
		addMarkersToMap();// 往地图上添加marker
		//setMapCustomStyleFile(this);
		/*basicmap = (Button)findViewById(R.id.basicmap);
		basicmap.setOnClickListener(this);
		rsmap = (Button)findViewById(R.id.rsmap);
		rsmap.setOnClickListener(this);
		nightmap = (Button)findViewById(R.id.nightmap);
		nightmap.setOnClickListener(this);
		navimap = (Button)findViewById(R.id.navimap);
		navimap.setOnClickListener(this);
		mStyleCheckbox = (CheckBox) findViewById(R.id.check_style);*/

		/*mStyleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				aMap.setMapCustomEnable(b);
			}
		});*/

	}
	/**
	 * 在地图上添加marker
	 */
	private void addMarkersToMap() {
		// 文字显示标注，可以设置显示内容，位置，字体大小颜色，背景色旋转角度
		TextOptions textOptions = new TextOptions()
				.position(Constants.BEIJING)
				.text("Text")
				.fontColor(Color.BLACK)
				.backgroundColor(Color.BLUE)
				.fontSize(30)
				.rotate(20)
				.align(Text.ALIGN_CENTER_HORIZONTAL, Text.ALIGN_CENTER_VERTICAL)
				.zIndex(1.f).typeface(Typeface.DEFAULT_BOLD);
		aMap.addText(textOptions);

		markerOption = new MarkerOptions();
		markerOption.position(Constants.xinyuewangka);
		markerOption.title("战斗吧，少年").snippet("快来，打定级赛了！！");

		markerOption.draggable(true);
		markerOption.icon(
		// BitmapDescriptorFactory
		// .fromResource(R.drawable.location_marker)
				BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.icon_geo)));
		// 将Marker设置为贴地显示，可以双指下拉看效果
		markerOption.setFlat(true);

		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

		MarkerOptions markerOption1 = new MarkerOptions().anchor(0.5f, 0.5f)
				.position(Constants.jiajiayuan).title("购物")
				.snippet("小姐姐陪你shopping").icons(giflist)
				.draggable(true).period(10);
		ArrayList<MarkerOptions> markerOptionlst = new ArrayList<MarkerOptions>();
		markerOptionlst.add(markerOption);
		markerOptionlst.add(markerOption1);
		List<Marker> markerlst = aMap.addMarkers(markerOptionlst, true);
		marker2 = markerlst.get(0);
		marker3= markerlst.get(1);
		//marker3.showInfoWindow();
		//marker2.showInfoWindow();
		

		/*marker3 = aMap.addMarker(new MarkerOptions().position(
				Constants.jiajiayuan).title("购物").snippet("小姐姐陪你shopping!").icon(
				BitmapDescriptorFactory.fromResource(R.drawable.icon_geo)));*/
	}
	
	/**
	 * 对长按地图事件回调
	 */
	@Override
	public void onMapLongClick(LatLng point) {
				
	/*	Latlon =String.valueOf(point);
		Toast.makeText(EventActivity.this, "位置已选择，请返回！", Toast.LENGTH_LONG).show();*/
		
	}
	
	/**
	 *  底图poi点击回调
	 */
	@Override
	public void onPOIClick(Poi poi) {
		//aMap.clear();
		Log.i("MY", poi.getPoiId()+poi.getName());
		MarkerOptions markOptiopns = new MarkerOptions();
		markOptiopns.position(poi.getCoordinate());
		TextView textView = new TextView(getApplicationContext());
		textView.setText("到"+poi.getName()+"去");
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		textView.setBackgroundResource(R.drawable.custom_info_bubble);
		markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
		aMap.addMarker(markOptiopns);
	}

	/**
	 * Marker 点击回调
	 * @param marker
	 * @return
     */
/*	@Override
	public boolean onMarkerClick(Marker marker) {

		// 构造导航参数
		NaviPara naviPara = new NaviPara();
		// 设置终点位置
		naviPara.setTargetPoint(marker.getPosition());
		// 设置导航策略，这里是避免拥堵
		naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
		try {
			// 调起高德地图导航
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps.AMapException e) {
			// 如果没安装会进入异常，调起下载页面
			AMapUtils.getLatestAMapApp(getApplicationContext());
		}
		//aMap.clear();
		return false;
	}*/


    @Override
    public void onInfoWindowClick(Marker marker) {
        
    	// 构造导航参数
		NaviPara naviPara = new NaviPara();
		// 设置终点位置
		naviPara.setTargetPoint(marker.getPosition());
		// 设置导航策略，这里是避免拥堵
		naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
		try {
			// 调起高德地图导航
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps.AMapException e) {
			// 如果没安装会进入异常，调起下载页面
			AMapUtils.getLatestAMapApp(getApplicationContext());
		}
		//aMap.clear();
		//return false;
        
    }


/*	private void setMapCustomStyleFile(Context context) {
		String styleName = "style_json.json";
		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		String filePath = null;
		try {
			inputStream = context.getAssets().open(styleName);
			byte[] b = new byte[inputStream.available()];
			inputStream.read(b);

			filePath = context.getFilesDir().getAbsolutePath();
			File file = new File(filePath + "/" + styleName);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			outputStream = new FileOutputStream(file);
			outputStream.write(b);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();

				if (outputStream != null)
					outputStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		aMap.setCustomMapStylePath(filePath + "/" + styleName);

		aMap.showMapText(false);

	}*/

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.basicmap:
				aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
				break;
			case R.id.rsmap:
				aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
				break;
			case R.id.nightmap:
				aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
				break;
			case R.id.navimap:
				aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
				break;
		}

		mStyleCheckbox.setChecked(false);

	}
	
	
	
}
