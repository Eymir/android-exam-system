package com.dream.ivpc.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.dream.ivpc.R;
import com.dream.ivpc.model.QuestionDetailBean;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class QuestionListAdapter extends BaseAdapter {
	Context mContext;
	List<QuestionDetailBean> questionList = new ArrayList<QuestionDetailBean>();
	HashMap<Integer,View> map = new HashMap<Integer,View>(); 
	
	public QuestionListAdapter(List<QuestionDetailBean> questionList,Context mContext){
		this.questionList = questionList;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return questionList.size();
	}

	@Override
	public Object getItem(int position) {
		return questionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		ViewHolder holder = null;
		
		if (map.get(position) == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.candidate_info_examdetail_item, null);
			holder = new ViewHolder();

			//set 3 component 
			holder.index = (TextView)view.findViewById(R.id.index);
			holder.catalog = (TextView)view.findViewById(R.id.catalog);
			holder.questionName = (TextView)view.findViewById(R.id.questionName);
			holder.resultView = (ImageView) view.findViewById(R.id.resultView);
			holder.qcontent = (TextView) view.findViewById(R.id.qcontent);
			
			map.put(position, view);
			view.setTag(holder);
		}else{
			view = map.get(position);
			holder = (ViewHolder)view.getTag();
		}
		
		QuestionDetailBean bean = questionList.get(position);
		
		holder.index.setText(String.valueOf(bean.getIndex()));
		holder.catalog.setText(bean.getCatalog());
		holder.questionName.setText(bean.getQuestionName());
		if(bean.isResult()){
			holder.resultView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.true_32));
		}else{
			holder.resultView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.false_32));
		}
		
		holder.qcontent.setText(bean.getQcontent());
		
		return view;
	}
	
	static class ViewHolder{
		TextView index;
		TextView catalog;
		TextView questionName;
		ImageView resultView;
		
		TextView qcontent;
	}
	



}
