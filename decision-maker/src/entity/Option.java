package entity;

import dev.langchain4j.internal.Json;
import dev.langchain4j.model.output.structured.Description;

public class Option {

  @Description("Id of the option")
  private String id;
  
  @Description("Condition to choose the option")
  private String condition;
  
  public String getId() {
    return id;
  }
  
  public void setId(String id) {
    this.id = id;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }
  
  @Override
  public String toString() {
    return Json.toJson(this);
  }
}