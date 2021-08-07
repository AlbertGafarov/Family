package ru.gafarov.Family.exception_handling;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.gafarov.Family.security.jwt.JwtAuthentificationException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationExceptionHandler extends OncePerRequestFilter {
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);

        } catch (JwtAuthentificationException e) {
            setErrorResponse(HttpStatus.UNAUTHORIZED, response, e);
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            //setErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, response, e);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, Throwable ex){
        response.setStatus(status.value());
        response.setContentType("application/json");
        // A class used for errors
        //ApiError apiError = new ApiError(status, ex);
//        try {
//           // String json = apiError.convertToJson();
//            //System.out.println(json);
//           // response.getWriter().write(json);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
   }

//    public AuthenticationExceptionHandler() {
//    }
}