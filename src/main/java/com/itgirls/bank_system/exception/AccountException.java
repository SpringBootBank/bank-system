package com.itgirls.bank_system.exception;

public class AccountException extends Throwable {

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidAccountException extends RuntimeException {
        public InvalidAccountException(String message) {
            super(message);
        }
    }

    public static class AccountAlreadyExistsException extends RuntimeException {
        public AccountAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) {
            super(message);
        }
    }
}