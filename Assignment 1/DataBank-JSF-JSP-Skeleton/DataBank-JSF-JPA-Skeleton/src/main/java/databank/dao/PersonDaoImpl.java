/*****************************************************************
 * File: PersonPojo.java Course materials (21F) CST8277
 *
 * @author Teddy Yap
 * @author Shahriar (Shawn) Emami
 * @author (original) Mike Norman
 */
package databank.dao;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import databank.ejb.PersonService;
import databank.model.PersonPojo;

/**
 * Description: Implements the C-R-U-D API for the database
 * 
 * TODO 01 - some components are managed by CDI.<br>
 * TODO 02 - methods which perform DML need @Transactional annotation.<br>
 * TODO 03 - fix the syntax errors to correct methods. <br>
 * TODO 04 - refactor this class. move all the method bodies and EntityManager to a new service class which is a
 * singleton (EJB).<br>
 * TODO 05 - inject the service class using EJB.<br>
 * TODO 06 - call all the methods of service from each appropriate method here.
 */
@Named
@ApplicationScoped
public class PersonDaoImpl implements PersonDao, Serializable {
	/** explicitly set serialVersionUID */
	private static final long serialVersionUID = 1L;

	//get the log4j2 logger for this class
	private static final Logger LOG = LogManager.getLogger();

	@EJB
	protected PersonService personservice;
	
	
	@Override
	public List< PersonPojo> readAllPeople() {
		LOG.debug( "reading all people");
		return personservice.readAllPeople();
	}
	
	@Override
	public PersonPojo createPerson( PersonPojo person) {
		LOG.debug( "creating a person = {}", person);
		return personservice.createPerson(person);
	}

	@Override
	public PersonPojo readPersonById( int personId) {
		LOG.debug( "read a specific person = {}", personId);
		return personservice.readPersonById(personId);
	}

	@Override
	public PersonPojo updatePerson( PersonPojo personWithUpdates) {
		LOG.debug( "updating a specific person = {}", personWithUpdates);
		return personservice.updatePerson(personWithUpdates);
	}

	@Override
	public void deletePersonById( int personId) {
		LOG.debug( "deleting a specific personID = {}", personId);
		PersonPojo personDeleted = readPersonById( personId);
		LOG.debug( "deleting a specific person = {}", personDeleted);
		personservice.deletePersonById(personId);
		

	}

}