# 4. Building AI Service

## In This Section, We Will:

- Understand the need for an AI Service in LLM-powered applications.
- Learn how LangChain4J AI Services help combine multiple AI components.
- Implement an AI Service.
- Modify our RESTful service to use the AI Service instead of calling the chat model directly.

---

## 1. Why Do We Need an AI Service?

LLM-powered applications typically require more than just a **chat model**. To provide **advanced AI functionality**, we also need:

- **Embedding models**
- **Embedding storage**
- **Chat memory** (to remember conversation history)
- **Other AI components**

Instead of managing these separately, we can use **LangChain4J AI Services**, which combine different AI components into a single service and make them work together.

Helidon’s **LangChain4J integration** makes it easy to create AI Services using **Helidon Inject**.

## 2. Creating the AI Service

An **AI Service** is a user-defined interface annotated with `@Ai.Service`. By default, it **automatically integrates all LangChain4J (LC4J) components** registered in Helidon's service registry. This means that if no specific configuration is provided, the service will use all available AI components.

At this stage, only the **`ChatLanguageModel`** has been configured, so it will be **automatically injected** into the service. Later, we will extend the AI Service by adding **embedding storage, chat memory**, and other components to enhance its capabilities.

Create a new file `ChatAiService.java` in your project and add the following code:

```java
package io.helidon.hol.lc4j;

import io.helidon.integrations.langchain4j.Ai;

import dev.langchain4j.service.SystemMessage;

@Ai.Service
interface ChatAiService {

    String chat(String question);
}
```

## 3. Updating the RESTful Service

Now, let's modify our existing `ChatBotService.java` to use the **AI Service** instead of interacting with the chat model directly.

### **Before:**

Previously, `ChatLanguageModel` was injected into `ChatBotService` and called directly:

```java
var answer = chatModel.generate(question);
```

### **After:**

Now, we will inject and use `ChatAiService` instead:

```java
var answer = chatAiService.chat(question);
```

Here is the updated version of `ChatBotService`:

```java
import io.helidon.service.registry.Service;
import io.helidon.webserver.http.HttpRules;
import io.helidon.webserver.http.HttpService;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;
import dev.langchain4j.service.ChatAiService;
import io.helidon.common.media.type.MediaTypes;

@Service.Singleton
class ChatBotService implements HttpService {

    private final ChatAiService chatAiService;

    @Service.Inject
    public ChatBotService(ChatAiService chatAiService) {
        // Injecting ChatAiService using constructor injection
        this.chatAiService = chatAiService;
    }

    @Override
    public void routing(HttpRules httpRules) {
        httpRules.get("/chat", this::chatWithAssistant);
    }

    private void chatWithAssistant(ServerRequest req, ServerResponse res) {
        // Reading the `question` query parameter from the request. 
        var question = req.query().get("question").orElse("Hello");

        // Calling the AI service to get the answer
        var answer = chatAiService.chat(question);

        // Setting response content type and returning the answer
        res.headers().contentType(MediaTypes.TEXT_PLAIN);
        res.send(answer);
    }
}
```

## **5. Testing the AI Service**

Recompile and run the application:

```sh
mvn clean package
java -jar target/helidon-ai-hol.jar
```

Now, test it in a browser:

```
http://localhost:8080/chat?question=Hello
```

Or, use `curl`:

```sh
curl -X GET "http://localhost:8080/chat?question=Hello"
```

**Expected Result:** The response should still be an AI-generated answer, just like before.

---

### Next Step → [Adding Retrieval-Augmented Generation (RAG)](hol/05_adding_rag.md)
