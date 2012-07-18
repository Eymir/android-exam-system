package com.msxt.examination;

import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.stream.XMLStreamException;

import com.msxt.model.Examination;
import com.msxt.model.ExaminationCatalog;
import com.msxt.model.ExaminationQuestion;
import com.msxt.model.Question;

@Named
@RequestScoped
public class ExamAgent {
	@PersistenceContext
    private EntityManager em;
	
	@EJB
	private CatalogParser cp;
	
	private Examination selectedExam = new Examination();
	
	private String catalogsXML;
	
	public void selectExamination(String id){
		/*
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Examination> cq = cb.createQuery( Examination.class );
		Root<Examination> root = cq.from( Examination.class );
		root.join( Examination_.catalogs );
		cq.select( root )
	 	  .where( cb.equal( root.get(Examination_.id), id) );
		
		selectedExam = em.createQuery( cq ).getResultList().get(0);
		*/
		selectedExam = em.find( Examination.class, id );
		//Load lazy data
		for( ExaminationCatalog ec : selectedExam.getCatalogs() )
			ec.getQuestions().get(0).getQuestion();
	}
	
	public void selectExamination4View(String id){
		selectedExam = em.find( Examination.class, id );
		//Load lazy data
		for( ExaminationCatalog ec : selectedExam.getCatalogs() ) 
			for( ExaminationQuestion eq : ec.getQuestions() ) {
				Question q = eq.getQuestion();
				q.getChoiceItems().get(0);
				String htmlContent = q.getContent().replaceAll("&", "&amp;")
												   .replaceAll(" ",  "&nbsp;")
									               .replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;")
									    		   .replaceAll("<",  "&lt;")
									    		   .replaceAll(">",  "&gt;")
									    		   .replaceAll("\n", "<br/>")
									    		   .replaceAll("\"", "&quot;");
				q.setContent( htmlContent );
				em.detach( q );
			}
	}
	
	public String save() throws XMLStreamException {
		Examination se = em.find( Examination.class, selectedExam.getId() );
		for( ExaminationCatalog ec : se.getCatalogs() )
			em.remove( ec );
		
		se.getCatalogs().clear();
		se.setName( selectedExam.getName() );
		se.setTime( selectedExam.getTime() );
		
		List<ExaminationCatalog> ecs = cp.parse( catalogsXML );
		for( ExaminationCatalog ec : ecs ) {
			ec.setExam( se );
			se.getCatalogs().add( ec );
		}
		em.persist( se );
		
		return "search";
	}
	
	public Examination getSelectedExam() {
		return selectedExam;
	}

	public String getCatalogsXML() {
		return catalogsXML;
	}

	public void setCatalogsXML(String catalogsXML) {
		this.catalogsXML = catalogsXML;
	}
}