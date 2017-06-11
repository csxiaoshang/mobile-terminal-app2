package co.herxun.impp.activity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import co.herxun.impp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMapLongClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.OnMarkerDragListener;
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


/**
 * ��ʾ�ߵµ�ͼ ��λ����ģʽ��ʹ�ã�������λ��׷�棬��ת
 * 
 * @author zpf_dzw
 *implements LocationSource,
		AMapLocationListener, OnCheckedChangeListener, OnMarkerClickListener ,OnMarkerClickListener 
 */
public  class LocationActivity extends BaseActivity implements OnClickListener,
OnPOIClickListener,OnInfoWindowClickListener,OnMapLongClickListener{
	private MapView mapView;
	private AMap aMap;
	private Button basicmap;
	private Button rsmap;
	private Button nightmap;
	private Button navimap;
	private MarkerOptions markerOption;
	private CheckBox mStyleCheckbox;
	private Marker marker2;// marker����2
	private Marker marker3;// marker����3
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_amap);
	    /*
         * �������ߵ�ͼ�洢Ŀ¼�����������ߵ�ͼ���ʼ����ͼ����;
         * ʹ�ù����п���������, ���������������ߵ�ͼ�洢��·����
         * ����Ҫ�����ߵ�ͼ���غ�ʹ�õ�ͼҳ�涼����·������
         * */
		//Demo��Ϊ�������������ʹ�����ص����ߵ�ͼ��ʹ��Ĭ��λ�ô洢���������Զ�������
		//  MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// �˷���������д

		init();

	}

	/**
	 * ��ʼ��AMap����
	 */
	private void init() {
		if (aMap == null) {
			aMap = mapView.getMap();
		}
	
		//��λ���
		MyLocationStyle myLocationStyle;
		myLocationStyle = new MyLocationStyle();//��ʼ����λ������ʽ��myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//������λ���ҽ��ӽ��ƶ�����ͼ���ĵ㣬��λ�������豸������ת�����һ�����豸�ƶ�����1��1�ζ�λ�����������myLocationType��Ĭ��Ҳ��ִ�д���ģʽ��
		myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE) ;//��λһ�Σ��ҽ��ӽ��ƶ�����ͼ���ĵ㡣
		myLocationStyle.interval(2000); //����������λģʽ�µĶ�λ�����ֻ��������λģʽ����Ч�����ζ�λģʽ�²�����Ч����λΪ���롣
		aMap.setMyLocationStyle(myLocationStyle);//���ö�λ�����Style
		//aMap.getUiSettings().setMyLocationButtonEnabled(true);����Ĭ�϶�λ��ť�Ƿ���ʾ���Ǳ������á�
		aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ������ʾ��λ���㣬false��ʾ���ض�λ���㲢�����ж�λ��Ĭ����false��
		aMap.setOnMapLongClickListener(this);// ��amap��ӳ�����ͼ�¼�������
		//POI
		aMap.setOnPOIClickListener(this);
		//aMap.setOnMarkerClickListener(this);
		aMap.setOnInfoWindowClickListener(this);
		//�¼�marker
		//aMap.setOnMarkerDragListener(this);// ����marker����ק�¼�������
		//aMap.setOnMapLoadedListener(this);// ����amap���سɹ��¼�������
		//aMap.setOnMarkerClickListener(this);// ���õ��marker�¼�������
		//aMap.setOnInfoWindowClickListener(this);// ���õ��infoWindow�¼�������
		//aMap.setInfoWindowAdapter(this);// �����Զ���InfoWindow��ʽ
		addMarkersToMap();// ����ͼ�����marker
		//setMapCustomStyleFile(this);
		basicmap = (Button)findViewById(R.id.basicmap);
		basicmap.setOnClickListener(this);
		rsmap = (Button)findViewById(R.id.rsmap);
		rsmap.setOnClickListener(this);
		nightmap = (Button)findViewById(R.id.nightmap);
		nightmap.setOnClickListener(this);
		navimap = (Button)findViewById(R.id.navimap);
		navimap.setOnClickListener(this);
		mStyleCheckbox = (CheckBox) findViewById(R.id.check_style);

		mStyleCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				aMap.setMapCustomEnable(b);
			}
		});

	}
	/**
	 * �ڵ�ͼ�����marker
	 */
	private void addMarkersToMap() {
		// ������ʾ��ע������������ʾ���ݣ�λ�ã������С��ɫ������ɫ��ת�Ƕ�
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
		markerOption.title("ս���ɣ�����").snippet("�������򶨼����ˣ���");

		markerOption.draggable(true);
		markerOption.icon(
		// BitmapDescriptorFactory
		// .fromResource(R.drawable.location_marker)
				BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.icon_geo)));
		// ��Marker����Ϊ������ʾ������˫ָ������Ч��
		markerOption.setFlat(true);

		ArrayList<BitmapDescriptor> giflist = new ArrayList<BitmapDescriptor>();
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		giflist.add(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

		MarkerOptions markerOption1 = new MarkerOptions().anchor(0.5f, 0.5f)
				.position(Constants.jiajiayuan).title("����")
				.snippet("С�������shopping").icons(giflist)
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
				Constants.jiajiayuan).title("����").snippet("С�������shopping!").icon(
				BitmapDescriptorFactory.fromResource(R.drawable.icon_geo)));*/
	}
	
	/**
	 * �Գ�����ͼ�¼��ص�
	 */
	@Override
	public void onMapLongClick(LatLng point) {
				
		MarkerOptions markerOption = new MarkerOptions();
	    markerOption.position(point);
	    markerOption.title("������").snippet("�����У�34.341568, 108.940174");

	    markerOption.draggable(true);//����Marker���϶�
	    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
	        .decodeResource(getResources(),R.drawable.icon_geo)));
	    // ��Marker����Ϊ������ʾ������˫ָ������ͼ�鿴Ч��
	    markerOption.setFlat(true);//����markerƽ����ͼЧ��
		
	}
	
	/**
	 *  ��ͼpoi����ص�
	 */
	@Override
	public void onPOIClick(Poi poi) {
		//aMap.clear();
		Log.i("MY", poi.getPoiId()+poi.getName());
		MarkerOptions markOptiopns = new MarkerOptions();
		markOptiopns.position(poi.getCoordinate());
		TextView textView = new TextView(getApplicationContext());
		textView.setText("��"+poi.getName()+"ȥ");
		textView.setGravity(Gravity.CENTER);
		textView.setTextColor(Color.BLACK);
		textView.setBackgroundResource(R.drawable.custom_info_bubble);
		markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
		aMap.addMarker(markOptiopns);
	}

	/**
	 * Marker ����ص�
	 * @param marker
	 * @return
     */
