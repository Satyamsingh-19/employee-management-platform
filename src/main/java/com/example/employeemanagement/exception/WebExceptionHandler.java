package com.example.employeemanagement.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(assignableTypes = com.example.employeemanagement.controller.PageController.class)
public class WebExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("pageTitle", "Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "pages/error";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequest(BadRequestException ex, Model model) {
        model.addAttribute("pageTitle", "Bad Request");
        model.addAttribute("errorMessage", ex.getMessage());
        return "pages/error";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException ex, Model model) {
        model.addAttribute("pageTitle", "Access Denied");
        model.addAttribute("errorMessage", "You do not have permission to access this page.");
        return "pages/error";
    }
}
