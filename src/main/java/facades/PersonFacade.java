package facades;

import dtos.PersonDTO;
import entities.Address;
import entities.Person;
import entities.Phone;
import errorhandling.EntityFoundException;
import errorhandling.EntityNotFoundException;
import errorhandling.InternalErrorException;

import javax.persistence.*;
import java.util.Objects;

/**
 * A facade class that manages {@link javax.persistence.Entity entities} and
 * {@link javax.persistence.EntityTransaction transactions} –
 * {@link EntityManager#persist creates},
 * {@link EntityManager#find reads},
 * {@link EntityManager#merge updates} and
 * {@link EntityManager#remove deletes} entities (CRUD operations)
 * and commits the transactions to the database.
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
     *            {@link javax.persistence.EntityManager} that manages the
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
     * Persists a new {@link Person} to the database.
     *
     * @param dto a {@link PersonDTO} with data about the person.
     * @see EntityManager#persist
     */
    public void createPerson(PersonDTO dto) throws InternalErrorException {
        // Convert DTO to entity
        Person person = new Person(dto);

        // Create entity manager to persist person
        EntityManager em = EMF.createEntityManager();
        try {
            // Begin entity transaction
            em.getTransaction().begin();
            // Persist address first (address has no foreign keys!) ... if it doesn't already exist
            persistOrSetAddress(em, person);
            em.persist(person.getAddress());
            // Persist person (using the foreign address id generated above)
            em.persist(person);
            // Persist phones (using the foreign person id generated above)
            for (Phone phone : person.getPhones()) {
                // Set the foreign key!
                phone.setPerson(person);
                // Persist phone (with the foreign person id set above)
                em.persist(phone);
            }
            // Commit entity transaction to database
            em.getTransaction().commit();
        }
        catch (PersistenceException e) {
            throw new InternalErrorException(e.getMessage());
        }
        finally {
            em.close();
        }
    }

    /**
     * Finds a {@link Person} with a given {@code id} in the database.
     *
     * @param id a person id.
     * @see EntityManager#find
     */
    public PersonDTO getPersonById(int id)  throws EntityNotFoundException, InternalErrorException {
        // Create entity manager to find person
        EntityManager em = EMF.createEntityManager();
        try {
            // Find person in the person table
            Person person = em.find(Person.class, id);
            if (person == null) {
                throw new EntityNotFoundException("Person with id " + id + " not found!");
            }

            // TODO: If we keep this method, it needs to find the missing information for the DTO

            // Convert entity to DTO
            return new PersonDTO(person);
        }
        catch (PersistenceException e) {
            throw new InternalErrorException(e.getMessage());
        }
        finally {
            em.close();
        }
    }

    /**
     * Finds a {@link Person} with a given {@code number} in the database.
     *
     * @param number a phone number belonging to the person.
     * @see EntityManager#find
     */
    public PersonDTO getPersonByNumber(int number)  throws EntityNotFoundException, InternalErrorException {
        // Create entity manager to find entity
        EntityManager em = EMF.createEntityManager();
        try {
            // Find the number in the phone table
            Phone phoneToFind = em.find(Phone.class, number);
            if (phoneToFind == null) {
                throw new EntityNotFoundException("Number " + number + " not found!");
            }
            // Find the associated person in the person table (using the found person id found above)
            Person person = em.find(Person.class, phoneToFind.getPerson().getId());
            /*if (person == null) {
                // This code should be unnecessary, as our database ensures that a phone cannot exist without a person
                throw new EntityNotFoundException("Person with number " + number + " not found!");
            }*/
            // Make sure to find all the phones associated with person (not just the one we looked up)
            TypedQuery<Phone> query = em.createQuery("SELECT ph FROM Phone ph WHERE ph.person.id = :pid", Phone.class);
            query.setParameter("pid", person.getId());
            for (Phone phone : query.getResultList())
                person.addPhone(phone);
            // Convert entity to DTO
            return new PersonDTO(person);
        }
        catch (PersistenceException e) {
            throw new InternalErrorException(e.getMessage());
        }
        finally {
            em.close();
        }
    }

    /**
     * Merges an updated {@link Person} with an existing {@link Person} in the database.
     *
     * @param dto a {@link PersonDTO} with data about the person.
     * @see EntityManager#merge
     */
    public void updatePerson(PersonDTO dto) throws EntityNotFoundException, EntityFoundException, InternalErrorException {
        // Convert DTO to entity
        Person person = new Person(dto);

        // Create an entity manager to merge updates into a person
        EntityManager em = EMF.createEntityManager();
        try {
            // Find the UNALTERED person in the person table (to see if it exists)
            Person personFound = em.find(Person.class, dto.getId());
            if (personFound == null) {
                // If the person doesn't exist, DO NOT UPDATE! (not possible anyway)
                throw new EntityNotFoundException("Could not find a person with the provided id " + dto.getId());
            }
            // Begin entity transaction
            em.getTransaction().begin();
            // Persist address first (address has no foreign keys!) ... if it doesn't already exist
            persistOrSetAddress(em, person);
            // Find the new phones in the phone table
            for (Phone phone : person.getPhones()) {
                // Find the new phone numbers in the phone table
                Phone phoneFound = em.find(Phone.class, phone.getNumber());
                if (phoneFound != null) {
                    // If it already exists and belongs to someone else...
                    if (!Objects.equals(phoneFound.getPerson().getId(), person.getId())) {
                        // we don't want to change the ownership of that phone number
                        throw new EntityFoundException(phone.getNumber() + " already exists!");
                    }
                    // If it exists, but it belongs to us, we don't want to persist it (again)
                    continue;
                }
                // If the phone number does not exist in the phone table:
                // Add the foreign person id to the phone (phone id cannot be null!)
                phone.setPerson(person);
                // Persist phone
                em.persist(phone);// <-- Has to be inside a transaction
            }
            // Merge the altered entity into the existing entity
            em.merge(person);// <-- Has to be inside a transaction
            // Commit entity transaction to database
            em.getTransaction().commit();
        }
        catch (PersistenceException e) {
            throw new InternalErrorException(e.getMessage());
        }
        finally {
            em.close();
        }
    }

    /**
     * Unsets an address id for a {@link Person} in the database.
     *
     * @param id a person id.
     * @see Person#removeAddress
     */
    public void removeAddressFromPerson(int id) throws EntityNotFoundException, InternalErrorException {
        // Create entity manaer to remove address
        EntityManager em = EMF.createEntityManager();
        try {
            // Find person in the person table
            Person person = em.find(Person.class, id);
            if (person == null) {
                throw new EntityNotFoundException("Could not find a person with the provided id " + id);
            }
            // Begin entity transaction
            em.getTransaction().begin();
            Address address = person.getAddress();// <-- Before removing, store address in a variable for later
            // 1.: Remove foreign address id from person (set address_id = null)
            person.removeAddress();
            // 2.: Find all (if any) persons living at the address
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.address.id = :aid", Person.class);
            query.setParameter("aid", address.getId());
            if (query.getResultList().size() == 0) {
                // If no persons live at this address, remove the address from the address table
                em.remove(address);
            }
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

    /**
     * Removes an {@link Address} from the database.
     *
     * @param id a person id.
     * @see EntityManager#remove
     */
    public void deleteAddress(int id) throws EntityNotFoundException, EntityFoundException, InternalErrorException {
        // Create entity manaer to remove address
        EntityManager em = EMF.createEntityManager();
        try {
            // Find address in the address table
            Address address = em.find(Address.class, id);
            if (address == null) {
                throw new EntityNotFoundException("The address already does not exist!");
            }
            // Begin entity transaction
            em.getTransaction().begin();
            // Find all (if any) persons living at the address
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.address.id = :aid", Person.class);
            query.setParameter("aid", address.getId());
            if (query.getResultList().size() > 0) {
                // If one or more persons live at the address, DO NOT DELETE! (Not possible anyway)
                throw new EntityFoundException("One or more persons live at this address!");
            }
            // If no person lives at the address, remove the address from the address table
            em.remove(address);
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

    public void persistOrSetAddress(EntityManager em, Person person) {
        // Find the new address in the address table
        TypedQuery<Address> query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.zipcode = :zipcode", Address.class);
        query.setParameter("street", person.getAddress().getStreet());
        query.setParameter("zipcode", person.getAddress().getZipcode());
        try {
            // If it already exists, add the foreign address id to the person
            person.setAddress(query.getSingleResult());
        }
        catch (NoResultException e) {
            // If it doesn't exist, persist the new address (generates a new address id)
            em.persist(person.getAddress());// <-- Has to be inside a transaction
        }
    }

}
