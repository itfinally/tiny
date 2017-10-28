import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.itfinally.core.util.DateUtils;
import top.itfinally.security.SecurityServerApplication;
import top.itfinally.security.repository.po.RoleEntity;
import top.itfinally.security.repository.dao.RoleDao;

import java.util.List;

import static top.itfinally.core.repository.QueryEnum.NOT_PAGING;
import static top.itfinally.core.repository.QueryEnum.NOT_STATUS_FLAG;

@AutoConfigureMockMvc
@RunWith( SpringJUnit4ClassRunner.class )
@SpringBootTest( classes = { SecurityServerApplication.class }, webEnvironment = SpringBootTest.WebEnvironment.MOCK )
public class DataVerifiesTesting {
    private RoleDao roleDao;

    @Autowired
    public DataVerifiesTesting setRoleDao( RoleDao roleDao ) {
        this.roleDao = roleDao;
        return this;
    }

    // 时间测试, ok
    // 数据库使用绝对时间, 与显示时间分离
    @Test
    public void dataTimeTesting() {
        List<RoleEntity> roles = roleDao
                .queryAll( NOT_PAGING.getVal(), NOT_PAGING.getVal(), NOT_STATUS_FLAG.getVal() );

        String result = DateUtils.format( roles.get( 0 ).getCreateTime(), true );
        System.out.println( " 数据时间：" + result );
    }
}
