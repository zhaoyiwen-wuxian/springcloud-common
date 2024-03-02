springcloud架构的统一父工程，（管理子模块，管理依赖插件，依赖版本等）
abillty:能力服务块：存放一些非业务相关的微服务，比如网关，身份认证等
        exce: 网关中的一些异常信息处理
        gateway:网关服务，包含token认证，接口信息认证是否放行，IP白名单认证，token是否要过期，用户还没有进行退出登录
busuiness:存放业务服务块，比如用户，订单，商品等
    core:微服务核心模块，微服务的核心运行模块
    protocol:请求参数核心模块
    fegin：微服务调用模块，当前微服务提供给其他微服务调用的模块
    protocol:请求参数的模块和验证请求参数是否合规
    server：编写业务层的代码逻辑
coommon:核心工具模块，根据不用的框架功能，划分不同的模块
    core:非wab环境的核心工具
    web：web环境的核心工具
    properties:在springboot中的yml中进行提示的字段
    mysql：mysql的核心配置文件
    mdc：监听日志的路径和存放位置并且按照yml中的服务名称写为日志名称和路径
    holder：applicationContext中静态变量得到Bean
    util:一些需要进行封装的公共方法
    aop：aop切面的整合
    annotation: 自定义注解
    redis: redis 的配置和封装
config:本地的统一配置文件，管理微服务的统一配置信息
data: 数据管理
    basic：实体的封装
    entity:实体类的管理
    mapper：映射文件的管理


select * from test t1 left join  test1 as t2 on t1.id=t2.uid

Mapper.xml中的SQL语句时，进行多表查询
@Data
@Accessors(chain = true)
public class Test implements Serializable {

    //使用mybatis-plus标签标识主键，如果数据库主键名称和当前字段名称不相同，可以通过value属性设置
    @TableId(type = IdType.AUTO)
    private Integer id;

    //未标识的字段，默认和数据库同名列映射    
    private Integer age;

    //如果数据库字段名称和当前字段名称不相同，可以通过@TableField注解value属性设置    
    //还可以通过该注解设置该列的typeHandler等属性
    @TableField("p_name")
    private String name;
   
    //防止插入报错，告诉Mybatis-plus忽略该字段
    @TableField(exist = false)
    //标识对多映射，指定对多的集合体中实际的类型
    //如果是对一映射，可以使用@ToOne注解即可
    @ToMore(type = Test2.class)
    private List<Test2> likes;
}

//关联的实体类 多的一方
@Data
@Accessors(chain = true)
public class Test2 implements Serializable {

    @TableId(type = IdType.AUTO)
    //需要注意，如果两个表的主键同名，需要通过@IdAlias注解设置一个别名id和数据库的主键映射，SQL语句中也必须指定id别名
    @IdAlias("lid")
    private Integer id;

    private Integer uid;

    private String likes;

    private Date likeTime;
}

//标注@AutoMapping的方法，才会启动自动映射的功能，起到一个局部控制的作用
@AutoMapping
List<Test> queryAll();
