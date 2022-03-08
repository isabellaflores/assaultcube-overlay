import iflores.vamos.Async;
import iflores.vamos.MemoryAddress;
import iflores.vamos.VProcess;

import java.nio.ByteBuffer;
import java.util.List;

public class EntityList {

    private final VProcess _process;
    private final MemoryAddress _localPlayerAddress;
    private final int _entityCount;
    private final Async<List<MemoryAddress>> _entityPointerArray;

    public EntityList(VProcess process, ByteBuffer buf) {
        int localPlayerAddress = buf.getInt();
        int entityListBaseAddress = buf.getInt();
        buf.getInt(); // entity list capacity?
        int entityCount = buf.getInt();
        _process = process;
        _localPlayerAddress = MemoryAddress.of(localPlayerAddress);
        _entityCount = entityCount;
        _entityPointerArray = process.readMemory(
                MemoryAddress.of(entityListBaseAddress),
                Parsers.createPointerArrayParser(entityCount)
        );
    }

    public int getEntityCount() {
        return _entityCount - 1; // first entry is always null
    }

    public Async<Entity> readLocalPlayer() {
        return _process.readMemory(_localPlayerAddress, Parsers.ENTITY_PARSER);
    }

    public Async<Entity> readEntity(int idx) {
        MemoryAddress memoryAddress = _entityPointerArray.get().get(idx + 1); // first entry is always null
        return memoryAddress == null ? Async.of(null) : _process.readMemory(memoryAddress, Parsers.ENTITY_PARSER);
    }

}