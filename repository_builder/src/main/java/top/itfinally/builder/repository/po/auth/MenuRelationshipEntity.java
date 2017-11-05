package top.itfinally.builder.repository.po.auth;


import top.itfinally.builder.annotation.Column;
import top.itfinally.builder.annotation.Table;
import top.itfinally.builder.repository.po.base.BaseEntity;

@Table( name = "security_menu_relationship" )
public class MenuRelationshipEntity extends BaseEntity<MenuRelationshipEntity> {
    /**
     * parentId 直接父节点和间接父节点
     * gap      与根节点的层级距离
     *
     *          0       0, 0
     *        /   \
     *       1     2    0, 1 & 0, 2
     *      /\    /\
     *     1 2   3 4    0, 1-1 . 1, 1-1; 0, 1-2 . 1, 1-2; 0, 2-3 . 2, 2-3; 0, 2-4 . 2, 2-4
     *    /\ /\  /\ /\
     *   5 6 7 8 9 10 11 12
     *
     * 根节点的 parent 指向自身, 即 menu_tree.id = menu_tree.parent_id
     * 同时 gap = 0
     * 注意, 这里的 parent_id 和 child_id 均为 menu_id
     *
     * 插入新节点时, 必须先把所有父节点枚举出来
     * 然后迭代插入父节点, 最后才到自己
     *
     * 插入节点
     * select parent_id, gap as List[MenuTree] from menu_tree where child_id = ?
     * insert into menu_tree( ..., parent_id, child_id, gap ) values
     * for( menuTree in List[MenuTree] ) ( ..., menuTree.parent_id, menu_id, menuTree.gap + 1 )
     *
     * insert into menu_tree( ..., parent_id, child_id, gap ) values ( ..., menu_id, menu_id, 0 )
     *
     * 搜索某节点下所有子节点
     * select child_id from menu_tree where status = ? and parent_id = ?
     *
     * 搜索某节点的所有父节点
     * select parent_id from menu_tree where status = ? and child_id = ?
     *
     * 删除某目录
     * select child_id as List[MenuTree] from menu_tree where parent_id = ?
     * update menu_tree set status = ? where parent_id in List[MenuTree]
     */

    private String parentId;
    private String childId;
    private int gap;

    @Column
    public String getParentId() {
        return parentId;
    }

    public MenuRelationshipEntity setParentId(String parentId) {
        this.parentId = parentId;
        return this;
    }

    @Column
    public String getChildId() {
        return childId;
    }

    public MenuRelationshipEntity setChildId(String childId) {
        this.childId = childId;
        return this;
    }

    @Column
    public int getGap() {
        return gap;
    }

    public MenuRelationshipEntity setGap(int gap) {
        this.gap = gap;
        return this;
    }

    @Override
    public String toString() {
        return "MenuTreeEntity{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", sortNo='" + sortNo + '\'' +
                ", createTime=" + createTime +
                ", deleteTime=" + deleteTime +
                ", updateTime=" + updateTime +
                ", parentId='" + parentId + '\'' +
                ", childId='" + childId + '\'' +
                ", gap=" + gap +
                '}';
    }
}
