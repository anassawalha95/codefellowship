package com.example.codefellowship;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Set;

@Controller
public class ApplicationUserController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;



    @GetMapping("/")
    public String homeRouting(){
        return "index.html";
    }

    @GetMapping("/signup")
    public String signupRouting(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "signup.html";
        }

        return "index.html";
    }


    @GetMapping("/login")
    public String loginRouting(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login.html";
        }
        return "index.html";
    }



    @GetMapping("/profile")
    public String profile(Model m, Principal p){
        ApplicationUser userDetails = (ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal();
        m.addAttribute("user", applicationUserRepository.findById (userDetails.getId()).get());
        return "profile.html";
    }


    @PostMapping("/signup")
    public RedirectView signup(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String dateOfBirth,
                               @RequestParam String bio){
        ApplicationUser newUser = new ApplicationUser(username,bCryptPasswordEncoder.encode(password),  firstName,  lastName,  dateOfBirth,  bio);
        newUser = applicationUserRepository.save(newUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(newUser, null, new ArrayList<>());
      SecurityContextHolder.getContext().setAuthentication(authentication);
        return new RedirectView("/");
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password,Model m, Principal p){
        ApplicationUser user =  applicationUserRepository.findByUsername(username);


        if(user==null){
            return "login.html";
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "index.html";
    }


    @GetMapping("/users")
    public String allUsers(Model m, Principal p){


        m.addAttribute("users", applicationUserRepository.findAll());
        return "allUsers.html";
    }


    @GetMapping("/users/{id}")
    public String users(@PathVariable(value = "id") int id , Model m){

        ApplicationUser user=  applicationUserRepository.findById(id).get();
        m.addAttribute("user",user);
        return "users.html";
    }


    @PutMapping("/profile")
    public RedirectView editProfileInfo( @RequestParam String password,
                                         @RequestParam String firstName,
                                         @RequestParam String lastName,
                                         @RequestParam String dateOfBirth,
                                         @RequestParam String bio,Principal p){

        String loggedInUserName = p.getName();
        ApplicationUser loggedInUser = applicationUserRepository.findByUsername(loggedInUserName);

            loggedInUser.setPassword(bCryptPasswordEncoder.encode(password));
            loggedInUser.setFirstName(firstName);
            loggedInUser.setLastName(lastName);
            loggedInUser.setDateOfBirth(dateOfBirth);
            loggedInUser.setBio(bio);
            applicationUserRepository.save(loggedInUser);

        return new RedirectView("/profile");

    }

    @GetMapping("/feed")

    public String feed(Model m, Principal p){

        ApplicationUser loggedInUserId=((ApplicationUser) ((UsernamePasswordAuthenticationToken) p).getPrincipal());

        m.addAttribute("allUsers", loggedInUserId);
        return "/feed.html";
    }


    @PostMapping("/follow")
    public RedirectView follow(@RequestParam(value = "followerId") Integer followerId, Model m, Principal p){

        ApplicationUser loggedInUser= (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ApplicationUser follower = applicationUserRepository.getOne(followerId);


        if(!loggedInUser.getUsers().contains(follower) &&  loggedInUser.getId() != follower.getId() ){
//            Set<ApplicationUser> x= loggedInUser.getUsers();
//            x.add(follower);
            loggedInUser.users.add(follower);
            applicationUserRepository.save(loggedInUser);


        }


        return new RedirectView("/users");
    }





}
