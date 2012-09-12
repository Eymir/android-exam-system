package com.dream.eexam.paper;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.dream.eexam.base.R;
import com.dream.eexam.server.DataUtil;
import com.msxt.client.model.Examination.Question;

public class PendQuestions extends BaseQuestion {
	
	public final static String LOG_TAG = "MultiChoices";

	GridView gridList;
	
    String qType = null;
    String qid = null;
	
//	Button preBtn;
//	TextView questionIndex;
//	Button nextBtn;
	
	//data statement
	MyListAdapter adapter;
	List<String> listItemID = new ArrayList<String>();
	Integer indexInExam;
	
	public void loadComponents(){
//		homeTV = (TextView)findViewById(R.id.homeTV);
//		remainingTimeLabel = (TextView)findViewById(R.id.remainingTimeLabel);
		remainingTime = (TextView)findViewById(R.id.remainingTime);
		submitTV = (TextView)findViewById(R.id.submitTV);
		
//		questionIndex = (TextView)findViewById(R.id.questionIndex);
		catalogsTV = (TextView)findViewById(R.id.header_tv_catalogs);
		pendQueNumber = (TextView)findViewById(R.id.pendQueNumber);
		
		backArrow = (ImageView)findViewById(R.id.backArrow);
		completedSeekBar = (SeekBar) findViewById(R.id.completedSeekBar);
		completedPercentage = (TextView)findViewById(R.id.completedPercentage);   	
    	nextArrow = (ImageView)findViewById(R.id.nextArrow);
	}
	
	public void setHeader(){
		//set exam header(Left)
//		homeTV.setText("Home");
		
		//set exam header(Center)
//		remainingTimeLabel.setText("Time Remaining: ");
		remainingTime.setText(String.valueOf(exam.getTime())+" mins");
		
		//set exam header(Right)
		submitTV.setText("Submit");
        submitTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(LOG_TAG, "submitTV.onClick()...");
				
		    	int waitQuestions = examQuestionSum - examAnsweredQuestionSum;
				 
				if (waitQuestions> 0) {
					AlertDialog.Builder builder = new AlertDialog.Builder(PendQuestions.this);
					builder.setMessage(String.valueOf(waitQuestions) + " question(s) are not answered, still submit?")
							.setCancelable(false)
							.setPositiveButton("Yes",
									new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog,int id) {
											submitAnswer();
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
					submitAnswer();
				}
			}
		});

        //set catalog bar(Center) 
		catalogsTV.setText(String.valueOf(cCatalogIndex)+". "+
				cCatalog.getDesc() + 
				"(Q" + String.valueOf(cQuestionIndex)+" - " + "Q" + String.valueOf(cQuestionIndex+queSumOfCCatalog-1)+")");
		catalogsTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showWindow(v);
			}
		});
		
		 //set catalog bar(Right) 
		pendQueNumber.setText("Pending("+Integer.valueOf(pendQuestions.size())+")");
		pendQueNumber.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("ccIndex", String.valueOf(cCatalogIndex));
				intent.putExtra("cqIndex", String.valueOf(cQuestionIndex));
				if(questionTypes[0].equals(cQuestionType)){
					intent.putExtra("questionType", "Multi Select");
					intent.setClass( getBaseContext(), PendQuestions.class);
				}else if(questionTypes[1].equals(cQuestionType)){
					intent.putExtra("questionType", "Single Select");
					intent.setClass( getBaseContext(), PendQuestions.class);
				}
				finish();
				startActivity(intent);
			}
		});
	}
	

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_questions);
        mContext = getApplicationContext();
        loadComponents();
        setHeader();
        //set List
        gridList = (GridView)findViewById(R.id.gridview);
        adapter = new MyListAdapter(pendQuestions);
        gridList.setAdapter(adapter);
        gridList.setOnItemClickListener(new OnItemClickListener(){
        	@Override
			public void onItemClick(AdapterView<?> adapter, View view, int arg2,
					long arg3) {

			}      	
        });
        setFooter();
    }
    
    public void setFooter(){
    	//set preBtn
    	backArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				moveDirect = -1;
				gotoNewQuestion(mContext,cCatalogIndex,cQuestionIndex,moveDirect);
			}
		});
        
		//set completedSeekBar
		int per = 100 * examAnsweredQuestionSum/examQuestionSum;
		completedSeekBar.setThumb(null);
		completedSeekBar.setProgress(per);
		completedSeekBar.setEnabled(false);
		//set completedSeekBar label
		completedPercentage.setText(String.valueOf(per)+"%");
		
		
		//set nextBtn
		nextArrow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				moveDirect = 1;
				gotoNewQuestion(mContext,cCatalogIndex,cQuestionIndex,moveDirect);
			}
		});
    }
    
    class MyListAdapter extends BaseAdapter{
    	List<Question> questions = new ArrayList<Question>();
    	private LayoutInflater mInflater;
    	
    	public MyListAdapter(List<Question> questions){
    		this.questions = questions;
    		mInflater = LayoutInflater.from(mContext);  
    	}

		@Override
		public int getCount() {
			return questions.size();
		}

		@Override
		public Object getItem(int position) {
			return questions.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Log.i(LOG_TAG,"getView()..."+" position="+position);
        	
			ViewHolder holder;  
            if (convertView == null){  
                holder = new ViewHolder();  
                convertView = mInflater.inflate(R.layout.pending_questions_item, null);  
                holder.questionBtn = (Button) convertView.findViewById(R.id.questionBtn);  
                convertView.setTag(holder);  
            }else {  
                holder = (ViewHolder) convertView.getTag();  
            }  
            
            Question question = questions.get(position);
//            qType = question.getType();
            qid = question.getId();
            
            holder.questionBtn.setText(String.valueOf(DataUtil.getQuestionExamIndex(exam, qid)));  
            holder.questionBtn.setOnClickListener(new Button.OnClickListener() {
    			public void onClick(View v) {
    				Log.i(LOG_TAG,"onClick()...");
    				
    				Button sButton = (Button)v;
    	            String indexInExam = sButton.getText().toString();
    	            Question nQuestion = DataUtil.getQuestionByIndexInExam(exam, Integer.valueOf(indexInExam));
    	            
    				//move question
    				Intent intent = new Intent();
    				intent.putExtra("ccIndex", String.valueOf(DataUtil.getCidByQid(exam, nQuestion.getId())));
    				intent.putExtra("cqIndex", String.valueOf(nQuestion.getIndex()));
    				intent.putExtra("questionType", nQuestion.getType());
    				
    				if(questionTypes[0].equals(nQuestion.getType())){
    					intent.setClass( mContext, MultiChoices.class);
    				}else if(questionTypes[1].equals(nQuestion.getType())){
    					intent.setClass( mContext, SingleChoices.class);
    				}else{
    					ShowDialog("Invalid qeustion type:"+cQuestionType);
    				}
    				finish();
    				startActivity(intent);
    			}
    		});
            return convertView; 
		}
    	
    }
    
    static class ViewHolder{
    	Button questionBtn;
    }
}