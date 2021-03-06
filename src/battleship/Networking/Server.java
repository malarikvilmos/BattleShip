package battleship.Networking;

import battleship.DataPackage.Data;
import battleship.DataPackage.DataConverter;
import battleship.Logic.GameLogic;
import java.net.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private ServerSocket sSocket = null;
    private GameLogic gameLogic;

    private int clientID = 0;
    private List<String>[] queueArray;
    private boolean close = false;

    public void addMessageToQueue(String message, int ID) {
        if (ID != -1) {
            queueArray[ID].add(message);
        }
    }

    public void close(){
        close = true;
        try {
            sSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Server(int port) {
        try {
            queueArray = new Vector[2];
            for (int i = 0; i < 2; ++i) {
                queueArray[i] = new Vector<>();
            }
            sSocket = new ServerSocket(port);
            gameLogic = new GameLogic();

            Thread threadQueuePoll = new Thread(() -> {
                while (!close) {
                    try {
                        Thread.sleep(10);
                        while (!gameLogic.messageQueue.isEmpty()) {
                            String BroadcastMessage = gameLogic.messageQueue.get(0);
                            gameLogic.messageQueue.remove(0);
                            if (BroadcastMessage != null) {
                                Data decoded = DataConverter.decode(BroadcastMessage);
                                int recipient = decoded.getRecipientID();
                                addMessageToQueue(BroadcastMessage, recipient);
                            }
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            threadQueuePoll.start();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void ServeClient() {
        Thread thread = new Thread(() -> {
            while (!close) {
                try {
                    Socket socket = sSocket.accept();
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    
                    if (bfr.readLine().equals("PING")){
                        socket.close();
                        continue;
                    }
                    
                    Integer ID = clientID++;

                    System.out.println("Someone joined the server with ID: " + ID);
                    int otherQueueID = (ID == 0) ? 1 : 0;
                    int ownQueueID = (ID == 0) ? 0 : 1;
                    addMessageToQueue(ID + "$ConnectionData$$" + ((ID.equals(0)) ? 1 : 0), otherQueueID);

                    bfw.write(ID.toString());
                    bfw.newLine();
                    bfw.flush();

                    Thread threadReader = new Thread(() -> {
                        while (!close) {
                            try {
                                String inMsg = bfr.readLine();
                                if (inMsg != null) {
                                    if (inMsg.equals("$DisconnectData$$-1")){
                                        int recipient = (ID == 0) ? 1 : 0;
                                        inMsg = "-1$ChatData$The other player has left the game.$" + recipient;
                                    }
                                    gameLogic.processMessage(DataConverter.decode(inMsg));
                                }
                            } catch (IOException ex) {
                                System.out.println(ex.getMessage());
                                break;
                            }
                        }

                    });
                    threadReader.start();

                    while (!close) {
                        Thread.sleep(10);
                        while (!queueArray[ownQueueID].isEmpty()) {
                            String message = queueArray[ownQueueID].get(0);
                            queueArray[ownQueueID].remove(0);
                            bfw.write(message);
                            bfw.newLine();
                            bfw.flush();
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("Player disconnected");
                    --clientID;
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    public static String getLocalIP() {
        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    if (i.getHostAddress().split("\\.")[0].equals("192")) {
                        return i.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "NO LOCAL IP FOUND";
    }
    
    public static boolean isServerAvailable(String ip, int port){
        boolean isAvailable;
        
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 1000);
            isAvailable = true;
            BufferedWriter bfw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            bfw.write("PING");
            bfw.newLine();
            bfw.flush();
            socket.close();
        } 
        catch (IOException ex) {
            isAvailable = false;
        }
        
        return isAvailable;
    }

    @Override
    public void run() {
        ServeClient();
        ServeClient();
        ServeClient();

        System.out.println(getLocalIP());
    }
}
