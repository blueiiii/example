package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.listener.AppStartupListener;

@Configuration
public class ListenerConfigure {
	@Bean
	public AppStartupListener appStartupListener(){
		return new AppStartupListener();
	}
}
