# Mybatis-Plus

## 入门

1. 创建数据库

2. 创建表

3. 初始化spring boot项目

4. 导入依赖

   ```xml
   <dependency>
       <groupId>mysql</groupId>
       <artifactId>mysql-connector-java</artifactId>
       <scope>runtime</scope>
   </dependency>
   
   <dependency>
       <groupId>com.baomidou</groupId>
       <artifactId>mybatis-plus-boot-starter</artifactId>
       <version>3.5.2</version>
   </dependency>
   ```

5. 连接数据库

   ```properties
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.datasource.type=com.zaxxer.hikari.HikariDataSource
   spring.datasource.url=jdbc:mysql://localhost:3306/ssm_test?serverTimezone=UTC&userUnicode=true&characterEncoding=UTF-8
   spring.datasource.username=root
   spring.datasource.password=000000
   ```

6. 使用mybatis-plus

   - enetiy

   - mapper接口

     ```java
     @Repository		//// 代表持久层
     public interface UserMapper extends BaseMapper<User> {
         //继承BaseMapper接口
     }
     ```

   - 主启动类上去扫描我们的mapper包下的所有接口

     ```java
     @MapperScan("com.mui.mapper")
     ```

   - 测试

     ```java
     @SpringBootTest
     class MybatisPlus02ApplicationTests {
     
         // 继承了BaseMapper，所有的方法都来自己父类
         // 我们也可以编写自己的扩展方法！
         @Autowired
         private UserMapper userMapper;
         
         @Test
         void contextLoads() {
             User user = userMapper.selectById(1);
             System.out.println(user);
         }
     }
     ```





## 新版配置

```java
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
```

```java
public interface UserService extends IService<User> {
}
```

```java
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
```







## 配置日志

```yaml
#日志功能
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```





## CRUD扩展

#### 主键生成策略

> 默认 ID_WORKER 全局唯一id

**雪花算法：**

 snowflake是Twitter开源的分布式ID生成算法，结果是一个long型的ID。其核心思想是：使用41bit作为 毫秒数，10bit作为机器的ID（5个bit是数据中心，5个bit的机器ID），12bit作为毫秒内的流水号（意味 着每个节点在每毫秒可以产生 4096 个 ID），最后还有一个符号位，永远是0。可以保证几乎全球唯 一！

```java
public class User {

    /**
     * value:匹配数据库ID
     * type：主键生成策略
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id_test;
    private String name;
    private Integer age;
    private String email;
}
```

**type其与的源码解释**

```java
public enum IdType {
    AUTO(0), // 数据库id自增
    NONE(1), // 未设置主键
    INPUT(2), // 手动输入
    ID_WORKER(3), // 默认的全局唯一id
    UUID(4), // 全局唯一id uuid
    ID_WORKER_STR(5); //ID_WORKER 字符串表示法
}
```





#### 自动填充

==创建时间==、==修改时间==！这些个操作一遍都是自动化完成的，我们不希望手动更新！

 阿里巴巴开发手册：所有的数据库表：gmt_create、gmt_modified几乎所有的表都要配置上！而且需 要自动化！

>方式一：数据库级别（工作中不允许你修改数据库）

1. 在表中新增字段 create_time, update_time

   ```sql
   alter table user
       add create_time datetime default current_timestamp null comment '创建时间';
   
   alter table user
       add update_time datetime default current_timestamp null comment '更新时间';（设置自动更新）
   ```

2. 同步实体类

3. 测试查看效果

> 方式二：代码级别 

1. 删除数据库的默认值、更新操作

2. 实体类字段属性上需要增加注解

   ```java
   @TableField(fill = FieldFill.INSERT)
   private Date createTime;
   @TableField(fill = FieldFill.INSERT_UPDATE)
   private Date updateTime;
   ```

3. 编写处理器来处理这个注解

   ```java
   package com.mui.handler;
   
   import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
   import lombok.extern.slf4j.Slf4j;
   import org.apache.ibatis.reflection.MetaObject;
   import org.springframework.stereotype.Component;
   
   import java.util.Date;
   
   @Slf4j
   @Component      //注册到IOC
   public class MyMetaObjectHandler implements MetaObjectHandler {
       // 插入时的填充策略
       @Override
       public void insertFill(MetaObject metaObject) {
           log.info("insert fill.......");
           //MetaObjectHandler setFieldValByName(String fieldName, Object fieldVal, MetaObject metaObject)
           this.setFieldValByName("createTime",new Date(),metaObject);
           this.setFieldValByName("updateTime",new Date(),metaObject);
   
       }
   
       //更新时的填充策略
       @Override
       public void updateFill(MetaObject metaObject) {
           log.info("update fill.......");
           this.setFieldValByName("updateTime",new Date(),metaObject);
       }
   }
   ```

4. 测试 

