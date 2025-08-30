package assistant;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import entity.Transaction;
import entity.TransactionSearchCriteria;
import service.OpenAiService;

/**
 * Assistant to handle AI-related tasks for Transaction. 
 * Supports converting user messages into structured Transaction objects for creation
 * and parsing search queries into TransactionSearchCriteria for filtering.
 */
public class TransactionAssistant {
  /**
   * Converts a natural language message (e.g. "Paid 200k for Grab ride yesterday")
   * into a Transaction entity using LangChain4j for structured JSON extraction.
   *
   * @param message The user-provided text describing the transaction.
   * @return A populated Transaction object parsed from the model's JSON output.
   */
  public Transaction createFromMessage(String message) {

    // Build a proxy implementation of ITransactionAssistant using LangChain4j.
    // This proxy will call the LLM, feed the annotated prompt, get back JSON, and parse into Transaction.
    ITransactionAssistant assistant = AiServices
        .builder(ITransactionAssistant.class)
        .chatModel(OpenAiService.getJsonChatModel())
        .build();

    // Delegate to the generated stub interface method
    Transaction newTransaction = assistant.createTransaction(message);

    return newTransaction;
  }

  /**
   * Converts a natural language search query (e.g. "Find all food expenses over 100k last month")
   * into a TransactionSearchCriteria entity using LangChain4j for structured JSON extraction.
   *
   * @param message The user-provided text describing the search criteria.
   * @return A populated TransactionSearchCriteria object parsed from the model's JSON output.
   */
  public TransactionSearchCriteria createSearchCriteriaFromMessage(String message) {

    // Build a proxy implementation of ITransactionAssistant using LangChain4j.
    // This proxy will call the LLM, feed the annotated prompt, get back JSON, and parse into TransactionSearchCriteria.
    ITransactionAssistant assistant = AiServices
        .builder(ITransactionAssistant.class)
        .chatModel(OpenAiService.getJsonChatModel())
        .build();

    // Delegate to the generated stub interface method
    TransactionSearchCriteria searchCriteria = assistant.createSearchCriteria(message);

    return searchCriteria;
  }

  /**
   * Interface that defines the prompt and expected structure for LangChain4j.
   * LangChain4j uses dynamic proxy to generate code that:
   * 1. Injects the message into the prompt (via @V binding)
   * 2. Sends prompt to LLM
   * 3. Parses returned JSON to Transaction instance
   */
  public interface ITransactionAssistant {
    
    @SystemMessage("""
        You are a helpful financial assistant.
        Parse the following message into a JSON transaction record.
        Return strictly JSON with no markdown or extra text.

        Output JSON format:
        {
          "amount": number,
          "type": "income" or "expense",
          "category": string,
          "description": string,
          "date": "YYYY-MM-DD"
        }
    """)
    @UserMessage("{{message}}")
    public Transaction createTransaction(@V("message") String message);

    @SystemMessage("""
        You are a helpful financial assistant specialized in parsing search queries.
        Parse the following search message into a JSON search criteria object.
        Return strictly JSON with no markdown or extra text.

        STRICT RULES:
        - Never guess values.
        - A field must only be filled if it is **explicitly stated or can be directly inferred** (e.g., "yesterday" → fromDate and toDate).
        - If a field is not explicitly mentioned, set it to null.
        - Do not assume a category, type, or description unless directly written in the query.
    """)
    @UserMessage("{{message}}")
    public TransactionSearchCriteria createSearchCriteria(@V("message") String message);
  }
}