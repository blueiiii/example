package com.example.controller;

import com.example.config.SftpConfig;
import com.example.service.SftpService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

@RestController
@Validated
@RequestMapping(value = "/sftp")
@Slf4j
public class StfpController {

    @Value("${sfpt.root.path}")
    private String sftpRootPath;

    @Value("${sfpt.local.uploadpath}")
    private String localUploadPath;
    @Value("${sfpt.local.downpath}")
    private String localDownloadPath;

    private SftpService sftpService;

    @ApiOperation(value = "list files", notes = "list files")
    @GetMapping("/list")
    public   List<String> listFiles() throws SftpException {

        List<String> fileNames = new ArrayList<>();
        Vector<ChannelSftp.LsEntry> vector =  sftpService.listFiles(sftpRootPath);
        vector.forEach(lsEntry -> {
            String filename = lsEntry.getFilename();
            if (!(".".equals(filename)|| "..".equals(filename))){
                fileNames.add(filename);
                log.info("文件:{}",filename);

            }
        });
        Collections.sort(fileNames);
        return fileNames;
    }

    @ApiOperation(value = "download file", notes = "download file")
    @PutMapping("/download")
    public String download(@RequestParam  String fileName) throws SftpException, IOException {
        sftpService.download(sftpRootPath,fileName,localDownloadPath+fileName);

        // 读取内容
        File file = new File(localDownloadPath+fileName);
        BufferedReader reader = null;
        StringBuffer sbf = new StringBuffer();
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempStr;
            while ((tempStr = reader.readLine()) != null) {
                sbf.append(tempStr);
            }
            reader.close();
            return sbf.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return sbf.toString();
    }
}