5. TableFile参数解释：

   ```java
   public enum FieldFill {
       /**
        * 默认不处理
        */
       DEFAULT,
       /**
        * 插入填充字段
        */
       INSERT,
       /**
        * 更新填充字段
        */
       UPDATE,
       /**
        * 插入和更新填充字段
        */
       INSERT_UPDATE
   }
   ```



#### 乐观锁

当要更新一条记录的时候，希望这条记录没有被别人更新

乐观锁实现方式：

-  取出记录时，获取当前 version 
- 更新时，带上这个version
-  执行更新时， set version = newVersion where version = oldVersion
-  如果version不对，就更新失败

```sql
乐观锁：1、先查询，获得版本号 version = 1
-- A
update user set name = "kuangshen", version = version + 1
where id = 2 and version = 1

-- B 线程抢先完成，这个时候 version = 2，会导致 A 修改失败！
update user set name = "kuangshen", version = version + 1
where id = 2 and version = 1
```

https://baomidou.com/pages/0d93c0/#optimisticlockerinnerinterceptor







#### 分页查询

配置拦截器组件

```java
// 分页插件
 /**
 * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
*/
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.H2));
    return interceptor;
}


/**
*旧版
*/
@Bean
public PaginationInterceptor paginationInterceptor() {
    return new PaginationInterceptor();
}

```

测试

```java
//分页查询
@Test
public void testSelectPage(){
    // 参数一：当前页
    // 参数二：页面大小
    Page<User> page = new Page<>(1,3);
    Page<User> userPage = userMapper.selectPage(page, null);

    page.getRecords().forEach(System.out::println);
    System.out.println(page.getTotal());
}
```



#### 删除操作

```java
@Test  //删除
public void testDeleteById(){
    //userMapper.deleteById(1584154079893770245L);
    userMapper.deleteBatchIds(Arrays.asList(1584153077811576833L,1584154079893770241L));
}

@Test
public void testDeleteMap(){
    Map<String,Object> map=new HashMap<>();
    map.put("id","6");
    userMapper.deleteByMap(map);
}
```

#### 逻辑删除

>物理删除 ：从数据库中直接移除  
>
>逻辑删除 ：再数据库中没有被移除，而是通过一个变量来让他失效！ deleted = 0 => deleted = 1

1. 在数据表中增加一个 deleted 字段

2. 实体类中增加属性

   ```java
   @TableLogic //逻辑删除
   private Integer deleted
   ```

3. 配置

   ```yaml
   mybatis-plus:
     global-config:
       db-config:
         logic-delete-field: flag # 全局逻辑删除的实体字段名(since 3.3.0,配置后可以忽略不配置步骤2)
         logic-delete-value: 1 # 逻辑已删除值(默认为 1)
         logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
   ```

逻辑删除走的是更新操作，查询时动态 AND deleted=0

```sql
UPDATE user SET deleted=1 WHERE id=? AND deleted=0
```

```sql
SELECT * FROM user WHERE id=? AND deleted=0
```





#### 条件构造器

wrapper

```java
@Test
public void test1(){
    // 查询name不为空的用户，并且邮箱不为空的用户，年龄大于等于12
    QueryWrapper<User> wrapper=new QueryWrapper<>();
    wrapper
            .isNotNull("name")
            .isNotNull("email")
            .ge("age",999);

    userMapper.selectList(wrapper).forEach(System.out::println); // 和我们刚才学习的map对比一下
}
```





## 数据安全保护

密钥加密：

```java
// 生成 16 位随机 AES 密钥
String randomKey = AES.generateRandomKey();

// 随机密钥加密
String result = AES.encrypt(data, randomKey);

```

YML 配置：

```yaml
// 加密配置 mpw: 开头紧接加密内容（ 非数据库配置专用 YML 中其它配置也是可以使用的 ）
spring:
  datasource:
    url: mpw:qRhvCwF4GOqjessEB3G+a5okP+uXXr96wcucn2Pev6Bf1oEMZ1gVpPPhdDmjQqoM
    password: mpw:Hzy5iliJbwDHhjLs1L0j6w==
    username: mpw:Xb+EgsyuYRXw7U7sBJjBpA==
```

```java
public void testKey(){
    String randomKey = AES.generateRandomKey();

    System.out.println(randomKey);	// 生成 16 位随机 AES 密钥

    String url="jdbc:mysql://localhost:3306/ssm_test";
    String username="root";
    String password ="000000";
	// 随机密钥加密
    String aesUrl = AES.encrypt(url, randomKey);	
    String aesUsername = AES.encrypt(username, randomKey);
    String aesPassword = AES.encrypt(password, randomKey);
    
    System.out.println("url:"+aesUrl);
    System.out.println("username:"+aesUsername);
    System.out.println("password:"+aesPassword);

}
```

注意！

- 加密配置必须以 mpw: 字符串开头
- 随机密钥请负责人妥善保管，当然越少人知道越好。
- ==本地是不能连接数据库的，这个是用于生产环境的==
