package facades;

import entities.Person;
import entities.Phone;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class PersonFacade {

    private static PersonFacade INSTANCE;
    private static EntityManagerFactory EMF;

    //Private Constructor to ensure Singleton
    private PersonFacade() {}


    /**
     *
     * @param emf
     * @return an instance of this facade class.
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

    public void createPerson(Person person) {
        EntityManager em = EMF.createEntityManager();
        try {
            em.getTransaction().begin();
            // Persist address
            em.persist(person.getAddress());
            // Persist person (using the above address id)
            em.persist(person);
            // Persist phones (using the above person id)
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

}