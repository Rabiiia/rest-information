package facades;

import dtos.PersonDTO;
import dtos.PhoneDTO;
import entities.Person;
import entities.Phone;
import errorhandling.EntityNotFoundException;
import errorhandling.InternalErrorException;
import utils.FacadeUtility;
import utils.StatusCode;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A facade class that manages {@link javax.persistence.Entity entities} and
 * {@link javax.persistence.EntityTransaction transactions} –
 * {@link EntityManager#persist creates},
 * {@link EntityManager#find reads},
 * {@link EntityManager#merge updates} and
 * {@link EntityManager#remove deletes} entities (CRUD operations)
 * and commits these transactions to a database.
 * <p>
 * Only one instance of this facade can be constructed
 * and is returned by {@link #getInstance}.
 */
public class PhoneFacade {
    private static PhoneFacade INSTANCE;
    private static EntityManagerFactory EMF;
    private static FacadeUtility UTIL;
    private static StatusCode RESPONSE_CODE;
    private static final boolean CHECK_OWNERSHIP = true;

    /**
     * The default constructor is set private to ensure the class only can be accessed
     * via {@link #getInstance}.
     */
    private PhoneFacade() {
    }

    /**
     * Returns a {@link PhoneFacade} object that can be used to
     * {@link EntityManager#persist create},
     * {@link EntityManager#find read},
     * {@link EntityManager#merge update} and
     * {@link EntityManager#remove delete} entities (CRUD operations)
     * and commit these transactions to the database.
     * <p>
     * Only one instance of this facade can be constructed,
     * and only this method can return that instance.
     *
     * @param emf an {@link javax.persistence.EntityManagerFactory} used to create the
     *            {@link javax.persistence.EntityManager} that manages the
     *            {@link javax.persistence.Entity entities} and
     *            {@link javax.persistence.EntityTransaction transactions} within the facade.
     * @return    the only allowed instance of {@link PhoneFacade}.
     */
    public static PhoneFacade getInstance(EntityManagerFactory emf) {
        if (INSTANCE == null) {
            EMF = emf;
            UTIL = FacadeUtility.getInstance(EMF);
            INSTANCE = new PhoneFacade();
        }
        return INSTANCE;
    }

    private EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Returns a {@link Set} of every {@link Phone} in the database.
     *
     * @return {@link Set}.
     * @see TypedQuery
     */
    public Set<PhoneDTO> getPhones() throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Phone> personQuery = em.createQuery("SELECT t FROM Phone t", Phone.class);
        Set<Phone> phones = new LinkedHashSet<>(personQuery.getResultList());
        // if (persons.size() == 0) {
        //   throw new EntityNotFoundException("There are currently no persons in the database.");
        // }
        Set<PhoneDTO> phoneDTOs = new LinkedHashSet<>();
        for (Phone phone : phones) {
            phoneDTOs.add(new PhoneDTO(phone));
        }
        return phoneDTOs;
    }

    /**
     * Removes a {@link Phone} with a given {@code number} from the database.
     *
     * @param number a phone number.
     * @throws EntityNotFoundException if no phone is found.
     * @see EntityManager#find
     * @see EntityManager#remove
     */
    public void removePhone(int number) throws EntityNotFoundException, InternalErrorException {
        // Find phone in person table
        Phone phone = UTIL.phoneExists(number);
        // Create entity manager to remove address
        EntityManager em = EMF.createEntityManager();
        try {
            // Begin entity transaction
            em.getTransaction().begin();
            // Remove phone from phone table
            em.remove(phone);
            // Commit entity transaction to database
            em.getTransaction().commit();
        }
        catch (PersistenceException e){
            throw new InternalErrorException(e.getMessage());
        }
        finally {
            em.close();
        }
    }
}
