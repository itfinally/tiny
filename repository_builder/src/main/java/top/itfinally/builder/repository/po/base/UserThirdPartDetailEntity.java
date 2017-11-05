package top.itfinally.builder.repository.po.base;

import java.util.Date;

public class UserThirdPartDetailEntity extends BaseEntity<UserThirdPartDetailEntity> {
    private int thirdType; // 账号类型 1：facebook 2：twitter 3：Instagram 4：google
    private String thirdId;
    private String fullName;
    private String email;
    private String phone;
    private String headImgUrl;
    private String description;
    private String url; // 个人主页

    private String country;
    private String state;
    private String city;
    private String district;
    private String address;
    private Date lastLogin;

    private UserDetailEntity user;

    public int getThirdType() {
        return thirdType;
    }

    public UserThirdPartDetailEntity setThirdType(int thirdType) {
        this.thirdType = thirdType;
        return this;
    }

    public String getThirdId() {
        return thirdId;
    }

    public UserThirdPartDetailEntity setThirdId(String thirdId) {
        this.thirdId = thirdId;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public UserThirdPartDetailEntity setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserThirdPartDetailEntity setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserThirdPartDetailEntity setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public UserThirdPartDetailEntity setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UserThirdPartDetailEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public UserThirdPartDetailEntity setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public UserThirdPartDetailEntity setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getState() {
        return state;
    }

    public UserThirdPartDetailEntity setState(String state) {
        this.state = state;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserThirdPartDetailEntity setCity(String city) {
        this.city = city;
        return this;
    }

    public String getDistrict() {
        return district;
    }

    public UserThirdPartDetailEntity setDistrict(String district) {
        this.district = district;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public UserThirdPartDetailEntity setAddress(String address) {
        this.address = address;
        return this;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public UserThirdPartDetailEntity setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    public UserDetailEntity getUser() {
        return user;
    }

    public UserThirdPartDetailEntity setUser(UserDetailEntity user) {
        this.user = user;
        return this;
    }

    @Override
    public String toString() {
        return "UserThirdPartDetailEntity{" +
                "thirdType=" + thirdType +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", thirdId='" + thirdId + '\'' +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", fullName='" + fullName + '\'' +
                ", deleteTime=" + deleteTime +
                ", email='" + email + '\'' +
                ", updateTime=" + updateTime +
                ", phone='" + phone + '\'' +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", lastLogin=" + lastLogin +
                ", user=" + user +
                '}';
    }
}
