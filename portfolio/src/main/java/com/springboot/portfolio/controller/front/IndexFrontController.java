package com.springboot.portfolio.controller.front;

import com.amazonaws.services.s3.AmazonS3;
import com.springboot.portfolio.data.dto.UserDto;
import com.springboot.portfolio.data.entity.User;
import com.springboot.portfolio.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class IndexFrontController {
    private final UserService userService;

    private HttpSession httpSession;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket;

    public IndexFrontController(UserService userService, HttpSession httpSession, AmazonS3 amazonS3) {
        this.userService = userService;
        this.httpSession = httpSession;
        this.amazonS3 = amazonS3;
    }
    @GetMapping("/index")
    public String getUserInfo(Model model) throws Exception {
        User newUser = (User) httpSession.getAttribute("logined");
        if (newUser!=null){
            String userId = newUser.getUser_id();

            User user = userService.findById(userId);

            /*
            // 이미지 파일 경로 설정
            String url = amazonS3.getUrl(bucket, filename).toString();

            String imgPath = user.getImage() != null ? "/bootstrap/assets/uploads/" + user.getImage() : "";

            user.setImage(imgPath);
             */

            String fileName = user.getImage();
            if (fileName != null && !fileName.isEmpty()) {
                user.setImage(fileName);
            }

            model.addAttribute("user",user);
            return "index";
        }
        else{
            return "unloginedindex";
        }

    }
}
