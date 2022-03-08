import iflores.vamos.VProcess;
import org.scijava.vecmath.Point3f;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Entity {

    private final VProcess _process;
    private final Point3f _location;
    private final String _name;

    public Entity(VProcess process, ByteBuffer buf) {
        _process = process;
        _location = new Point3f(
                buf.getFloat(0x4),
                buf.getFloat(0x8),
                buf.getFloat(0xC)
        );
        byte[] nameBuf = new byte[16];
        buf.position(0x205);
        buf.get(nameBuf);
        int i = 0;
        while (i < nameBuf.length) {
            if (nameBuf[i] == 0) {
                break;
            }
            i++;
        }
        _name = new String(nameBuf, 0, i, StandardCharsets.ISO_8859_1);
    }

    public String getName() {
        return _name;
    }

    @Override
    public String toString() {
        return _name;
    }

    public Point3f getLocation() {
        return _location;
    }

}
