package io.helidon.hol.lc4j;

import java.util.function.Supplier;

import io.helidon.service.registry.Service;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;

@Service.Singleton
public class EmbeddingModelFactory implements Supplier<EmbeddingModel> {

    @Override
    public EmbeddingModel get() {
        return new AllMiniLmL6V2EmbeddingModel();
    }
}
