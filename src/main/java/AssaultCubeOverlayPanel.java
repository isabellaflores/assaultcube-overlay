import org.scijava.vecmath.Matrix4f;
import org.scijava.vecmath.Point2i;
import org.scijava.vecmath.Point3f;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AssaultCubeOverlayPanel extends JPanel {

    public static final Font FONT = new Font("SansSerif", Font.BOLD, 12);
    private final AssaultCubeReticle _reticle = new AssaultCubeReticle();
    private List<Entity> _entities = new ArrayList<>();
    private Matrix4f _viewMatrix;
    private Entity _localPlayer;

    public AssaultCubeOverlayPanel() {
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        _reticle.draw(g2, getWidth(), getHeight());
        if (_viewMatrix != null && _localPlayer != null) {
            for (Entity entity : _entities) {
                Point3f entityLocation = entity.getLocation();
                Point3f headLoc3d = new Point3f(entityLocation.getX(), entityLocation.getY(), entityLocation.getZ() + 0.6f);
                Point2i headLoc2d = AssaultCubeOverlay.worldToScreen(_viewMatrix, headLoc3d, getWidth(), getHeight());
                if (headLoc2d != null) {
                    g2.setColor(Color.RED);
                    float distanceFromLocalPlayer = Math.abs(entity.getLocation().distance(_localPlayer.getLocation()));
                    float triangleSize = 200 / distanceFromLocalPlayer;
                    g2.fillPolygon(
                            new int[]{headLoc2d.x, (int) (headLoc2d.x - triangleSize * 2.0f / 3.0f), (int) (headLoc2d.x + triangleSize * 2.0f / 3.0f)},
                            new int[]{headLoc2d.y, (int) (headLoc2d.y - triangleSize), (int) (headLoc2d.y - triangleSize)},
                            3
                    );
                    Point3f textLoc3d = new Point3f(headLoc3d.getX(), headLoc3d.getY(), headLoc3d.getZ());
                    Point2i textLoc2d = AssaultCubeOverlay.worldToScreen(_viewMatrix, textLoc3d, getWidth(), getHeight());
                    if (textLoc2d != null) {
                        g2.setFont(FONT.deriveFont(240.0f / distanceFromLocalPlayer));
                        FontMetrics metrics = g2.getFontMetrics();
                        int textX = textLoc2d.x - metrics.stringWidth(entity.getName()) / 2;
                        int textY = textLoc2d.y - metrics.getHeight();
                        g2.drawString(entity.getName(), textX, textY);
                    }
                }
            }
        }
    }

    public void update(List<Entity> entities, Matrix4f viewMatrix, Entity localPlayer) {
        _entities = entities;
        _viewMatrix = viewMatrix;
        _localPlayer = localPlayer;
        repaint();
    }
}
