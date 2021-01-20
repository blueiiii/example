package com.example.config;

import com.jcraft.jsch.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
public class SftpConfig {

    //CI sftp账号信息
    @Value("${sfpt.host}")
    private String host;
    @Value("${sfpt.username}")
    private String username;
    @Value("${sfpt.password}")
    private String password;
    @Value("${sfpt.port}")
    private Integer port;


    @Bean
    public  ChannelSftp getSftpClient() throws JSchException{
        //获取SFTP Client
        int connectCount = 0;
        int maxConnectCount = 3;
        ChannelSftp sftp;
        while (true) {
            try {
                sftp = connect(host, port, username, password);
            } catch (Exception e) {
                connectCount = connectCount + 1;
                if (connectCount < maxConnectCount) {
                    log.error("connect sftp server failed!");
                    continue;
                } else {
                    log.error("connect sftp server failed!");
                    throw e;
                }
            }
            break;
        }
        return sftp;
    }

    /**
     * 连接sftp服务器
     *
     * @param host     主机
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return
     */
    private  ChannelSftp connect(String host, int port, String username,
                                 String password) throws JSchException {
        ChannelSftp sftp = null;
        JSch jsch = new JSch();
        jsch.getSession(username, host, port);
        Session sshSession = jsch.getSession(username, host, port);
        log.debug("Session created.");
        sshSession.setPassword(password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        sshSession.setConfig(sshConfig);
        sshSession.connect();
        log.debug("Session connected.");
        log.debug("Opening Channel.");
        Channel channel = sshSession.openChannel("sftp");
        channel.connect();
        sftp = (ChannelSftp) channel;
        log.debug("Connected to " + host + ".");
        return sftp;
    }

}
