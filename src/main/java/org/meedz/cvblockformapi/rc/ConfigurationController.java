package org.meedz.cvblockformapi.rc;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.meedz.cvblockformapi.model.Experience;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.meedz.cvblockformapi.repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @PostMapping("/experience")
    ResponseEntity<?> postExperience(@RequestParam BigInteger skillFolderId, @RequestBody String jsonString) {
        Document doc = Document.parse(jsonString);
        Experience result = mongoRepository.createExperience(skillFolderId, doc);
        return new ResponseEntity<>(result.getExperience_id(), HttpStatus.CREATED);
    }

    @PostMapping("/skillfolder")
    ResponseEntity<?> postSkillFolder(@RequestBody String jsonString) {
        Document doc = Document.parse(jsonString);
        SkillFolder skillFolder = mongoRepository.createSkillFolderFromDocument(doc);
        return ResponseEntity.ok(skillFolder);
    }


}