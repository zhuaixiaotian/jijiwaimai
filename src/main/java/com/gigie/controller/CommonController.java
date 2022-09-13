package com.gigie.controller;

import com.gigie.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @PostMapping("/upload")
    public R<String> upload  (MultipartFile file)
    {
        log.info(file.toString());
        String originalFilename = file.getOriginalFilename();

        int i = originalFilename.lastIndexOf(".");
        String substring = originalFilename.substring(i);
        String s = UUID.randomUUID().toString();
        String path="d:\\sd\\img\\";
       File dir= new File(path);
       if (!dir.exists()) {
           dir.mkdirs();
       }
        String filename= path+s+substring;

        System.out.println(filename);
        File localfile=new File(filename);
        try {
            file.transferTo(localfile);
        } catch (IOException e) {
            e.printStackTrace();
        }


        return  R.success(s+substring);
    }
    @GetMapping("/download")
    public  void download(String name, HttpServletResponse response)
    {
        try {
            FileInputStream fileInputStream = new FileInputStream("d:\\sd\\img\\"+name);
            ServletOutputStream outputStream = response.getOutputStream();
            byte[] bytes = new byte[1024];
            response.setContentType("image/jpg");
            int len=0;
            while((len = fileInputStream.read(bytes))!=-1)
            {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }
                outputStream.close();
                fileInputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }














}