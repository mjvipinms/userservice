package com.ibs.userservice.service;

import com.ibs.userservice.entity.Role;
import com.ibs.userservice.repository.RoleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }

    /**
     *
     * @return returns all roles
     */
    public List<Role> getAllRoles(){
        return roleRepository.findAll(Sort.sort(Role.class).by(Role::getRoleName));
    }
}
