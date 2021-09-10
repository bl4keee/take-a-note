package com.bwalczak.note.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;
import java.io.Serializable;
import java.util.Collection;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Data
@SuperBuilder
@JsonInclude(NON_NULL)
public class HttpResponse<T> implements Serializable {

    protected String reason;
    protected int statusCode;
    protected String message;
    protected String timeStamp;
    protected HttpStatus status;
    protected Collection<? extends T> data;
}
