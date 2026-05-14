package es.codeurjc.daw.library.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, Model model, HttpServletRequest request) {

        ex.printStackTrace();

        if (request.getRequestURI() != null && request.getRequestURI().startsWith("/api/v1/")) {
             HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
            String message = ex.getMessage();

            if(request.getRequestURI().startsWith("/api/v1/chart/")){
                status = HttpStatus.NOT_FOUND;
                message = "Page not found";
            }
            
            if (ex instanceof ResponseStatusException rse) {
                status = HttpStatus.valueOf(rse.getStatusCode().value());
                message = rse.getReason();
            }

            return ResponseEntity.status(status)
                    .body(Map.of(
                            "status", status.value(),
                            "error", status.getReasonPhrase(),
                            "message", message != null ? message : "Unexpected error",
                            "path", request.getRequestURI()));
        }

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";
    }
}
