package top.itfinally.builder.repository.po.base;

public class UserDetailEntity extends BaseEntity<UserDetailEntity> {
	private String loginName;
	private String password;
	private String salt;
	
	private String nickname;
	private String description;
	private ResourceEntity head;
	private String country;
	private String state;
	private String city;
	private String district;
	private String address;
	private String email;
	private String phone;
	private int gender;
	private int birth;
	private int vipLevel; 		// UserVipKind.xxx.value()

	private int likeCount;		//被赞数
	private int sysLikeCount;
	private int videoCount;
	private int shareCount;		//被分享数
	private int commentCount;
	private int notifyNew;
	
	private int followings;		// 用户关注
	private int followers;		// 用户粉丝
	private int likes;			//点赞别人的视频数统计

	public String getLoginName() {
		return loginName;
	}

	public UserDetailEntity setLoginName( String loginName ) {
		this.loginName = loginName;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public UserDetailEntity setPassword( String password ) {
		this.password = password;
		return this;
	}

	public String getSalt() {
		return salt;
	}

	public UserDetailEntity setSalt( String salt ) {
		this.salt = salt;
		return this;
	}

	public String getNickname() {
		return nickname;
	}

	public UserDetailEntity setNickname( String nickname ) {
		this.nickname = nickname;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public UserDetailEntity setDescription( String description ) {
		this.description = description;
		return this;
	}

	public ResourceEntity getHead() {
		return head;
	}

	public UserDetailEntity setHead( ResourceEntity head ) {
		this.head = head;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public UserDetailEntity setCountry( String country ) {
		this.country = country;
		return this;
	}

	public String getState() {
		return state;
	}

	public UserDetailEntity setState( String state ) {
		this.state = state;
		return this;
	}

	public String getCity() {
		return city;
	}

	public UserDetailEntity setCity( String city ) {
		this.city = city;
		return this;
	}

	public String getDistrict() {
		return district;
	}

	public UserDetailEntity setDistrict( String district ) {
		this.district = district;
		return this;
	}

	public String getAddress() {
		return address;
	}

	public UserDetailEntity setAddress( String address ) {
		this.address = address;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public UserDetailEntity setEmail( String email ) {
		this.email = email;
		return this;
	}

	public String getPhone() {
		return phone;
	}

	public UserDetailEntity setPhone( String phone ) {
		this.phone = phone;
		return this;
	}

	public int getGender() {
		return gender;
	}

	public UserDetailEntity setGender( int gender ) {
		this.gender = gender;
		return this;
	}

	public int getBirth() {
		return birth;
	}

	public UserDetailEntity setBirth( int birth ) {
		this.birth = birth;
		return this;
	}

	public int getVipLevel() {
		return vipLevel;
	}

	public UserDetailEntity setVipLevel( int vipLevel ) {
		this.vipLevel = vipLevel;
		return this;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public UserDetailEntity setLikeCount( int likeCount ) {
		this.likeCount = likeCount;
		return this;
	}

	public int getSysLikeCount() {
		return sysLikeCount;
	}

	public UserDetailEntity setSysLikeCount( int sysLikeCount ) {
		this.sysLikeCount = sysLikeCount;
		return this;
	}

	public int getVideoCount() {
		return videoCount;
	}

	public UserDetailEntity setVideoCount( int videoCount ) {
		this.videoCount = videoCount;
		return this;
	}

	public int getShareCount() {
		return shareCount;
	}

	public UserDetailEntity setShareCount( int shareCount ) {
		this.shareCount = shareCount;
		return this;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public UserDetailEntity setCommentCount( int commentCount ) {
		this.commentCount = commentCount;
		return this;
	}

	public int getNotifyNew() {
		return notifyNew;
	}

	public UserDetailEntity setNotifyNew( int notifyNew ) {
		this.notifyNew = notifyNew;
		return this;
	}

	public int getFollowings() {
		return followings;
	}

	public UserDetailEntity setFollowings( int followings ) {
		this.followings = followings;
		return this;
	}

	public int getFollowers() {
		return followers;
	}

	public UserDetailEntity setFollowers( int followers ) {
		this.followers = followers;
		return this;
	}

	public int getLikes() {
		return likes;
	}

	public UserDetailEntity setLikes( int likes ) {
		this.likes = likes;
		return this;
	}

	@Override
	public String toString() {
		return "UserDetailEntity{" +
				"loginName='" + loginName + '\'' +
				", password='" + password + '\'' +
				", salt='" + salt + '\'' +
				", nickname='" + nickname + '\'' +
				", description='" + description + '\'' +
				", head=" + head +
				", country='" + country + '\'' +
				", state='" + state + '\'' +
				", city='" + city + '\'' +
				", district='" + district + '\'' +
				", address='" + address + '\'' +
				", email='" + email + '\'' +
				", phone='" + phone + '\'' +
				", gender=" + gender +
				", birth=" + birth +
				", vipLevel=" + vipLevel +
				", likeCount=" + likeCount +
				", sysLikeCount=" + sysLikeCount +
				", videoCount=" + videoCount +
				", shareCount=" + shareCount +
				", commentCount=" + commentCount +
				", notifyNew=" + notifyNew +
				", followings=" + followings +
				", followers=" + followers +
				", likes=" + likes +
				'}';
	}
}
