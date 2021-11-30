package bloodbank.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

@TestMethodOrder( MethodOrderer.OrderAnnotation.class)
public class TestCRUDAddress extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	private static Phone phone;
	private static Address address;
	private static Person person;
	private static Contact contact;
	private static final String EMAIL = "test@test.com";
	private static final String CONTACT_TYPE = "Home";

	@BeforeAll
	static void setupAllInit() {
		phone = new Phone();
		phone.setNumber( "0", "234", "5678900");

		address = new Address();
		address.setAddress( "123", "abcd Dr.W", "ottawa", "ON", "CA", "A1B2C3");

		person = new Person();
		person.setFullName( "Teddy", "Yap");
	}

	@BeforeEach
	void setup() {
		em = getEntityManager();
		et = em.getTransaction();
	}

	@AfterEach
	void tearDown() {
		em.close();
	}

	@Order( 1)
	@Test
	void testEmpty() {

		long result = getTotalCount(em, Address.class);
		assertThat( result, is( comparesEqualTo( 0L)));

	}

	@Order( 2)
	@Test
	void testCreate() {
		et.begin();
		
		contact = new Contact();
		contact.setAddress( address);
		contact.setPhone( phone);
		contact.setEmail( EMAIL);
		contact.setContactType( CONTACT_TYPE);
		contact.setOwner( person);
		em.persist( contact);
		et.commit();

		long result = getTotalCount(em, Address.class);

		// there should only be one row in the DB
		assertThat( result, is( greaterThanOrEqualTo( 1L)));
//		assertEquals( result, 1);
		
		result = getTotalCount(em, Person.class);
		assertThat( result, is( greaterThanOrEqualTo( 1L)));

		result = getTotalCount(em, Phone.class);
		assertThat( result, is( greaterThanOrEqualTo( 1L)));
		
	}

	@Order( 3)
	@Test
	void testCreateInvalid() {
		et.begin();
		Contact contactHome = new Contact();
		contactHome.setAddress( address);
//		contactHome.setPhone( phone);
		contactHome.setEmail( "test@test.com");
		contactHome.setContactType( "Home");
		contactHome.setOwner( person);
		// we expect a failure because phone is part of the composite key
		assertThrows( PersistenceException.class, () -> em.persist( contactHome));
		et.commit();
	}

	@Test
	void testRead() {
		/*
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// create query for Contact
		CriteriaQuery< Contact> query = builder.createQuery( Contact.class);
		// select c from Contact c
		Root< Contact> root = query.from( Contact.class);
		query.select( root);
		// create query and set the parameter
		TypedQuery< Contact> tq = em.createQuery( query);
		// get the result as row count
		List< Contact> contacts = tq.getResultList();
		*/
		List<Address> addresses = getAll(em,Address.class);

		assertThat( addresses, contains( equalTo( address)));
	}

	@Test
	void testReadDependencies() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// create query for Address
		CriteriaQuery< Address> query = builder.createQuery( Address.class);
		// select a from Address a
		Root< Address> root = query.from( Address.class);
		query.select( root);
		query.where( builder.equal( root.get( Address_.id), builder.parameter( Integer.class, "id")));
		// create query and set the parameter
		TypedQuery< Address> tq = em.createQuery( query);
		tq.setParameter( "id", address.getId());
		// get the result as row count
		Address returnedAddress = tq.getSingleResult();

		assertThat( returnedAddress.getStreetNumber(), equalTo(address.getStreetNumber()));
		assertThat( returnedAddress.getStreet(), equalTo(address.getStreet()));
		assertThat( returnedAddress.getCity(), equalTo(address.getCity()));
		assertThat( returnedAddress.getProvince(), equalTo(address.getProvince()));
		assertThat( returnedAddress.getCountry(), equalTo(address.getCountry()));
		assertThat( returnedAddress.getZipcode(), equalTo(address.getZipcode()));
	}

	@Test
	void testUpdate() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// create query for Address
		CriteriaQuery< Address> query = builder.createQuery( Address.class);
		// select a from Address a
		Root< Address> root = query.from( Address.class);
		query.select( root);
		query.where( builder.equal( root.get( Address_.id), builder.parameter( Integer.class, "id")));
		// create query and set the parameter
		TypedQuery< Address> tq = em.createQuery( query);
		tq.setParameter( "id", address.getId());
		// get the result as row count
		Address returnedAddress = tq.getSingleResult();

		String newStreetNumber = "999";
		String newZipcode = "H0H0H0";

		et.begin();
		returnedAddress.setStreetNumber( newStreetNumber);
		returnedAddress.setZipcode( newZipcode);
		em.merge( returnedAddress);
		et.commit();

		returnedAddress = tq.getSingleResult();

		assertThat( returnedAddress.getStreetNumber(), equalTo( newStreetNumber));
		assertThat( returnedAddress.getZipcode(), equalTo( newZipcode));
	}

	@Test
	void testUpdateDependencies() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// create query for Contact
		CriteriaQuery< Contact> query = builder.createQuery( Contact.class);
		// select c from Contact c where c.id=:id
		Root< Contact> root = query.from( Contact.class);
		query.select( root);
		query.where( builder.equal( root.get( Contact_.id), builder.parameter( ContactPK.class, "id")));
		// create query and set the parameter
		TypedQuery< Contact> tq = em.createQuery( query);
		tq.setParameter( "id", contact.getId());
		// get the result as row count
		Contact returnedContact = tq.getSingleResult();

		phone = returnedContact.getPhone();
		phone.setNumber( "9", "876", "5432100");

		address = returnedContact.getAddress();
		address.setAddress( "7654", "zxcv Dr.E", "Vancouver", "BS", "CA", "Z9Y8X7W");

		person = returnedContact.getOwner();
		person.setFullName( "Jack", "Jackson");

		et.begin();
		returnedContact.setAddress( address);
		returnedContact.setPhone( phone);
		returnedContact.setOwner( person);
		em.merge( returnedContact);
		et.commit();

		returnedContact = tq.getSingleResult();

		assertThat( returnedContact.getOwner(), equalTo( person));
		assertThat( returnedContact.getPhone(), equalTo( phone));
		assertThat( returnedContact.getAddress(), equalTo( address));
	}

	@Order( Integer.MAX_VALUE - 1)
	@Test
	void testDeleteDependecy() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// create query for Contact
		CriteriaQuery< Contact> query = builder.createQuery( Contact.class);
		// select c from Contact c
		Root< Contact> root = query.from( Contact.class);
		query.select( root);
		query.where( builder.equal( root.get( Contact_.id), builder.parameter( ContactPK.class, "id")));
		// create query and set the parameter
		TypedQuery< Contact> tq = em.createQuery( query);
		tq.setParameter( "id", contact.getId());
		// get the result as row count
		Contact returnedContact = tq.getSingleResult();

		int addressId = returnedContact.getAddress().getId();

		et.begin();
		returnedContact.setAddress( null);
		em.merge( returnedContact);
		et.commit();

		returnedContact = tq.getSingleResult();

		assertThat( returnedContact.getAddress(), is( nullValue()));

		// create query for long as we need the number of found rows
		CriteriaQuery< Long> query2 = builder.createQuery( Long.class);
		// select count(a) from Address a where a.id=:id
		Root< Address> root2 = query2.from( Address.class);
		query2.select( builder.count( root2));
		query2.where( builder.equal( root2.get( Address_.id), builder.parameter( Integer.class, "id")));
		// create query and set the parameter
		TypedQuery< Long> tq2 = em.createQuery( query2);
		tq2.setParameter( "id", addressId);
		// get the result as row count
		long result = tq2.getSingleResult();
		// because it can be null so it is not removed
		assertThat( result, is( equalTo( 1L)));
	}

	@Order( Integer.MAX_VALUE)
	@Test
	void testDelete() {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		// create query for Contact
		CriteriaQuery< Contact> query = builder.createQuery( Contact.class);
		// select c from Contact c
		Root< Contact> root = query.from( Contact.class);
		query.select( root);
		query.where( builder.equal( root.get( Contact_.id), builder.parameter( ContactPK.class, "id")));
		// create query and set the parameter
		TypedQuery< Contact> tq = em.createQuery( query);
		tq.setParameter( "id", contact.getId());
		// get the result as row count
		Contact returnedContact = tq.getSingleResult();

		et.begin();
		// add another row to db to make sure only the correct row is deleted
		Contact contactHome = new Contact();
		contactHome.setPhone( new Phone().setNumber( "2", "673", "9845385"));
		contactHome.setContactType( "Work");
		contactHome.setOwner( returnedContact.getOwner());
		em.persist( contactHome);
		et.commit();

		et.begin();
		em.remove( returnedContact);
		et.commit();

		// create query for long as we need the number of found rows
		CriteriaQuery< Long> query2 = builder.createQuery( Long.class);
		// select count(a) from Address a where a.id=:id
		Root< Contact> root2 = query2.from( Contact.class);
		query2.select( builder.count( root2));
		query2.where( builder.equal( root2.get( Contact_.id), builder.parameter( ContactPK.class, "id")));
		// create query and set the parameter
		TypedQuery< Long> tq2 = em.createQuery( query2);
		tq2.setParameter( "id", returnedContact.getId());
		// get the result as row count
		long result = tq2.getSingleResult();
		assertThat( result, is( equalTo( 0L)));

		// create query and set the parameter
		TypedQuery< Long> tq3 = em.createQuery( query2);
		tq3.setParameter( "id", contactHome.getId());
		// get the result as row count
		result = tq3.getSingleResult();
		
		assertThat( result, is( equalTo( 1L)));
	}
}
