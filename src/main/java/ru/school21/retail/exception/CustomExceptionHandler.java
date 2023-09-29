package ru.school21.retail.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.MappingException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.sql.SQLException;

@Slf4j
@ControllerAdvice
class CustomExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ModelAndView bindExceptionHandler(BindException ex) {
        return showError(ex.getMessage());
    }

    @ExceptionHandler(value = MappingException.class)
    public ModelAndView mappingExceptionHandler(MappingException ex) {
        return showError(ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ModelAndView dataIntegrityViolationHandler(Exception ex) {
        return showError("The entered data violates the integrity of the database: "
                + ex.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public ModelAndView sqlErrorHandler(Exception ex) {
        return showError(ex.getMessage());
    }

    @ExceptionHandler({IOException.class, Exception.class})
    public ModelAndView defaultErrorHandler(Exception ex) {
        return showError(ex.getMessage());
    }

    private ModelAndView showError(String message) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", message);
        modelAndView.setViewName("error");
        log.warn(message);
        return modelAndView;
    }
}