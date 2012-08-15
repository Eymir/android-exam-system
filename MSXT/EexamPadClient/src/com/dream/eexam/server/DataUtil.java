package com.dream.eexam.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.msxt.client.model.Examination;
import com.msxt.client.model.LoginSuccessResult;
import com.msxt.client.model.transfer.Message2ModelTransfer;
import com.msxt.client.server.ServerProxy.Result;
import com.msxt.client.server.ServerProxy.STATUS;
import com.msxt.client.server.WebServerProxy;

public class DataUtil {

	public static void main(String args[]){
		WebServerProxy proxy = new WebServerProxy("192.168.1.101",8080);
		Result result = proxy.login("test", "test");
		if(STATUS.SUCCESS.equals(result.getStatus())){
			System.out.println(result.getSuccessMessage());
		}else if(STATUS.ERROR.equals(result.getStatus())){
			System.out.println(result.getErrorMessage());
		}
		
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public Examination getExam(Result result) {
		DocumentBuilder db;
		ByteArrayInputStream is;
		Document doc = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			is = new ByteArrayInputStream(result.getSuccessMessage().getBytes());
			doc = db.parse(is);
			is.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = doc.getDocumentElement();
		Examination exam = Message2ModelTransfer.Factory.getInstance().parseExamination(root);
		return exam;
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public LoginSuccessResult getLoginSuccessResult(Result result) {
		DocumentBuilder db;
		ByteArrayInputStream is;
		Document doc = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			is = new ByteArrayInputStream(result.getSuccessMessage().getBytes());
			doc = db.parse(is);
			is.close();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element root = doc.getDocumentElement();
		LoginSuccessResult successResult = Message2ModelTransfer.Factory.getInstance().parseResult(root);
		return successResult;
	}	

}