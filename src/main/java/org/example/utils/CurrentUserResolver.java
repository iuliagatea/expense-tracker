package org.example.utils;

import org.example.annotation.CurrentUser;
import org.example.exception.UserNotAuthenticatedException;
import org.example.model.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private org.example.config.CurrentUser currentUser;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CurrentUser.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            currentUser.setCurrentUser(null);
            throw new UserNotAuthenticatedException("User must be authenticated to access this resource");
        } else {
            AppUser user = currentUser.getCurrentUser();

            if (user == null) {
                throw new UserNotAuthenticatedException("Authenticated user not found in database");
            }
            return user;
        }
    }
}