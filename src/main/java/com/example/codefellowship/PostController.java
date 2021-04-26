package com.example.codefellowship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class PostController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    ApplicationUserRepository applicationUserRepository;


    @PostMapping("/addPost")
    public RedirectView addPost(@RequestParam String body , Principal p){

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        String formatDateTime = now.format(format);

        ApplicationUser user = applicationUserRepository.findByUsername(p.getName());

        Post post =new Post(body,formatDateTime,user);

        postRepository.save(post);

        return  new RedirectView("/profile" );
    }


}
