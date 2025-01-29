package io.helidon.hol.lc4j;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import io.helidon.common.config.Config;
import io.helidon.service.registry.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

@Service.Singleton
public class MenuItemsIngestor {
    private static final String CONFIG_KEY = "app.menu-items";
    private static final Logger LOGGER = Logger.getLogger(MenuItemsIngestor.class.getName());

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;

    private final Path jsonPath;

    @Service.Inject
    MenuItemsIngestor(Config config,
                      EmbeddingStore<TextSegment> embeddingStore,
                      EmbeddingModel embeddingModel) {
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
        this.jsonPath = config.get(CONFIG_KEY)
                .as(Path.class)
                .orElseThrow(() -> new IllegalStateException(CONFIG_KEY + " is a required configuration key for RAG"));
    }

    public void ingest() {
        var start = System.currentTimeMillis();

        var ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();

        var menuItems = readMenuItems();

        var documents = menuItems.stream()
                .map(this::generateDocument)
                .toList();

        ingestor.ingest(documents);

        LOGGER.info(String.format("DEMO %d documents ingested in %d ms", menuItems.size(),
                                  System.currentTimeMillis() - start));
    }

    private List<MenuItem> readMenuItems() {
        var objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(jsonPath.toFile(), new TypeReference<>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Document generateDocument(MenuItem item) {
        var str = String.format(
                "%s: %s. Category: %s. Price: $%.2f. Tags: %s. Add-ons: %s.",
                item.getName(),
                item.getDescription(),
                item.getCategory(),
                item.getPrice(),
                String.join(", ", item.getTags()),
                String.join(", ", item.getAddOns())
        );

        return Document.from(str);
    }

    /**
     * This is the embedding model we want to use.
     */
    @Service.Singleton
    static class EmbeddingModelFactory implements Supplier<EmbeddingModel> {
        @Override
        public EmbeddingModel get() {
            return new AllMiniLmL6V2EmbeddingModel();
        }
    }

    /**
     * And the embedding store we want to use.
     */
    @Service.Singleton
    static class EmbeddingStoreFactory implements Supplier<EmbeddingStore<TextSegment>> {
        @Override
        public EmbeddingStore<TextSegment> get() {
            return new InMemoryEmbeddingStore<>();
        }
    }
}
