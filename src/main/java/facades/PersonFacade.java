package facades;

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
}
