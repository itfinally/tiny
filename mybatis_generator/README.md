#### 前言

为了偷懒才写的一个生成器, 因为相同的结构不想写太多次, 勤奋从来都不是什么优良品质, 懒惰才是社会进步的第一生产力.

这个生成器在写完第一版后就发现根本不可能做到又轻巧又能智能地支持任意的结构设计, 所以直接
改版, 而我也没想过要写成像 mybatis 官方那种一大堆配置的生成器, 干脆写成比较有针对性的, 结构固定.

想了下之前使用 hibernate 的体验, 直接用注解注明字段和表会比较方便.
由于结构是固定的, 所以所有数据实体必须继承一个抽象实体, 比如每个表的 id, createTime, deleteTime, updateTime 
之类的就可以抽象成一个抽象实体去继承.

这个生成器仅仅生成 mybatis 的 mapper, xml 以及对应的 dao, 而不会生成对应的数据表, 个人觉得如果涉及到建表,
那么将会衍生出诸如 数据类型映射, 维护表关系 等一系列的问题, 这些可不是一两千行代码可以解决的问题, 感觉要是做这么彻底还不如直接回头用 hibernate.

关于建表, 还是老老实实自己设计比较好, 一旦数据表设计混乱, 那么必然会导致代码量暴增, 毕竟前人挖坑, 后人是要补的.

#### demo

```java
import top.itfinally.builder.annotation.*;

@MetaData
class A {
    private String id;
    
    public A setId() {}
   
    @Id
    @Column
    public String getId() {}
}

@Table( name = "t_b" )
class B extends A {
    private String name;
    private String photoAddress;
    
    @Column
    private int no;
    
    public B setName() {}
    
    @Column
    public String getName() {}
    
    @Column
    public B setPhotoAddress() {}
    
    public String getPhotoAddress() {}
    
    public B setNo() {}
    
    public int getNo() {}
}

@Table( name = "t_c" )
class C extends A {
    private B b;
    
    public B getB() {}
    
    @Association( join = B.class )
    public C setB() {}
}
```
* @MetaData 是用来声明该类是一个抽象实体
* @Table 是用来声明该类是一个表, 所有被 @Table 声明的实体都会生成对应的 dao/mapper/xml 文件
* @Column 声明该字段是表内的行, 如果没有该注解修饰, 则忽略
* @Association 声明这是个多对一的关联字段, 该注解存在时会忽略 @Column 注解

每个表都应该有一个 id , 用 @Id 声明.

在默认情况下, 生成器会使用字段名进行 驼峰-下划线 两种命名的转换, 如果要声明数据表字段的, 可以使用 column 属性进行修改. 
另外, @Association 的 join 属性是填写你要关联的实体的类对象, @Column, @Association 这类字段声明的注解可以写在
字段, 或者 getter/setter 方法上, 扫描的优先级是 field > getter > setter .

写好相关的实体后基本就完成 80% 了.

使用很简单, 直接配置 BuilderConfigure 类, 然后用 FileBuilder 生成即可
```java
import top.itfinally.builder.bootstrap.BuilderConfigure;
import top.itfinally.builder.core.FileBuilder;

class Main {
    public static void main( String[] args ) {
        BuilderConfigure configure = new BuilderConfigure()
        .setTargetFolder( "存放生成文件的目录, 一般与实体类根目录同级" )
        .setScanPackage( "扫描路径, 填写你的实体类根目录" )
        .setPackageName( "填写生成的类的 package , 要与 targetFolder 的路径保持重合" )
        .setBaseEntity( "抽象实体的类对象" )
        .setAbstractDaoCls( "抽象 dao 类, 设置后所有生成的 dao 都会继承该类, 可选" )
        .setBaseMapperCls( "基础 mapper 类, 同上, 可选" )
        .setTimeUnit( "时间类型, 必须与抽象实体使用的时间类型一致, 仅支持 Date.class 与 long.class, 默认 long.class, 可选" )
        .setEntityEndWith( "实体类结尾的单词, 比如 AEntity, BEntity 等, 默认是 Entity, 可选" );
        
        // 检查必要的配置
        configure.checking();
        
        new FileBuilder( configure )
        
        // 生成 基础 mapper 与 抽象 dao
        .initialize()
        
        // 生成所有实体对应的 mapper 与 dao 
        .build();
    }
}
```

呀, 跑完之后就可以使用啦, 实在是受够不断重复写 mapper.xml 里面的实体关联和定义了.