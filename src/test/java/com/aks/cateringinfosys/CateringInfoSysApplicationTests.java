package com.aks.cateringinfosys;

import com.aks.cateringinfosys.entry.City;
import com.aks.cateringinfosys.mappers.CityMapper;
import org.assertj.core.internal.bytebuddy.implementation.bind.annotation.IgnoreForBinding;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootTest
class CateringInfoSysApplicationTests {

	@Autowired
	DataSource dataSource;
	@Resource
	CityMapper cityMapper;
	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Test
	void contextLoads() {
		System.out.println(dataSource);
		System.out.println(cityMapper.queryCityList());
	}

	@Test
	void redisConnectTest(){
		stringRedisTemplate.opsForValue().set("hello","hello");
		String hello = stringRedisTemplate.opsForValue().get("hello");
		System.out.println(hello);
	}

}
