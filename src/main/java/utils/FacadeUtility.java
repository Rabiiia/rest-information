package utils;

import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import errorhandling.EntityNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.LinkedHashSet;
import java.util.Set;

public class FacadeUtility {
    private static FacadeUtility INSTANCE;
    private static EntityManagerFactory EMF;

    /**
     * The default constructor is set private to ensure an instance only can be
     * accessed via {@link #getInstance}.
     */
    private FacadeUtility() {
    }

    public static FacadeUtility getInstance(EntityManagerFactory emf) {
        if (INSTANCE == null) {
            EMF = emf;
            INSTANCE = new FacadeUtility();
        }
        return INSTANCE;
    }

    /**
     * Checks if an {@link Address} with a given {@code street} and {@code zipcode} exists in the database.
     *
     * @param street
     * @param zipcode
     * @return an {@link Address} – if one exits.
     * @throws EntityNotFoundException if no address is found.
     * @see TypedQuery
     */
    public Address addressExists(String street, int zipcode) throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Address> query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.zipcode = :zipcode", Address.class);
        query.setParameter("street", street);
        query.setParameter("zipcode", zipcode);
        try {
            return query.getSingleResult();
        }
        catch (NoResultException e) {
            throw new EntityNotFoundException("No one in our system is associated with the address " + street + ", " + zipcode + ".");
        }
    }

    /**
     * Checks if a {@link Phone} with a given {@code number} exists in the database.
     *
     * @param number a phone number.
     * @return a {@link Phone} – if one exits.
     * @throws EntityNotFoundException if no phone is found.
     * @see EntityManager#find
     */
    public Phone phoneExists(int number) throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        Phone phone = em.find(Phone.class, number);
        if (phone == null) {
            throw new EntityNotFoundException("No one in our system is associated with the phone number " + number + ".");
        }
        return phone;
    }

    /**
     * Checks if a {@link Person} with a given {@code id} exists in the database.
     *
     * @param id a person id.
     * @return a {@link Person} – if one exits.
     * @throws EntityNotFoundException if no person is found.
     * @see EntityManager#find
     */
    public Person personExists(int id) throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        Person person = em.find(Person.class, id);
        if (person == null) {
            throw new EntityNotFoundException("Could not find a person with the id " + id + ".");
        }
        return person;
    }

    /**
     * Checks if a {@link Hobby} with a given {@code id} exists in the database.
     *
     * @param id a hobby id.
     * @return a {@link Hobby} – if one exits.
     * @throws EntityNotFoundException if no hobby is found.
     * @see TypedQuery
     */
    public Hobby hobbyExists(int id) throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        Hobby hobby = em.find(Hobby.class, id);
        if (hobby == null) {
            throw new EntityNotFoundException("Could not find the " + id + " hobby.");
        }
        return hobby;
    }

    /**
     * Checks if a {@link Hobby} with a given {@code name} exists in the database.
     *
     * @param name a hobby name.
     * @return a {@link Hobby} – if one exits.
     * @throws EntityNotFoundException if no hobby is found.
     * @see TypedQuery
     */
    public Hobby hobbyNameExists(String name) throws EntityNotFoundException {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Hobby> hobbyQuery = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class);
        hobbyQuery.setParameter("name", name);
        try {
            return hobbyQuery.getSingleResult();
        }
        catch (NoResultException e) {
            throw new EntityNotFoundException("Could not find the " + name + " hobby.");
        }
    }

    /**
     * Returns a {@link Set} of every {@link Phone} with a given person {@code id}.
     *
     * @param id a person id.
     * @return {@link Set}.
     * @see TypedQuery
     */
    public Set<Phone> getPhonesByPersonId(int id) {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Phone> phoneQuery = em.createQuery("SELECT t FROM Person p JOIN p.phones t WHERE p.id = :pid", Phone.class);
        phoneQuery.setParameter("pid", id);
        return new LinkedHashSet<>(phoneQuery.getResultList());
    }

    /**
     * Returns a {@link Set} of every {@link Hobby} with a given person {@code id}.
     *
     * @param id a person id.
     * @return {@link Set}.
     * @see TypedQuery
     */
    public Set<Hobby> getHobbiesByPersonId(int id) {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Hobby> hobbyQuery = em.createQuery("SELECT h FROM Person p JOIN p.hobbies h WHERE p.id = :pid", Hobby.class);
        hobbyQuery.setParameter("pid", id);
        return new LinkedHashSet<>(hobbyQuery.getResultList());
    }

    /**
     * Finds a {@link Set} of every {@link Person} with a given address {@code id} in the database.
     *
     * @param id an address id.
     * @return {@link Set}.
     * @see TypedQuery
     */
    public Set<Person> getPersonsByAddressId(int id) {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Person> personQuery = em.createQuery("SELECT p FROM Person p WHERE p.address.id = :aid", Person.class);
        personQuery.setParameter("aid", id);
        return new LinkedHashSet<>(personQuery.getResultList());
    }

    /**
     * Finds a {@link Set} of every {@link Person} with a given hobby {@code id} in the database.
     *
     * @param id a hobby id.
     * @return {@link Set}.
     * @see TypedQuery
     */
    public Set<Person> getPersonsByHobbyId(int id) {
        EntityManager em = EMF.createEntityManager();
        TypedQuery<Person> personQuery = em.createQuery("SELECT p FROM Hobby h JOIN h.persons p WHERE h.id = :hid", Person.class);
        personQuery.setParameter("hid", id);
        return new LinkedHashSet<>(personQuery.getResultList());
    }
}
