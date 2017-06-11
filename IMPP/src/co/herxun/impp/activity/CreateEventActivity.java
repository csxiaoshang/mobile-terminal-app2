package co.herxun.impp.activity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import java.util.HashMap;
import com.arrownock.social.IAnSocialCallback;
import com.ycf.blog_05_chinesechoosedemo.datedialog.DateTimePickerDialog;
import com.ycf.blog_05_chinesechoosedemo.datedialog.DateTimePickerDialog.OnDateTimeSetListener;
import com.arrownock.exception.ArrownockException;
import co.herxun.impp.IMppApp;
import co.herxun.impp.R;
import co.herxun.impp.adapter.UserChooseListAdapter;
import co.herxun.impp.controller.EventManager;
import co.herxun.impp.controller.UserManager;
import co.herxun.impp.fragment.ExploreFragment;
import co.herxun.impp.im.controller.IMManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import co.herxun.impp.view.AppBar;
import android.widget.TextView;
import android.widget.RelativeLayout;
import co.herxun.impp.utils.DBug;
import co.herxun.impp.utils.Utils;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity; 
import co.herxun.impp.R;
import java.util.Map;
import java.util.Set;

import com.arrownock.social.AnSocial;
import com.arrownock.social.AnSocialFile;
import com.arrownock.social.AnSocialMethod;
import com.arrownock.social.IAnSocialCallback;
public class CreateEventActivity extends Activity
{
	private Button btn_choose;	
	private AppBar appbar;
	private TextView tv_choose;
	private TextView tv_choosetype;
	 private String[] areas = new String[]{"聚餐","旅行", "户外运动", "学习", "游戏", "购物", "户外娱乐" };
	 public String TypeID;
	 private RadioOnClick OnClick = new RadioOnClick(1);
	 private ListView areaListView;
	 private Button btn_choosetype;
	 private Button btn_publish;
	 private Button btn_chooseplace;
	 private AnSocial anSocial;
	 private EditText tx;
	 private String str_type;
	 private String str_time;
	 
	 private UserChooseListAdapter mUserChooseListAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publish);
        appbar = (AppBar) findViewById(R.id.create_post_app_bar);
		appbar.getLogoView().setImageResource(R.drawable.menu_back);
		appbar.getLogoView().setLayoutParams(new RelativeLayout.LayoutParams(Utils.px2Dp(this, 56),Utils.px2Dp(this, 56)));
		appbar.getLogoView().setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				setResult(Activity.RESULT_CANCELED);
				onBackPressed();
			}
		});
		appbar.getTextView().setVisibility(View.VISIBLE);
		appbar.getTextView().setText(R.string.wall_create_event_title);
		tx =(EditText)findViewById(R.id.wall_create_et2);
		tv_choose = (TextView)this.findViewById(R.id.dateDisplay);
		tv_choosetype = (TextView)this.findViewById(R.id.typeDisplay);
		btn_choose=(Button)this.findViewById(R.id.btn_choose);
		btn_publish=(Button)this.findViewById(R.id.btn_publish);
		btn_choosetype=(Button)this.findViewById(R.id.btn_choosetype);
		btn_chooseplace=(Button)this.findViewById(R.id.btn_chooseplace);
		btn_choosetype.setOnClickListener(new RadioClickListener());
		btn_choose.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				showDialog();
			}
		});
		btn_chooseplace.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//选择活动地点
				//*********************************************************************************//
				Intent i = new Intent(CreateEventActivity.this,LocationActivity.class);
				startActivity(i);
				
			}
		});
		
		btn_publish.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				/*//创建群组
				String topicName = "";
				Set<String> members = mUserChooseListAdapter.getChosenUser();
				members.add(UserManager.getInstance(v.getContext()).getCurrentUser().clientId);
				for(String member:members){
					topicName += UserManager.getInstance(v.getContext()).getUserByClientId(member).userName+",";
				}
				topicName = topicName.substring(0,topicName.length()-1);
				IMManager.getInstance(v.getContext()).createTopic(topicName,members);*/
				//创建事件
				createEvent();
			}
		});
		
	}
	public void showDialog()
	{
		DateTimePickerDialog dialog  = new DateTimePickerDialog(this, System.currentTimeMillis());
		dialog.setOnDateTimeSetListener(new OnDateTimeSetListener()
	      {
			public void OnDateTimeSet(AlertDialog dialog, long date)
			{
				tv_choose.setText(getStringDate(date));
				str_time=getStringDate(date);
				//Toast.makeText(PublishActivity.this, "您输入的日期是："+getStringDate(date), Toast.LENGTH_LONG).show();
			}
		});
		dialog.show();
	}
	/**
	* 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	*
	*/
	public static String getStringDate(Long date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = formatter.format(date);
		
		return dateString;
	}
	/**
	* 设置活动类型的代码
	*
	*/

   class RadioClickListener implements OnClickListener {
   @Override
   public void onClick(View v) {
    AlertDialog ad =new AlertDialog.Builder(CreateEventActivity.this).setTitle("选择活动类型")
    .setSingleChoiceItems(areas,OnClick.getIndex(),OnClick).create();
    areaListView=ad.getListView();
    ad.show();
   }
     }
    
   class RadioOnClick implements DialogInterface.OnClickListener{
   private int index;
 
   public RadioOnClick(int index){
    this.index = index;
   }
   public void setIndex(int index){
    this.index=index;
   }
   public int getIndex(){
    return index;
   }
   public void onClick(DialogInterface dialog, int whichButton){
     setIndex(whichButton);
     tv_choosetype.setText(areas[index]);
     str_type=areas[index];
   //  Toast.makeText(PublishActivity.this, "您已经选择了 " +  ":" + areas[index], Toast.LENGTH_LONG).show();
     dialog.dismiss();
   }
 }
   /**
	* 发布事件的代码
	*
	*/
   
   private void createEvent(){
		appbar.getMenuItemView().setEnabled(false);
		EventManager.createEvent(this,UserManager.getInstance(this).getCurrentUser().userId, str_type, tx.getText().toString() ,str_time,
				new IAnSocialCallback(){
					@Override
					public void onFailure(JSONObject arg0) {
						DBug.e("createEvent.onFailure",arg0.toString());
						appbar.getMenuItemView().setEnabled(true);
					}
					@Override
					public void onSuccess(JSONObject arg0) {
						DBug.e("createEvent.onSuccess",arg0.toString());
						setResult(Activity.RESULT_OK);
					}
		});
		
		onBackPressed();
   }
 
	/**
	* 返回事件的代码
	*
	*/
	 public void onBackPressed(){
			super.onBackPressed();
			overridePendingTransition(android.R.anim.fade_in,android.R.anim.slide_out_right);
		}
}
