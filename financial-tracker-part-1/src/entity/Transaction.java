package entity;

import java.time.LocalDate;

import entity.Transaction.Category;
import entity.Transaction.Type;

public class Transaction {
  
  /**
   * The type of the transaction: income or expense.
   */
  public enum Type {
    INCOME, EXPENSE;
    
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
    FOOD, DRINK, PARTY, CLOTHES, SALARY, INVESTMENT_INTEREST, OTHER;
    
    public String getDisplayName() {
      StringBuilder builder = new StringBuilder();
      return builder
          .append(this.name().charAt(0))
          .append(this.name().substring(1).toLowerCase()).toString();
    }
  }

  private double amount; // Amount of the transaction
  private Type type; //Type of the transaction (INCOME or EXPENSE)
  private Category category; // Category of the transaction (e.g., FOOD, SALARY)
  private String description; // Description or note about the transaction
  private LocalDate date; // The date the transaction occurred

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
}