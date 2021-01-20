package com.example.service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

@Service
public class SftpService {

    @Autowired
    private ChannelSftp sftp;

    /**
     * 上传文件
     *
     * @param directory  上传的目录
     * @param uploadFile 要上传的文件
     */
    public  void upload(String directory, String uploadFile) throws SftpException, IOException {
        sftp.cd(directory);
        File file = new File(uploadFile);
        FileInputStream fileInputStream = new FileInputStream(file);
        sftp.put(fileInputStream, file.getName());
        fileInputStream.close();
    }
    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     */
    public  void download(String directory, String downloadFile, String saveFile) throws SftpException, IOException {
        sftp.cd(directory);
        File file = new File(saveFile);
        FileOutputStream outputStream = null;
        outputStream = new FileOutputStream(file);
        sftp.get(downloadFile, outputStream);
        outputStream.close();
    }

    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @return
     * @throws com.jcraft.jsch.SftpException
     */
    public  Vector listFiles(String directory) throws SftpException {
        return sftp.ls(directory);
    }

    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     */
    public  void delete(String directory, String deleteFile) throws SftpException {
        sftp.cd(directory);
        sftp.rm(deleteFile);
    }

}
