package assitant;

import java.util.List;

import ch.ivyteam.ivy.environment.Ivy;
import dev.langchain4j.internal.Json;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import entity.Option;
import service.OpenAiService;

public class DecisionMaker {
  
  /**
   * Uses an AI assistant to choose the best {@link Option} 
   * that matches the given user message.
   *
   * @param options a list of candidate {@link Option} objects in JSON-compatible form
   * @param message the user's message describing their intent or requirement
   * @return the most suitable {@link Option} selected by the AI
   */
  public Option makeDecision(List<Option> options, String message) {
    IDecisionMaker assistant = AiServices
        .builder(IDecisionMaker.class)
        .chatModel(OpenAiService.getJsonChatModel())
        .build();

    return assistant.makeDecision(options, message);
  }
  
  /**
   * AI interface defining the decision-making process.
   * <p>
   * This interface is bound to an LLM via LangChain4j annotations:
   * <ul>
   *   <li>{@link SystemMessage} provides the static decision rules and context.</li>
   *   <li>{@link UserMessage} injects the dynamic user input.</li>
   *   <li>{@link V} binds method parameters to template variables in the prompts.</li>
   * </ul>
   * The AI model is expected to return exactly one of the provided {@link Option} objects.
   * </p>
   */
  private interface IDecisionMaker {
    
    @SystemMessage("""
      OPTIONS (JSON array):
      {{options}}
      -------------------------------
      INSTRUCTIONS:
      1) INPUT: 'options' is injected as a JSON array of Option objects. Each Option is a full JSON object (e.g. {"id": "...", "condition": "...").
      2) TASK: Choose the single most suitable Option that matches the user's message.
      3) OUTPUT: Return **only** the chosen Option as raw JSON (exactly one of the objects from the OPTIONS array). Do NOT add, rename the options.
      4) TIE-BREAKER: If multiple options are equally suitable, return the one that appears earliest in the OPTIONS array.
      5) The chosen option must be EXACTLY one of the provided option, don't create or update the option by yourself.
      """)
    @UserMessage("{{message}}")
    public Option makeDecision(@V("options") List<Option> options, @V("message") String message);
  }
}
