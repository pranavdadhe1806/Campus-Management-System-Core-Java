package com.collegeapp.service;

public class DuplicateEntityException extends ServiceException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}
