/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import javax.persistence.EntityManagerFactory;

import dtos.AddressDTO;
import dtos.PersonDTO;
import dtos.PhoneDTO;
import utils.EMF_Creator;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 * @author tha
 */
public class Populator {
    public static void populate(){
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
        PersonFacade facade = PersonFacade.getInstance(emf);
        // Mock up DTOs
        AddressDTO address = new AddressDTO("Nørgaardsvej 30", 2800);
        Set<PhoneDTO> phones = new LinkedHashSet<>();
        phones.add(new PhoneDTO(87654321, "Home"));
        phones.add(new PhoneDTO(76543210, "Mobile"));
        PersonDTO pdto = new PersonDTO("Thomas", "Hartmann", "tha@cphbusiness.dk", phones, address);
        // Persist person
        facade.createPerson(pdto);
    }
    
    public static void main(String[] args) {
        populate();
    }

}
