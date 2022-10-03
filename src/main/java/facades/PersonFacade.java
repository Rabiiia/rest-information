package facades;

import dtos.PersonDTO;
import entities.Person;
import entities.Phone;
import errorhandling.EntityNotFoundException;
import errorhandling.InternalErrorException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

/**
 * A facade class that manages {@link javax.persistence.Entity entities} and
 * {@link javax.persistence.EntityTransaction transactions} –
 * {@link EntityManager#persist creates},
 * {@link EntityManager#find reads},
 * {@link EntityManager#merge updates} and
 * {@link EntityManager#remove deletes} entities (CRUD operations)
 * and commits the transaction to the database.
 * <p>
 * Only one instance of this facade can be constructed
 * and is returned by {@link #getInstance}.
 */
public class PersonFacade {
    private static PersonFacade INSTANCE;
    private static EntityManagerFactory EMF;

    /** A private constructor to ensure the class only can be accessed by {@link #getInstance}. */
    private PersonFacade() {
    }

    /**
     * Returns a {@link PersonFacade} object that can be used to
     * {@link EntityManager#persist create},
     * {@link EntityManager#find read},
     * {@link EntityManager#merge update} and
     * {@link EntityManager#remove delete} entities (CRUD operations)
     * and commit the transactions to the database.
     * <p>
     * Only one instance of this facade can be constructed,
     * and only this method can return that instance.
     *
     * @param emf an {@link javax.persistence.EntityManagerFactory} used to create the
     *            {@link javax.persistence.EntityManager} that manage the
     *            {@link javax.persistence.Entity entities} and
     *            {@link javax.persistence.EntityTransaction transactions} within the facade.
     * @return    the only allowed instance of {@link PersonFacade}.
     */
    public static PersonFacade getInstance(EntityManagerFactory emf) {
        if (INSTANCE == null) {
            EMF = emf;
            INSTANCE = new PersonFacade();
        }
        return INSTANCE;
    }

    private EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Creates a new {@link Person} from a {@link PersonDTO} and commits a transaction
     * to the database.
     *
     * @param dto a {@link PersonDTO} with data about a person.
     * @see EntityManager#persist
     */
    public void createPerson(PersonDTO dto) {
        // Convert DTO to entity
        Person person = new Person(dto);

        // Create entity manager to begin transaction
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            // Persist address
            em.persist(person.getAddress());
            // Persist person (using the address id generated above)
            em.persist(person);
            // Persist phones (using the person id generated above)
            for (Phone phone : person.getPhones()) {
                phone.setPerson(person);
                em.persist(phone);
            }
            em.getTransaction().commit();
        }
        // catch (Exception e) {
        //     throw e;
        // }
        finally {
            em.close();
        }
    }

    /**
     * Searches for a {@link Person} with the given {@code id} in the database and
     * returns a {@link PersonDTO} with the read data.
     *
     * @param id the person's id.
     * @return a {@link PersonDTO} with data about a person.
     * @see      EntityManager#find
     */
    public PersonDTO getPersonById(int id) {
        // Create entity manager to find entity
        EntityManager em = EMF.createEntityManager();
        try {
            Person person = em.find(Person.class, id);
            // Convert entity to DTO
            return new PersonDTO(person);
        }
        // catch (Exception e) {
        //     throw e;
        // }
        finally {
            em.close();
        }
    }


    public void editPerson(PersonDTO personDTO)throws EntityNotFoundException, InternalErrorException{

        EntityManager em = EMF.createEntityManager();

        try{
            Person oldPerson = em.find(Person.class,personDTO.getId());
           // Address newAddress = em.find(Address)

            if(oldPerson == null){
                throw new EntityNotFoundException("Could not find a person with the provided id " + personDTO.getId());
            }

            oldPerson.setFirstName(personDTO.getFirstName());
            oldPerson.setLastName(personDTO.getLastName());
            oldPerson.setEmail(personDTO.getEmail());
            //if(personDTO.getAddress() =! null)
            //oldPerson.setAddress();


            em.getTransaction().begin();
            em.merge(oldPerson);
            em.getTransaction().commit();

        }
        catch (PersistenceException e){
            throw new InternalErrorException("Internal Server Problem. We are sorry for the inconvenience");
        }
        finally {
            em.close();
        }
    }

}
