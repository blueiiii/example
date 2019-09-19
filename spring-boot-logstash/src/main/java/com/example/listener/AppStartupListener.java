package com.example.listener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统初始化，用于系统初始化工作
 * @author Administrator
 *
 */
@Slf4j
@WebListener
public class AppStartupListener implements ServletContextListener{
		
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
		log.info("服务器启动...");
		System.out.println("服务器启动.....");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		log.info("服务器退出...");
		
	}
}
