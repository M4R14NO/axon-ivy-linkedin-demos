package assistant;

import ch.ivyteam.ivy.environment.Ivy;
import ch.ivyteam.ivy.scripting.objects.List;
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
  public List<Transaktion> createFromMessage(String message) {

    // Build a proxy implementation of ITransaktionAssistant using LangChain4j.
    // This proxy will call the LLM, feed the annotated prompt, get back JSON, and parse into Transaction.
	  Ivy.log().info("Get JSON ChatModel");
	  Boolean useOllama = true;
	  ITransaktionAssistant assistant = AiServices
        .builder(ITransaktionAssistant.class)
        .chatModel(OpenAiService.getJsonChatModel(useOllama))
        .build();

    // Delegate to the generated stub interface method
    TransaktionResponse response = assistant.createTransaktionen(message);

    // Extract the list from the wrapper
    return response.getTransactions();
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
        You are a helpful financial assistant specialized in parsing transaction data from voice transcriptions obtained by an automatic speech recognition service.
        
        Your task: Extract transaction information from user messages and return structured JSON.
        Rules:
        - Return ONLY valid JSON, no markdown formatting or explanatory text
        - Handle single or multiple transactions per message.
        - If there are multiple transactions in a user message, it is possible that they have a different transaction type.
        - If amounts are ambiguous, use your best judgement or set amount to null
        - Use positive numbers; transactionType field indicates direction

        Output format:
	    {
	      "transactions": [
	        {
	          "amount": number or null,
	          "transactionType": "income" | "expense",
	          "description": string
	        }
	      ]
	    }
    	
    	
    	Examples:
			Input: "Spent 50€ on groceries and got 20 euro cashback"
			Output: {"transactions": [{"amount": 50, "transactionType": "expense", "description": "groceries"}, {"amount": 20, "transactionType": "income", "description": "cashback"}]}
			
			Input: "Paid for coffee"
			Output: {"transactions": [{"amount": null, "transactionType": "expense", "description": "coffee"}]}
    """)
    @UserMessage("{{message}}")
    TransaktionResponse createTransaktionen(@V("message") String message);
  }
}