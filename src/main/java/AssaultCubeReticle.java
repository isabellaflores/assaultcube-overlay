import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class AssaultCubeReticle {

    private static final double RETICLE_POINT_RADIUS = 3.5;
    private static final double RETICLE_SHORT_DIMENSION = 2.0;
    private static final double RETICLE_LONG_DIMENSION = 7.0;
    private static final double RETICLE_SPACING = 2.0;

    public void draw(Graphics2D g2, int width, int height) {
        g2.setColor(Color.RED);

        double centerX = width / 2.0 + 1.0;
        double centerY = height / 2.0;
        Ellipse2D.Double reticlePoint = new Ellipse2D.Double(
                centerX - RETICLE_POINT_RADIUS,
                centerY - RETICLE_POINT_RADIUS,
                RETICLE_POINT_RADIUS * 2.0,
                RETICLE_POINT_RADIUS * 2.0
        );
        g2.fill(reticlePoint);
        for (Direction direction : Direction.values()) {
            drawReticleLine(g2, direction, centerX, centerY);
        }
    }

    private void drawReticleLine(Graphics2D g2, Direction direction, double centerX, double centerY) {
        Rectangle2D.Double line;
        double width;
        double height;
        if (direction.isVertical()) {
            width = RETICLE_SHORT_DIMENSION;
            height = RETICLE_LONG_DIMENSION;
            line = new Rectangle2D.Double(
                    centerX - width / 2.0,
                    centerY - height / 2.0 + direction.getMultiplier() * (height / 2.0 + RETICLE_POINT_RADIUS + RETICLE_SPACING),
                    width,
                    height
            );
        } else {
            width = RETICLE_LONG_DIMENSION;
            height = RETICLE_SHORT_DIMENSION;
            line = new Rectangle2D.Double(
                    centerX - width / 2.0 + direction.getMultiplier() * (width / 2.0 + RETICLE_POINT_RADIUS + RETICLE_SPACING),
                    centerY - height / 2.0,
                    width,
                    height
            );
        }
        g2.fill(line);
    }

    enum Direction {
        NORTH(true, -1), EAST(false, 1), SOUTH(true, 1), WEST(false, -1);

        private final int _multiplier;
        private final boolean _vertical;

        Direction(boolean vertical, int multiplier) {
            _vertical = vertical;
            _multiplier = multiplier;
        }

        public boolean isVertical() {
            return _vertical;
        }

        public int getMultiplier() {
            return _multiplier;
        }
    }


}
