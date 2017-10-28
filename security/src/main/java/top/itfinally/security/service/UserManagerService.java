package top.itfinally.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.itfinally.security.repository.dao.UserAuthorityDao;

@Service
public class UserManagerService {

    private UserAuthorityDao userAuthorityDao;


    @Autowired
    public UserManagerService setUserAuthorityDao( UserAuthorityDao userAuthorityDao ) {
        this.userAuthorityDao = userAuthorityDao;
        return this;
    }
}
