package com.restaurant.views.RestaurantTable;

import com.restaurant.models.RestaurantTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Set;

public class TableMapPanel extends JPanel {
    private final int maxX, maxY;
    private final List<RestaurantTable> tables;
    private final Set<Integer> availableIds;
    private final int cellSize = 70;
    private Point dragStartCell = null;
    private Point dragCurrentCell = null;
    private boolean dragging = false;

    public TableMapPanel(int maxX, int maxY,
                         List<RestaurantTable> tables,
                         Set<Integer> availableIds,
                         Listener listener) {
        this.maxX = maxX;
        this.maxY = maxY;
        this.tables = tables;
        this.availableIds = availableIds;
        setPreferredSize(new Dimension(maxX * cellSize, maxY * cellSize));
        setBackground(Color.WHITE);
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point cell = toCell(e.getX(), e.getY());
                for (RestaurantTable t : tables) {
                    if (cell.x >= t.getStartX() && cell.x <= t.getEndX() &&
                            cell.y >= t.getStartY() && cell.y <= t.getEndY()) {
                        listener.onExistingTable(t, cell.x, cell.y);
                        return;
                    }
                }
                dragStartCell = cell;
                dragCurrentCell = cell;
                dragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!dragging) return;
                Point cell = toCell(e.getX(), e.getY());
                int x0 = Math.min(dragStartCell.x, cell.x);
                int y0 = Math.min(dragStartCell.y, cell.y);
                int x1 = Math.max(dragStartCell.x, cell.x);
                int y1 = Math.max(dragStartCell.y, cell.y);
                if (!regionOverlapsTable(x0, y0, x1, y1)) {
                    listener.onNewRegion(x0, y0, x1, y1);
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
                dragging = false;
                dragStartCell = dragCurrentCell = null;
                repaint();
            }
        };
        addMouseListener(mouseListener);
        MouseMotionListener mouseMotionListener = new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!dragging) return;
                Point cell = toCell(e.getX(), e.getY());
                int x0 = Math.min(dragStartCell.x, cell.x);
                int y0 = Math.min(dragStartCell.y, cell.y);
                int x1 = Math.max(dragStartCell.x, cell.x);
                int y1 = Math.max(dragStartCell.y, cell.y);
                if (!regionOverlapsTable(x0, y0, x1, y1)) {
                    dragCurrentCell = cell;
                    repaint();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        };
        addMouseMotionListener(mouseMotionListener);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.LIGHT_GRAY);
        for (int x = 0; x <= maxX; x++) {
            int xx = x * cellSize;
            g.drawLine(xx, 0, xx, maxY * cellSize);
        }
        for (int y = 0; y <= maxY; y++) {
            int yy = y * cellSize;
            g.drawLine(0, yy, maxX * cellSize, yy);
        }
        for (RestaurantTable t : tables) {
            int sx = t.getStartX() * cellSize,
                    sy = t.getStartY() * cellSize,
                    ex = (t.getEndX() + 1) * cellSize,
                    ey = (t.getEndY() + 1) * cellSize;
            int w = ex - sx, h = ey - sy;
            boolean avail;
            if (availableIds == null) {
                avail = t.isAvailable();
            } else {
                avail = availableIds.contains(t.getId());
            }
            g.setColor(avail
                    ? new Color(144, 238, 144)
                    : new Color(240, 128, 128));
            g.fillRoundRect(sx + 2, sy + 2, w - 4, h - 4, 16, 16);
            g.setColor(Color.DARK_GRAY);
            String label = "#" + t.getNumber();
            FontMetrics fm = g.getFontMetrics();
            int tx = sx + (w - fm.stringWidth(label)) / 2;
            int ty = sy + (h + fm.getAscent()) / 2 - 2;
            g.drawString(label, tx, ty);
        }
        if (dragging && dragStartCell != null && dragCurrentCell != null) {
            int x0 = Math.min(dragStartCell.x, dragCurrentCell.x);
            int y0 = Math.min(dragStartCell.y, dragCurrentCell.y);
            int x1 = Math.max(dragStartCell.x, dragCurrentCell.x);
            int y1 = Math.max(dragStartCell.y, dragCurrentCell.y);
            int rx = x0 * cellSize, ry = y0 * cellSize;
            int rw = (x1 - x0 + 1) * cellSize;
            int rh = (y1 - y0 + 1) * cellSize;
            g.setColor(new Color(0, 0, 255, 64));
            g.fillRect(rx, ry, rw, rh);
            g.setColor(Color.BLUE);
            g.drawRect(rx, ry, rw, rh);
        }
    }

    private Point toCell(int mx, int my) {
        int cx = mx / cellSize;
        int cy = my / cellSize;
        cx = Math.max(0, Math.min(maxX - 1, cx));
        cy = Math.max(0, Math.min(maxY - 1, cy));
        return new Point(cx, cy);
    }

    private boolean regionOverlapsTable(int sx, int sy, int ex, int ey) {
        for (RestaurantTable t : tables) {
            int tx0 = t.getStartX(), ty0 = t.getStartY();
            int tx1 = t.getEndX(), ty1 = t.getEndY();
            if (!(ex < tx0 || sx > tx1 || ey < ty0 || sy > ty1)) {
                return true;
            }
        }
        return false;
    }

    public interface Listener {
        void onExistingTable(RestaurantTable table, int clickX, int clickY);

        void onNewRegion(int startX, int startY, int endX, int endY);
    }
}
