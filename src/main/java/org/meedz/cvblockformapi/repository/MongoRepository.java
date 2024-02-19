package org.meedz.cvblockformapi.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.InsertOneResult;
import org.bson.Document;
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
                .experience_years(document.getInteger("experience_years"))
                .email(document.getString("email"))
                .tjm(document.getInteger("experience_years"))
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

    public void createExperience(int skillFolderId, Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // push the experience in experiences in the skill_folder_id
        BasicDBObject push_data = new BasicDBObject("$push", new BasicDBObject("experiences", document));

        // update in database the skillFolder
        collection.findOneAndUpdate(query, push_data);
    }

    public void createSkill(int skillFolderId, Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // push the experience in experiences in the skill_folder_id
        BasicDBObject push_data = new BasicDBObject("$push", new BasicDBObject("skills", document));

        // update in database the skillFolder
        collection.findOneAndUpdate(query, push_data);
    }


    //utils
    public int getRandomId() {
        Random rand = new Random();
        return rand.nextInt((1000000 - 1) + 1) + 1;
    }

}