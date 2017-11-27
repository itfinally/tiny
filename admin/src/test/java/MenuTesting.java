import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import top.itfinally.admin.AdminBootstrap;
import top.itfinally.admin.repository.po.MenuItemEntity;
import top.itfinally.admin.web.vo.MenuItemVoBean;
import top.itfinally.core.vo.CollectionResponseVoBean;
import top.itfinally.core.vo.SingleResponseVoBean;
import top.itfinally.security.SecurityServerApplication;

import javax.servlet.Filter;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@RunWith( SpringJUnit4ClassRunner.class )
@SpringBootTest( classes = { AdminBootstrap.class }, webEnvironment = SpringBootTest.WebEnvironment.MOCK )
public class MenuTesting {
    private final ObjectMapper jsonMapper = new ObjectMapper();
    private Filter springSecurityFilterChain;
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Autowired
    public MenuTesting setSpringSecurityFilterChain( Filter springSecurityFilterChain ) {
        this.springSecurityFilterChain = springSecurityFilterChain;
        return this;
    }

    @Autowired
    public MenuTesting setContext( WebApplicationContext context ) {
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

    private MenuItemVoBean addedRootMenuItem( String name, boolean isLeaf ) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add( "name", name );
        params.add( "isLeaf", Boolean.toString( isLeaf ) );

        JavaType menuItemType = jsonMapper.getTypeFactory().constructParametricType(
                SingleResponseVoBean.class, MenuItemVoBean.class
        );

        SingleResponseVoBean<MenuItemVoBean> rootResponse = jsonMapper.readValue(
                mockMvc.perform( post( "/menu/added_root_menu" ).params( params ) )
                        .andReturn().getResponse().getContentAsString(), menuItemType
        );

        return rootResponse.getResult();
    }

    private MenuItemVoBean addedMenuItem( String parentId, String name, boolean isLeaf ) throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add( "name", name );
        params.add( "parentId", parentId );
        params.add( "isLeaf", Boolean.toString( isLeaf ) );

        JavaType menuItemType = jsonMapper.getTypeFactory().constructParametricType(
                SingleResponseVoBean.class, MenuItemVoBean.class
        );

        SingleResponseVoBean<MenuItemVoBean> rootResponse = jsonMapper.readValue(
                mockMvc.perform( post( "/menu/added_menu" ).params( params ) )
                        .andReturn().getResponse().getContentAsString(), menuItemType
        );

        return rootResponse.getResult();
    }

    @Test
    public void addedRootMenu() throws Exception {
        MenuItemVoBean root2 = addedRootMenuItem( "节点2", false );
        addedMenuItem( root2.getId(), "节点2-1", true );
        addedMenuItem( root2.getId(), "节点2-2", true );

        MenuItemVoBean root23 = addedMenuItem( root2.getId(), "节点2-3", false );
        addedMenuItem( root23.getId(), "节点2-3-1", true );
        addedMenuItem( root23.getId(), "节点2-3-2", true );

        MenuItemVoBean root3 = addedRootMenuItem( "节点3", false );
        addedMenuItem( root3.getId(), "节点3-1", true );
        addedMenuItem( root3.getId(), "节点3-3", true );

        MenuItemVoBean root32 = addedMenuItem( root3.getId(), "节点3-2", false );
        addedMenuItem( root32.getId(), "节点3-2-1", true );
        addedMenuItem( root32.getId(), "节点3-2-2", true );
    }

    @Test
    public void getMenuTree() throws Exception {
        JavaType menuItemType = jsonMapper.getTypeFactory().constructParametricType(
                CollectionResponseVoBean.class, MenuItemVoBean.class
        );

        CollectionResponseVoBean<MenuItemVoBean> menuItems = jsonMapper.readValue(
                mockMvc.perform( get( "/menu/get_menu_tree" ) )
                        .andReturn().getResponse().getContentAsString(), menuItemType
        );

        System.out.println(menuItems);
    }
}
