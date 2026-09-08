package it.unicam.cs.mpgc.rpg125676.view.support;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

/**
 * Centralizes the visual theme of the game.
 */
public final class VintageTheme {

    public enum ButtonTone {
        ACTION,
        PRIMARY,
        SECONDARY,
        DANGER,
        FINAL
    }

    public static final Color TEXT = Color.rgb(215, 208, 189);
    public static final Color TEXT_BRIGHT = Color.rgb(237, 224, 195);
    public static final Color TEXT_MUTED = Color.rgb(149, 136, 112);
    public static final Color BORDER = Color.rgb(102, 90, 69);
    public static final Color PAPER = Color.rgb(211, 197, 165);
    public static final Color PAPER_TEXT = Color.rgb(48, 42, 34);
    public static final Color DANGER = Color.rgb(197, 95, 84);
    public static final Color LUCIDITY_GOOD = Color.rgb(138, 146, 113);
    public static final Color LUCIDITY_WARNING = Color.rgb(169, 130, 80);
    public static final Color LUCIDITY_CRITICAL = Color.rgb(159, 73, 66);
    public static final Color ATTENTION_DORMANT = Color.rgb(119, 110, 91);
    public static final Color ATTENTION_HUNTING = Color.rgb(168, 108, 73);
    public static final Color ATTENTION_UNLEASHED = Color.rgb(166, 63, 57);

    private static final CornerRadii SQUARE = CornerRadii.EMPTY;

    private VintageTheme() {}

    public static void screen(Region region) {
        region.setBackground(background(Color.rgb(23, 21, 17), Color.rgb(15, 14, 12)));
    }

    public static void menuScreen(Region region) {
        region.setBackground(background(Color.rgb(41, 35, 28), Color.rgb(13, 12, 10)));
    }

    public static void card(Region region) {
        region.setBackground(background(Color.rgb(48, 42, 33, 0.97), Color.rgb(25, 23, 19, 0.98)));
        region.setBorder(border(Color.rgb(113, 98, 77), 1));
        region.setEffect(new DropShadow(28, Color.rgb(0, 0, 0, 0.75)));
    }

    public static void topPanel(Region region) {
        region.setBackground(background(Color.rgb(23, 20, 15), Color.rgb(14, 13, 10)));
        region.setBorder(new Border(new BorderStroke(Color.rgb(81, 71, 54), BorderStrokeStyle.SOLID, SQUARE, new BorderWidths(0, 0, 1, 0))));
    }

    public static void bottomPanel(Region region) {
        region.setBackground(solid(Color.rgb(17, 16, 13)));
        region.setBorder(new Border(new BorderStroke(Color.rgb(62, 56, 45), BorderStrokeStyle.SOLID, SQUARE, new BorderWidths(1, 0, 0, 0))));
    }

    public static void sidePanel(Region region) {
        region.setBackground(background(Color.rgb(33, 29, 23), Color.rgb(23, 21, 16)));
        region.setBorder(new Border(new BorderStroke(Color.rgb(76, 67, 52), BorderStrokeStyle.SOLID, SQUARE, new BorderWidths(0, 1, 0, 1))));
    }

    public static void centerPanel(Region region) {
        region.setBackground(background(Color.rgb(18, 17, 14), Color.rgb(13, 12, 10)));
    }

    public static void borderedPanel(Region region) {
        region.setBackground(solid(Color.rgb(32, 28, 22)));
        region.setBorder(border(Color.rgb(98, 85, 64), 1));
    }

    public static void roomFrame(Region region, boolean confrontation) {
        Color borderColor = confrontation ? Color.rgb(143, 68, 62) : Color.rgb(118, 103, 79);
        double borderWidth = confrontation ? 6 : 5;

        region.setBackground(background(Color.rgb(33, 29, 23), Color.rgb(12, 11, 9)));
        region.setBorder(border(borderColor, borderWidth));
        region.setPadding(new Insets(7));
        region.setEffect(new DropShadow(confrontation ? 22 : 20, confrontation ? Color.rgb(117, 36, 31, 0.80) : Color.rgb(0, 0, 0, 0.80)));
    }

    public static void confrontationPanel(Region region) {
        region.setBackground(solid(Color.rgb(85, 31, 28, 0.20)));
        region.setBorder(border(Color.rgb(117, 66, 61), 1));
        region.setPadding(new Insets(12));
    }

