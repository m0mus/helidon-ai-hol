package io.helidon.hol.lc4j;

import java.util.function.Supplier;

import io.helidon.service.registry.Service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

@Service.Singleton
public class EmbeddingStoreFactory implements Supplier<EmbeddingStore<TextSegment>> {

    @Override
    public EmbeddingStore<TextSegment> get() {
        return new InMemoryEmbeddingStore<>();
    }
}