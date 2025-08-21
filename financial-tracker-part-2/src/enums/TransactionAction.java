package enums;

import entity.Option;

public enum TransactionAction {
  INSERT("insert", "the message is about insert or create a transaction"),
  SEARCH("search", "the message is related to search transaction by some criteria"),
  CLEAR_SEARCH("clear", "the message is about clearing search results and showing all transactions");

  private String id;
  private String condition;
  
  private TransactionAction(String id, String condition) {
    this.id = id;
    this.condition = condition;
  }
  
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
  
  public Option getOption() {
    Option newOption = new Option();
    newOption.setId(id);
    newOption.setCondition(condition);
    return newOption;
  }
}
