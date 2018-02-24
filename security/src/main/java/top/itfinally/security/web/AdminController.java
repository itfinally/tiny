package top.itfinally.security.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.itfinally.core.vo.BaseResponseVoBean;
import top.itfinally.security.service.AdminManagerService;

@RestController
@RequestMapping( "/admin" )
public class AdminController {

  private AdminManagerService adminManagerService;

  @Autowired
  public AdminController setAdminManagerService( AdminManagerService adminManagerService ) {
    this.adminManagerService = adminManagerService;
    return this;
  }

  @GetMapping( "/initializing" )
  public BaseResponseVoBean initialization() {
    return adminManagerService.initialization();
  }

  @GetMapping( "/create" )
  public BaseResponseVoBean createAdmin() {
    return adminManagerService.createAdminAccount();
  }

  @GetMapping( "/lock" )
  public BaseResponseVoBean lockAdmin() {
    return adminManagerService.lockAdminAccount();
  }
}
