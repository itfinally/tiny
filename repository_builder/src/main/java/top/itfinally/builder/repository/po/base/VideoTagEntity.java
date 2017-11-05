package top.itfinally.builder.repository.po.base;

import java.util.Date;

public class VideoTagEntity extends BaseEntity<VideoTagEntity> {
    private Date endTime;
    private String title;
    private String description;
    private int likeCount;
    private int sysLikeCount;
    private int videoCount;
    private int peopleCount;
    private boolean isActivity;

    private String imageUrl;
    private String thumbUrl;

    private int thumbWidth;
    private int thumbHeight;

    public Date getEndTime() {
        return endTime;
    }

    public VideoTagEntity setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public VideoTagEntity setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public VideoTagEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public VideoTagEntity setLikeCount(int likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public int getSysLikeCount() {
        return sysLikeCount;
    }

    public VideoTagEntity setSysLikeCount(int sysLikeCount) {
        this.sysLikeCount = sysLikeCount;
        return this;
    }

    public int getVideoCount() {
        return videoCount;
    }

    public VideoTagEntity setVideoCount(int videoCount) {
        this.videoCount = videoCount;
        return this;
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public VideoTagEntity setPeopleCount(int peopleCount) {
        this.peopleCount = peopleCount;
        return this;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public VideoTagEntity setActivity(boolean activity) {
        isActivity = activity;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public VideoTagEntity setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public VideoTagEntity setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
        return this;
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public VideoTagEntity setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
        return this;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public VideoTagEntity setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
        return this;
    }

    @Override
    public String toString() {
        return "VideoTagEntity{" +
                "endTime=" + endTime +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", likeCount=" + likeCount +
                ", sysLikeCount=" + sysLikeCount +
                ", id='" + id + '\'' +
                ", videoCount=" + videoCount +
                ", status=" + status +
                ", peopleCount=" + peopleCount +
                ", sortNo='" + sortNo + '\'' +
                ", isActivity=" + isActivity +
                ", createTime=" + createTime +
                ", imageUrl='" + imageUrl + '\'' +
                ", deleteTime=" + deleteTime +
                ", thumbUrl='" + thumbUrl + '\'' +
                ", updateTime=" + updateTime +
                ", thumbWidth=" + thumbWidth +
                ", thumbHeight=" + thumbHeight +
                '}';
    }
}
