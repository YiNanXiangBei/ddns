# 说明
ddns-web项目是一个独立的子项目，主要实现了一个简单的web服务器，提供了基本的登录登出功能（session方式），同时定义了基本的拦截器、控制器接口，除此之外，本项目为了尽可能简单，免除了数据库的操作（如有需要可以自己定义实现数据库操作相应接口），而是使用配置文件形式保存基本信息，为了配置能够及时生效，还配有相应监听器，实现相应监听器，可以及时将配置文件更新到必要的地方。
# 如何使用
* 拦截器

实现模块中`IRequestMiddleware`接口，同时将实现的方法注入拦截管理器`MiddlewareManager`中即可，具体使用方式如下：
```java
//BaseMiddleware实现类
public class BaseMiddleware implements IRequestMiddleware{
    @Override
    public void preRequest(FullHttpRequest request) throws Exception {
    
    }
}
```
```java
//在初始化容器中增加拦截器
MiddlewareManager.addMiddleware(new BaseMiddleware());
```
* 控制器

控制层采用了类似于`spring-boot`的注解控制层`@Controller`方式，本项目也提供了基于注解的`@Controller`实现，用户只需要在使用到控制类的地方增加对应注解，即可将其变成控制器，除此之外，我们也采用了`@GetMapping`和`@PostMapping`来针对`get`和`post`请求进行处理，基本使用方式和`spring-boot`没有任何差异。*值得注意的是，为了让项目能够精准扫描到您项目下的`controller`，我们提供了`IRouteConfig`接口，简单实现该接口并提供对应扫描路径可以帮助精准扫描到对应控制层代码*；基本使用方式如下：
```java
//说明基本扫描规则，只需要指定对应PACKAGE_NAME即可
public class BaseRouteConfig implements IRouteConfig {

    private final static String PACKAGE_NAME = "org.yinan.ddns.web.controller";

    @Override
    public void init() {
        init0(PACKAGE_NAME);
    }

}
```
```java
//目前项目仅支持请求参数为FullHttpRequest，返回类型为ResponseInfo
@Controller
public class BaseController {
    @PostMapping("/login")
    public ResponseInfo login(FullHttpRequest request) {
    
    }
    
    @PostMapping("/logout")
    public ResponseInfo logout(FullHttpRequest request) {
    
    }
}
```
*说明：代码中`FullHttpRequest`类型是`netty`的请求类型，通过该请求参数可以获取到请求过来的基本数据；`ResponseInfo`是我们封装的返回类型，可以在其中增加`header`、`cookie`以及一些`code`、`message`和`data`*
* `get`请求获取一些文件

本项目支持通过`get`请求获取文件，目前支持的文件类型有`jpg|jpeg|html|gif|js|css|json|ico`,目前不支持其它类型的扩展文件，后续可能会提供接口来自己配置相关类型,具体静态文件位置我们目前是使用`java -D`命令进行指定，通过定义`app.home`路径指定静态文件位置
* 配置文件动态加载更新

通过实现`IConfig`接口中的几个方法，采用单例模式构造相关配置，通过定义配置文件位置（这里使用的是`java`中原有的`user.home`，也可以自己执行相关参数来代表文件路径），动态加载更新
```java
public class BaseConfig implements IConfig{
    private static final String CONFIG_FILE;
    private static BaseConfig instance = new BaseConfig();
    static {
        String dataPath = System.getProperty("user.home") + "/" + ".yinan/";
        File file = new File(dataPath);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        CONFIG_FILE = dataPath + "/config.json";
    }
    
    @Override
    public void update(String configJson) {

    }

    @Override
    public void addConfigChangedListener(ConfigChangedListener listener) {
        configChangedListeners.add(listener);
    }

    @Override
    public void notifyListener() {
        List<ConfigChangedListener> changedListeners = new ArrayList<>(configChangedListeners);
        changedListeners.forEach(ConfigChangedListener::onChanged);
    }

    @Override
    public void removeConfigChangedListener(ConfigChangedListener listener) {
        configChangedListeners.remove(listener);
    }
    
    public static BaseConfig getInstance() {
        return instance;
    }
}
```