package org.meedz.cvblockformapi.rc;

import org.bson.Document;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.meedz.cvblockformapi.repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:8081")
@RequestMapping("/apitest")
public class ConfigurationController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private MongoRepository mongoRepository;

    @PostMapping("/document")
    ResponseEntity<?> addDocument(@RequestBody String jsonString) {
        Document doc = Document.parse(jsonString);
        Document result = mongoTemplate.insert(doc, "skillfolder");
        return new ResponseEntity<>(result.get("skill_folder_id"), HttpStatus.CREATED);
    }

    @GetMapping("/skillfolder")
    ResponseEntity<?> getSkillFolder(@RequestParam long id) {
        SkillFolder skillFolder = mongoRepository.getSkillFolderById(id);
        return ResponseEntity.ok(skillFolder);
    }

    @GetMapping("/skillfolders")
    ResponseEntity<?> getSkillFolders() {
        List<SkillFolder> skillFolders = mongoRepository.getSkillFolders();
        return ResponseEntity.ok(skillFolders);
    }

}