package entity;

import java.io.Serializable;
import java.time.LocalDate;

import dev.langchain4j.model.output.structured.Description;

public class Transaction implements Serializable {

  private static final long serialVersionUID = -1848377102648678130L;

  /**
   * The type of the transaction: income or expense.
   */
  public enum Type {
    INCOME, EXPENSE, NONE;
    
    /**
     * Returns a user-friendly display name of the transaction type.
     * For example, "INCOME" becomes "Income".
     */
    public String getDisplayName() {
      StringBuilder builder = new StringBuilder();
      return builder
          .append(this.name().charAt(0))
          .append(this.name().substring(1).toLowerCase()).toString();
    }
  }
  
  /**
   * Represents the category of the transaction.
   */
  public enum Category {
    FOOD, DRINK, PARTY, CLOTHES, SALARY, INVESTMENT_INTEREST, OTHER, NONE;
    
    public String getDisplayName() {
      StringBuilder builder = new StringBuilder();
      return builder
          .append(this.name().charAt(0))
          .append(this.name().substring(1).toLowerCase()).toString();
    }
  }

  @Description("Amount of the transaction. This is a money field")
  private double amount;
  @Description("Type of the transaction")
  private Type type;
  @Description("Category of the transaction")
  private Category category;
  @Description("Description or note about the transaction")
  private String description;
  @Description("The date the transaction occurred")
  private LocalDate date;
  @Description("ID of the transaction. NEVER change this field")
  private String id;

  public double getAmount() {
      return amount;
  }

  public void setAmount(double amount) {
      this.amount = amount;
  }

  public Type getType() {
      return type;
  }

  public void setType(Type type) {
      this.type = type;
  }

  public Category getCategory() {
      return category;
  }

  public void setCategory(Category category) {
      this.category = category;
  }

  public String getDescription() {
      return description;
  }

  public void setDescription(String description) {
      this.description = description;
  }

  public LocalDate getDate() {
      return date;
  }

  public void setDate(LocalDate date) {
      this.date = date;
  }
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}