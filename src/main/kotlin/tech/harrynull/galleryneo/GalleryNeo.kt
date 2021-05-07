package tech.harrynull.galleryneo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericToStringSerializer

@SpringBootApplication
@EnableCaching
class GalleryNeo(private val redisProperties: RedisProperties) {
    @Bean
    fun lettuceConnectionFactory() =
        LettuceConnectionFactory(RedisStandaloneConfiguration(redisProperties.host, redisProperties.port))

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any>? {
        val template = RedisTemplate<String, Any>()
        template.setConnectionFactory(lettuceConnectionFactory())
        template.valueSerializer = GenericToStringSerializer(Any::class.java)
        return template
    }

    @Bean
    fun cacheManager() = RedisCacheManager.builder(lettuceConnectionFactory()).build()
}

fun main(args: Array<String>) {
    runApplication<GalleryNeo>(*args)
}
