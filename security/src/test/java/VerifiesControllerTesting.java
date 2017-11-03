import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.SecurityServerApplication;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.po.UserAuthorityEntity;
import top.itfinally.security.web.vo.RoleVoBean;
import top.itfinally.security.web.vo.UserAuthorityVoBean;

import javax.servlet.Filter;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RunWith( SpringJUnit4ClassRunner.class )
@SpringBootTest( classes = { SecurityServerApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.MOCK )
public class VerifiesControllerTesting {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private Filter springSecurityFilterChain;
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Autowired
    public VerifiesControllerTesting setSpringSecurityFilterChain( Filter springSecurityFilterChain ) {
        this.springSecurityFilterChain = springSecurityFilterChain;
        return this;
    }

    @Autowired
    public VerifiesControllerTesting setContext( WebApplicationContext context ) {
        this.context = context;
        return this;
    }

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup( context )
                .addFilter( springSecurityFilterChain )
                .build();
    }

    // 系统初始化测试, ok
    @Test
    public void initializerTesting() throws Exception {
        String response = mockMvc.perform( get( "/admin/initialization" ) )
                .andExpect( status().isOk() )
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println( response );
    }

    // 创建管理员用户, ok
    @Test
    public void createAdmin() throws Exception {
        String response = mockMvc.perform( get( "/admin/create" ) )
                .andExpect( status().isOk() )
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println( response );
    }

    // 登陆( 成功 or 密码错误 ), ok
    @Test
    public void loginTesting() throws Exception {
        String basic = "Basic " + new String( Base64.encode( "admin:admin".getBytes() ), "utf-8" );
        String loginJsonString = mockMvc.perform( post( "/verifies/login" ).header( "Authorization", basic ) )
                .andExpect( status().isOk() )
                .andReturn()
                .getResponse()
                .getContentAsString();

        JavaType userType = jsonMapper.getTypeFactory().constructParametricType(
                SingleResponseVoBean.class, String.class
        );

        SingleResponseVoBean<String> response = jsonMapper.readValue( loginJsonString, userType );
        System.out.println( response );
    }

    public String login() throws Exception {
        String basic = "Basic " + new String( Base64.encode( "testing:950116".getBytes() ), "utf-8" );
        String loginJsonString = mockMvc.perform( post( "/verifies/login" ).header( "Authorization", basic ) )
                .andExpect( status().isOk() )
                .andReturn()
                .getResponse()
                .getContentAsString();

        JavaType userType = jsonMapper.getTypeFactory().constructParametricType(
                SingleResponseVoBean.class, String.class
        );

        SingleResponseVoBean<String> response = jsonMapper.readValue( loginJsonString, userType );
        return response.getResult();
    }

    // 修改用户角色, ok
    @Test
    public void grantRoleTesting() throws Exception {
        String token = "Bearer " + login();

        String userJsonString = mockMvc.perform( get( "/user/get_own_authority_details" ).header( "Authorization", token ) )
                .andExpect( status().isOk() )
                .andReturn()
                .getResponse()
                .getContentAsString();

        JavaType userType = jsonMapper.getTypeFactory().constructParametricType(
                SingleResponseVoBean.class, UserAuthorityVoBean.class
        );

        // 获取用户 authority 数据
        SingleResponseVoBean<UserAuthorityVoBean> userResponse = jsonMapper
                .readValue( userJsonString, userType );

        String roleJsonString = mockMvc.perform( get( "/authorization/get_roles" ).header( "Authorization", token ) )
                .andExpect( status().isOk() )
                .andReturn()
                .getResponse()
                .getContentAsString();

        JavaType roleType = jsonMapper.getTypeFactory().constructParametricType(
                CollectionResponseVoBean.class, RoleVoBean.class
        );

        // 获取所有 role 元数据
        CollectionResponseVoBean<RoleVoBean> roleResponse = jsonMapper.readValue( roleJsonString, roleType );
        List<RoleVoBean> roles = new ArrayList<>( roleResponse.getResult() );

        List<String> requestBody = new ArrayList<>();
        requestBody.add( roles.get( 0 ).getId() );

        // 开始测试 grant_roles_to 接口, 撤销 admin 账户的 ADMIN 并赋予 USER 角色
        String grantJsonString = mockMvc.perform( post( "/authorization/grant_role_to/{authorityId}", userResponse.getResult().getId() )
                .header( "Authorization", token )
                .header( "Content-Type", "application/json" )
                .content( jsonMapper.writeValueAsString( requestBody ) )
        ).andDo( print() )
                .andExpect( status().isOk() )
                .andReturn()
                .getResponse()
                .getContentAsString();

        System.out.println( grantJsonString );
    }

    @Test
    public void createUser() throws Exception {
        String token = "Bearer " + login();

        String actionJsonString = mockMvc.perform( get( "/user/create_user" ).header( "Authorization", token ) )
                .andDo( print() )
                .andExpect( status().isOk() )
                .andReturn()
                .getResponse()
                .getContentAsString();

        JavaType roleType = jsonMapper.getTypeFactory().constructParametricType(
                SingleResponseVoBean.class, Integer.class
        );

        SingleResponseVoBean response = jsonMapper.readValue( actionJsonString, roleType );
        System.out.println( response );
    }
}