    public static void feedbackPanel(Region region, boolean success) {
        region.setBackground(solid(Color.rgb(32, 28, 22)));
        region.setBorder(border(success ? Color.rgb(129, 116, 86) : Color.rgb(135, 75, 68), 1));
        region.setPadding(new Insets(6, 12, 6, 12));
    }

    public static void fieldNotes(TextArea control) {
        control.setFont(Font.font("Georgia", 13));
        control.setBackground(solid(PAPER));
        control.setBorder(border(Color.rgb(118, 104, 78), 2));
    }

    public static void comboBox(Region control) {
        control.setBackground(background(Color.rgb(55, 49, 41), Color.rgb(40, 36, 30)));
        control.setBorder(border(Color.rgb(107, 94, 73), 1));
    }

    public static void divider(Region region) {
        region.setMinHeight(1);
        region.setPrefHeight(1);
        region.setMaxHeight(1);
        region.setBackground(solid(Color.rgb(98, 88, 70)));
    }

    public static void label(Label label, double size, Color color, FontWeight weight, boolean italic) {
        label.setTextFill(color);
        label.setFont(Font.font("Georgia", weight, italic ? FontPosture.ITALIC : FontPosture.REGULAR, size));
    }

    public static void button(Button button, ButtonTone tone) {
        ButtonPalette palette = palette(tone);
        applyButtonPalette(button, palette.base(), palette.border(), palette.text());
        button.setPadding(new Insets(9, 14, 9, 14));
        button.setCursor(javafx.scene.Cursor.HAND);
        button.setOnMouseEntered(event -> {
            if (!button.isDisabled()) {
                applyButtonPalette(button, palette.hover(), palette.hoverBorder(), palette.text());
            }
        });

        button.setOnMouseExited(event -> applyButtonPalette(button, palette.base(), palette.border(), palette.text()));
        button.disableProperty().addListener((observable, oldValue, disabled) -> button.setOpacity(disabled ? 0.38 : 1.0));
    }

    public static Border border(Color color, double width) {
        return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, SQUARE, new BorderWidths(width)));
    }

    public static Background solid(Color color) {
        return new Background(new BackgroundFill(color, SQUARE, Insets.EMPTY));
    }

    private static Background background(Color top, Color bottom) {
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, top), new Stop(1, bottom));
        return new Background(new BackgroundFill(gradient, SQUARE, Insets.EMPTY));
    }

    private static void applyButtonPalette(Button button, Color background, Color border, Color text) {
        button.setBackground(solid(background));
        button.setBorder(VintageTheme.border(border, 1));
        button.setTextFill(text);
        button.setFont(Font.font("Georgia", FontWeight.BOLD, 13));
    }

    private static ButtonPalette palette(ButtonTone tone) {
        return switch (tone) {
            case ACTION -> new ButtonPalette(
                    Color.rgb(57, 50, 41),
                    Color.rgb(81, 69, 54),
                    Color.rgb(112, 97, 75),
                    Color.rgb(160, 135, 101),
                    Color.rgb(224, 213, 188)
            );
            case PRIMARY -> new ButtonPalette(
                    Color.rgb(104, 85, 66),
                    Color.rgb(123, 101, 77),
                    Color.rgb(146, 119, 91),
                    Color.rgb(166, 136, 101),
                    Color.rgb(241, 228, 200)
            );
            case SECONDARY -> new ButtonPalette(
                    Color.rgb(52, 46, 38),
                    Color.rgb(73, 64, 54),
                    Color.rgb(98, 86, 68),
                    Color.rgb(136, 118, 91),
                    Color.rgb(222, 210, 183)
            );
            case DANGER -> new ButtonPalette(
                    Color.rgb(101, 54, 50),
                    Color.rgb(122, 65, 59),
                    Color.rgb(146, 90, 83),
                    Color.rgb(177, 105, 96),
                    Color.rgb(240, 215, 207)
            );
            case FINAL -> new ButtonPalette(
                    Color.rgb(85, 68, 89),
                    Color.rgb(105, 84, 110),
                    Color.rgb(128, 105, 134),
                    Color.rgb(155, 126, 162),
                    Color.rgb(232, 217, 232)
            );
        };
    }

    private record ButtonPalette(Color base, Color hover, Color border, Color hoverBorder, Color text) {}
}
