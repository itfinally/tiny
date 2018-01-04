package top.itfinally.security.service;

import top.itfinally.security.repository.po.AbstractUserDetailsEntity;

public interface AbstractCreatedAdminService<User extends AbstractUserDetailsEntity<User>> {
    AbstractUserDetailsEntity<User> getAdmin();
}
