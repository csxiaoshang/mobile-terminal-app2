package co.herxun.impp.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import co.herxun.impp.R;
import co.herxun.impp.activity.CommentActivity;
import co.herxun.impp.activity.PictureActivity;
import co.herxun.impp.activity.UserDetailActivity;
import co.herxun.impp.controller.UserManager;
import co.herxun.impp.controller.EventManager;
import co.herxun.impp.im.controller.IMManager;
import co.herxun.impp.im.controller.IMManager.GetMessageCallback;
import co.herxun.impp.im.model.Chat;
import co.herxun.impp.im.model.ChatUser;
import co.herxun.impp.im.model.Message;
import co.herxun.impp.im.view.MessageListItem;
import co.herxun.impp.imageloader.ImageLoader;
import co.herxun.impp.model.Like;
import co.herxun.impp.model.Event;
import co.herxun.impp.model.User;
import co.herxun.impp.utils.Constant;
import co.herxun.impp.utils.DBug;
import co.herxun.impp.utils.Utils;

public class EventListAdapter extends BaseAdapter {
	private Context ct;
	private List<Event> eventList;
	private EventManager mEventManager;
	
	public EventListAdapter(Context ct,EventManager eventMngr){
		this.ct = ct;
		mEventManager = eventMngr;
		eventList = new ArrayList<Event>();
	}
	
	public void applyData(List<Event> msgs){
		eventList.clear();
		eventList.addAll(msgs);
		
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return eventList.size();
	}

	@Override
	public Event getItem(int position) {
		return eventList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EventListItem view = (EventListItem) convertView;
		if (convertView == null) {
			view = new EventListItem(parent.getContext());
		}
		view.setData(position);
		
		return view;
	}


	public void updateItem(int index , Event event){
		eventList.remove(index);
		eventList.add(index,event);
		notifyDataSetChanged();
	}
	
	
	public class EventListItem extends RelativeLayout{
		private LinearLayout photoContainer;
		private TextView textUserName,textTime,textContent,textLike,textComment;
		private ImageView imgUserIcon;
		private View btnLike,btnComment;
		
		public EventListItem(Context ct) {
			super(ct); 
			inflate(getContext(), R.layout.view_post_item, this);
			photoContainer = (LinearLayout) findViewById(R.id.view_post_scrollView);
			imgUserIcon = (ImageView) findViewById(R.id.view_post_user_icon);
			textUserName = (TextView) findViewById(R.id.view_post_user_name);
			textTime = (TextView) findViewById(R.id.view_post_timestamp);
			textContent = (TextView) findViewById(R.id.view_post_user_content);
			
			
		}
		
		public void setData(final int index){
			final Event data = eventList.get(index);
			textUserName.setText(data.title);		
			textTime.setText(data.start_time);										
			if(data.information!=null&&data.information.length()>0){
				textContent.setVisibility(View.VISIBLE);
				textContent.setText(data.information);
			}else{
				textContent.setVisibility(View.GONE);
			}
		}				
		
	}
	
}
