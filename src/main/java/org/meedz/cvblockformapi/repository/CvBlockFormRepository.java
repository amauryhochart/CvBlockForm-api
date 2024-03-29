package org.meedz.cvblockformapi.repository;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
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
    private static final String DATABASE_NAME = "cvblockform";
    private static final String COLLECTION_NAME = "skillfolder";

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> allDocuments() {
        final List<String> list = new ArrayList<>();
        final MongoCollection<Document> data = mongoClient.getDatabase(DATABASE_NAME).getCollection(COLLECTION_NAME);
        data.find().map(Document::toJson).forEach(list::add);
        return list;
    }

    public SkillFolder getSkillFolderById(long skillFolderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("skill_folder_id").is(skillFolderId));
        List<SkillFolder> skillFolderList = mongoTemplate.find(query, SkillFolder.class);
        SkillFolder skillFolder = skillFolderList.get(0);
        return skillFolder;
    }

    public List<SkillFolder> getSkillFolders() {
        Query query = new Query();
        return mongoTemplate.find(query, SkillFolder.class);
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

    /**
     * Delete an Experience in the experiences list of a skillFolder in database.
     *
     * @param skillFolderId the id of the skillFolder
     * @return document      BsonDocument of the concerned SkillFolder Object
     */
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

        // remove the experience in the skillFolder in database and return a Document
        return collection.findOneAndUpdate(query, remove);
    }

    /**
     * Delete a Learning in the learnings list of a skillFolder in database.
     *
     * @param skillFolderId the id of the skillFolder
     * @return document      BsonDocument of the concerned SkillFolder Object
     */
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

        // remove the learning in the skillFolder in database and return a Document
        return collection.findOneAndUpdate(query, remove);
    }

    /**
     * Create the SkillFolder Document in DB.
     *
     * @param document BsonDocument of the concerned SkillFolder Object
     * @return document if success
     */
    public Document createSkillFolder(Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");
        BsonValue newlyInsertedId = collection.insertOne(document).getInsertedId();
        BasicDBObject query = new BasicDBObject();
        query.put("_id", newlyInsertedId);
        return collection.find(query).first();
    }

    /**
     * Delete a SkillFolder in database.
     *
     * @param skillFolderId the id of the skillFolder
     * @return the result of deletion (with acknowledged or not)
     */
    public DeleteResult deleteSkillFolder(int skillFolderId) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // remove the experience in the skillFolder in database
        return collection.deleteOne(query);
    }

    /**
     * Update a skillFolder in the database.
     *
     * @param skillFolderId the id of the concerned skillFolder
     * @param document      BsonDocument of the concerned SkillFolder Object
     * @return UpdateResult result of the update operation with counted modifications
     */
    public UpdateResult putSkillFolder(int skillFolderId, Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id
        BasicDBObject query = new BasicDBObject();
        query.put("skill_folder_id", skillFolderId);

        // update fields in the skill_folder
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("modification_date", Date.from(Instant.now()));
        updateFields.append("first_name", document.getString("first_name"));
        updateFields.append("last_name", document.getString("last_name"));
        updateFields.append("actual_function", document.getString("actual_function"));
        updateFields.append("experience_years", document.getInteger("experience_years"));
        updateFields.append("email", document.getString("email"));
        updateFields.append("availability", document.getDate("availability"));
        updateFields.append("tjm", document.getInteger("tjm"));
        updateFields.append("mobility", document.getString("mobility"));
        updateFields.append("preamble", document.getString("preamble"));
        updateFields.append("skills", document.getString("skills"));
        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);

        // update in database the skillFolder
        return collection.updateOne(query, setQuery);
    }

    /**
     * Update an experience in the experience list in the skillFolder.
     *
     * @param skillFolderId the id of the concerned skillFolder
     * @param document      BsonDocument of the concerned Experience Object
     * @return UpdateResult result of the update operation with counted modifications
     */
    public UpdateResult putExperience(int skillFolderId, Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id and experience_id
        Bson filter = Filters.and(Filters.eq("skill_folder_id", skillFolderId), Filters.eq("experiences.experience_id", document.get("experience_id")));

        // update fields in the experience with the $ which represent the element in array
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("experiences.$.modification_date", Date.from(Instant.now()));
        updateFields.append("experiences.$.client", document.getString("client"));
        updateFields.append("experiences.$.work_function", document.getString("work_function"));
        updateFields.append("experiences.$.begin_date", document.getDate("begin_date"));
        updateFields.append("experiences.$.ending_date", document.getDate("ending_date"));
        updateFields.append("experiences.$.details", document.getString("details"));

        // specify a "set" operation
        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);

        // update in database the experience
        return collection.updateOne(filter, setQuery);
    }

    /**
     * Update a learning in the learning list in the skillFolder.
     *
     * @param skillFolderId the id of the concerned skillFolder
     * @param document      BsonDocument of the concerned Learning Object
     * @return UpdateResult result of the update operation with counted modifications
     */
    public UpdateResult putLearning(int skillFolderId, Document document) {
        // get the collection
        MongoCollection<Document> collection = mongoTemplate.getCollection("skillfolder");

        // set a query with skill_folder_id and learning_id
        Bson filter = Filters.and(Filters.eq("skill_folder_id", skillFolderId), Filters.eq("learnings.learning_id", document.get("learning_id")));

        // update fields in the learning with the $ which represent the element in array
        BasicDBObject updateFields = new BasicDBObject();
        updateFields.append("learnings.$.modification_date", Date.from(Instant.now()));
        updateFields.append("learnings.$.name", document.getString("name"));
        updateFields.append("learnings.$.institution", document.getString("institution"));
        updateFields.append("learnings.$.date", document.getDate("date"));

        // specify a "set" operation
        BasicDBObject setQuery = new BasicDBObject();
        setQuery.append("$set", updateFields);

        // update in database the experience
        return collection.updateOne(filter, setQuery);
    }


    //utils

    /**
     * UTILS - Get a randomId for object creation in DB.
     *
     * @return a random Integer
     */
    public int getRandomId() {
        Random rand = new Random();
        return rand.nextInt((10000000 - 1) + 1) + 1;
    }

}