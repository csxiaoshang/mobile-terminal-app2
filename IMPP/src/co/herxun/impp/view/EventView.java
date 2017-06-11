package co.herxun.impp.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;

import co.herxun.impp.R;
import co.herxun.impp.adapter.EventListAdapter;
import co.herxun.impp.controller.SocialManager;
import co.herxun.impp.controller.UserManager;
import co.herxun.impp.controller.EventManager;
import co.herxun.impp.controller.EventManager.FetchEventCallback;
import co.herxun.impp.model.Like;
import co.herxun.impp.model.Post;
import co.herxun.impp.utils.DBug;
import co.herxun.impp.utils.Utils;
import co.herxun.impp.view.EventView;
import co.herxun.impp.model.Event;

public class EventView extends SwipeRefreshLayout implements Observer{
	private EventListAdapter mEventListAdapter;
	private ListView mListView;
	private FrameLayout headerView;
	private RelativeLayout footer;
	private EventManager mEventManager;
	private Map<String,Integer> eventIdIndexMap;
	private Context ct;
	
	private boolean isListViewScrollButtom = false;
	
	public EventView(Context context) {
		super(context);
		init(context);
	}
	public EventView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context ct){
		this.ct = ct;

		setColorSchemeColors(ct.getResources().getColor(R.color.no1));
		//setProgressViewOffset(true,mTA.mSr.mResolK.szPDtoPC(-84),mTA.mSr.mResolK.szPDtoPC(104));
		setOnRefreshListener(mOnRefreshListener);
		
		mListView = new ListView(ct);
		addView(mListView);
		
		headerView = new FrameLayout(ct);
		headerView.setBackgroundColor(0xffff0000);
		headerView.setLayoutParams(new AbsListView.LayoutParams(-1,-2));
		mListView.addHeaderView(headerView);

		footer = new RelativeLayout(ct);
		footer.setLayoutParams(new AbsListView.LayoutParams(-1, Utils.px2Dp(ct, 72)));
		ProgressBar mPb = new ProgressBar(ct);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(Utils.px2Dp(ct, 50),Utils.px2Dp(ct, 50));
		rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		footer.addView(mPb,rlp);
		mListView.setOnScrollListener(new OnScrollListener(){
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == SCROLL_STATE_IDLE ){
					if(isListViewScrollButtom){
						if(mEventManager.canLoadMore()){
							Log.e("onScrollStateChanged","load more");
							mEventManager.loadMore(new FetchEventCallback(){
								@Override
								public void onFailure(String errorMsg) {
									
								}
								@Override
								public void onFinish(List<Event> data) {
									eventIdIndexMap.clear();
									for(int i =0;i<data.size();i++){
										eventIdIndexMap.put(data.get(i).eventId, i);
									}
									mEventListAdapter.applyData(data);
								}
							});

						}else{
							if(mListView.getFooterViewsCount()>0){
								mListView.removeFooterView(footer);
							}
						}
					}
				}
			}

			@Override
			public void onScroll(final AbsListView view, int firstVisibleItem,final int visibleItemCount, int totalItemCount) {
				isListViewScrollButtom = firstVisibleItem + visibleItemCount == totalItemCount;
				if(mEventManager!=null)
					DBug.e("onScroll",mEventManager.canLoadMore()+"?");
				if(mListView.getFooterViewsCount()==0 && mEventManager!=null && mEventManager.canLoadMore()){
					mListView.addFooterView(footer);
				}
			}
		});
	}
	
	public void setHeaderView(View view){
		headerView.addView(view);
	}
	
	
	public void setEventManager(EventManager eventMngr){
		this.mEventManager = eventMngr;
		mEventManager.addObserver(this);
		
		mEventListAdapter = new EventListAdapter(ct,mEventManager);
		mListView.setAdapter(mEventListAdapter);
		
		initEventData();
		//mPostListAdapter.fillLocalData();
	}

	public void initEventData(){
		eventIdIndexMap = new HashMap<String,Integer>();
		mEventManager.init(new FetchEventCallback(){
			@Override
			public void onFailure(String errorMsg) {
				setRefreshing(false);
				DBug.e("mEventManager.onFailure",errorMsg);
			}

			@Override
			public void onFinish(List<Event> data) {
				setRefreshing(false);
				for(int i =0;i<data.size();i++){
					eventIdIndexMap.put(data.get(i).eventId, i);
				}
				mEventListAdapter.applyData(data);
			}
		});
		
		
	}
	
	private OnRefreshListener mOnRefreshListener = new OnRefreshListener(){
		@Override
		public void onRefresh() {
			initEventData();
		}
	};

	@Override
	public void update(Observable observable, Object data) {
		if(data instanceof Event){
			Event event = (Event)data;
			if(eventIdIndexMap.containsKey(event.eventId)){
				mEventListAdapter.updateItem(eventIdIndexMap.get(event.eventId), event);
			}
		}
	}
	
}
