package org.meedz.cvblockformapi.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
public class CvBlockFormRepository {
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
//                .consultant(document.get("consultant", Consultant.class))
//                .skills(document.getList("skills", Skill.class))
//                .experiences(document.getList("experiences", Experience.class))
//                .learnings(document.getList("learnings", Learning.class))
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

    /**
     * Create the Experience Document in DB by updating the SkillFolder Document with collection.findOneAndUpdate.
     *
     * @param skillFolderId Integer skillFolderId
     * @param document      BsonDocument of the concerned Experience Object
     * @return document if success
     */
    public Document createExperience(int skillFolderId, Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // push the experience in experiences in the skill_folder_id
        BasicDBObject push_data = new BasicDBObject("$push", new BasicDBObject("experiences", document));

        // update in database the skillFolder
        return collection.findOneAndUpdate(query, push_data);
    }

    /**
     * Create the Skill Document in DB by updating the SkillFolder Document with collection.findOneAndUpdate.
     *
     * @param skillFolderId Integer skillFolderId
     * @param document      BsonDocument of the concerned Skill Object
     * @return document if success
     */
    public Document createSkill(int skillFolderId, Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // push the skill in skills in the skill_folder_id
        BasicDBObject push_data = new BasicDBObject("$push", new BasicDBObject("skills", document));

        // update in database the skillFolder
        collection.findOneAndUpdate(query, push_data);
        return document;
    }

    /**
     * Create the Learning Document in DB by updating the SkillFolder Document with collection.findOneAndUpdate.
     *
     * @param skillFolderId Integer skillFolderId
     * @param document      BsonDocument of the concerned Learning Object
     * @return document if success
     */
    public Document createLearning(int skillFolderId, Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // push the learning in learnings in the skill_folder_id
        BasicDBObject push_data = new BasicDBObject("$push", new BasicDBObject("learnings", document));

        // update in database the skillFolder
        return collection.findOneAndUpdate(query, push_data);
    }

    public Document deleteExperience(int skillFolderId, int experienceId) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // remove the experience in experiences in the skill_folder_id
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("experience_id", experienceId);
        BasicDBObject fields = new BasicDBObject("experiences", map);
        BasicDBObject remove = new BasicDBObject("$pull", fields);

        // remove the experience in the skillFolder in database
        return collection.findOneAndUpdate(query, remove);
    }

    public Document deleteSkill(int skillFolderId, int skillId) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // remove the skill in skills in the skill_folder_id
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("skill_id", skillId);
        BasicDBObject fields = new BasicDBObject("skills", map);
        BasicDBObject remove = new BasicDBObject("$pull", fields);

        // remove the experience in the skillFolder in database
        return collection.findOneAndUpdate(query, remove);
    }

    public Document deleteLearning(int skillFolderId, int learningId) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // remove the learning in learnings in the skill_folder_id
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("learning_id", learningId);
        BasicDBObject fields = new BasicDBObject("learnings", map);
        BasicDBObject remove = new BasicDBObject("$pull", fields);

        // remove the experience in the skillFolder in database
        return collection.findOneAndUpdate(query, remove);
    }

    public Document createSkillFolder(Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");
        BsonValue newlyInsertedId = collection.insertOne(document).getInsertedId();
        BasicDBObject query = new BasicDBObject();
        query.put("_id", newlyInsertedId);
        return collection.find(query).first();
    }

    public DeleteResult deleteSkillFolder(int skillFolderId) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // remove the experience in the skillFolder in database
        return collection.deleteOne(query);
    }


    //utils
    public int getRandomId() {
        Random rand = new Random();
        return rand.nextInt((1000000 - 1) + 1) + 1;
    }

}