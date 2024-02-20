package org.meedz.cvblockformapi.rc;

import org.bson.Document;
import org.meedz.cvblockformapi.dto.DtoExperience;
import org.meedz.cvblockformapi.dto.DtoLearning;
import org.meedz.cvblockformapi.dto.DtoSkill;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.meedz.cvblockformapi.repository.CvBlockFormRepository;
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
@RequestMapping("/cvblockform")
public class CvBlockFormController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CvBlockFormRepository cvBlockFormRepository;

    /**
     * Create an Object Document directly in DB.
     *
     * @param jsonString the formatted JSON of the Document with raw fields
     * @return the id of tbe skill folder with 201 HTTP CREATED
     */
    @PostMapping("/document")
    ResponseEntity<?> addDocument(@RequestBody String jsonString) {
        Document doc = Document.parse(jsonString);
        Document result = mongoTemplate.insert(doc, "skillfolder");
        return new ResponseEntity<>(result.get("skill_folder_id"), HttpStatus.CREATED);
    }

    /**
     * Get the skillFolder concerned.
     *
     * @param id long skillFolderId
     * @return the skillFolder concerned with 200 HTTP OK
     */
    @GetMapping("/skillfolder")
    ResponseEntity<?> getSkillFolder(@RequestParam long id) {
        SkillFolder skillFolder = cvBlockFormRepository.getSkillFolderById(id);
        return ResponseEntity.ok(skillFolder);
    }

    /**
     * Get all the skillFolders.
     *
     * @return a List of the skillFolders with 200 HTTP OK
     */
    @GetMapping("/skillfolders")
    ResponseEntity<?> getSkillFolders() {
        List<SkillFolder> skillFolders = cvBlockFormRepository.getSkillFolders();
        return ResponseEntity.ok(skillFolders);
    }

    /**
     * Create an experience in the skillFolder concerned.
     *
     * @param skillFolderId Integer skillFolderId
     * @param dtoExperience JSON DTO of the experience in the React form
     * @return the dtoExperience concerned with 201 HTTP CREATED
     */
    @PostMapping("/experience")
    ResponseEntity<DtoExperience> postExperience(@RequestParam int skillFolderId, @RequestBody DtoExperience dtoExperience) {
        // Fill the base info, like creation_date...
        dtoExperience.put("experience_id", cvBlockFormRepository.getRandomId());
        DtoExperience reworkedDtoExperience = (DtoExperience) getBaseReworkedDtoExperience(skillFolderId, dtoExperience);

        // fill specific date info for experience
        String beginDate = dtoExperience.getString("begin_date");
        reworkedDtoExperience.replace("begin_date", beginDate != null ? Date.from(Instant.parse(beginDate)) : null);
        String endingDate = dtoExperience.getString("ending_date");
        reworkedDtoExperience.replace("ending_date", endingDate != null ? Date.from(Instant.parse(endingDate)) : null);

        // create in DB the document
        cvBlockFormRepository.createExperience(skillFolderId, reworkedDtoExperience);
        return ResponseEntity.status(HttpStatus.CREATED).body(reworkedDtoExperience);
    }

    /**
     * Create a skill in the skillFolder concerned.
     *
     * @param skillFolderId Integer skillFolderId
     * @param dtoSkill      JSON DTO of the skill in the React form
     * @return the dtoSkill concerned with 201 HTTP CREATED
     */
    @PostMapping("/skill")
    ResponseEntity<DtoSkill> postSkill(@RequestParam int skillFolderId, @RequestBody DtoSkill dtoSkill) {
        // Fill the base info, like creation_date...
        dtoSkill.put("skill_id", cvBlockFormRepository.getRandomId());
        DtoSkill reworkedDtoSkill = (DtoSkill) getBaseReworkedDtoExperience(skillFolderId, dtoSkill);

        // create in DB the document
        cvBlockFormRepository.createSkill(skillFolderId, reworkedDtoSkill);
        return ResponseEntity.status(HttpStatus.CREATED).body(reworkedDtoSkill);
    }

    /**
     * Create a learning/formation in the skillFolder concerned.
     *
     * @param skillFolderId Integer skillFolderId
     * @param dtoLearning   JSON DTO of the skillFolder in the React form
     * @return the dtoLearning concerned with 201 HTTP CREATED
     */
    @PostMapping("/learning")
    ResponseEntity<DtoLearning> postLearning(@RequestParam int skillFolderId, @RequestBody DtoLearning dtoLearning) {
        // Fill the base info, like creation_date...
        dtoLearning.put("skill_id", cvBlockFormRepository.getRandomId());
        DtoLearning reworkedDtoLearning = (DtoLearning) getBaseReworkedDtoExperience(skillFolderId, dtoLearning);

        // fill specific date info for learning
        String date = dtoLearning.getString("date");
        reworkedDtoLearning.replace("date", date != null ? Date.from(Instant.parse(date)) : null);

        // create in DB the document
        cvBlockFormRepository.createLearning(skillFolderId, reworkedDtoLearning);
        return ResponseEntity.status(HttpStatus.CREATED).body(reworkedDtoLearning);
    }

    /**
     * Base methods to create abstract infos...
     *
     * @param skillFolderId Integer
     * @param document      BsonDocument
     * @return BsonDocument
     */
    private Document getBaseReworkedDtoExperience(int skillFolderId, Document document) {
        document.put("creation_date", Date.from(Instant.now()));
        document.put("modification_date", Date.from(Instant.now()));
        document.put("deleted", false);
        document.put("skill_folder_id", skillFolderId);
        return document;
    }

    @PostMapping("/skillfolder")
    ResponseEntity<?> postSkillFolder(@RequestBody String jsonString) {
        Document doc = Document.parse(jsonString);
        SkillFolder skillFolder = cvBlockFormRepository.createSkillFolderFromDocument(doc);
        return ResponseEntity.ok(skillFolder);
    }


}