package com.salon.service;

import com.salon.entity.Role;
import com.salon.entity.User;
import com.salon.entity.UserRole;
import com.salon.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public void assignRoleToUser(User user, Role role) {
        UserRole userRole = new UserRole(user, role);
        userRoleRepository.save(userRole);
    }

    public List<UserRole> getRolesByUser(User user) {
        return userRoleRepository.findByUser(user);
    }
}
