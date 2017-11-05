package top.itfinally.builder.repository.po.auth;



import top.itfinally.builder.annotation.Association;
import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.repository.po.base.BaseEntity;

import java.util.Date;

@Table( name = "security_employee_details" )
public class EmployeeDetailEntity extends BaseEntity<EmployeeDetailEntity> {
    private int no;
    private int sex;
    private String name;
    private String phone;

    private int loginTime;
    private Date lastLogin;

    private RoleEntity role;

    @Column
    public int getNo() {
        return no;
    }

    public EmployeeDetailEntity setNo(int no) {
        this.no = no;
        return this;
    }

    @Column
    public int getSex() {
        return sex;
    }

    public EmployeeDetailEntity setSex(int sex) {
        this.sex = sex;
        return this;
    }

    @Column
    public String getName() {
        return name;
    }

    public EmployeeDetailEntity setName(String name) {
        this.name = name;
        return this;
    }

    @Column
    public String getPhone() {
        return phone;
    }

    public EmployeeDetailEntity setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    @Column
    public int getLoginTime() {
        return loginTime;
    }

    public EmployeeDetailEntity setLoginTime(int loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    @Column
    public Date getLastLogin() {
        return lastLogin;
    }

    public EmployeeDetailEntity setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    @Association( join = RoleEntity.class )
    public RoleEntity getRole() {
        return role;
    }

    public EmployeeDetailEntity setRole(RoleEntity role) {
        this.role = role;
        return this;
    }

    @Override
    public String toString() {
        return "EmployeeDetailEntity{" +
                "no=" + no +
                ", sex=" + sex +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", id='" + id + '\'' +
                ", loginTime=" + loginTime +
                ", status=" + status +
                ", lastLogin=" + lastLogin +
                ", sortNo='" + sortNo + '\'' +
                ", role=" + role +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
