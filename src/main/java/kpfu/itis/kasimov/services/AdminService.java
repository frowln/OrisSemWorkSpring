package kpfu.itis.kasimov.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @PreAuthorize("hasAuthority('admin') or hasAuthority('some_other')")
    public void doAdminStuff() {
        System.out.println("Only admin here");
    }
}
