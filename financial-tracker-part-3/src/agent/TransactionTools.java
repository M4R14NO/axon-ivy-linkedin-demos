package agent;

import java.util.List;

import assistant.TransactionAssistant;
import dev.langchain4j.agent.tool.Tool;
import entity.Transaction;
import entity.TransactionAgentResponse;
import entity.TransactionSearchCriteria;
import enums.TransactionAction;
import repo.TransactionRepository;

/**
 * A collection of tools for handling transactions.
 * 
 * These methods can be exposed to an AI agent so it can create, search,
 * update, or delete transactions based on natural language input.
 */
public class TransactionTools {

  TransactionAssistant assistant;
  
  public TransactionTools() {
    this.assistant = new TransactionAssistant();
  }

  /**
   * Creates a new transaction from a natural language message.
   *
   * @param message description of the transaction (e.g., "Paid 200k for Grab ride yesterday")
   * @return the newly created Transaction saved in the repository wrapped inside @TransactionAgentResponse
   */
  @Tool("Use when intends to create or record a new transaction.")
  public TransactionAgentResponse createTransactionTool(String message) {

    // Use TransactionAssistant to create new Transaction object
    Transaction newTransaction = assistant.createFromMessage(message);

    // Persist the new Transaction object to repo
    TransactionRepository.getInstance().create(newTransaction);

    // Build agent response:
    // Set type: INSERT
    // Set transaction: the new Transaction object
    return TransactionAgentResponse.builder()
        .action(TransactionAction.INSERT)
        .transaction(newTransaction)
        .build();
  }

  /**
   * Searches for transactions matching criteria described in natural language.
   *
   * @param message search query (e.g., "Find all food expenses over 100k last month")
   * @return list of matching transactions wrapped inside @TransactionAgentResponse
   */
  @Tool("Use when need to find list of transactions matching criteria.")
  public TransactionAgentResponse searchTransactions(String message) {
    // Use TransactionAssistant to create search criteria
    TransactionSearchCriteria searchCriteria =
        assistant.createSearchCriteriaFromMessage(message);

    // Use search criteria to find transactions in the repo
    List<Transaction> matchedTransactions = 
      TransactionRepository.getInstance().findBySearchCriteria(searchCriteria);

    // Build agent response:
    // Set type: SEARCH
    // Set transactions: found transactions from repo
    return TransactionAgentResponse.builder()
        .action(TransactionAction.SEARCH)
        .transactions(matchedTransactions)
        .build();
  }

  /**
   * Deletes a transaction.
   *
   * @param transaction the transaction to delete
   * @return list of matching transactions wrapped inside @TransactionAgentResponse
   */
  @Tool("Use when wants to delete a transaction (identifies a transaction to remove).")
  public TransactionAgentResponse deleteTransaction(Transaction transaction) {
    String error = "";
    if (transaction == null) {
      error = "Transaction cannot be empty";
    }
    TransactionRepository.getInstance().delete(transaction);
    return TransactionAgentResponse.builder()
        .action(TransactionAction.DELETE)
        .transaction(transaction)
        .errorMessage(error)
        .build();
  }

  /**
   * Updates a transaction.
   *
   * @param transaction the transaction to update
   */
  @Tool("Use when want update or modify an existing transaction.")
  public TransactionAgentResponse updateTransaction(Transaction transaction) {
    String error = "";
    if (transaction == null) {
      error = "Transaction cannot be empty";
    }
    
    Transaction updated = TransactionRepository.getInstance().update(transaction);
    return TransactionAgentResponse.builder()
        .action(TransactionAction.UPDATE)
        .transaction(updated)
        .errorMessage(error)
        .build();
  }
  
  /**
   * Searches for a single transaction  matching criteria described in natural language.
   *
   * @param message search query (e.g., "Find all food expenses over 100k last month")
   * @return the matching transaction
   */
  @Tool("Use when need to retrieve information of a transaction.")
  public Transaction searchOneTransaction(String message) {
    TransactionSearchCriteria searchCriteria = assistant.createSearchCriteriaFromMessage(message);
    return TransactionRepository.getInstance().findBySearchCriteria(searchCriteria).get(0);
  }
}
