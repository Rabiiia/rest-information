/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;

import dtos.HobbyDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import utils.EMF_Creator;

/**
 *
 * @author tha
 */
public class Populator {
    private static Person p1, p2, p3, p4;
    private static Hobby h1, h2, h3, h4, h5;

    public static void populate(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            deleteRows(em);
            populatePersons(em);
            p1.addHobby(h2);
            p2.addHobby(h2);
            p3.addHobby(h2);
            p4.addHobby(h2);
            em.getTransaction().commit();
            System.out.println("Successfully populated the database!");
        }
        catch (PersistenceException e) {
            System.out.println("Failed to populate the database!");
            System.out.println(e.getMessage());
        }
        finally {
            em.close();
        }
    }

    public static void populateForTest() {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactoryForTest();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            deleteRows(em);
            populatePersons(em);
            populateHobbies(em);
            p1.addHobby(h2);
            p2.addHobby(h2);
            p3.addHobby(h2);
            p4.addHobby(h2);
            p3.addHobby(h1);
            p4.addHobby(h1);
            em.getTransaction().commit();
            System.out.println("Successfully populated the database!");
        }
        catch (PersistenceException e) {
            System.out.println("Failed to populate the database!");
            System.out.println(e.getMessage());
        }
        finally {
            em.close();
        }
    }

    public static void deleteRows(EntityManager em) {
        // Delete all rows (if any exists)
        em.createNamedQuery("Phone.deleteAllRows").executeUpdate();
        em.createNamedQuery("Person.deleteAllRows").executeUpdate();
        em.createNamedQuery("Address.deleteAllRows").executeUpdate();
        em.createNamedQuery("Hobby.deleteAllRows").executeUpdate();
        em.createNativeQuery("DELETE FROM hobby_person").executeUpdate();
        // Reset auto incrementation
        em.createNativeQuery("ALTER TABLE person AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE address AUTO_INCREMENT = 1").executeUpdate();
        em.createNativeQuery("ALTER TABLE hobby AUTO_INCREMENT = 1").executeUpdate();
    }

    public static void populatePersons(EntityManager em) {
        // Create and persist addresses first (addresses have no foreign keys!)
        Address a1 = new Address("Falkonner Alle", 2800);
        Address a2 = new Address("Nørregaard", 3500);
        em.persist(a1);
        em.persist(a2);
        // Create and persist persons (using the foreign address ids generated above)
        p1 = new Person("Mogens","Hansen", "mh@mail.dk", a1);
        p2 = new Person("Sigurd", "Jensen","sj@mail.dk", a1);
        p3 = new Person("Bjørn", "Nielsen","bn@mail.dk", a2);
        p4 = new Person("Astrid", "Mikkelsen","am@mail.dk", a2);
        em.persist(p1);
        em.persist(p2);
        em.persist(p3);
        em.persist(p4);
        // Create and persist phones (using the foreign person ids generated above)
        em.persist(new Phone(11111111, "Mobile", p1));
        em.persist(new Phone(22222222, "Mobile", p2));
        em.persist(new Phone(33333333, "Mobile", p3));
        em.persist(new Phone(44444444, "Mobile", p4));
        em.persist(new Phone(12345678, "Office", p1));
        em.persist(new Phone(87654321, "Office", p2));
    }

    public static void populateHobbies(EntityManager em) {
        // Create and persist hobbies
        h1 = new Hobby("", "Musik", "", "");
        h2 = new Hobby("", "Fuglekiggeri", "", "");
        h3 = new Hobby("", "Søvn", "", "");
        h4 = new Hobby("", "Yoga", "", "");
        h5 = new Hobby("", "Squash", "", "");
        em.persist(h1);
        em.persist(h2);
        em.persist(h3);
        em.persist(h4);
        em.persist(h5);
    }
    
    public static void main(String[] args) {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        populate(emf);
    }

}
