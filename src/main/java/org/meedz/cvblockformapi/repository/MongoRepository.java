package org.meedz.cvblockformapi.repository;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.meedz.cvblockformapi.model.SkillFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MongoRepository {
    private static final String DatabaseName = "cvblockform";
    private static final String CollectionName = "skillfolder";

    @Autowired
    private MongoClient client;

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<String> allDocuments() {
        final List<String> list = new ArrayList<>();
        final MongoCollection<Document> data = client.getDatabase(DatabaseName).getCollection(CollectionName);
        data.find().map(Document::toJson).forEach(list::add);
        return list;
    }

    public SkillFolder getSkillFolderById(long SkillFolderId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("skill_folder_id").is(SkillFolderId));
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
}