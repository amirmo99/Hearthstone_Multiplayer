package UI.GUI;

import configs.GameGraphicConstants;
import configs.PanelsConfigs;
import configs.PathConfigs;
import network.client.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

abstract public class MyPanel extends JPanel {

    private BufferedImage bg;
    protected PanelsConfigs panelsConfigs;
    protected PathConfigs pathConfigs;
    protected GameGraphicConstants graphicConstants;

    protected Client client;

    public MyPanel(Client client) {
        super();
        this.client = client;
        panelsConfigs = new PanelsConfigs();
        pathConfigs = new PathConfigs();
        graphicConstants = new GameGraphicConstants();
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(getBackgroundImage(), 0, 0,(int) graphicConstants.getGameWidth(),(int) graphicConstants.getGameHeight(), null);
    }

    protected void drawBackground(String path) {
        try {
            bg = ImageIO.read(new File(path));
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Wrong background path: " + path);
        }
    }

    protected void changePanel(String panelName) {
        client.getMainFrame().setActivePanel(panelName);
    }

    protected void configureButton(JButton button, Dimension dimension, int fontSize, ActionListener actionListener) {
        Font font = new Font(panelsConfigs.getFont(), Font.BOLD, fontSize);
        button.setFont(font);
        button.setForeground(Color.WHITE);
        button.setAlignmentX(CENTER_ALIGNMENT);
        if (dimension != null)
            button.setPreferredSize(dimension);
        button.addActionListener(actionListener);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
    }

    protected void configureField(JTextField textField, int fontSize, boolean editable, Color color) {

        Font font = new Font(panelsConfigs.getFont(), Font.ITALIC, fontSize);
        textField.setFont(font);
        textField.setAlignmentX(CENTER_ALIGNMENT);
        textField.setForeground(color);

        if (!editable) {
            textField.setEditable(false);
            textField.setOpaque(false);
            textField.setBorder(null);
        }
    }

    protected void configureLabel(JLabel label, int fontSize, Color color) {
        Font font = new Font(panelsConfigs.getFont(), Font.ITALIC, fontSize);
        label.setFont(font);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        label.setForeground(color);
    }

    protected void createTitle(String label) {
        TitledBorder title = BorderFactory.createTitledBorder(label);
        title.setTitleJustification(TitledBorder.CENTER);
        title.setTitlePosition(TitledBorder.BELOW_TOP);
        title.setTitleColor(panelsConfigs.getLabelThemeColor());
        Font font = new Font(panelsConfigs.getFont(), Font.ITALIC, panelsConfigs.getBigTitleSize());
        title.setTitleFont(font);
        setBorder(title);
    }

    //Getters and Setters
    public BufferedImage getBackgroundImage() {
        return bg;
    }

}
