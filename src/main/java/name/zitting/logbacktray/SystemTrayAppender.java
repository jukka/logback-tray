/*
 * Copyright 2013 Jukka Zitting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.zitting.logbacktray;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class SystemTrayAppender extends AppenderBase<ILoggingEvent> {

    private final SystemTray tray = SystemTray.getSystemTray();

    private TrayIcon icon = null;

    private Image image = createDefaultImage();

    private String tooltip = null;

    public String getImage() {
        return image.toString();
    }

    public void setImage(String image) {
        try {
            Dimension size = tray.getTrayIconSize();
            this.image = ImageIO.read(new File(image)).getScaledInstance(
                    size.width, size.height, Image.SCALE_SMOOTH);
        } catch (IOException e) {
            addError("Unable to load icon image: " + image, e);
        }
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @Override
    public void start() {
        final TrayIcon i = new TrayIcon(image, tooltip);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    tray.add(i);
                } catch (AWTException e) {
                    addError("System tray initialization failed", e);
                }
            }
        });
        icon = i;

        super.start();
    }

    @Override
    public void stop() {
        super.stop();

        final TrayIcon i = icon;
        if (i != null) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    tray.remove(i);
                }
            });
            icon = null;
        }
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        final MessageType type;
        Level level = eventObject.getLevel();
        if (level.isGreaterOrEqual(Level.ERROR)) {
            type = MessageType.ERROR;
        } else if (level.isGreaterOrEqual(Level.WARN)) {
            type = MessageType.WARNING;
        } else {
            type = MessageType.INFO;
        }

        final String caption = level.toString();
        final String text = eventObject.getFormattedMessage();
        final TrayIcon i = icon;
        if (i != null) {
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    i.displayMessage(caption, text, type);
                }
            });
        }
    }

    private Image createDefaultImage() {
        Dimension size = tray.getTrayIconSize();
        BufferedImage icon = new BufferedImage(
                size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = icon.createGraphics();
        graphics.setColor(Color.GREEN);
        graphics.fillRect(2, 2, size.width - 3, size.height - 3);
        return icon;
    }

}
