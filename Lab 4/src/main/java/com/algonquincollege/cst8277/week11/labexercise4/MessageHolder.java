/**
 * File: MessageHolder.java <br>
 * Course materials (21F) CST 8277
 * 
 * @author Teddy Yap
 * 
 * @author Shariar (Shawn) Emami
 * @date Mar 29, 2021
 * 
 * @author (original) Mike Norman
 * @date 2020 11
 */
package com.algonquincollege.cst8277.week11.labexercise4;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageHolder implements Serializable {
	private static final long serialVersionUID = 1L;

	public MessageHolder() {
		super();
		date = LocalDateTime.now();
	}

	protected String theMessage;
	protected String student;
	protected String studentNumber;

	protected LocalDateTime date;

	public MessageHolder( String theMessage) {
		this();
		this.theMessage = theMessage;
	}

	// make JSON field 'msg', not 'theMessage' - Java beans naming convention (again!)
	public String getMsg() {
		return theMessage;
	}

	public void setMsg( String msg) {
		this.theMessage = msg;
	}

	public String getStudent() {
		return student;
	}

	public void setStudent( String student) {
		this.student = student;
	}

	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber( String studentNumber) {
		this.studentNumber = studentNumber;
	}

	public MessageHolder setStudentDetails( String student, String studentNumber) {
		setStudent( student);
		setStudentNumber( studentNumber);
		return this;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate( LocalDateTime date) {
		this.date = date;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder( "(MessageHolder)msg = ").append( getMsg()).append( " [date = ")
				.append( getDate()).append( "]");
		return builder.toString();
	}
}