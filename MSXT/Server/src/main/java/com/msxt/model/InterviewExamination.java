package com.msxt.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class InterviewExamination {
	@Id
	@Column(name="ID")
	@GenericGenerator(name="uuidGG",strategy="uuid")
	@GeneratedValue(generator="uuidGG")
	private String id;
	
	@Version
	private Integer version;
	
	@ManyToOne
	@JoinColumn(name="interview_id")
	private Interview interview;
	
	@ManyToOne
	@JoinColumn(name="exam_id")
	private Examination exam;
	
	@OneToMany(mappedBy = "interviewExamination", cascade = CascadeType.REMOVE)
	private List<ExaminationQuestionAnswer> examQuestionAnswers;
	
	@Column(name="exam_score")
	private int examScore;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Interview getInterview() {
		return interview;
	}
	public void setInterview(Interview interview) {
		this.interview = interview;
	}
	public Examination getExam() {
		return exam;
	}
	public void setExam(Examination exam) {
		this.exam = exam;
	}
	
	public int getExamScore() {
		return examScore;
	}
	
	public void setExamScore(int examScore) {
		this.examScore = examScore;
	}
	
	public List<ExaminationQuestionAnswer> getExamQuestionAnswers() {
		return examQuestionAnswers;
	}
	
	public void setExamQuestionAnswers(
			List<ExaminationQuestionAnswer> examQuestionAnswers) {
		this.examQuestionAnswers = examQuestionAnswers;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	public Integer getVersion() {
		return version;
	}
}
