package com.saferent.exception;

import com.saferent.exception.message.ApiResponseError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice // merkezi exception handle etmek için
public class SafeRentExceptionHandler extends ResponseEntityExceptionHandler {

        // AMACIM : custom bir exception sistemini kurmak, gelebilecek exceptionları
        // override ederek, istediğim yapıda cevap verilmesini sağlamak

    private ResponseEntity<Object> buildResponseEntity(ApiResponseError error) {
        return new ResponseEntity<>(error,error.getStatus());
    } // return'u kisaltmak icin olusturuldu

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        ApiResponseError error = new ApiResponseError(HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false)
        );

        return buildResponseEntity(error);

        //ASDFAS

    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<String> errors = ex.getBindingResult().getFieldErrors().
                stream().
                map(e->e.getDefaultMessage()).
                collect(Collectors.toList());

        ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST,
                errors.get(0).toString(),
                request.getDescription(false));

        return buildResponseEntity(error);
    }


    // Run-time Exception handler
    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(
            RuntimeException ex, WebRequest request){

        ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getDescription(false));

        return buildResponseEntity(error);
    }


    // General Exception handler (Parent of Runtime exception)
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> handleGeneralException(
            Exception ex, WebRequest request){

        ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getDescription(false));

        return buildResponseEntity(error);
    }



}
