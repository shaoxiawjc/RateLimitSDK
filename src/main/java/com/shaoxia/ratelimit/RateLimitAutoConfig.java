package com.shaoxia.ratelimit;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author wjc28
 * @version 1.0
 * @description: TODO
 * @date 2024-06-08 16:29
 */
@Configuration
@ConfigurationProperties("ratelimiter")
@Data
@ComponentScan
public class RateLimitAutoConfig {

	private String redisAddress;

	private String redisPort;


	@Bean
	@ConditionalOnMissingBean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer().setAddress("redis://"+redisAddress+":"+redisPort+"");
		return Redisson.create(config);
	}
}
