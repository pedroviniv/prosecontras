/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifpb.controllers;

import br.edu.ifpb.entity.Opinion;
import br.edu.ifpb.entity.Topic;
import br.edu.ifpb.entity.UserProfile;
import br.edu.ifpb.repository.ImageTopicRepository;
import br.edu.ifpb.repository.TopicRepository;
import br.edu.ifpb.services.OpinionService;
import br.edu.ifpb.services.RelationshipService;
import br.edu.ifpb.services.TopicoService;
import br.edu.ifpb.validator.TopicValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

/**
 *
 * @author kieckegard
 */
@Controller
public class TopicController {
    
    @Autowired
    private HttpSession httpSession;
    
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private TopicoService topicoService;

    @Autowired
    private RelationshipService relationshipService;

    @Autowired
    OpinionService opinionService;

    @RequestMapping(value = "/topic", method=RequestMethod.GET)
    public ModelAndView viewTopico(@RequestParam ("id") String idTopico){

        Topic topic = topicoService.findById(idTopico);

        Map<String, String> percentages = relationshipService.getPercentages(topic);

        ModelAndView mav = new ModelAndView("topico");

        List<Opinion> userOpinion = opinionService.getUserOpinionsByTopicOrderedByDate((UserProfile) httpSession.getAttribute("user"), topic);
        if (userOpinion != null)
            mav.addObject("userOpinions", userOpinion);
        mav.addObject("topic", topic);
        mav.addObject("percentages", percentages);

        return mav;
    }

    @RequestMapping(value = "/novoTopico", method=RequestMethod.GET)
    public String novoTopico(TopicValidator topicValidator) {
        return "novoTopico";
    }
    
    @RequestMapping(value="/novoTopico", method=RequestMethod.POST)
    public ModelAndView cadastrarNovoTopico(@Valid TopicValidator topicValidator, BindingResult result, Model model, @RequestParam("photo") MultipartFile  file) {
        
        String name = topicValidator.getName();
        String description = topicValidator.getDescription();
        UserProfile userProfile = (UserProfile) httpSession.getAttribute("user");
        
        model.addAttribute("name", name);
        model.addAttribute("description", description);
        
        ModelAndView modelAndView = new ModelAndView("novoTopico");

        if (result.hasErrors()) {
            return new ModelAndView("novoTopico");
        } else {

//            Topic topic = new Topic();
//            topic.setName(name);
//            topic.setDescription(description);
//            topic.setPostedDateTime(LocalDateTime.now());
//            topic.setActor(userProfile);
//
//            topic = topicRepository.save(topic);
//
//            try {
////                String photoPath = PhotoUtils.salvarFoto("src/main/resources/static/img/topic/", topic.getId().toString()+file.getOriginalFilename(), file.getInputStream());
////                topic.setPhotoPath(photoPath);
//
//                String objid = ImageTopicRepository.saveTopicImage(file, topic.getId());
//                topic.setPhotoPath(objid);
//
//                topicRepository.save(topic);
//                modelAndView.getModel().put("succes", true);
//            }
//            catch (IOException ex) {
//
//            }

            Topic topic = topicoService.saveTopic(
                    topicValidator.getName(),
                    topicValidator.getDescription(),
                    userProfile,
                    file);
            if (topic != null) {

                modelAndView.getModel().put("succes", true);

            }
            
        }
        
        return modelAndView;
    }


    @RequestMapping(value = "/topicsearch", method = RequestMethod.POST)
    public ModelAndView buscar(@RequestParam("parametro") String parametroBusca) {

        ModelAndView mav = new ModelAndView("/topicsearch");

        mav.addObject("parametro", parametroBusca);

        mav.addObject("topics", topicoService.findByNameLikeIgnoreCase(parametroBusca));

        return mav;

    }


    @RequestMapping(value = "/image/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getImage(@PathVariable String id) {

        byte[] file = ImageTopicRepository.getTopicImage2(id);

        return ResponseEntity.ok()
                .contentLength(file.length)
//                .contentType(MediaType.parseMediaType(file.getGridFSFile().getMetadata().getString("content-type")))
                .body(new InputStreamResource(new ByteArrayInputStream(file)));
    }




}
