import iflores.vamos.MemoryAddress;
import iflores.vamos.MemoryObjectParser;
import org.jetbrains.annotations.NotNull;
import org.scijava.vecmath.Matrix4f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public interface Parsers {

    MemoryObjectParser<EntityList> ENTITY_LIST_PARSER = new MemoryObjectParser<>(16, EntityList::new);
    MemoryObjectParser<Entity> ENTITY_PARSER = new MemoryObjectParser<>(0x215, Entity::new);
    MemoryObjectParser<Rectangle> VIEWPORT_DIMENSIONS_PARSER = new MemoryObjectParser<>(4,
            (p, b) -> p.readMemory(
                    MemoryAddress.of(b.getInt()).add(0x10),
                    new MemoryObjectParser<>(16, (p2, b2) ->
                            new Rectangle(
                                    b2.getInt(),
                                    b2.getInt(),
                                    b2.getInt(),
                                    b2.getInt()
                            )
                    )
            ).get()
    );
    MemoryObjectParser<Matrix4f> MATRIX4F_PARSER = new MemoryObjectParser<>(
            64,
            (p, buf) -> new Matrix4f(
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat(),
                    buf.getFloat()
            )
    );

    @NotNull
    static MemoryObjectParser<List<MemoryAddress>> createPointerArrayParser(int entityCount) {
        return new MemoryObjectParser<>(
                entityCount * 4,
                (process, buf) -> {
                    List<MemoryAddress> results = new ArrayList<>();
                    for (int i = 0; i < entityCount; i++) {
                        int address = buf.getInt();
                        results.add(address == 0 ? null : MemoryAddress.of(address));
                    }
                    return results;
                }
        );
    }

}
