/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sasi
 */
public class CustomException extends Exception{

    private int code = 0;

    private String errorMessage = null;

    public CustomException(int code, String message) {
        this.code = code;
        this.errorMessage = message;
    }

    public int getCode() {
        return code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
