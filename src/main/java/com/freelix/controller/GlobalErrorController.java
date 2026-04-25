package com.freelix.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class GlobalErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object msgObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        if (statusObj != null) {
            int status = Integer.parseInt(statusObj.toString());
            model.addAttribute("status", status);
            if (status == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("message", "The page you're looking for doesn't exist.");
            } else if (status == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("message", "You don't have permission to access this resource.");
            } else {
                model.addAttribute("message", msgObj != null ? msgObj.toString() : "An unexpected error occurred.");
            }
        }
        return "error";
    }
}
