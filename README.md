# cluster4session

这个项目不再维护，建议直接使用spring session 

## 功能简介

利用redis进行session的集中存储，以实现应用的无状态化。
这个工程是在global-session-filter上更改而成，但是不依赖于它。

更改的主要目的是为了应用spring DelegatingFilterProxy
集成，并进行配置分离，且后台的存储器更换成redis.
增加序列化接口，去掉了原始的对于memcached 代理工程的依赖.

主要解决的问题是web容器session共享的问题，主要作的改进利作redis作为session共享的容器.
对于session共享这个问题解决方案，修改tomcat容器已的相当成熟的组件memcached-session-manager 现在线上应用的就是这个；
redis-session-manager，它们的共同特点都更改tomcat的配置，在这一层作拦截，即给tomcat增加Valve.缺点，就
是需要更改tomcat的权限，它是容器级的更改，而非应用级的.




cluster4session，即提供的功能为应用级的，即引入相应的jar包，配置相应的filter.




## 配置示例:

### web.xml


	<filter>
		<filter-name>GlobalSessionFilter</filter-name>
			<filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
		<init-param>
			<param-name>targetBeanName</param-name>
			<param-value>globalSessionFilter</param-value>
		</init-param>
	</filter>
	
	<filter-mapping>
	    <filter-name>GlobalSessionFilter</filter-name>
	    <url-pattern>/*</url-pattern>
	    <dispatcher>REQUEST</dispatcher>
	</filter-mapping>



### spring.xml



	<bean id="globalSessionFilter" class="com.zz.globalsession.filter.support.RedisSessionFilter" init-method="initSettings" >
		<property name="jedisPool" ref="jedisPool" />
	<!-- 		<property name="cookieName" value="cookieSessionId" /> cookie名字 -->
		<property name="domain" value="test.zz.com" />
	<!-- 		<property name="cookiePath" value="/databatchweb" />  -->
		<property name="secure" value="false" /> <!-- 只有https 才可以设为true-->
		<property name="httpOnly" value="true" />
		<property name="sessiontTimeout" value="30" /> <!-- 单位分钟 -->
		<!--<property name="excludeRegExp" value="/.+\.(html|jpg|jpeg|png|gif|js|css|swf)" 
			/> -->
	</bean>
	
	
	<!--配置redis -->
	<bean id="jedisPoolConfig" class="redis.clients.jedis.JedisPoolConfig">
		<property name="maxTotal" value="${redis.poolconfig.maxTotal}" />
		<property name="maxIdle" value="${redis.poolconfig.maxIdle}" />
		<property name="minIdle" value="${redis.poolconfig.minIdle}" />
		<property name="maxWaitMillis" value="${redis.poolconfig.maxWaitMills}" />
		<property name="testOnBorrow" value="${redis.poolconfig.testOnBorrow}" />
		<property name="testOnReturn" value="${redis.poolconfig.testOnReturn}" />
		<property name="testWhileIdle" value="${redis.poolconfig.testWhileIdle}" />
	</bean>

	<bean id="jedisPool" class="redis.clients.jedis.JedisPool">
		<constructor-arg index="0" ref="jedisPoolConfig" />
		<constructor-arg index="1" value="***" />
		<constructor-arg index="2" value="6389" />
	</bean>


	
	
