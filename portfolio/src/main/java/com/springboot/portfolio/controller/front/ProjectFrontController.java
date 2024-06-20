package com.springboot.portfolio.controller.front;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.springboot.portfolio.data.dto.*;

import com.springboot.portfolio.data.entity.Project;
import com.springboot.portfolio.service.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;



@Controller
@RequestMapping("/projects")
public class ProjectFrontController {
    //private static String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/bootstrap/assets/project_uploads";

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket;

    private String folderName = "project_imgs/";
    private final ProjectService projectService;

    public ProjectFrontController(AmazonS3 amazonS3, ProjectService projectService) {
        this.amazonS3 = amazonS3;
        this.projectService = projectService;
    }

    @GetMapping()
    public  String getProject(Model model, HttpSession session) {
        if (session.getAttribute("logined") != null) {
            List<ProjectDto> projects = projectService.findByUserId();

            for (ProjectDto projectDto : projects) {
                String imgPath = projectDto.getImg() != null ? projectDto.getImg() : "";
                projectDto.setImg(imgPath);
            }

            Collections.reverse(projects);
            model.addAttribute("projects", projects);

            return "projects";
        } else {
            return "needlogin";
        }
    }

    @PostMapping(value="/add", consumes = "multipart/form-data")
    public String addProject(@ModelAttribute("project") ProjectFrontRequestDto projectFrontRequestDto, @RequestParam("img") MultipartFile file) throws Exception {
        String fileName = null;
        ProjectRequestDto projectRequestDto = new ProjectRequestDto();

        projectRequestDto.setName(projectFrontRequestDto.getName());
        projectRequestDto.setRole(projectFrontRequestDto.getRole());
        projectRequestDto.setDescription(projectFrontRequestDto.getDescription());
        projectRequestDto.setSkill_list(projectFrontRequestDto.getSkill_list());
        projectRequestDto.setGithub(projectFrontRequestDto.getGithub());
        projectRequestDto.setStart_date(projectFrontRequestDto.getStart_date());
        projectRequestDto.setEnd_date(projectFrontRequestDto.getEnd_date());

        if(!file.isEmpty()) {
            fileName = file.getOriginalFilename();

            /*
            // Normalize the file path
            Path path = Paths.get(UPLOAD_DIR, fileName);

            // Save the file on server
            try {
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path);
            } catch (IOException e) {
                e.printStackTrace();
            }
             */
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 폴더명을 포함한 객체 키 설정
            String key = folderName + fileName;

            amazonS3.putObject(bucket, key, file.getInputStream(), metadata);
            String imgUrl = amazonS3.getUrl(bucket, key).toString();
            projectRequestDto.setImg(imgUrl);
        }

        projectService.createProject(projectRequestDto);
        return "redirect:/projects";
    }
    
    @PostMapping(value="/update", consumes = "multipart/form-data")
    public String updateProject(@ModelAttribute("project") ProjectFrontUpdateReqDto projectReqDto, @RequestParam("img") MultipartFile file) throws Exception {
        System.out.println("hihi");
        String fileName = null;
        ProjectDto projectDto1 = new ProjectDto();

        projectDto1.setProject_id(projectReqDto.getProject_id());
        projectDto1.setName(projectReqDto.getName());
        projectDto1.setRole(projectReqDto.getRole());
        projectDto1.setDescription(projectReqDto.getDescription());
        projectDto1.setSkill_list(projectReqDto.getSkill_list());
        projectDto1.setGithub(projectReqDto.getGithub());
        projectDto1.setStart_date(projectReqDto.getStart_date());
        projectDto1.setEnd_date(projectReqDto.getEnd_date());


        if(!file.isEmpty()) {
            fileName = file.getOriginalFilename();
/*
            // Normalize the file path
            Path path = Paths.get(UPLOAD_DIR, fileName);

            // Save the file on server
            try {
                Files.createDirectories(path.getParent());
                Files.copy(file.getInputStream(), path);
            } catch (IOException e) {
                e.printStackTrace();
            }

 */
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 폴더명을 포함한 객체 키 설정
            String key = folderName + fileName;

            amazonS3.putObject(bucket, key, file.getInputStream(), metadata);
            String imgUrl = amazonS3.getUrl(bucket, key).toString();
            projectDto1.setImg(imgUrl);
        }
        projectService.updateProject(projectDto1);

        return "redirect:/projects"; // Redirect to avoid form resubmission
    }
}
