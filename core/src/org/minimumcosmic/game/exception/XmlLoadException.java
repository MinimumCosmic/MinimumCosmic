package org.minimumcosmic.game.exception;

import java.io.IOException;

/**
 * Created by Kostin on 15.07.2017.
 */

public class XmlLoadException extends IOException {
    private final String []message = {
            "Can't load item\nfrom inventory.xml",
            "Can't load\nchild or attribute\nfrom modules.xml",
            "Can't load\nresearchpoint from\nresearchpoint.xml",
            "Unknown exception"
    };
    int currentException;

    public XmlLoadException(int number){
        if(number >= 0 || number < message.length){
            currentException = number;
        }
        else{
            currentException = message.length - 1;
        }
    }

    public String getException(){
        return message[currentException];
    }
}
