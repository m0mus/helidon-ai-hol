package io.helidon.hol.lc4j;

import java.util.function.Supplier;

import io.helidon.service.registry.Service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;

@Service.Singleton
public class ContentRetrieverFactory implements Supplier<ContentRetriever> {
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    @Service.Inject
    public ContentRetrieverFactory(EmbeddingStore<TextSegment> embeddingStore,
                                   EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @Override
    public ContentRetriever get() {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }
}