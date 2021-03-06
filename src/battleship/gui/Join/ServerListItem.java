//Csaba
package battleship.gui.Join;

import battleship.Networking.ServerAddress;
import battleship.Resources.Resources;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author nycs0
 */
public class ServerListItem extends JPanel {

    public ServerAddress serverAddress;
    public JLabel name, ipPort;
    public boolean available = false;

    public ServerListItem(ServerAddress serverAddress) {
        this.setLayout(null);
        this.serverAddress = serverAddress;

        setBackground(Resources.BackgroundColor);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                Entered();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Exited();
            }
        });
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                name.setSize(size().width, 35);
                ipPort.setSize(size().width, 35);
            }
        });

        name = new JLabel(serverAddress.getName(), SwingConstants.CENTER);
        name.setSize(100, 35);
        name.setLocation(0, 0);
        name.setFont(new Font("Dialog", Font.BOLD, 20));
        this.add(name);

        ipPort = new JLabel(serverAddress.getIP() + ":" + serverAddress.getPort(), SwingConstants.CENTER);
        ipPort.setSize(100, 35);
        ipPort.setLocation(0, 30);
        this.add(ipPort);
    }

    public void setAvailable(boolean value) {
        if (value) {
            ipPort.setForeground(Color.GREEN);
        } else {
            ipPort.setForeground(Color.RED);
        }
        available = value;
    }

    public ServerAddress getServerAddress() {
        return serverAddress;
    }

    public void Select() {
        setBorder(BorderFactory.createLineBorder(Color.WHITE));
    }

    public void UnSelect() {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    private void Entered() {
        int darkeningLevel = -32;

        Color color = new Color(
                getBackground().getRed() + darkeningLevel < 0 ? 0 : getBackground().getRed() + darkeningLevel,
                getBackground().getGreen() + darkeningLevel < 0 ? 0 : getBackground().getGreen() + darkeningLevel,
                getBackground().getBlue() + darkeningLevel < 0 ? 0 : getBackground().getBlue() + darkeningLevel
        );
        setBackground(color);
    }

    private void Exited() {
        int darkeningLevel = 32;
        Color color = new Color(
                getBackground().getRed() + darkeningLevel > 256 ? 255 : getBackground().getRed() + darkeningLevel,
                getBackground().getGreen() + darkeningLevel > 256 ? 255 : getBackground().getGreen() + darkeningLevel,
                getBackground().getBlue() + darkeningLevel > 256 ? 255 : getBackground().getBlue() + darkeningLevel
        );
        setBackground(color);
    }
}
