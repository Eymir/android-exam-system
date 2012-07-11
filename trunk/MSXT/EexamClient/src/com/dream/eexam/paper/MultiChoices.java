package com.dream.eexam.paper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.dream.eexam.base.BaseActivity;
import com.dream.eexam.base.QuestionsAll;
import com.dream.eexam.base.R;
import com.dream.eexam.model.Choice;

public class MultiChoices extends BaseActivity {

	//set question sub header
	private TextView currentTV = null;
	private TextView allTV = null;
	
	private TextView questionTV = null;
	
	//LinearLayout listLayout
	ListView listView;
	
	//LinearLayout listFooter
	private Button preBtn;
	private Button nextBtn;
	
	List<Choice> choices = new ArrayList<Choice>();
	
	Context mContext;
	MyListAdapter adapter;
	List<Integer> listItemID = new ArrayList<Integer>();

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paper_multi_choices);
        mContext = getApplicationContext();
        
        //hard code data
    	choices.add(new Choice(1, "goto"));
    	choices.add(new Choice(2, "malloc"));
    	choices.add(new Choice(3, "extends"));
    	choices.add(new Choice(4, "FALSE"));

        //set question text
    	currentTV = (TextView)findViewById(R.id.header_tv_current);
    	currentTV.setBackgroundColor(Color.parseColor("#4428FF"));
    	currentTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//go to question 1
				Intent intent = new Intent();
				intent.setClass( mContext, MultiChoices.class);
				startActivity(intent);
			}
		});
        //set question text
    	allTV = (TextView)findViewById(R.id.header_tv_all);
    	allTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//go to question 1
				Intent intent = new Intent();
				intent.setClass( mContext, QuestionsAll.class);
				startActivity(intent);
			}
		});
        
        //set question text
        questionTV = (TextView)findViewById(R.id.questionTV);
        questionTV.setText("Q1:Which of the following are Java keywords?");
        questionTV.setTextColor(Color.BLACK);
        
        //set List
        listView = (ListView)findViewById(R.id.lvChoices);
        adapter = new MyListAdapter(choices);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
        	@Override
			public void onItemClick(AdapterView<?> adapter, View view, int arg2,
					long arg3) {
        		CheckBox cb = (CheckBox)view.findViewById(R.id.list_select);
        		cb.setChecked(!cb.isChecked());
			}      	
        });
        
        preBtn = (Button)findViewById(R.id.preBtn);
        preBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				preBtn.setBackgroundResource(R.drawable.btn_sys);
//				listItemID.clear();
//				for(int i=0;i<adapter.mChecked.size();i++){
//					if(adapter.mChecked.get(i)){
//						Choice choice = adapter.choices.get(i);
//						listItemID.add(Integer.valueOf(choice.getId()));
//					}
//				}
//				if(listItemID.size()==0){
//					ShowDialog("At lease choose one item!");
//				}else{
//					if(preBtn.isEnabled()){
//
//					}
//				}
			}
		});
        
        nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//go to question 1
				Intent intent = new Intent();
				intent.setClass( mContext, SingleChoices.class);
				startActivity(intent);
			}
		});
    
    }
    
    class MyListAdapter extends BaseAdapter{
    	List<Boolean> mChecked = new ArrayList<Boolean>();
    	List<Choice> choices = new ArrayList<Choice>();
    	
		HashMap<Integer,View> map = new HashMap<Integer,View>(); 
    	
    	public MyListAdapter(List<Choice> choices){
//    		choices = new ArrayList<Choice>();
    		
    		this.choices = choices;
    		
    		for(int i=0;i<choices.size();i++){
    			mChecked.add(false);
    		}
    	}

		@Override
		public int getCount() {
			return choices.size();
		}

		@Override
		public Object getItem(int position) {
			return choices.get(position);
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
				Log.i(LOG_TAG,"position1 = "+position);
				
				LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = mInflater.inflate(R.layout.paper_multi_choices_item, null);
				holder = new ViewHolder();
				
				//set 3 component 
				holder.selected = (CheckBox)view.findViewById(R.id.list_select);
				holder.index = (TextView)view.findViewById(R.id.list_index);
				holder.choiceDesc = (TextView)view.findViewById(R.id.list_choiceDesc);
				
				final int p = position;
				map.put(position, view);
				
				holder.selected.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						
						CheckBox cb = (CheckBox)buttonView;
						mChecked.set(p, cb.isChecked());
					}
				});
				
				view.setTag(holder);
			}else{
				Log.i(LOG_TAG,"position2 = "+position);
				view = map.get(position);
				holder = (ViewHolder)view.getTag();
			}
			
			Choice choice = choices.get(position);
			holder.selected.setChecked(mChecked.get(position));
			holder.index.setText(String.valueOf(choice.getId()));
			holder.choiceDesc.setText(choice.getChoiceDesc());
			
			return view;
		}
    	
    }
    
    static class ViewHolder{
    	CheckBox selected;
    	TextView index;
    	TextView choiceDesc;
    }
}
