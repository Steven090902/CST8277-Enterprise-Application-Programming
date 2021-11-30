package bloodbank.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.is;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import common.JUnitBase;

@SuppressWarnings("unused")

@TestMethodOrder( MethodOrderer.OrderAnnotation.class)
public class TestCRUDPhone extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	//TODO - useful static variables

	@BeforeAll
	static void setupAllInit() {
		//do something useful
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
	void testObvious() {
		long result = 0L;
		assertThat(result, is( comparesEqualTo( 0L)));
	}
}