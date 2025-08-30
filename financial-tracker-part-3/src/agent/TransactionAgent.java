package agent;

import java.util.Date;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import entity.TransactionAgentResponse;
import service.OpenAiService;

public class TransactionAgent {

  public TransactionAgentResponse runTool(String message) {
    ITransactionAgent agent = AiServices
        .builder(ITransactionAgent.class)
        .chatModel(OpenAiService.getJsonChatModel())
        .tools(new TransactionTools())
        .build();

    return agent.answerWithTool(message, new Date());
  }

  public interface ITransactionAgent {
    @SystemMessage("""
        You are an assistant that can use tools when needed.
        Today: {{today}}

        ---
        Business rules:
        - Before delete or update a transaction, must retrieve information of it.
        """)
    @UserMessage("{{message}}")
    TransactionAgentResponse answerWithTool(@V("message") String message, @V("today") Date today);
  }
}
