package epam.controller.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Error {

    private String message;
    private String type;
    private String code;
}
