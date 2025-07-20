package aero.sita.mgt.auth_service.Configurations;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;
import java.security.SignatureException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity handleExpiredJwtException(ExpiredJwtException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", HttpStatus.UNAUTHORIZED.value());
        response.put("error", "Token is expired");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Object> handleSignatureException(SignatureException ex) {
        return buildErrorResponse("token with signature incorrect", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        return buildErrorResponse("Authentication exception", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<Object> handleMalformedJwtException(MalformedJwtException ex) {
        return buildErrorResponse("Token JWT malformed", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<Object> handleUnsupportedJwtException(UnsupportedJwtException ex) {
        return buildErrorResponse("Token type not supported", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return buildErrorResponse("Token is empty or invalid", HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status.value());
        response.put("error", message);
        return new ResponseEntity<>(response, status);
    }
}
