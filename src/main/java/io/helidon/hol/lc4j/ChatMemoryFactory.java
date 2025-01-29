package io.helidon.hol.lc4j;

import java.util.function.Supplier;

import io.helidon.service.registry.Service;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

@Service.Singleton
public class ChatMemoryFactory implements Supplier<ChatMemory> {
    @Override
    public ChatMemory get() {
        return MessageWindowChatMemory.withMaxMessages(10);
    }
}