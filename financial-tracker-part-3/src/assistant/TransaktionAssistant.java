package assistant;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import financial.tracker.part3.Transaktion;
import service.OpenAiService;

/**
 * Assistant to handle AI-related tasks for Transaction. 
 * Supports converting user messages into structured Transaction objects for creation
 * and parsing search queries into TransactionSearchCriteria for filtering.
 */
public class TransaktionAssistant {
  /**
   * Converts a natural language message (e.g. "Paid 200k for Grab ride yesterday")
   * into a Transaktion entity using LangChain4j for structured JSON extraction.
   *
   * @param message The user-provided text describing the transaction.
   * @return A populated Transaction object parsed from the model's JSON output.
   */
  public Transaktion createFromMessage(String message) {

    // Build a proxy implementation of ITransaktionAssistant using LangChain4j.
    // This proxy will call the LLM, feed the annotated prompt, get back JSON, and parse into Transaction.
    ITransaktionAssistant assistant = AiServices
        .builder(ITransaktionAssistant.class)
        .chatModel(OpenAiService.getJsonChatModel())
        .build();

    // Delegate to the generated stub interface method
    Transaktion newTransaktion = assistant.createTransaktion(message);

    return newTransaktion;
  }

  /**
   * Interface that defines the prompt and expected structure for LangChain4j.
   * LangChain4j uses dynamic proxy to generate code that:
   * 1. Injects the message into the prompt (via @V binding)
   * 2. Sends prompt to LLM
   * 3. Parses returned JSON to Transaction instance
   */
  public interface ITransaktionAssistant {
    
    @SystemMessage("""
        You are a helpful financial assistant.
        Parse the following message into a JSON transaction record.
        Return strictly JSON with no markdown or extra text.

        Output JSON format:
        {
          "amount": number,
          "typ": "income" or "expense",
          "description": string
        }
    """)
    @UserMessage("{{message}}")
    public Transaktion createTransaktion(@V("message") String message);
  }
}