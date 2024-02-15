package org.meedz.cvblockformapi.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.meedz.cvblockformapi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

import com.mongodb.client.MongoDatabase;

@Repository
public class MongoRepository {
    private static final String DatabaseName = "cvblockform";
    private static final String CollectionName = "skillfolder";

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> allDocuments() {
        final List<String> list = new ArrayList<>();
        final MongoCollection<Document> data = mongoClient.getDatabase(DatabaseName).getCollection(CollectionName);
        data.find().map(Document::toJson).forEach(list::add);
        return list;
    }

    public SkillFolder getSkillFolderById(long skillFolderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("skill_folder_id").is(skillFolderId));
        List<SkillFolder> skillFolderList = mongoTemplate.find(query, SkillFolder.class);
        SkillFolder skillFolder = skillFolderList.get(0);
        System.out.println(skillFolder.getSkills());
        return skillFolder;
    }

    public List<SkillFolder> getSkillFolders() {
        Query query = new Query();
        List<SkillFolder> skillFolderList = mongoTemplate.find(query, SkillFolder.class);
        System.out.println(skillFolderList.stream().map((SkillFolder::getSkill_folder_id)));
        return skillFolderList;
    }

    public SkillFolder createSkillFolderFromDocument(Document document) {
        MongoDatabase database = mongoClient.getDatabase(DatabaseName);
        MongoCollection<Document> collection = database.getCollection(CollectionName);

        SkillFolder skillFolder = SkillFolder.builder()
                .skill_folder_id(getRandomId())
                .creation_date(Date.from(Instant.now()))
                .modification_date(Date.from(Instant.now()))
                .deleted(false)
                .experience_years(BigInteger.valueOf(document.getInteger("experience_years")))
                .email(document.getString("email"))
                .tjm(BigInteger.valueOf(document.getInteger("experience_years")))
                .mobility(document.getString("mobility"))
                .languages(document.getList("languages", String.class))
                .consultant(document.get("consultant", Consultant.class))
                .skills(document.getList("skills", Skill.class))
                .experiences(document.getList("experiences", Experience.class))
                .learnings(document.getList("learnings", Learning.class))
                .build();

        Document consultant = new Document()
                .append("_id", new ObjectId())
                .append("skill_folder_id", skillFolder.getSkill_folder_id())
                .append("creation_date", skillFolder.getCreation_date())
                .append("deleted", skillFolder.getDeleted());

        InsertOneResult result = collection.insertOne(new Document()
                .append("_id", new ObjectId())
                .append("skill_folder_id", skillFolder.getSkill_folder_id())
                .append("creation_date", skillFolder.getCreation_date())
                .append("deleted", skillFolder.getDeleted())
                .append("experience_years", skillFolder.getExperience_years())
                .append("tjm", skillFolder.getTjm())
                .append("mobility", skillFolder.getMobility())
                .append("languages", skillFolder.getLanguages())
                .append("consultant", consultant)
                .append("skills", Collections.addAll(skillFolder.getSkills()))
                .append("experiences", Collections.addAll(skillFolder.getExperiences()))
                .append("learnings", Collections.addAll(skillFolder.getLearnings())));
        return skillFolder;
    }

    public Experience createExperience(BigInteger skillFolderId, Document document) {
        Date beginDate = Date.from(Instant.parse(document.getString("begin_date")));
        Date endingDate = Date.from(Instant.parse(document.getString("ending_date")));
        Experience experience = Experience.builder()
                .experience_id(getRandomId())
                .creation_date(Date.from(Instant.now()))
                .modification_date(Date.from(Instant.now()))
                .deleted(false).skill_folder_id(skillFolderId)
                .client(document.getString("client"))
                .work_function(document.getString("work_function"))
                .begin_date(beginDate)
                .ending_date(endingDate)
                .details(document.getString("details"))
                .build();

        MongoDatabase database = mongoClient.getDatabase(DatabaseName);
        MongoCollection<Document> collection = database.getCollection(CollectionName);

        // Get the old skillFolder
        SkillFolder skillFolder = getSkillFolderById(skillFolderId.longValue());

        // Add the new experience
        List<Experience> newExperienceList = skillFolder.getExperiences();
        newExperienceList.add(experience);
        skillFolder.setExperiences(newExperienceList);

        // Delete the old skillFolder
        Bson query = Filters.eq("skill_folder_id", skillFolderId.intValue());
        DeleteResult result = collection.deleteOne(query);

        // Add the updated one
        if (result.wasAcknowledged()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String skillFolderAsString = objectMapper.writeValueAsString(skillFolder);
                Document doc = Document.parse(skillFolderAsString);
                Document created = mongoTemplate.insert(doc, CollectionName);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }

        return experience;
    }


    //utils
    public BigInteger getRandomId() {
        // Define the range
        BigInteger min = new BigInteger("1"); // Minimum value
        BigInteger max = new BigInteger("1000000000"); // Maximum value
        // Generate a random BigInteger within the specified range
        BigInteger bigIntegerRange = max.subtract(min).add(BigInteger.ONE);
        return new BigInteger(bigIntegerRange.bitLength(), new Random()).add(min);
    }

}