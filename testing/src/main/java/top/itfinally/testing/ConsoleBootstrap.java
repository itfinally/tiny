package top.itfinally.testing;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import top.itfinally.security.repository.DepartmentRepository;
import top.itfinally.security.repository.DepartmentRoleRepository;
import top.itfinally.security.repository.RoleRepository;
import top.itfinally.security.repository.entity.DepartmentEntity;
import top.itfinally.security.repository.entity.DepartmentRoleEntity;
import top.itfinally.security.repository.entity.RoleEntity;

import java.util.List;

@SpringBootApplication( scanBasePackages = "top.itfinally" )
public class ConsoleBootstrap {
  public static void main( String... args ) {
    SpringApplication.run( ConsoleBootstrap.class, args );
  }

  @Component
  public static class Listener implements ApplicationListener<ContextRefreshedEvent> {

    private RoleRepository roleRepository;
    private DepartmentRepository departmentRepository;
    private DepartmentRoleRepository departmentRoleRepository;

    @Autowired
    public Listener setRoleRepository( RoleRepository roleRepository ) {
      this.roleRepository = roleRepository;
      return this;
    }

    @Autowired
    public Listener setDepartmentRepository( DepartmentRepository departmentRepository ) {
      this.departmentRepository = departmentRepository;
      return this;
    }

    @Autowired
    public Listener setDepartmentRoleRepository( DepartmentRoleRepository departmentRoleRepository ) {
      this.departmentRoleRepository = departmentRoleRepository;
      return this;
    }

    @Override
    public void onApplicationEvent( @NotNull ContextRefreshedEvent event ) {
      if ( departmentRepository.existByName( "department1" ) ) {
        return;
      }

      DepartmentEntity department1 = new DepartmentEntity().setName( "department1" );
      DepartmentEntity department2 = new DepartmentEntity().setName( "department2" );

      RoleEntity role1 = new RoleEntity().setName( "role1" );
      RoleEntity role2 = new RoleEntity().setName( "role2" );
      RoleEntity role3 = new RoleEntity().setName( "role3" );
      RoleEntity role4 = new RoleEntity().setName( "role4" );

      DepartmentRoleEntity dr1 = new DepartmentRoleEntity().setRole( role1 ).setDepartment( department1 );
      DepartmentRoleEntity dr2 = new DepartmentRoleEntity().setRole( role2 ).setDepartment( department2 );
      DepartmentRoleEntity dr3 = new DepartmentRoleEntity().setRole( role3 ).setDepartment( department1 );
      DepartmentRoleEntity dr4 = new DepartmentRoleEntity().setRole( role4 ).setDepartment( department2 );

      departmentRepository.saveAll( Lists.newArrayList( department1, department2 ) );
      roleRepository.saveAll( Lists.newArrayList( role1, role2, role3, role4 ) );
      departmentRoleRepository.saveAll( Lists.newArrayList( dr1, dr2, dr3, dr4 ) );

      List<RoleEntity> dr1_1 = departmentRoleRepository.queryRolesByDepartmentIdIs( department1.getId() );
      System.out.println( dr1_1 );
    }
  }
}