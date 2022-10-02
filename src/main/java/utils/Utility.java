/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Properties;
import java.util.Set;
import com.google.gson.*;
import dtos.PersonDTO;

import java.io.UnsupportedEncodingException;

/**
 *
 * @author tha
 */
public class Utility {
    private static Gson gson = new GsonBuilder().create();
    
    public static void printAllProperties() {
        Properties prop = System.getProperties();
        Set<Object> keySet = prop.keySet();
        for (Object obj : keySet) {
            System.out.println("System Property: {"
                    + obj.toString() + ","
                    + System.getProperty(obj.toString()) + "}");
        }
    }
    
    public static PersonDTO jsonToDto(String json) throws UnsupportedEncodingException{
        return gson.fromJson(new String(json.getBytes("UTF8")), PersonDTO.class);
    }
    
    public static String DtoToJson(PersonDTO rmDTO){
        return gson.toJson(rmDTO, PersonDTO.class);
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException {
        // printAllProperties();
        
        // Test jsonToDto and DtoToJson
        String json = "{" +
                "'id':1," +
                "'firstName':'Fornavn'," +
                "'lastName':'Efternavn'" +
                "'email':'eksempel@mail.dk'" +
                "'phones':[" +
                "{" +
                "'number':'12345678'," +
                "'description':'Mobile'," +
                "}" +
                "]" +
                "'address':{" +
                "'street':'Nørgaardsvej 30'," +
                "'zipcode':'2800'," +
                "'city':'Kongens Lyngby'," +
                "}" +
                "}";
        PersonDTO pdto = jsonToDto(json);
        System.out.println(pdto);

        String jsonFromDto = DtoToJson(pdto);
        System.out.println(jsonFromDto);
    }

}
