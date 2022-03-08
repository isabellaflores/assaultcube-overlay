import iflores.vamos.*;
import org.scijava.vecmath.Matrix4f;
import org.scijava.vecmath.Point2i;
import org.scijava.vecmath.Point3f;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AssaultCubeOverlay extends Overlay {

    public static final String PROCESS_NAME = "ac_client.exe";
    private MemoryObjectReader<EntityList> _entityListReader;
    private MemoryObjectReader<Matrix4f> _viewMatrixReader;
    private MemoryObjectReader<Rectangle> _viewportDimensionsReader;

    @Override
    public void run(VConfig config) {
        Updater<VProcess> processUpdater = new Updater<VProcess>(
                500,
                state -> {
                    VProcess process = Vamos.getProcessByName(PROCESS_NAME).get();
                    state.setDelay(process == null ? 500 : 5000);
                    state.update(process);
                }
        ).start();
        Updater<VMemoryRegions> memoryRegionsUpdater = new Updater<VMemoryRegions>(
                Long.MAX_VALUE,
                state -> {
                    VMemoryRegions memoryRegions = processUpdater.get().getMemoryRegions().get();
                    state.update(memoryRegions);
                }
        ).start();
        Updater<VMemoryRegion> exeBaseUpdater = new Updater<VMemoryRegion>(
                Long.MAX_VALUE,
                state -> {
                    VMemoryRegion exeBase = memoryRegionsUpdater.get().getByName(PROCESS_NAME);
                    state.update(exeBase);
                }
        ).start();
        _entityListReader = new MemoryObjectReader<>(
                processUpdater,
                () -> exeBaseUpdater.get().getRegionStart().add(0x187c0c),
                Parsers.ENTITY_LIST_PARSER
        );
        _viewMatrixReader = new MemoryObjectReader<>(
                processUpdater,
                () -> exeBaseUpdater.get().getRegionStart().add(0x17afe0),
                Parsers.MATRIX4F_PARSER
        );
        _viewportDimensionsReader = new MemoryObjectReader<>(
                processUpdater,
                () -> exeBaseUpdater.get().getRegionStart().add(0x182884),
                Parsers.VIEWPORT_DIMENSIONS_PARSER
        );
        JFrame f = new OverlayFrame(true);
        new Updater<>(
                500,
                state -> {
                    Rectangle viewportDimensions = _viewportDimensionsReader.readMemory().get();
                    Rectangle bounds = config.getRequestedOverlayBounds();
                    f.setBounds(
                            (int) (bounds.getX() + viewportDimensions.getX()),
                            (int) (bounds.getY() + viewportDimensions.getY()),
                            viewportDimensions.width,
                            viewportDimensions.height
                    );
                }
        ).start();
        AssaultCubeOverlayPanel assaultCubeOverlayPanel = new AssaultCubeOverlayPanel();
        f.add(assaultCubeOverlayPanel, BorderLayout.CENTER);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setVisible(true);
        //noinspection InfiniteLoopStatement
        while (true) {
            EntityList entityList = _entityListReader.readMemory().get();
            List<Entity> entities = new ArrayList<>();
            Entity localPlayer;
            if (entityList != null) {
                for (int i = 0; i < entityList.getEntityCount(); i++) {
                    Entity entity = entityList.readEntity(i).get();
                    if (entity != null) {
                        entities.add(entity);
                    }
                }
                localPlayer = entityList.readLocalPlayer().get();
            } else {
                localPlayer = null;
            }
            Matrix4f viewMatrix = _viewMatrixReader.readMemory().get();
            SwingUtilities.invokeLater(() -> {
                assaultCubeOverlayPanel.update(entities, viewMatrix, localPlayer);
            });
        }
    }

    public static Point2i worldToScreen(Matrix4f matrix, Point3f location, int viewportWidth, int viewportHeight) {

        float screenX = (matrix.m00 * location.getX()) + (matrix.m10 * location.getY()) + (matrix.m20 * location.getZ()) + matrix.m30;
        float screenY = (matrix.m01 * location.getX()) + (matrix.m11 * location.getY()) + (matrix.m21 * location.getZ()) + matrix.m31;
        float screenZ = (matrix.m03 * location.getX()) + (matrix.m13 * location.getY()) + (matrix.m23 * location.getZ()) + matrix.m33;

        float camX = viewportWidth / 2f;
        float camY = viewportHeight / 2f;

        float x = camX + (camX * screenX / screenZ);
        float y = camY - (camY * screenY / screenZ);

        return screenZ > 0.001f  //not behind us
                ? new Point2i((int) x, (int) y)
                : null;
    }

}
