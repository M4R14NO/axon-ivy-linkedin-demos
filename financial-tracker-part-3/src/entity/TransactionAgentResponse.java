package entity;

import java.util.List;

import dev.langchain4j.model.output.structured.Description;
import enums.TransactionAction;

/**
 * Represents the structured response returned by the Transaction Agent.
 * <p>
 * This class captures the intent of the agent (action), any resulting transaction(s),
 * and potential errors during the tool execution.
 */
public class TransactionAgentResponse {
  
  public static Builder builder() {
    return new Builder();
  }
  /**
   * The type of action the agent intends to perform.
   * Possible values: INSERT, SEARCH, UPDATE, DELETE.
   */
  @Description("Indicates the action requested by the user: INSERT, SEARCH, UPDATE, or DELETE.")
  private TransactionAction action;

  /**
   * A single transaction involved in the action.
   */
  @Description("A single transaction relevant to the action.")
  private Transaction transaction;

  /**
   * A list of transactions matching search criteria.
   * Used when the user asks to retrieve or list multiple transactions.
   */
  @Description("List of transactions returned for a search request; empty or null for non-search actions.")
  private List<Transaction> transactions;

  /**
   * Error information in case the agent cannot process the request successfully.
   */
  @Description("Details of any error that occurred while performing the requested action.")
  private String errorMessage;

  // Getters and setters

  public TransactionAction getAction() {
    return action;
  }

  public void setAction(TransactionAction action) {
      this.action = action;
  }

  public Transaction getTransaction() {
      return transaction;
  }

  public void setTransaction(Transaction transaction) {
      this.transaction = transaction;
  }

  public List<Transaction> getTransactions() {
      return transactions;
  }

  public void setTransactions(List<Transaction> transactions) {
      this.transactions = transactions;
  }

  public String getErrorMessage() {
      return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
  }
  
  public static class Builder {
    private TransactionAction action;
    private Transaction transaction;
    private List<Transaction> transactions;
    private String errorMessage;
    
    public Builder() {}

    public Builder action(TransactionAction action) {
      this.action = action;
      return this;
    }

    public Builder transaction(Transaction transaction) {
      this.transaction = transaction;
      return this;
    }

    public Builder transactions(List<Transaction> transactions) {
      this.transactions = transactions;
      return this;
    }
    
    public Builder errorMessage(String errorMessage) {
      this.errorMessage = errorMessage;
      return this;
    }
    
    public TransactionAgentResponse build() {
      TransactionAgentResponse response = new TransactionAgentResponse();
      response.setAction(action);
      response.setTransaction(transaction);
      response.setTransactions(transactions);
      response.setErrorMessage(errorMessage);
      return response;
    }
  }
}
