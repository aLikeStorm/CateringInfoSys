package com.aks.cateringinfosys;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling  //开启定时任务的发现
public class CateringInfoSysApplication {

	public static void main(String[] args) {
		SpringApplication.run(CateringInfoSysApplication.class, args);
	}

}
