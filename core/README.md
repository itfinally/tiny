这个库主要是收集一些所有模块都会用到的公用工具和类, 以防依赖构建时连带一些没用的业务or流程也打包进模块里.
这个文档就说说有什么工具可用, 以及整个项目的构建是怎样的.

#### Base repository
其实 mybatis 的优点也正是它自身的缺点, 针对这个情况, 当初也试了写 mybatis 的拦截器,
尝试 mybatis 的 java api 方式的动态 SQL, 也想过 mybatis 的 xml 是否能继承( 结果当然是不能 ).

最后尝试出一种比较合理的方式, 就是恢复 dao 层( 虽然 mybatis 官方宣称不用实现 dao ), 其设计结构如下:
```
        BaseMapper  -> Maper1,     Mapper2,    Mapper3,  more...
            ｜
        AbstractDao -> Dao1,       Dao2,       Dao3,     more...
        
                       Service1,   Service2,   Service3, more...
```

就是说, Mappers 会有一个 BaseMapper, Daos 会有一个 AbstractDao, 而 AbstractDao 
实现 BaseMapper 接口( 为了泛型和类型检查 ), 而所有的 service 直接调用 dao, 不允许越级调用 mapper.
 
乍一看, 好像也没什么, 不过多了个 dao 层, 好像还画蛇添足了? 事实上, dao 负责事务( 是的, service 纯粹负责业务 ), 
逻辑外键检查, 缓存数据运算结果, 并且通过合理合并处理 mapper, 对上层提供更符合语义的接口.
而 mapper 是完全面向数据库表, 仅仅负责 sql 的处理( mybatis 的 xml mapper 绑定 ), 
简而言之, dao 是对上层的更高层次的封装.

其实这种结构更符合将来如果涉及到把整个数据库抽离成服务的设计, 尤其是完全没法进行跨库 join 的场景,
而这个也是在作者设计类似的系统时参考一些文章, 咨询过别人后想到比较合理的设计, 
配合 rpc 使用更佳, 当然这不是本文重点.

说到逻辑外键检查, 是因为物理外键在插入时, 会连带外键关联的数据一并锁定( 尤其是父表也存在频繁修改的情况下容易出现死锁 ). 
当然这个逻辑外键检查不完全可靠( 数据可以在系统查询后检查前被其他线程变更 ), 但是这个缺点可以通过合理设计规避掉.
 
言归正传, 数据库表的通用的字段( 比如 id, createTime 之类的 )被抽成一个 BaseEntity, 而在 mybatis 中所有 mapper 的 resultMap 
都会通过 extend 属性继承 baseEntity 的字段( 然而 sql 的定义并不能继承 ).
```xml
<mapper namespace="top.itfinally.security.repository.mapper.PermissionMapper">
    <resultMap id="permissionEntity" type="PermissionEntity"
    
               extends="top.itfinally.core.repository.mapper.BaseMapper.baseEntity">
               <!-- 注意这里的 extends 属性 -->
               
        <result property="name" column="name" javaType="String"/>
        <result property="description" column="description" javaType="String"/>
    </resultMap>
</mapper>
```

以上描述的类均在 repository 包内.


#### util

整个工具包均使用函数式风格来编写, 不得不说这种风格写出的代码更安全, 更符合人类阅读.
当然业务代码的快速实现还是用回命令式风格比较好, 毕竟从学校到工作用的都是这种, 就像呼吸一样自然.

所谓函数式编程, 我的初步理解是酱紫的:
 * 所有变量均为 final, 一经初始化就不能再更改状态, 要更改状态只能重新定义( 完全无并发问题 )
 * 因为变量状态不可变, 所以不存在 for, while, 迭代全靠递归( 不得不承认递归的运行速度更快 )
 * 尽量把各个步骤拆成细粒度的函数, 通过输入/输出修改数据, 而不是修改变量本身( 在函数式编程里, 函数成为一等公民是有原因的 )
 
可惜 jvm 不存在尾递归优化的概念, 不然递归是完全不会溢出的, 测试过在数据量过多的时候, for 循环也会导致栈溢出.

工具包内可用的工具如下:
 * CollectionUtils  | 内含集合 并/交/差 三种集合运算以及多集合并行迭代的 zip 方法.( 如果用过 python 就知道 zip 是神马啦 )
 * DateUtils        | 问了下大家遇到的各种日期表现形式而写的日期处理工具, 包含时区处理.( 机器时间与显示形式的相互转换 )
 * FileScanUtils    | 一个文件迭代工具, 给出一个有效路径, 这家伙能把整个族谱翻出来.( 在遍历大概 12k 份文件后会栈溢出, 所以文件太多的话建议进行拆分迭代 )
 * ObjectUtils      | 一个看着没什么卵用的工具, 但是里面的 getOrDefault 操作旨在提供类似 ?. 这种空检查的访问效果.( 什么时候 java 也在语法层上支持就更好了 )
 * PropertiesUtils  | 提供一个更便于读写 properties 文件的工具, 这个工具总是会从 claasspath 下开始寻找文件.
 * RegExpUtils      | java 原生的 re api 比较麻烦, 根据 python 的 re 模块风格封装而成.
 * RestUrlScanHelper| 一个扫描 Controller/RestController 接口的工具, 会把所有接口信息( 包括参数类型和参数名, 是的没错, 参数名也可以扫出来 )收集起来返回一个接口信息集合.
 
注意, 以上工具需要依赖 guava 以及 spring mvc.