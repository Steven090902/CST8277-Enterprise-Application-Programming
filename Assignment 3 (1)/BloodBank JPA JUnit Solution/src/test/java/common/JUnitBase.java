package common;

import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.Contact;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.Phone;

/**
 * super class for all junit tests, holds common methods for creating {@link EntityManagerFactory} and truncating the DB
 * before all.
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @version Mar 12, 2021
 */
public class JUnitBase {

	protected static final Logger LOG = LogManager.getLogger();

	/**
	 * default name of Persistence Unit = "bloodbank-PU"
	 */
	private static final String PERSISTENCE_UNIT = "bloodbank-PU";

	/**
	 * static instance of {@link EntityManagerFactory} for subclasses
	 */
	protected static EntityManagerFactory emf;

	/**
	 * create an instance of {@link EntityManagerFactory} using {@link JUnitBase#PERSISTENCE_UNIT}.<br>
	 * redirects to {@link JUnitBase#buildEMF(String)}.
	 * 
	 * @return an instance of EntityManagerFactory
	 */
	protected static EntityManagerFactory buildEMF() {
		return buildEMF( PERSISTENCE_UNIT);
	}

	/**
	 * create an instance of {@link EntityManagerFactory} using provided Persistence Unit name.
	 * 
	 * @return an instance of EntityManagerFactory
	 */
	protected static EntityManagerFactory buildEMF( String persistenceUnitName) {
		Objects.requireNonNull( persistenceUnitName, "Persistence Unit name cannot be null");
		if ( persistenceUnitName.isBlank()) {
			throw new IllegalArgumentException( "Persistence Unit name cannot be empty or just white space");
		}
		return Persistence.createEntityManagerFactory( PERSISTENCE_UNIT);
	}

	/**
	 * create a new instance of {@link EntityManager}.<br>
	 * must call {@link JUnitBase#buildEMF()} or {@link JUnitBase#buildEMF(String)} first.
	 * 
	 * @return an instance of {@link EntityManager}
	 */
	protected static EntityManager getEntityManager() {
		if ( emf == null) {
			throw new IllegalStateException( " EntityManagerFactory is null, must call JUnitBase::buildEMF first");
		}
		return emf.createEntityManager();
	}

	/**
	 * Delete all Entities. Order of delete matters.
	 */
	protected static void deleteAllData() {
		EntityManager em = getEntityManager();

		// TODO JB01 - begin transaction and truncate all tables. order matters.
		EntityTransaction et = em.getTransaction();

		et.begin();
		
		deleteAllFrom(Contact.class, em);
		deleteAllFrom(Phone.class, em);
		deleteAllFrom(Address.class, em);
		deleteAllFrom(DonationRecord.class, em);
		deleteAllFrom(BloodDonation.class, em);
		deleteAllFrom(BloodBank.class, em);
		deleteAllFrom(Person.class, em);
		
		et.commit();

	}

	/**
	 * Delete all instances of provided type form the DB. Same operation as Truncate.
	 * 
	 * @see <a href = "https://stackoverflow.com/questions/23269885/truncate-delete-from-given-the-entity-class">
	 *      StackOverflow: Truncate with JPA</a>
	 * @param <T>        - Type of entity to delete, can be inferred by JVM when method is being executed.
	 * @param entityType - class type of entity, like Address.class
	 * @param em         - EntityManager to be used
	 * @return the number of entities updated or deleted
	 */
	public static < T> int deleteAllFrom( Class< T> entityType, EntityManager em) {
		// TODO JB03 - using CriteriaBuilder create a CriteriaDelete to execute a truncate on DB.
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaDelete<T> query = cb.createCriteriaDelete(entityType);
		query.from(entityType);
		em.createQuery(query).executeUpdate();

		return -1;
	}

	protected static < T> long getTotalCount( EntityManager em, Class< T> clazz) {
		// TODO JB04 - optional helper method. create a CriteriaQuery here to be reused in your tests.
		// method signature is just a suggestion it can be modified if need be.
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery< Long> query = builder.createQuery( Long.class);
		Root<T> root = query.from(clazz);
		query.select( builder.count( root));
		TypedQuery< Long> tq = em.createQuery( query);
		long result = tq.getSingleResult();

		return result;
	}

	protected static < T> List< T> getAll( EntityManager em, Class< T> clazz) {
		// TODO JB05 - optional helper method. create a CriteriaQuery here to be reused in your tests.
		// method signature is just a suggestion it can be modified if need be.
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery( clazz);
		Root< T> root = query.from( clazz);
		query.select( root);
		TypedQuery< T> tq = em.createQuery( query);
		List< T> results = tq.getResultList();

		//return null;
		return results;
	}

	protected static < T, R> T getWithId( EntityManager em, Class< T> clazz, Class< R> classPK,
			SingularAttribute< ? super T, R> sa, R id) {
		// TODO JB06 - optional helper method. create a CriteriaQuery here to be reused in your tests.
		// method signature is just a suggestion it can be modified if need be.
		
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		query.select(root).where(builder.equal(root.get(sa), builder.parameter(classPK.getClass(), "id")));
		TypedQuery<T> tq = em.createQuery(query);
		tq.setParameter("id", id);
		List<T> results = tq.getResultList();

		// return null;
		if (results != null)
			return results.get(0);
		else
			return null;
	}

	protected static < T, R> long getCountWithId( EntityManager em, Class< T> clazz, Class< R> classPK,
			SingularAttribute< ? super T, R> sa, R id) {
		// TODO JB07 - optional helper method. create a CriteriaQuery here to be reused in your tests.
		// method signature is just a suggestion it can be modified if need be.
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(clazz);
		Root<T> root = query.from(clazz);
		query.select(root).where(builder.equal(root.get(sa), builder.parameter(classPK.getClass(), "id")));
		TypedQuery<T> tq = em.createQuery(query);
		tq.setParameter("id", id);
		List<T> results = tq.getResultList();

		// return -1;
		if (results != null)
			return results.size();
		else
			return -1;
	}

	@BeforeAll
	static void setupAll() {
		emf = buildEMF();
		deleteAllData();
	}

	@AfterAll
	static void tearDownAll() {
		emf.close();
	}
}
