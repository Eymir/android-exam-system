package com.msxt.client.swing.panel;

import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.msxt.client.model.Examination;
import com.msxt.client.swing.component.QuestionButton;
import com.msxt.client.swing.model.ExamBuildContext;
import com.msxt.client.swing.utilities.GBC;

public class CatalogPanel extends JPanel{

	private static final long serialVersionUID = -5876976956518188963L;
	
	public CatalogPanel(Examination.Catalog ec, ExamBuildContext ebc){
		this.setLayout( new GridBagLayout() );
		
		List<Examination.Question> eqs = ec.getQuestions();
		add( new JLabel( ec.getIndex()+". "), new GBC(0, 0, 1, eqs.size()+1 ).setAnchor( GBC.NORTHEAST )  );
		add( new JLabel( ec.getDesc() ), new GBC(1, 0).setAnchor( GBC.WEST ).setInsets(0,0,10,0) );
		for( int i=0; i<eqs.size(); i++ ) {
			Examination.Question eq = eqs.get(i);			
			QuestionPanel qp = new SingleChoicePanel( eq );
			qp.setOpaque( false );
			add( qp, new GBC(1, i + 1).setWeight(100, 100).setInsets(0,0,10,0).setFill( GBC.HORIZONTAL ) );
			
			QuestionButton qb = ebc.getButton( eq );
			qb.getQuestion().setQuestionPanel( qp );
			qp.addStateChangeListener( qb );
		}
	}
}
