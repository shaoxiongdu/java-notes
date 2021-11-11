# Spring-Web开发

> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes)

## 静态资源访问与定制化

>  请求进来，先去找Controller看能不能处理。
>  不能处理的所有请求又都交给静态资源处理器。
>  静态资源也找不到则响应404页面

### 1. 默认静态资源目录

只要静态资源放在类路径(`resources`)下： `/static`  `/public` `/resources` `/META-INF/resources`的文件均可以通过`当前项目根路径/ `+` 静态资源名 `访问

### 2. 静态资源访问前缀

   ```yaml
spring:
  mvc:
    static-path-pattern: /res/**
   ```

此时，通过`当前项目根路径/`+ `res/` + `静态资源名`

### 3. 自定义静态资源路径

   ```yaml
spring:
  resources:
    static-locations: [classpath:/haha/]
   ```

此时，静态组件文件夹为类路径下的haha。

### 4. 默认欢迎页面

​	将index.html放入静态资源目录 访问项目自动跳转

### 4. 自定义图标

​	将图标名改为favicon.ico放入静态资源目录即可。

## Restful请求

- /user*    *GET-*获取用户*    *DELETE-**删除用户*     *PUT-**修改用户*      *POST-**保存用户*

- 用法：

    - 表单`method=post`，隐藏域 `_method=put`

      ```html
      <form action="/user" method="POST">
          <input name="_method" hidden value="DELETE">
          <input type="submit" value="DELETE提交 删除用户信息">
        </form>
      ```

    - SpringBoot中手动开启

      ```properties
      #开启REST风格
      spring.mvc.hiddenmethod.filter.enabled=true
      ```

    - 控制层

      ```java
      @RequestMapping(value = "/user",method = RequestMethod.GET)
          public String getUser(){
              return null;
          }
      ```

    

    



















> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes) 更多Java相关知识，欢迎访问！