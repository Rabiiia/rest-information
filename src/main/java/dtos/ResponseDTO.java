/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

public class ResponseDTO {
    private int code;
    private String message;

    public ResponseDTO(int code, String message) {
        this.code = code;
        this.message = message.replace("\n", "<br>");
    }

}