/*	@Override
	public boolean onMarkerClick(Marker marker) {

		// ���쵼������
		NaviPara naviPara = new NaviPara();
		// �����յ�λ��
		naviPara.setTargetPoint(marker.getPosition());
		// ���õ������ԣ������Ǳ���ӵ��
		naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
		try {
			// ����ߵµ�ͼ����
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps.AMapException e) {
			// ���û��װ������쳣����������ҳ��
			AMapUtils.getLatestAMapApp(getApplicationContext());
		}
		//aMap.clear();
		return false;
	}*/


    @Override
    public void onInfoWindowClick(Marker marker) {
        
    	// ���쵼������
		NaviPara naviPara = new NaviPara();
		// �����յ�λ��
		naviPara.setTargetPoint(marker.getPosition());
		// ���õ������ԣ������Ǳ���ӵ��
		naviPara.setNaviStyle(AMapUtils.DRIVING_AVOID_CONGESTION);
		try {
			// ����ߵµ�ͼ����
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps.AMapException e) {
			// ���û��װ������쳣����������ҳ��
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
	 * ����������д
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * ����������д
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * ����������д
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
				aMap.setMapType(AMap.MAP_TYPE_NORMAL);// ʸ����ͼģʽ
				break;
			case R.id.rsmap:
				aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// ���ǵ�ͼģʽ
				break;
			case R.id.nightmap:
				aMap.setMapType(AMap.MAP_TYPE_NIGHT);//ҹ����ͼģʽ
				break;
			case R.id.navimap:
				aMap.setMapType(AMap.MAP_TYPE_NAVI);//������ͼģʽ
				break;
		}

		mStyleCheckbox.setChecked(false);

	}
}

