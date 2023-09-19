package com.greenfoxacademy.springwebapp.exception.handlers;

import com.greenfoxacademy.springwebapp.dtos.ErrorDTO;
import com.greenfoxacademy.springwebapp.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@RestControllerAdvice
public class GlobalExceptionHandler extends ExceptionHandlerExceptionResolver {
    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidTokenException.class)
    public ResponseEntity<Object> handleInvalidTokenException(InvalidTokenException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", "Invalid token: " + exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = BuildingNotFoundException.class)
    public ResponseEntity<Object> handleBuildingNotFoundException(BuildingNotFoundException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidBuildingException.class)
    public ResponseEntity<Object> handleInvalidBuildingException(InvalidBuildingException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = KingdomNotFoundException.class)
    public ResponseEntity<Object> handleKingdomNotFoundException(KingdomNotFoundException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = MissingParameterException.class)
    public ResponseEntity<Object> handleMissingParameterException(MissingParameterException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = InvalidBuildingTypeException.class)
    public ResponseEntity<Object> handleInvalidBuildingTypeException(InvalidBuildingTypeException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = NotEnoughResourcesException.class)
    public ResponseEntity<Object> handleNotEnoughResourcesException(NotEnoughResourcesException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InsufficientBuildingLevelException.class)
    public ResponseEntity<Object> handleInsufficientBuildingLevelException(InsufficientBuildingLevelException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = InvalidBuildingLevelException.class)
    public ResponseEntity<Object> handleInvalidBuildingLevelException(InvalidBuildingLevelException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = UnfinishedBuildingException.class)
    public ResponseEntity<Object> handleNotFinishBuildingException(UnfinishedBuildingException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = InvalidIDException.class)
    public ResponseEntity<Object> handleInvalidIDException(InvalidIDException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NotValidAcademyException.class)
    public ResponseEntity<Object> handleNotValidAcademyException(NotValidAcademyException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = EmailNotVerifiedException.class)
    public ResponseEntity<Object> handleEmailNotVerifiedException(EmailNotVerifiedException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = UserAlreadyVerifiedException.class)
    public ResponseEntity<Object> handleUserAlreadyVerifiedException(UserAlreadyVerifiedException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = TroopNotFoundException.class)
    public ResponseEntity<Object> handleTroopNotFoundException(TroopNotFoundException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = InvalidTroopException.class)
    public ResponseEntity<Object> handleInvalidTroopException(InvalidTroopException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = KingdomAlreadyDefeatedException.class)
    public ResponseEntity<Object> handleKingdomAlreadyDefeatedException(KingdomAlreadyDefeatedException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IllegalTroopSelectionException.class)
    public ResponseEntity<Object> handleIllegalTroopException(IllegalTroopSelectionException exception) {
        return new ResponseEntity<>(new ErrorDTO("error", exception.getMessage()), HttpStatus.BAD_REQUEST);
    }
}