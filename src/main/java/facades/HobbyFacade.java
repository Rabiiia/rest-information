package facades;

import dtos.HobbyDTO;
import dtos.PersonDTO;
import entities.Hobby;
import entities.Person;
import errorhandling.EntityNotFoundException;
import errorhandling.InternalErrorException;
import utils.FacadeUtility;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A facade class that manages {@link javax.persistence.Entity entities} –
 * {@link EntityManager#persist creates},
 * {@link EntityManager#find reads},
 * {@link EntityManager#merge updates} and
 * {@link EntityManager#remove deletes} entities (CRUD operations) -
 * and commits {@link javax.persistence.EntityTransaction transactions} to a database.
 * <p>
 * Only one instance of this facade can be constructed
 * and is returned by {@link #getInstance}.
 */
public class HobbyFacade {
    private static EntityManagerFactory EMF;
    private static HobbyFacade INSTANCE;
    private static PersonFacade PERSON_FACADE;
    private static FacadeUtility UTIL;

    /**
     * The default constructor is set private to ensure an instance only can be
     * accessed via {@link #getInstance}.
     */
    private HobbyFacade() {
    }

    /**
     * Returns a {@link HobbyFacade} object that can be used to
     * {@link EntityManager#persist create},
     * {@link EntityManager#find read},
     * {@link EntityManager#merge update} and
     * {@link EntityManager#remove delete} (CRUD) {@link javax.persistence.Entity entities}
     * and commit {@link javax.persistence.EntityTransaction transactions}
     * with these operations to the database.
     * <p>
     * Only one instance of this facade can be constructed,
     * and only this method can return that instance.
     *
     * @param emf an {@link javax.persistence.EntityManagerFactory} used to create the
     *            {@link javax.persistence.EntityManager} that manages the
     *            {@link javax.persistence.Entity entities} and
     *            {@link javax.persistence.EntityTransaction transactions} within the facade.
     * @return    the only allowed instance of {@link HobbyFacade}.
     */
    public static HobbyFacade getInstance(EntityManagerFactory emf) {
        if (INSTANCE == null) {
            EMF = emf;
            UTIL = FacadeUtility.getInstance(EMF);
            INSTANCE = new HobbyFacade();
        }
        return INSTANCE;
    }

    private EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    /**
     * Returns a {@link Set} of every {@link Hobby} in the database.
     *
     * @return {@link Set}.
     * @see TypedQuery
     */
    public Set<HobbyDTO> getHobbies() throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Hobby> hobbyQuery = em.createQuery("SELECT h FROM Hobby h", Hobby.class);
        Set<Hobby> hobbies = new LinkedHashSet<>(hobbyQuery.getResultList());
        if (hobbies.size() == 0) {
            throw new EntityNotFoundException("There are currently no hobbies in the database.");
        }
        Set<HobbyDTO> hobbyDTOs = new LinkedHashSet<>();
        for (Hobby hobby : hobbies) {
            hobbyDTOs.add(new HobbyDTO(hobby));
        }
        return hobbyDTOs;
    }

    /**
     * Returns a {@link Set<Hobby>} of every {@link Hobby} with a given hobby {@code name}.
     *
     * @param name a hobby name.
     * @return {@link Set<Hobby>}.
     * @see TypedQuery
     */
    public PersonDTO.InnerHobbyDTO getHobbyByName(String name) throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Hobby> hobbyQuery = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class);
        hobbyQuery.setParameter("name", name);
        try {
            return new PersonDTO.InnerHobbyDTO(hobbyQuery.getSingleResult());
        }
        catch (NoResultException e) {
            throw new EntityNotFoundException("We currently have no hobbies in our system.");
        }
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
        Set<Person> persons = UTIL.getPersonsByHobbyId(hobby.getId());
        // Prepare DTO for persons
        Set<PersonDTO> personDTOs = new LinkedHashSet<>();
        for (Person person : persons) {
            // Make sure to include all the person's phones
            person.setPhones(UTIL.getPhonesByPersonId(person.getId()));
            // Make sure to include all the person's hobbies (not just the one we looked up)
            person.setHobbies(UTIL.getHobbiesByPersonId(person.getId()));
            // Convert entity to DTO
            personDTOs.add(new PersonDTO(person));
        }
        return personDTOs;
    }

    /**
     * Finds a {@link Set} of every {@link Person} with a given hobby {@code name} in the database.
     *
     * @param query a hobby name.
     * @return {@link Set}.
     * @see TypedQuery
     * @see EntityManager#find
     */
    public Set<HobbyDTO> searchHobbies(String query) {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Hobby> hobbyQuery = em.createQuery("SELECT h FROM Hobby h WHERE h.name LIKE :query", Hobby.class);
        hobbyQuery.setParameter("query", "%" + query + "%");
        Set<HobbyDTO> hobbyDTOs = new LinkedHashSet<>();
        for (Hobby hobby : hobbyQuery.getResultList()) {
            hobbyDTOs.add(new HobbyDTO(hobby));
        }
        return hobbyDTOs;
    }
}
