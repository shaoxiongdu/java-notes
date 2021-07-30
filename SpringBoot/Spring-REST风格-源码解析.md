# Spring-REST风格-源码解析
> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes)

SpringBoot支持的RESTful风格的请求方式，底层是如何实现的呢？
## 准备

- html

  ```html
  <form action="/user" method="POST">
      <input name="_method" hidden value="DELETE">
      <input type="submit" value="DELETE提交 删除用户信息">
  </form>
  ```

- Controller

  ```java
  @RequestMapping(value = "/user",method = RequestMethod.DELETE)
      public String deleteUser(){
          return "DELETE-删除用户";
      }
  ```

## 开始Debug

- 表单提交会带上**_method=PUT**

  ```html
  <form action="/user" method="POST">
      <input name="_method" hidden value="DELETE">
      <input type="submit" value="DELETE提交 删除用户信息">
  </form>
  ```

- 通过Debug发现，请求过来被**HiddenHttpMethodFilter**拦截

  ```java
  // 拦截所有请求
  @Override
  	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
  			throws ServletException, IOException {
  
  		HttpServletRequest requestToUse = request;
  
          //请求是否有错误，并且是POST
  		if ("POST".equals(request.getMethod()) && request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) == null) { 
              //获取到名称为`this.methodParam`的参数
  			String paramValue = request.getParameter(this.methodParam); 
              //如果有这个参数
  			if (StringUtils.hasLength(paramValue)) {
                  //转化为英文大写 DELETE
  				String method = paramValue.toUpperCase(Locale.ENGLISH); //获取到_method的值。
  				if (ALLOWED_METHODS.contains(method)) { //如果是允许的值
                      //将之前的原生请求中的请求方式改为参数_method的请求方式
  					requestToUse = new HttpMethodRequestWrapper(request, method); //包装 
  				}
  			}
  		}
  
          //将包装好的请求放行。
  		filterChain.doFilter(requestToUse, response); 
  	}
  ```

  通过DeBug我们发现，首先请求进来，会if判断是否是POST请求，并且请求中没有错误。之后用`request.getParameter`方法获取到名称为`this.methodParam`的参数。我们发现,`this.methodParam`的值为`_mothod`

  ![image-20210726144142519](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210726144142519.png)

  所以获取到了请求中参数名称为`_method`的值。DELETE

  ![image-20210726143515937](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210726143515937.png)

  然后通过`String method = paramValue.toUpperCase(Locale.ENGLISH);`转换为大写。之后判断是包含在否是允许的请求中。通过Debug，允许的请求如下

  ![image-20210726144219958](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210726144219958.png)

  也就是说兼容以下请求；**PUT**.**DELETE**.**PATCH**，

  之后会重新创建一个HttpMethodRequestWrapper请求。

  这个类支持了请求传参。将原生的请求方式改为传递过来的参数`DELETE`。

  ```java
  public HttpMethodRequestWrapper(HttpServletRequest request, String method) {
  			super(request);
  			this.method = method;
  		}
  ```

  ![image-20210726145110249](https://gitee.com/ShaoxiongDu/imageBed/raw/master//images/image-20210726145110249.png)

  将包装好的请求放行。

  ```java
  filterChain.doFilter(requestToUse, response);
  ```

  此时，

  ### 原生的请求由POST方法改为参数`_method`中指定的请求类型。然后由Controller层接收。

> 文章已同步至GitHub开源项目: [Java超神之路](https://github.com/shaoxiongdu/java-notes) 更多Java相关知识，欢迎访问！