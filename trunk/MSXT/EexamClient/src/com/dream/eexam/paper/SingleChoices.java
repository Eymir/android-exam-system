package com.dream.eexam.paper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.dream.eexam.base.PapersActivity;
import com.dream.eexam.base.R;
import com.dream.eexam.model.Choice;
import com.dream.eexam.model.Question;
import com.dream.eexam.util.DatabaseUtil;
import com.dream.eexam.util.XMLParseUtil;

public class SingleChoices extends BaseQuestion {

	//set exam header
	private TextView remainingTime = null;
	
	//set question sub header
	private TextView catalogsTV = null;
	private TextView currentTV = null;
	private TextView waitTV = null;
	
	private TextView questionTV = null;
	
	//LinearLayout listLayout
	ListView listView;
	
	//LinearLayout listFooter
	private Button preBtn;
	private Button nextBtn;
	
	private Question question;
	List<Choice> choices = new ArrayList<Choice>();
	
	Context mContext;
	MyListAdapter adapter;
	List<String> listItemID = new ArrayList<String>();
	StringBuffer answerString = new StringBuffer();

	SharedPreferences sharedPreferences;
	public void setHeader(){
//		sharedPreferences = this.getSharedPreferences("eexam",MODE_PRIVATE);
//		QuestionProgress qp = getQuestionProgress(sharedPreferences);
		
		//set question text
		remainingTime = (TextView)findViewById(R.id.remainingTime);
		remainingTime.setText("Time Remaining: "+String.valueOf(paperBean.getTime())+" mins");
		
		//set question text
		catalogsTV = (TextView)findViewById(R.id.header_tv_catalogs);
		catalogsTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showWindow(v);
				if(pressedItemIndex!=-1){
					catalogsTV.setText(groups.get(pressedItemIndex));
				}
			}
		});

		//set question text
    	currentTV = (TextView)findViewById(R.id.header_tv_current);
    	currentTV.setText("Q "+String.valueOf(currentQuestionIndex));
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
    	waitTV = (TextView)findViewById(R.id.header_tv_waiting);
//    	waitTV.setText(String.valueOf(qp.getWaitingQueIdsList().size()));
    	waitTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//go to question 1
//				Intent intent = new Intent();
//				intent.setClass( mContext, QuestionsWaiting.class);
//				startActivity(intent);
			}
		});  
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paper_single_choices);
        mContext = getApplicationContext();
        
        setHeader();
		
//        InputStream inputStream =  PapersActivity.class.getClassLoader().getResourceAsStream("sample_paper.xml");
        try {
			question = XMLParseUtil.readQuestion(inputStream, 1, currentQuestionIndex);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        //set question text
        questionTV = (TextView)findViewById(R.id.questionTV);
        questionTV.setText(question.getQuestionDesc());
        questionTV.setTextColor(Color.BLACK);
        
        //set List
        listView = (ListView)findViewById(R.id.lvChoices);
        adapter = new MyListAdapter(question.getChoices());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener(){
        	@Override
			public void onItemClick(AdapterView<?> adapter, View view, int arg2,
					long arg3) {
        		RadioButton cb = (RadioButton)view.findViewById(R.id.list_select);
        		cb.setChecked(!cb.isChecked());
			}      	
        });
        
        preBtn = (Button)findViewById(R.id.preBtn);
        preBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				direction = -1;
				relocationQuestion();
			}
		});
        
        nextBtn = (Button)findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				direction = 1;
				relocationQuestion();
			}
		});
    
    }
    
    //save answer if not empty 
    public void relocationQuestion(){
    	listItemID.clear();
		
		//get selection
		for (int i = 0; i < adapter.mChecked.size(); i++) {
			if (adapter.mChecked.get(i)) {
				Choice choice = adapter.choices.get(i);
				listItemID.add(String.valueOf(choice.getChoiceIndex()));
				answerString.append(String.valueOf(choice.getChoiceIndex()));
				answerString.append(",");
			}

			if (listItemID.size() == 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(SingleChoices.this);
				builder.setMessage("Answer this question late?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										gotoNewQuestion();
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,int id) {
										dialog.cancel();
									}
								});
				builder.show();
			} else {
				// save data
		    	DatabaseUtil dbUtil = new DatabaseUtil(this);
		    	dbUtil.open();
		    	dbUtil.createSystemConfig(String.valueOf(question.getIndex()), answerString.toString());
		    	dbUtil.close();
				
		    	gotoNewQuestion();
			}
		}
    }
    
    //go to next or previous question
    public void gotoNewQuestion(){
    	//get first question in paper
		InputStream inputStream =  MultiChoices.class.getClassLoader().getResourceAsStream("sample_paper.xml");
		Question question = null;
        try {
              question = XMLParseUtil.readQuestion(inputStream,1,currentQuestionIndex+direction);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(question!=null){
			if("Choice:M".equals(question.getQuestionType())){
				//go to question 1
				Intent intent = new Intent();
				intent.setClass( getBaseContext(), MultiChoices.class);
				startActivity(intent);
			}else if("Choice:S".equals(question.getQuestionType())){
				//go to question 1
				Intent intent = new Intent();
				intent.setClass( getBaseContext(), SingleChoices.class);
				startActivity(intent);
			}
		}else{
			ShowDialog("Can not get question!");
		}
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
				view = mInflater.inflate(R.layout.paper_single_choices_item, null);
				holder = new ViewHolder();
				
				//set 3 component 
				holder.radioButton = (RadioButton)view.findViewById(R.id.radioButton);
				holder.index = (TextView)view.findViewById(R.id.index);
				holder.choiceDesc = (TextView)view.findViewById(R.id.choiceDesc);
				
				final int p = position;
				map.put(position, view);
				
				holder.radioButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						
						RadioButton cb = (RadioButton)buttonView;
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
			holder.radioButton.setChecked(mChecked.get(position));
			holder.index.setText(String.valueOf(choice.getChoiceIndex()));
			holder.choiceDesc.setText(choice.getChoiceDesc());
			
			return view;
		}
    	
    }
    
    static class ViewHolder{
    	RadioButton radioButton;
    	TextView index;
    	TextView choiceDesc;
    }
}
