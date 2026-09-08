package it.unicam.cs.mpgc.rpg125676.view.component;

import it.unicam.cs.mpgc.rpg125676.view.support.VintageTheme;
import javafx.geometry.Pos;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Status bar with a configurable fill color.
 */
public class StatusBar extends StackPane {

    private final Region fill = new Region();
    private double progress;

    public StatusBar(double preferredWidth) {
        setMinWidth(0);
        setPrefWidth(preferredWidth);
        setMaxWidth(preferredWidth);
        setMinHeight(12);
        setPrefHeight(12);
        setMaxHeight(12);
        setAlignment(Pos.CENTER_LEFT);
        setBackground(VintageTheme.solid(Color.rgb(23, 21, 16)));
        setBorder(VintageTheme.border(Color.rgb(81, 71, 55), 1));
        fill.setManaged(false);
        fill.setBackground(VintageTheme.solid(VintageTheme.ATTENTION_DORMANT));
        getChildren().add(fill);
    }

    public void setProgress(double value) {
        progress = Math.max(0, Math.min(1, value));
        requestLayout();
    }

    public double getProgress() {
        return progress;
    }

    public void setFillColor(Color color) {
        fill.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
    }

    @Override
    protected void layoutChildren() {
        double left = snappedLeftInset();
        double right = snappedRightInset();
        double top = snappedTopInset();
        double bottom = snappedBottomInset();
        double availableWidth = Math.max(0, getWidth() - left - right);
        double availableHeight = Math.max(0, getHeight() - top - bottom);
        fill.resizeRelocate(left, top, availableWidth * progress, availableHeight);
    }
}
