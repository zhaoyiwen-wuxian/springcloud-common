package com.trench.cofig.configuration;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
import com.trench.aop.api.EnableApiVersion;
import com.trench.cofig.intercepter.WebIntercepterConfig;
import com.trench.cofig.util.ApplicationUtil;
import com.trench.exception.GlobalException;
import com.trench.feign.FeignParamInterceptor;
import com.trench.feign.FeignParamterProperties;
import com.trench.lock.RedisDistributedLock;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class BaseConfiguration {

    //基础配置类
    @Configuration
    public static class BaseAutoConfiguration {
        @Bean
        private ApplicationUtil getApplicationUtil(){return new ApplicationUtil();}
    }
    //内部配置
    @Configuration
    public static class WebConfiguration{
        //装配全局异常处理类
        @Bean
        private GlobalException getGlobalException(){
            return new GlobalException();
        }

        //拦截器
        @Bean
        private WebIntercepterConfig getWebIntercepterConfig(){return new WebIntercepterConfig();}
    }

    //naces配置
    @Configuration
    @EnableDiscoveryClient
    public static class NacosConfiguration{

        @Bean
        @Primary
        private NacosDiscoveryProperties  getNacosDiscoveryProperties(){
            NacosDiscoveryProperties properties=new NacosDiscoveryProperties();
            Map<String,String> map=new ConcurrentHashMap<>();
            map.put("online.time",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            properties.setMetadata(map);
            return properties;
        }
    }
    @Configuration
    @EnableFeignClients(basePackages = {"com.trench.server"})
    public static class FeignConfiguration{
    }

    @Configuration
    @EnableApiVersion //自动进行注册版本号
    public static class ApiVersionConfiguration{
    }

    //feign重试配置
    @Configuration
    public static class feignConfiguration{

        /**
         * long period, 重试的间隔
         * long maxPeriod, 重试的最大间隔时间，默认为1秒，重试间隔按照一定的规律进行逐渐增大，但是不能超过最大的间隔时间
         * int maxAttempts 最大的重试次数：默认5次
         * */
        @Bean
        public Retryer getRetryer(){
            return new Retryer.Default(100,500,2);
        }
    }


    //Ribbon配置
    @Configuration
    public static class RibbonLoadBalanceMicroOrderConfig{
        private String name = "business-class";
        @Bean
        @ConditionalOnClass
        public IClientConfig defaultClientConfigImpl() {

            DefaultClientConfigImpl config = new DefaultClientConfigImpl();
            config.loadProperties(name);
            config.set(CommonClientConfigKey.MaxAutoRetries,2);
            config.set(CommonClientConfigKey.MaxAutoRetriesNextServer,2);
            config.set(CommonClientConfigKey.ConnectTimeout,2000);
            config.set(CommonClientConfigKey.ReadTimeout,4000);
            config.set(CommonClientConfigKey.OkToRetryOnAllOperations,true);
            return config;
        }


        /*
         * 判断服务是否存活
         * 不建议使用，每次请求前都ping服务，判断连接是否成功
         * */
        //@Bean
   /*     public IPing iPing() {
            //这个实现类会去调用服务来判断服务是否存活，默认是new NoOpPing();
            return new PingUrl();
        }*/

        //负载均衡策略
        @Bean
        public IRule ribbonRule() {
            //线性轮训
            new RoundRobinRule();
            //可以重试的轮训
            new RetryRule();
            //根据运行情况来计算权重
            new WeightedResponseTimeRule();
            //过滤掉故障实例，选择请求数最小的实例
            new BestAvailableRule();
            //随机
            return new RandomRule();
        }
    }

    @Configuration
    @EnableEurekaClient
    public static class MiroRibbonLoadConfiguration{
        @Bean
        @LoadBalanced
        public RestTemplate getRestTemplate(){
            return new RestTemplate();
        }
    }

    @Configuration
    @EnableConfigurationProperties(value = FeignParamterProperties.class)
    @EnableFeignClients(basePackages = "com.trench.feign",defaultConfiguration = FeignParamInterceptor.class)
    public static class FeignParamInterceptor{
    }
    @Configuration
    public static class RedisConfig{

        @Value("${spring.redis.database}")
        private int database;
        @Value("${spring.redis.host}")
        private String host;
        @Value("${spring.redis.port}")
        private int port;
        @Value("${spring.redis.password}")
        private String password;
        @Value("${spring.redis.timeout}")
        private int timeout;
        @Value("${spring.redis.jedis.pool.max-active}")
        private int maxActive;
        @Value("${spring.redis.jedis.pool.max-wait}")
        private long maxWaitMillis;
        @Value("${spring.redis.jedis.pool.max-idle}")
        private int maxIdle;
        @Value("${spring.redis.jedis.pool.min-idle}")
        private int minIdle;


        /**
         * 连接池配置信息
         */

        @Bean
        public JedisPoolConfig jedisPoolConfig() {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            // 最大连接数
            jedisPoolConfig.setMaxTotal(maxActive);
            // 当池内没有可用连接时，最大等待时间
            jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
            // 最大空闲连接数
            jedisPoolConfig.setMinIdle(maxIdle);
            // 最小空闲连接数
            jedisPoolConfig.setMinIdle(minIdle);
            // 其他属性可以自行添加
            return jedisPoolConfig;
        }



        /**
         * redisTemplate配置
         * 序列化的几种方式:
         * OxmSerializer
         * ByteArrayRedisSerializer
         * GenericJackson2JsonRedisSerializer
         * GenericToStringSerializer
         * StringRedisSerializer
         * JdkSerializationRedisSerializer
         * Jackson2JsonRedisSerializer
         * @param redisConnectionFactory redis连接工厂
         * @return RedisTemplate
         */
        @Bean
        public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<Object, Object> template = new RedisTemplate<>();
            // 配置连接工厂
            template.setConnectionFactory(redisConnectionFactory);
            // 设置key的序列化方式
            template.setKeySerializer(new StringRedisSerializer());
            // 设置value的序列化方式
            template.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
            return template;
        }

        /**
         * 缓存管理器
         *
         * @param connectionFactory
         * @return
         */
        @Bean
        public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
            return RedisCacheManager.create(connectionFactory);
        }

        @Bean
        public RedisTemplate<String, Serializable> redisTemplate(JedisConnectionFactory connectionFactory) {
            RedisTemplate<String, Serializable> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.afterPropertiesSet();
            return template;
        }

        @Bean
        public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
            StringRedisTemplate template = new StringRedisTemplate();
            template.setConnectionFactory(factory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.afterPropertiesSet();
            return template;
        }

        @Bean
        @ConditionalOnBean(StringRedisTemplate.class)
        public RedisDistributedLock redisDistributedLock(StringRedisTemplate redisTemplate) {
            return new RedisDistributedLock(redisTemplate);
        }

        /**
         * Jedis 连接
         *
         * @param jedisPoolConfig
         * @return
         */
        @Bean
        public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
            JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling()
                    .poolConfig(jedisPoolConfig).and().readTimeout(Duration.ofMillis(timeout)).build();
            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
            redisStandaloneConfiguration.setHostName(host);
            redisStandaloneConfiguration.setPort(port);
            redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
            return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
        }

    }
}

