package com.nandy007.web.configurer

import com.nandy007.web.core.StaticHelper

// import java.util.HashSet;
// import java.util.Set;

import com.nandy007.web.utils.RedisUtil
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import redis.clients.jedis.JedisPoolConfig// import org.springframework.data.redis.connection.RedisClusterConfiguration;

// import org.springframework.data.redis.connection.RedisInvalidSubscriptionException;
// import org.springframework.data.redis.connection.RedisNode;
// import org.springframework.data.redis.connection.RedisSentinelConfiguration;
// import redis.clients.jedis.JedisCluster;


@Configuration
@PropertySource("classpath:config/redis.properties")
class RedisConfig {

    @Value("\${redis.hostName}")
    private val hostName: String? = null

    @Value("\${redis.port}")
    private val port: Int? = null

    @Value("\${redis.timeout}")
    private val timeout: Int? = null

    @Value("\${redis.maxIdle}")
    private val maxIdle: Int? = null

    @Value("\${redis.maxTotal}")
    private val maxTotal: Int? = null

    @Value("\${redis.maxWaitMillis}")
    private val maxWaitMillis: Int? = null

    @Value("\${redis.minEvictableIdleTimeMillis}")
    private val minEvictableIdleTimeMillis: Int? = null

    @Value("\${redis.numTestsPerEvictionRun}")
    private val numTestsPerEvictionRun: Int? = null

    @Value("\${redis.timeBetweenEvictionRunsMillis}")
    private val timeBetweenEvictionRunsMillis: Long = 0

    @Value("\${redis.testOnBorrow}")
    private val testOnBorrow: Boolean = false

    @Value("\${redis.testWhileIdle}")
    private val testWhileIdle: Boolean = false


    // @Value("${spring.redis.cluster.nodes}")
    // private String clusterNodes;

    // @Value("${spring.redis.cluster.max-redirects}")
    // private Integer mmaxRedirectsac;

    /**
     * JedisPoolConfig 连接池
     * @return
     */
    @Bean
    fun jedisPoolConfig(): JedisPoolConfig {
        val jedisPoolConfig = JedisPoolConfig()
        // 最大空闲数
        jedisPoolConfig.maxIdle = maxIdle!!
        // 连接池的最大数据库连接数
        jedisPoolConfig.maxTotal = maxTotal!!
        // 最大建立连接等待时间
        jedisPoolConfig.maxWaitMillis = maxWaitMillis!!.toLong()
        // 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        jedisPoolConfig.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis!!.toLong()
        // 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        jedisPoolConfig.numTestsPerEvictionRun = numTestsPerEvictionRun!!
        // 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        jedisPoolConfig.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis
        // 是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个
        jedisPoolConfig.testOnBorrow = testOnBorrow
        // 在空闲时检查有效性, 默认false
        jedisPoolConfig.testWhileIdle = testWhileIdle

        return jedisPoolConfig
    }

    /**
     * 单机版配置
     * @Title: JedisConnectionFactory
     * @param @param jedisPoolConfig
     * @param @return
     * @return JedisConnectionFactory
     * @autor lpl
     * @date 2018年2月24日
     * @throws
     */
    @Bean
    fun JedisConnectionFactory(jedisPoolConfig: JedisPoolConfig): JedisConnectionFactory {
        val factory = JedisConnectionFactory()
        //连接池
        factory.setPoolConfig(jedisPoolConfig)
        //IP地址
        factory.hostName = hostName!!
        //端口号
        factory.port = port!!
        //如果Redis设置有密码
        //JedisConnectionFactory.setPassword(password);
        //客户端超时时间单位是毫秒
        factory.timeout = timeout!!

        return factory
    }

    /**
     * 实例化 RedisTemplate 对象
     *
     * @return
     */
    @Bean
    fun functionDomainRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        initDomainRedisTemplate(redisTemplate, redisConnectionFactory)
        return redisTemplate
    }

    /**
     * 设置数据存入 redis 的序列化方式,并开启事务
     *
     * @param redisTemplate
     * @param factory
     */
    private fun initDomainRedisTemplate(redisTemplate: RedisTemplate<String, Any>, factory: RedisConnectionFactory) {
        //如果不配置Serializer，那么存储的时候缺省使用String，如果用User类型存储，那么会提示错误User can't cast to String！
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = GenericJackson2JsonRedisSerializer()
        redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()
        // 开启事务
        redisTemplate.setEnableTransactionSupport(true)
        redisTemplate.setConnectionFactory(factory)
    }

    /**
     * 注入封装RedisTemplate
     * @Title: redisUtil
     * @return RedisUtil
     * @autor lpl
     * @date 2017年12月21日
     * @throws
     */
    @Bean(name = arrayOf("redisUtil"))
    fun redisUtil(redisTemplate: RedisTemplate<String, Any>): RedisUtil {
        val redisUtil = RedisUtil()
        redisUtil.setRedisTemplate(redisTemplate)
        StaticHelper.redisUtil = redisUtil
        return redisUtil
    }
}