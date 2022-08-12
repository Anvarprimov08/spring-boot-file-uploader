package uz.spring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.spring.model.Attachment;
import uz.spring.model.AttachmentContent;
import uz.spring.repository.AttachmentContentRepository;
import uz.spring.repository.AttachmentRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping(value = "/attachment")
public class AttachmentController {
    private String UPLOAD_PATH = "yuklamalar";
//    create bean
    @Autowired
    AttachmentRepository attachmentRepository;

//    create bean
    @Autowired
    AttachmentContentRepository attachmentContentRepository;

//    file uploader to database
    @PostMapping(value = "/uploadDb")
    public String addFileToDb(MultipartHttpServletRequest request) {
        Iterator<String> fileNames = request.getFileNames();
        String next = fileNames.next();
        List<MultipartFile> files = request.getFiles(next);
        int cnt = 0;
        for (MultipartFile file : files) {
            cnt = cnt + addFileDb(file);
        }
        return cnt + " files si saved to db";
    }

//    file uploader to system
    @PostMapping(value = "/uploadSystem")
    public String addFileToSystem(MultipartHttpServletRequest request) {
        Iterator<String> fileNames = request.getFileNames();
        String next = fileNames.next();
        List<MultipartFile> files = request.getFiles(next);
        int cnt = 0;
        for (MultipartFile file : files) {
            cnt = cnt + addFileSystem(file);
        }
        return cnt + " files si saved to system";
    }

//    getting all attachments as a list and sending it to postman
    @GetMapping(value = "/info")
    public String getAttachments(HttpServletResponse response) throws IOException {
        List<Attachment> attachmentList = attachmentRepository.findAll();
        FileCopyUtils.copy(listToString(attachmentList), response.getWriter());
        return "or see intellij console";
    }

//    getting main content of an attachment
    @GetMapping(value = "/info/{id}")
    public void getattachment(@PathVariable int id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isEmpty())
            return;
        Attachment attachment = optionalAttachment.get();
        response.setContentType(attachment.getContentType());
        response.setHeader("Content-disposition", "attachment; filename="+attachment.getFileOriginalName());
        if (attachment.getDirectoryName() == null){ // this works only when file was saved to database
            Optional<AttachmentContent> optionalAttachmentContent = attachmentContentRepository.findByAttachment_Id(attachment.getId());
            if (optionalAttachmentContent.isEmpty())
                return;
            AttachmentContent attachmentContent = optionalAttachmentContent.get();
            FileCopyUtils.copy(attachmentContent.getMainContent(), response.getOutputStream());
        }
        else {//this works only when file was saved to file system
            FileCopyUtils.copy(new FileInputStream(UPLOAD_PATH+"/"+attachment.getDirectoryName()), response.getOutputStream());
        }
    }

//    saving file to database
    private int addFileDb(MultipartFile file) {
        if (file == null)
            return 0;

        Attachment attachment = new Attachment();
        attachment.setFileOriginalName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());

        AttachmentContent attachmentContent = new AttachmentContent();
        try {
            attachmentContent.setMainContent(file.getBytes());
            Attachment savedAttachment = attachmentRepository.save(attachment);
            attachmentContent.setAttachment(savedAttachment);
            attachmentContentRepository.save(attachmentContent);
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

//    saving file to file system
    private int addFileSystem(MultipartFile file) {
        if (file == null)
            return 0;

        Attachment attachment = new Attachment();
        String originalFilename = file.getOriginalFilename();
        String[] splitList = originalFilename.split("\\.");
        String directoryName = UUID.randomUUID().toString() +"."+ splitList[splitList.length-1];

        attachment.setFileOriginalName(originalFilename);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setDirectoryName(directoryName);

        Path path = Paths.get(UPLOAD_PATH + "/" + directoryName);
        try {
            Files.copy(file.getInputStream(), path);
            attachmentRepository.save(attachment);
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

//    this convert LIst<Attachment> to string
    private String listToString(List<Attachment> attachmentList) {
        StringBuilder stringBuilder = new StringBuilder("");
            attachmentList.forEach(attachment -> {
                stringBuilder.append(attachment.toString());
            });
        return stringBuilder.toString();
    }
}
