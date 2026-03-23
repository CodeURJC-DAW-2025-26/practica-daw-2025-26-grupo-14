package es.codeurjc.daw.library.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(Exception.class)
    public Object handleGeneralException(Exception ex, Model model, HttpServletRequest request) {

        ex.printStackTrace();

        if (request.getRequestURI() != null && request.getRequestURI().startsWith("/api/v1/")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                            "message", ex.getMessage() != null ? ex.getMessage() : "Unexpected error",
                            "path", request.getRequestURI()));
        }

        model.addAttribute("errorMessage", ex.getMessage());

        return "error";
    }
}
