package org.meedz.cvblockformapi.rc;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.meedz.cvblockformapi.dto.DtoExperience;
import org.meedz.cvblockformapi.dto.DtoLearning;
import org.meedz.cvblockformapi.dto.DtoSkillFolder;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.meedz.cvblockformapi.repository.CvBlockFormRepository;
import org.meedz.cvblockformapi.service.CvBlockFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cvblockform")
public class CvBlockFormController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CvBlockFormRepository cvBlockFormRepository;

    @Autowired
    private CvBlockFormService cvBlockFormService;

    /**
     * Get HTTP headers for response.
     *
     * @return headers
     */
    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=UTF-8");
//        headers.add("Content-Encoding", "gzip");
        return headers;
    }

    /**
     * Base methods to create abstract info...
     *
     * @param skillFolderId Integer
     * @param document      BsonDocument
     * @return BsonDocument
     */
    private Document getBaseReworkedDto(int skillFolderId, Document document) {
        document.put("creation_date", Date.from(Instant.now()));
        document.put("modification_date", Date.from(Instant.now()));
        document.put("deleted", false);
        document.put("skill_folder_id", skillFolderId);
        return document;
    }

    /**
     * Endpoint to Create an Object Document directly in DB.
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
     * Endpoint to Get the skillFolder concerned.
     *
     * @param id long skillFolderId
     * @return the skillFolder concerned with 200 HTTP OK
     */
    @GetMapping("/skillfolder")
    ResponseEntity<?> getSkillFolder(@RequestParam long id) {
        HttpHeaders headers = getHttpHeaders();
        SkillFolder skillFolder = cvBlockFormRepository.getSkillFolderById(id);
        return ResponseEntity.ok().headers(headers).body(skillFolder);
    }

    /**
     * Endpoint to Get all the skillFolders.
     *
     * @return a List of the skillFolders with 200 HTTP OK
     */
    @GetMapping("/skillfolders")
    ResponseEntity<?> getSkillFolders() {
        List<SkillFolder> skillFolders = cvBlockFormRepository.getSkillFolders();
        return ResponseEntity.ok(skillFolders);
    }

    /**
     * Endpoint to Create an experience in the skillFolder concerned.
     *
     * @param skillFolderId Integer skillFolderId
     * @param dtoExperience JSON DTO of the experience in the React form
     * @return the dtoExperience concerned with 201 HTTP CREATED
     */
    @PostMapping("/experience")
    ResponseEntity<?> postExperience(@RequestParam int skillFolderId, @RequestBody DtoExperience dtoExperience) {
        // Fill the base info, like creation_date...
        dtoExperience.put("experience_id", cvBlockFormRepository.getRandomId());
        DtoExperience reworkedDtoExperience = (DtoExperience) getBaseReworkedDto(skillFolderId, dtoExperience);

        // fill specific date info for experience
        String beginDate = dtoExperience.getString("begin_date");
        reworkedDtoExperience.replace("begin_date", beginDate != null ? Date.from(Instant.parse(beginDate)) : null);
        String endingDate = dtoExperience.getString("ending_date");
        reworkedDtoExperience.replace("ending_date", endingDate != null ? Date.from(Instant.parse(endingDate)) : null);

        // create in DB the document
        Document document = cvBlockFormRepository.createExperience(skillFolderId, reworkedDtoExperience);
        if (document != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(reworkedDtoExperience);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in creating experience " + dtoExperience.get("experience_id") + " in the skillFolder " + skillFolderId);
        }
    }

    /**
     * Endpoint to Create a learning/formation in the skillFolder concerned.
     *
     * @param skillFolderId Integer skillFolderId
     * @param dtoLearning   JSON DTO of the learning in the React form
     * @return the dtoLearning concerned with 201 HTTP CREATED
     */
    @PostMapping("/learning")
    ResponseEntity<?> postLearning(@RequestParam int skillFolderId, @RequestBody DtoLearning dtoLearning) {
        // Fill the base info, like creation_date...
        dtoLearning.put("learning_id", cvBlockFormRepository.getRandomId());
        DtoLearning reworkedDtoLearning = (DtoLearning) getBaseReworkedDto(skillFolderId, dtoLearning);

        // fill specific date info for learning
        String begin_date = dtoLearning.getString("begin_date");
        reworkedDtoLearning.replace("begin_date", begin_date != null ? Date.from(Instant.parse(begin_date)) : null);
        String ending_date = dtoLearning.getString("ending_date");
        reworkedDtoLearning.replace("ending_date", ending_date != null ? Date.from(Instant.parse(ending_date)) : null);

        // create in DB the document
        Document document = cvBlockFormRepository.createLearning(skillFolderId, reworkedDtoLearning);
        if (document != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(reworkedDtoLearning);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in creating learning " + dtoLearning.get("learning_id") + " in the skillFolder " + skillFolderId);
        }
    }

    /**
     * Endpoint to Delete an experience in the SkillFolder.experiences list.
     *
     * @param skillFolderId Integer skillFolderId
     * @param experienceId  Integer experienceId
     * @return a ResponseEntity with 204 HTTP NO_CONTENT
     */
    @DeleteMapping("/experience")
    ResponseEntity<?> deleteExperience(@RequestParam int skillFolderId, @RequestParam int experienceId) {
        Document document = cvBlockFormRepository.deleteExperience(skillFolderId, experienceId);
        if (document != null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in deleting experience " + experienceId + " in the skillFolder " + skillFolderId);
        }
    }

    /**
     * Endpoint to Delete a learning in the SkillFolder.learnings list.
     *
     * @param skillFolderId Integer skillFolderId
     * @param learningId    Integer learningId
     * @return a ResponseEntity with 204 HTTP NO_CONTENT
     */
    @DeleteMapping("/learning")
    ResponseEntity<?> deleteLearning(@RequestParam int skillFolderId, @RequestParam int learningId) {
        Document document = cvBlockFormRepository.deleteLearning(skillFolderId, learningId);
        if (document != null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in deleting learning " + learningId + " in the skillFolder " + skillFolderId);
        }
    }

    /**
     * Endpoint to Create a SkillFolder.
     *
     * @param dtoSkillFolder JSON DTO of the skillFolder in the React form
     * @return the dtoSkillFolder concerned with 201 HTTP CREATED
     */
    @PostMapping("/skillfolder")
    ResponseEntity<?> postSkillFolder(@RequestBody DtoSkillFolder dtoSkillFolder) {
        // Fill the base info, like creation_date...
        DtoSkillFolder reworkedDtoSkillFolder = (DtoSkillFolder) getBaseReworkedDto(cvBlockFormRepository.getRandomId(), dtoSkillFolder);

        // fill specific date info for the consultant in the skillFolder
        String date = dtoSkillFolder.getString("availability");
        reworkedDtoSkillFolder.replace("availability", date != null ? Date.from(Instant.parse(date)) : null);

        reworkedDtoSkillFolder.putIfAbsent("experiences", new ArrayList<>());
        reworkedDtoSkillFolder.putIfAbsent("learnings", new ArrayList<>());
        reworkedDtoSkillFolder.putIfAbsent("languages", new ArrayList<>());
        reworkedDtoSkillFolder.putIfAbsent("skills", "");

        // create in DB the document
        Document document = cvBlockFormRepository.createSkillFolder(reworkedDtoSkillFolder);
        if (document != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(reworkedDtoSkillFolder);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in creating skillFolder " + reworkedDtoSkillFolder.getInteger("skill_folder_id"));
        }
    }

    /**
     * Endpoint to Delete a skillFolder.
     *
     * @param skillFolderId Integer skillFolderId
     * @return a ResponseEntity with 204 HTTP NO_CONTENT
     */
    @DeleteMapping("/skillfolder")
    ResponseEntity<?> deleteSkillFolder(@RequestParam int skillFolderId) {
        DeleteResult deleted = cvBlockFormRepository.deleteSkillFolder(skillFolderId);
        if (deleted.wasAcknowledged()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in deleting skillFolder " + skillFolderId);
        }
    }

    /**
     * Endpoint to Edit a skillFolder.
     *
     * @param skillFolderId  Integer skillFolderId
     * @param dtoSkillFolder JSON DTO of the skillFolder in the React form
     * @return the JSON DTO of the skillFolder with 200 HTTP OK
     */
    @PutMapping("/skillfolder")
    ResponseEntity<?> putSkillFolder(@RequestParam int skillFolderId, @RequestBody DtoSkillFolder dtoSkillFolder) {
        // Fill the base info, like creation_date...
        DtoSkillFolder reworkedDtoSkillFolder = (DtoSkillFolder) getBaseReworkedDto(skillFolderId, dtoSkillFolder);

        // fill specific date info for the consultant in the skillFolder
        String date = dtoSkillFolder.getString("availability");
        reworkedDtoSkillFolder.replace("availability", date != null ? Date.from(Instant.parse(date)) : null);

        reworkedDtoSkillFolder.putIfAbsent("experiences", new ArrayList<>());
        reworkedDtoSkillFolder.putIfAbsent("learnings", new ArrayList<>());
        reworkedDtoSkillFolder.putIfAbsent("languages", new ArrayList<>());
        reworkedDtoSkillFolder.putIfAbsent("skills", "");

        // update in DB the document
        UpdateResult updateResult = cvBlockFormRepository.putSkillFolder(skillFolderId, reworkedDtoSkillFolder);
        if (updateResult.getMatchedCount() > 0) {
            return ResponseEntity.ok(reworkedDtoSkillFolder);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in edit skillFolder " + skillFolderId);
        }
    }

    /**
     * Endpoint to Edit an experience in the SkillFolder.experiences list.
     *
     * @param skillFolderId Integer skillFolderId
     * @param dtoExperience JSON DTO of the experience in the React form
     * @return the JSON DTO of the experience with 200 HTTP OK
     */
    @PutMapping("/experience")
    ResponseEntity<?> putExperience(@RequestParam int skillFolderId, @RequestBody DtoExperience dtoExperience) {
        // Fill the base info, like creation_date...
        DtoExperience reworkedDtoExperience = (DtoExperience) getBaseReworkedDto(skillFolderId, dtoExperience);

        // fill specific date info for experience
        String beginDate = dtoExperience.getString("begin_date");
        reworkedDtoExperience.replace("begin_date", beginDate != null ? Date.from(Instant.parse(beginDate)) : null);
        String endingDate = dtoExperience.getString("ending_date");
        reworkedDtoExperience.replace("ending_date", endingDate != null ? Date.from(Instant.parse(endingDate)) : null);

        // update in DB the document
        UpdateResult updateResult = cvBlockFormRepository.putExperience(skillFolderId, reworkedDtoExperience);
        if (updateResult.getMatchedCount() > 0) {
            return ResponseEntity.ok(reworkedDtoExperience);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in edit experience " + dtoExperience.get("experience_id") + " in the skillFolder " + skillFolderId);
        }
    }

    /**
     * Endpoint to Edit a learning in the SkillFolder.learnings list.
     *
     * @param skillFolderId Integer skillFolderId
     * @param dtoLearning   JSON DTO of the learning in the React form
     * @return the JSON DTO of the learning with 200 HTTP OK
     */
    @PutMapping("/learning")
    ResponseEntity<?> putLearning(@RequestParam int skillFolderId, @RequestBody DtoLearning dtoLearning) {
        // Fill the base info, like creation_date...
        DtoLearning reworkedDtoLearning = (DtoLearning) getBaseReworkedDto(skillFolderId, dtoLearning);

        // fill specific date info for learning
        String begin_date = dtoLearning.getString("begin_date");
        reworkedDtoLearning.replace("begin_date", begin_date != null ? Date.from(Instant.parse(begin_date)) : null);
        String ending_date = dtoLearning.getString("ending_date");
        reworkedDtoLearning.replace("ending_date", ending_date != null ? Date.from(Instant.parse(ending_date)) : null);

        // update in DB the document
        UpdateResult updateResult = cvBlockFormRepository.putLearning(skillFolderId, reworkedDtoLearning);
        if (updateResult.getMatchedCount() > 0) {
            return ResponseEntity.ok(reworkedDtoLearning);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error in edit learning " + dtoLearning.get("learning_id") + " in the skillFolder " + skillFolderId);
        }
    }

    @GetMapping("/pdf")
    ResponseEntity<?> getPdf(@RequestParam long skillFolderId) {
        // get the pdf from DB
        SkillFolder skillFolder = cvBlockFormRepository.getSkillFolderById(skillFolderId);
        File pdf = cvBlockFormService.generatePdfFromHtml(skillFolder);
        try {
            InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(pdf));
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(inputStreamResource);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}