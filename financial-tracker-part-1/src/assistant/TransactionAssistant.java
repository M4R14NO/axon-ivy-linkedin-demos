package assistant;

import java.util.Date;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import entity.Transaction;
import service.OpenAiService;

/**
 * Assistant to handle AI-related tasks for Transaction. E.g., convert a user message into a structured Transaction object.
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
    
    // Initialize OpenAI-backed service.
    OpenAiService aiService = new OpenAiService();

    // Build a proxy implementation of ITransactionAssistant using LangChain4j.
    // This proxy will call the LLM, feed the annotated prompt, get back JSON, and parse into Transaction.
    ITransactionAssistant assistant = AiServices
        .builder(ITransactionAssistant.class)
        .chatModel(aiService.getModel())
        .build();

    // Delegate to the generated stub interface method
    return assistant.createTransaction(message, new Date());
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

        Today: {{today}}
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
    public Transaction createTransaction(@V("message") String message, @V("today") Date date);
  }
}