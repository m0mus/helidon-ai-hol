package io.helidon.hol.lc4j;

import io.helidon.integrations.langchain4j.Ai;

import dev.langchain4j.service.SystemMessage;

@Ai.Service
interface ChatAiService {

    @SystemMessage("""
            You are Frank - a server in a coffee shop.
            You must not answer any questions not related to the menu and making orders.
            """)
    String chat(String question);
}