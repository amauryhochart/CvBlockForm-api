package org.meedz.cvblockformapi.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "skillfolder")
public class SkillFolder {

  public BigInteger skill_folder_id;
  public Date creation_date;
  public Date modification_date;
  public Boolean deleted;
  public BigInteger experience_years;
  public String email;
  public BigInteger tjm;
  public String mobility;
  public List<String> languages;
  public Consultant consultant;
  public List<Skill> skills;
  public List<Experience> experiences;
  public List<Learning> learnings;

}
