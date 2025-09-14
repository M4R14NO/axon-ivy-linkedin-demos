package entity;

import java.time.LocalDate;

import dev.langchain4j.model.output.structured.Description;
import entity.Transaction.Category;
import entity.Transaction.Type;

public class Transaction {
  
  @Override
	public String toString() {
		return "Transaction [amount=" + amount + ", type=" + type + ", category=" + category + ", description="
				+ description + ", date=" + date + "]";
	}

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

  @Description("Amount of the financial transaction. This is a money field")
  private double amount;
  @Description("Type of the financial transaction. Either expense or income")
  private Type type;
  @Description("Category of the transaction. Could be anything from food to clothes and many more. But can also include categories determining an income.")
  private Category category;
  @Description("Description or note about the financial transaction. Should be start with 'Description:'")
  private String description;
  @Description("The date the transaction occurred. The user can pass the date in any format, e.g. as a natural language input like 'yesterday' or a concrete date like '17.04.2022' or a mix of natural language and digits.")
  private LocalDate date;

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