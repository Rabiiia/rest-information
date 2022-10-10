package facades;

import dtos.PersonDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import errorhandling.EntityFoundException;
import errorhandling.EntityNotFoundException;
import errorhandling.InternalErrorException;
import utils.FacadeUtility;
import utils.StatusCode;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
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
public class PersonFacade {
    private static PersonFacade INSTANCE;
    private static EntityManagerFactory EMF;
    private static FacadeUtility UTIL;
    private static StatusCode RESPONSE_CODE;
    private static final boolean CHECK_OWNERSHIP = true;

    /**
     * The default constructor is set private to ensure the class only can be accessed
     * via {@link #getInstance}.
     */
    private PersonFacade() {
    }

    /**
     * Returns a {@link PersonFacade} object that can be used to
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
     * @return    the only allowed instance of {@link PersonFacade}.
     */
    public static PersonFacade getInstance(EntityManagerFactory emf) {
        if (INSTANCE == null) {
            EMF = emf;
            UTIL = FacadeUtility.getInstance(EMF);
            INSTANCE = new PersonFacade();
        }
        return INSTANCE;
    }

    private EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Returns a {@link Set} of every {@link Person} in the database.
     *
     * @return {@link Set}.
     * @see TypedQuery
     */
    public Set<PersonDTO> getPersons() throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Person> personQuery = em.createQuery("SELECT p FROM Person p", Person.class);
        Set<Person> persons = new LinkedHashSet<>(personQuery.getResultList());
       // if (persons.size() == 0) {
         //   throw new EntityNotFoundException("There are currently no persons in the database.");
       // }
        Set<PersonDTO> personDTOs = new LinkedHashSet<>();
        for (Person person : persons) {
            personDTOs.add(new PersonDTO(person));
        }
        return personDTOs;
    }

    /**
     * Finds a {@link Person} with a given phone {@code number} in the database.
     *
     * @param number a phone number.
     * @return a {@link PersonDTO} with data about a person – if one exists.
     * @throws EntityNotFoundException if no person is found.
     * @see EntityManager#find
     */
    public PersonDTO getPersonByNumber(int number) throws EntityNotFoundException {
        // Get the person associated with the number
        Person person = UTIL.phoneExists(number).getPerson();
        // Make sure to include all the person's phones (not just the one we looked up)
        person.setPhones(UTIL.getPhonesByPersonId(person.getId()));
        // Make sure to include all the person's hobbies
        person.setHobbies(UTIL.getHobbiesByPersonId(person.getId()));
        // Convert entity to DTO
        return new PersonDTO(person);
    }

    /**
     * Finds a {@link Set} of every {@link Person} with a given hobby {@code name} in the database.
     *
     * @param name a hobby name.
     * @return {@link Set}.
     * @throws EntityNotFoundException if the hobby is not found.
     * @see TypedQuery
     * @see EntityManager#find
     */
    public Set<PersonDTO> getPersonsByHobbyName(String name) throws EntityNotFoundException, InternalErrorException {
        // Find hobby in hobby table
        Hobby hobby = UTIL.hobbyNameExists(name);
        // Find persons who have this hobby
        hobby.setPersons(UTIL.getPersonsByHobbyId(hobby.getId()));
        // Prepare DTO for persons
        Set<PersonDTO> personDTOs = new LinkedHashSet<>();
        for (Person person : hobby.getPersons()) {
            // Make sure to include all the person's phones
            person.setPhones(UTIL.getPhonesByPersonId(person.getId()));
            // Convert entity to DTO
            personDTOs.add(new PersonDTO(person));
        }
        return personDTOs;
    }

    /**
     * Finds every {@link Person} with a given {@code zipcode} in the database.
     *
     * @param zipcode
     * @throws EntityNotFoundException if no persons are found.
     * @see EntityManager#find
     */
    public Set<PersonDTO> getPersonsByZipcode(int zipcode) throws EntityNotFoundException, InternalErrorException {
        // Create entity manager to find entity
        EntityManager em = EMF.createEntityManager();
        // Find the person in the person table
        TypedQuery<Person> personQuery = em.createQuery("SELECT p FROM Person p WHERE p.address.zipcode = :zipcode", Person.class);
        personQuery.setParameter("zipcode", zipcode);
        try {
            // Prepare a set of person DTOs
            Set<PersonDTO> personDTOs = new LinkedHashSet<>();
            for (Person person : personQuery.getResultList()) {
                // Make sure to add all phones associated with the person
                person.setPhones(UTIL.getPhonesByPersonId(person.getId()));
                // Convert entity to DTO
                personDTOs.add(new PersonDTO(person));
            }
            return personDTOs;
        }
        catch (PersistenceException e) {
            throw new InternalErrorException(e.getMessage());
        }
        finally {
            em.close();
        }
    }

    /**
     * Persists a new {@link Person} to the database.
     *
     * @param dto a {@link PersonDTO} with data about the person.
     * @see EntityManager#persist
     */
    public void createPerson(PersonDTO dto) throws EntityFoundException, EntityNotFoundException, InternalErrorException {
        // Convert DTO to entity
        Person person = new Person(dto);
        // Create entity manager to persist person
        EntityManager em = EMF.createEntityManager();
        try {
            // Begin entity transaction
            em.getTransaction().begin();
            // Set an id for the address (address currently has no id)
            person.setAddress(getAddressWithId(person.getAddress(), em));
            // Overwrite hobbies without ids with hobbies with ids
            person.setHobbies(getHobbiesWithIds(person.getHobbies()));
            // Persist person (using the foreign address id set above)
            em.persist(person);
            // Persist phones (using the foreign person id generated above)
            updatePhones(person, em);
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
     * Merges an altered {@link Person} with an existing {@link Person} in the database.
     *
     * @param dto a {@link PersonDTO} with new data about the person.
     * @see EntityManager#merge
     */
    public void updatePerson(PersonDTO dto) throws EntityNotFoundException, EntityFoundException, InternalErrorException {
        // Convert DTO to entity
        Person person = new Person(dto);
        // Update ONLY if the person exists!
        UTIL.personExists(person.getId());// <-- Throws an exception otherwise
        // Create an entity manager to merge updates into a person
        EntityManager em = EMF.createEntityManager();
        try {
            // Begin entity transaction
            em.getTransaction().begin();
            // Set an id for the address (address currently has no id)
            person.setAddress(getAddressWithId(person.getAddress(), em));
            // Update phones
            updatePhones(person, em, CHECK_OWNERSHIP);
            // Overwrite hobbies without ids with hobbies with ids
            person.setHobbies(getHobbiesWithIds(person.getHobbies()));
            // Merge altered entity into existing entity
            em.merge(person);
            // Commit entity transaction to database
            em.getTransaction().commit();
        }
        catch (PersistenceException e) {
            throw new InternalErrorException("Failed to update person.\n" + e.getMessage());
        }
        finally {
            em.close();
        }
    }

    /**
     * Deletes a {@link Person} from the database.
     *
     * @param id a person id.
     * @see EntityManager#remove
     */
    public void deletePerson(int id) throws EntityNotFoundException, InternalErrorException {
        // Remove ONLY if the person exists!
        Person person = UTIL.personExists(id);// <-- Throws an exception otherwise
        // Create an entity manager to remove the person
        EntityManager em = EMF.createEntityManager();
        try {
            // Begin entity transaction
            em.getTransaction().begin();
            // Remove person
            em.remove(person);
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
     * Returns an {@link Address} with an id given an {@link Address} with no id.
     *
     * @param address an {@link Address} with no id.
     * @param em an {@link EntityManager} with a reference to an {@link EntityTransaction}.
     * @return an {@link Address} with an id.
     * @see TypedQuery
     * @see EntityManager#persist
     */
    private Address getAddressWithId(Address address, EntityManager em) throws PersistenceException {
        try {
            // If it already exists, get the existing address id
            return UTIL.addressExists(address.getStreet(), address.getZipcode());
        }
        catch (EntityNotFoundException e) {
            // If it doesn't exist, persist the address to the address table
            em.persist(address);// <-- Generates a new address id
            return address;
        }
    }

    /**
     * Persists every {@link Phone} belonging to a {@link Person} to the database.
     *
     * @param person the owner of the phones.
     * @param em an {@link EntityManager} with a reference to an {@link EntityTransaction}.
     * @throws EntityFoundException
     */
    private void updatePhones(Person person, EntityManager em) throws EntityFoundException, InternalErrorException {
        updatePhones(person, em, false);
    }

    /**
     * Persists every {@link Phone} belonging to a {@link Person} to the database.
     *
     * @param person the owner of the phones.
     * @param em an {@link EntityManager} with a reference to an {@link EntityTransaction}.
     * @param checkOwnership set to {@link #CHECK_OWNERSHIP}
     *                       if the person already exists in the database.
     * @throws EntityFoundException
     */
    public void updatePhones(Person person, EntityManager em, boolean checkOwnership) throws EntityFoundException, InternalErrorException {
        for (Phone phone : person.getPhones()) {
            try {
                Phone existingPhone = UTIL.phoneExists(phone.getNumber());
                // If it already exists and belongs to someone else...
                if (!Objects.equals(existingPhone.getPerson().getId(), person.getId()) || !checkOwnership) {
                    // we don't want to change the ownership of that phone number
                    throw new EntityFoundException("The phone number " + phone.getNumber() + " is already associated with another person!");
                }
                // If it exists, but it belongs to us, we simply want to update the description
                em.merge(phone);
            }
            catch (EntityNotFoundException nfe) {
                // If the phone number does not exist in the phone table:
                try {
                    // Add the foreign person id to the phone (person id cannot be null!)
                    phone.setPerson(person);
                    // Persist phone
                    em.persist(phone);
                }
                catch (PersistenceException pe) {
                    throw new InternalErrorException("Could not create phone.");
                }
            }
            catch (PersistenceException pe) {
                throw new InternalErrorException("Could not update phone.");
            }
        }
    }

    /**
     * Returns a {@link Set} of {@link Hobby hobbies} with ids
     * given a {@link Set} of {@link Hobby hobbies} with ids.
     *
     * @param hobbiesWithoutIds a {@link Set} with no id.
     * @return {@link Set}.
     * @see TypedQuery
     */
    public Set<Hobby> getHobbiesWithIds(Set<Hobby> hobbiesWithoutIds) throws EntityNotFoundException {
        Set<Hobby> hobbiesWithIds = new LinkedHashSet<>();
        for (Hobby hobby : hobbiesWithoutIds) {
            hobbiesWithIds.add(UTIL.hobbyNameExists(hobby.getName()));
        }
        return hobbiesWithIds;
    }

    /**
     * Removes an address {@code id} from a {@link Person} in the database.
     * <p>
     * Also removes the {@link Address} from the database,
     * if the person was the only one living there.
     *
     * @param id a person id.
     * @throws EntityNotFoundException if no address is found.
     * @see EntityManager#find
     * @see EntityManager#remove
     * @see TypedQuery
     * @see Person#removeAddress
     */
    public void removeAddressFromPerson(int id) throws EntityNotFoundException, InternalErrorException {
        // Find person in the person table
        Person person = UTIL.personExists(id);
        // Before removing the address, store it in a variable for later use
        Address address = person.getAddress();
        // Create entity manager to remove address
        EntityManager em = EMF.createEntityManager();
        try {
            // Begin entity transaction
            em.getTransaction().begin();
            // Remove foreign address id from person (set address_id = null)
            person.removeAddress();
            // Merge the altered person with the existing person
            em.merge(person);
            // If no persons live at this address...
            if (UTIL.getPersonsByAddressId(address.getId()).size() == 0) {
                // remove the address from the address table
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
     * Removes a {@link Phone} with a given {@code number} from the database.
     *
     * @param number a phone number.
     * @throws EntityNotFoundException if no phone is found.
     * @see EntityManager#find
     * @see EntityManager#remove
     */
    public void removePhone(int number, EntityManager em) throws EntityNotFoundException, InternalErrorException {
        // Find phone in person table
        Phone phone = UTIL.phoneExists(number);
        try {
            // Remove phone from phone table
            em.remove(phone);
        }
        catch (PersistenceException e){
            throw new InternalErrorException("Could not remove phone from database.");
        }
        finally {
            em.close();
        }
    }

    public void removePhones(Set<Phone> phones, EntityManager em) throws EntityNotFoundException, InternalErrorException {
        for (Phone phone : phones) {
            removePhone(phone.getNumber(), em);
        }
    }
}
