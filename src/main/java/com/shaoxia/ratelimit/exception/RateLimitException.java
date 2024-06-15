package com.shaoxia.ratelimit.exception;

/**
 * @author wjc28
 * @version 1.0
 * @description: TODO
 * @date 2024-06-08 18:10
 */

public class RateLimitException extends Exception{
	public RateLimitException(String message){
		super(message);
	}
}
