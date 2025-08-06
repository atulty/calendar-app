package startapp.ai;

import org.springframework.stereotype.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;

@Service
public class AiIntegrationService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;

    public AiIntegrationService(ChatModel chatModel,
                                EmbeddingModel embeddingModel) {
        // build a ChatClient from the injected ChatModel
        this.chatClient = ChatClient.builder(chatModel).build();
        // directly use the injected EmbeddingModel
        this.embeddingModel = embeddingModel;
    }

    // e.g. natural-language summary
    public String summarizeEvent(String eventDescription) {
        return chatClient
                .prompt()
                .system("Summarize this event:")
                .user(eventDescription)
                .call()
                .content();
    }

    // e.g. embeddings-based similarity
    public String embedText(String text) {
        return embeddingModel.embed(text).toString();
    }

    // …etc.
}
