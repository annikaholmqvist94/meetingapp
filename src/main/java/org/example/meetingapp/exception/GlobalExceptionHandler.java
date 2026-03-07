package org.example.meetingapp.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Hanterar ResourceNotFoundException — möte hittades inte
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Mötet hittades inte");
        model.addAttribute("errorMessage",
                "Möte med id " + ex.getId() + " finns inte eller har tagits bort.");
        model.addAttribute("backUrl", "/meetings");
        return "error/not-found";
    }

    // Hanterar oväntade fel
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Något gick fel");
        model.addAttribute("errorMessage",
                "Ett oväntat fel inträffade. Försök igen senare.");
        model.addAttribute("backUrl", "/meetings");
        return "error/not-found";
    }
}