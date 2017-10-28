package top.itfinally.security.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.security.service.AdminManagerService;

@RestController
@RequestMapping( "/admin" )
public class AdminManagerController {
    private AdminManagerService adminManagerService;

    @Autowired
    public AdminManagerController setAdminManagerService( AdminManagerService adminManagerService ) {
        this.adminManagerService = adminManagerService;
        return this;
    }

    @ResponseBody
    @GetMapping( "/initialization" )
    public BaseResponseVoBean initialization() {
        return adminManagerService.initialization();
    }

    @ResponseBody
    @GetMapping( "/create" )
    public BaseResponseVoBean admin() {
        return adminManagerService.createAdminAccount();
    }

    @ResponseBody
    @GetMapping( "/lock" )
    public BaseResponseVoBean lockAdmin() {
        return adminManagerService.lockAdminAccount();
    }
}
