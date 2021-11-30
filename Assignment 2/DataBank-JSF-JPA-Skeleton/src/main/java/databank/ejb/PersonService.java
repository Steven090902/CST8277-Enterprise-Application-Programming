package databank.ejb;

import java.util.List;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import databank.model.PersonPojo;

@Singleton
public class PersonService {

	@PersistenceContext
	protected EntityManager em;
	
	
	
	public List< PersonPojo> readAllPeople() {
		//use the named JPQL query from the PersonPojo class to grab all the people
		TypedQuery< PersonPojo> allPeopleQuery = em.createNamedQuery( PersonPojo.PERSON_FIND_ALL, PersonPojo.class);
		//execute the query and return the result/s.
		return allPeopleQuery.getResultList();
	}
	

	@Transactional
	public PersonPojo createPerson( PersonPojo person) {
		em.persist( person);
		return person;
	}


	public PersonPojo readPersonById( int personId) {
		return em.find( PersonPojo.class, personId);
	}


	@Transactional
	public PersonPojo updatePerson( PersonPojo personWithUpdates) {
		PersonPojo mergedPersonPojo = em.merge( personWithUpdates);
		return null;
	}


	@Transactional
	public void deletePersonById( int personId) {
		PersonPojo person = readPersonById( personId);
		if ( person != null) {
			em.refresh( person);
			em.remove( person);
		}
	}


}