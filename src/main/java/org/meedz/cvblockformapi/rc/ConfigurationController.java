package org.meedz.cvblockformapi.rc;

import org.bson.Document;
import org.meedz.cvblockformapi.dto.DtoExperience;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.meedz.cvblockformapi.repository.MongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Date;
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

    @PostMapping("/experience")
    ResponseEntity<DtoExperience> postExperience(@RequestParam int skillFolderId, @RequestBody DtoExperience dtoExperience) {
        // Fill the base info, like creation_date...
        DtoExperience reworkedDtoExperience = getBaseReworkedDtoExperience(skillFolderId, dtoExperience);

        // fill specific infos for experience
        String beginDate = dtoExperience.getString("begin_date");
        reworkedDtoExperience.replace("begin_date", beginDate != null ? Date.from(Instant.parse(beginDate)) : null);
        String endingDate = dtoExperience.getString("ending_date");
        reworkedDtoExperience.replace("ending_date", endingDate != null ? Date.from(Instant.parse(endingDate)) : null);

        // create in DB the document
        mongoRepository.createExperience(skillFolderId, reworkedDtoExperience);
        return ResponseEntity.status(HttpStatus.CREATED).body(reworkedDtoExperience);
    }

    private DtoExperience getBaseReworkedDtoExperience(int skillFolderId, DtoExperience dtoExperience) {
        dtoExperience.put("experience_id", mongoRepository.getRandomId());
        dtoExperience.put("creation_date", Date.from(Instant.now()));
        dtoExperience.put("modification_date", Date.from(Instant.now()));
        dtoExperience.put("deleted", false);
        dtoExperience.put("skill_folder_id", skillFolderId);
        return dtoExperience;
    }

    @PostMapping("/skillfolder")
    ResponseEntity<?> postSkillFolder(@RequestBody String jsonString) {
        Document doc = Document.parse(jsonString);
        SkillFolder skillFolder = mongoRepository.createSkillFolderFromDocument(doc);
        return ResponseEntity.ok(skillFolder);
    }


}