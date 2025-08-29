package javaspringdddenvironment.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import javaspringdddenvironment.tossautoassetlog.common.ApiException;
import javaspringdddenvironment.tossautoassetlog.common.ApiResponse;
import javaspringdddenvironment.tossautoassetlog.common.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import the_monitor.application.dto.ErrorReasonDto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> validation(ConstraintViolationException e, WebRequest request) {
        String errorMessage = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("ConstraintViolationException 추출 도중 에러 발생"));

        return handleExceptionInternalConstraint(e, javaspringdddenvironment.tossautoassetlog.common.ErrorStatus.valueOf(errorMessage),
                HttpHeaders.EMPTY, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String errorMessage = Optional.ofNullable(fieldError.getDefaultMessage())
                            .orElse("");
                    errors.merge(fieldName, errorMessage,
                            (existingErrorMessage, newErrorMessage) -> existingErrorMessage + ", "
                                    + newErrorMessage);
                });

        return handleExceptionInternalArgs(ex, HttpHeaders.EMPTY,
                javaspringdddenvironment.tossautoassetlog.common.ErrorStatus.valueOf("_BAD_REQUEST"), request, errors);
    }

    @ExceptionHandler
    public ResponseEntity<Object> exception(Exception e, WebRequest request) {
        e.printStackTrace();

        return handleExceptionInternalFalse(e, javaspringdddenvironment.tossautoassetlog.common.ErrorStatus._INTERNAL_SERVER_ERROR,
                HttpHeaders.EMPTY, javaspringdddenvironment.tossautoassetlog.common.ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus(), request,
                e.getMessage());
    }

    @ExceptionHandler(value = javaspringdddenvironment.tossautoassetlog.common.ApiException.class)
    public ResponseEntity onThrowException(ApiException apiException, HttpServletRequest request) {
        ErrorReasonDto errorReasonHttpStatus = apiException.getErrorReasonHttpStatus();
        return handleExceptionInternal(apiException, errorReasonHttpStatus, null, request);
    }

    private ResponseEntity<Object> handleExceptionInternal(Exception e, ErrorReasonDto reason,
                                                           HttpHeaders headers, HttpServletRequest request) {

        javaspringdddenvironment.tossautoassetlog.common.ApiResponse<Object> body = javaspringdddenvironment.tossautoassetlog.common.ApiResponse.onFailure(reason.getCode(), reason.getMessage(),
                null);

        WebRequest webRequest = new ServletWebRequest(request);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                reason.getHttpStatus(),
                webRequest
        );
    }

    private ResponseEntity<Object> handleExceptionInternalFalse(Exception e,
                                                                javaspringdddenvironment.tossautoassetlog.common.ErrorStatus errorCommonStatus,
                                                                HttpHeaders headers, HttpStatus status, WebRequest request, String errorPoint) {
        javaspringdddenvironment.tossautoassetlog.common.ApiResponse<Object> body = javaspringdddenvironment.tossautoassetlog.common.ApiResponse.onFailure(errorCommonStatus.getCode(),
                errorCommonStatus.getMessage(), errorPoint);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                status,
                request
        );
    }

    private ResponseEntity<Object> handleExceptionInternalArgs(Exception e, HttpHeaders headers,
                                                               javaspringdddenvironment.tossautoassetlog.common.ErrorStatus errorCommonStatus,
                                                               WebRequest request, Map<String, String> errorArgs) {
        javaspringdddenvironment.tossautoassetlog.common.ApiResponse<Object> body = javaspringdddenvironment.tossautoassetlog.common.ApiResponse.onFailure(errorCommonStatus.getCode(),
                errorCommonStatus.getMessage(), errorArgs);

        return new ResponseEntity<>(body, errorCommonStatus.getHttpStatus());
    }

    private ResponseEntity<Object> handleExceptionInternalConstraint(Exception e,
                                                                     ErrorStatus errorCommonStatus,
                                                                     HttpHeaders headers, WebRequest request) {
        javaspringdddenvironment.tossautoassetlog.common.ApiResponse<Object> body = ApiResponse.onFailure(errorCommonStatus.getCode(),
                errorCommonStatus.getMessage(), null);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.getHttpStatus(),
                request
        );
    }
}
