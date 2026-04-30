package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.example.model.AppUser;

@Getter
@Setter
public class CurrentUser {
    private AppUser currentUser;
}