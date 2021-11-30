/******************************************************
 * File: PersonDaoImpl.java Course materials (21F) CST8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 */
package databank.dao;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.sql.DataSource;

import databank.model.PersonPojo;


/**
 * Description: Implements the C-R-U-D API for the database
 */
@Named
@ApplicationScoped
public class PersonDaoImpl implements PersonDao, Serializable {
	/** explicitly set serialVersionUID */
	private static final long serialVersionUID = 1L;

	private static final String DATABANK_DS_JNDI = "java:app/jdbc/databank";
	private static final String READ_ALL = "select * from person";
	private static final String READ_PERSON_BY_ID = "select * from person where id = ?";
	private static final String INSERT_PERSON = "insert into person (first_name,last_name,email,phone,created) values(?,?,?,?,UNIX_TIMESTAMP())";
	private static final String UPDATE_PERSON_ALL_FIELDS = "update person set first_name = ?, last_name = ?, email = ?, phone = ? where id = ?";
	private static final String DELETE_PERSON_BY_ID = "delete from person where id = ?";

	@Inject
	protected ExternalContext externalContext;

	private void logMsg(String msg) {
		((ServletContext) externalContext.getContext()).log(msg);
	}

	@Resource(lookup = DATABANK_DS_JNDI)
	protected DataSource databankDS;

	protected Connection conn;
	protected PreparedStatement readAllPstmt;
	protected PreparedStatement readByIdPstmt;
	protected PreparedStatement createPstmt;
	protected PreparedStatement updatePstmt;
	protected PreparedStatement deleteByIdPstmt;

	@PostConstruct
	protected void buildConnectionAndStatements() {
		try {
			logMsg("building connection and stmts");
			conn = databankDS.getConnection();
			readAllPstmt = conn.prepareStatement(READ_ALL);
			createPstmt = conn.prepareStatement(INSERT_PERSON, RETURN_GENERATED_KEYS);
			// TODO initialize other PreparedStatements
			updatePstmt = conn.prepareStatement(UPDATE_PERSON_ALL_FIELDS);
			deleteByIdPstmt = conn.prepareStatement(DELETE_PERSON_BY_ID);
			readByIdPstmt = conn.prepareStatement(READ_PERSON_BY_ID);
		} catch (Exception e) {
			logMsg("something went wrong getting connection from database: " + e.getLocalizedMessage());
		}
	}

	@PreDestroy
	protected void closeConnectionAndStatements() {
		try {
			logMsg("closing stmts and connection");
			readAllPstmt.close();
			createPstmt.close();
			conn.close();
			// TODO close other PreparedStatements
			updatePstmt.close();
			deleteByIdPstmt.close();
			readByIdPstmt.close();
		} catch (Exception e) {
			logMsg("something went wrong closing stmts or connection: " + e.getLocalizedMessage());
		}
	}

	@Override
	public List<PersonPojo> readAllPeople() {
		logMsg("reading all People");
		List<PersonPojo> people = new ArrayList<>();
		try (ResultSet rs = readAllPstmt.executeQuery();) {

			while (rs.next()) {
				PersonPojo newPerson = new PersonPojo();
				newPerson.setId(rs.getInt("id"));
				newPerson.setFirstName(rs.getString("first_name"));
				// TODO complete the person initialization
				newPerson.setLastName(rs.getString("last_name"));
				newPerson.setEmail(rs.getString("email"));
				newPerson.setPhoneNumber(rs.getString("phone"));
				newPerson.setCreated(rs.getLong("created"));
				people.add(newPerson);
			}
		} catch (SQLException e) {
			logMsg("something went wrong accessing database: " + e.getLocalizedMessage());
		}
		return people;
	}

	@Override
	public PersonPojo createPerson(PersonPojo person) {
		logMsg("creating an person");
		// TODO complete
		try {
			createPstmt.setString(1, person.getFirstName());
			createPstmt.setString(2, person.getLastName());
			createPstmt.setString(3, person.getEmail());
			createPstmt.setString(4, person.getPhoneNumber());
			createPstmt.execute();
		} catch (SQLException e) {
			logMsg("something went wrong while creating a person: " + e.getLocalizedMessage());
		}

		return person;
	}

	@Override
	public PersonPojo readPersonById(int personId) {
		logMsg("read a specific person");
		// TODO complete
		PersonPojo personinfo = null;
		try {
			readByIdPstmt.setInt(1, personId);
			ResultSet rs = readByIdPstmt.executeQuery(); 
			while (rs.next()) {
				personinfo = new PersonPojo();
				personinfo.setId(rs.getInt("id"));
				personinfo.setFirstName(rs.getString("first_name"));
				personinfo.setLastName(rs.getString("last_name"));
				personinfo.setEmail(rs.getString("email"));
				personinfo.setPhoneNumber(rs.getString("phone"));
			}
		} catch (SQLException e) {
			logMsg("something went wrong while reading a specific person: " + e.getLocalizedMessage());
		}
		return personinfo;
	}

	@Override
	public void updatePerson(PersonPojo person) {
		logMsg("updating a specific person");
		// TODO complete
		try {
			updatePstmt.setString(1, person.getFirstName());
			updatePstmt.setString(2, person.getLastName());
			updatePstmt.setString(3, person.getEmail());
			updatePstmt.setString(4, person.getPhoneNumber());
			updatePstmt.setInt(5, person.getId());
			updatePstmt.executeUpdate();

		} catch (SQLException e) {
			logMsg("something went wrong while updating a person: " + e.getLocalizedMessage());
		}
	}

	@Override
	public void deletePersonById(int personId) {
		logMsg("deleting a specific person");
		// TODO complete
		try {
			deleteByIdPstmt.setInt(1, personId);
			deleteByIdPstmt.execute();
		}catch (SQLException e) {
			logMsg("something went wrong while deleting a person: " + e.getLocalizedMessage());
		}
		
	}
}