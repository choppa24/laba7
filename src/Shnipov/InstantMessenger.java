package Shnipov;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;

public class InstantMessenger {
    private MainFrame frame;

    public InstantMessenger(final MainFrame f) {
        this.frame = f;
        startServer();
    }

    public void sendMessage() {
        try {
            final String senderName = frame.getTextFieldFrom().getText();
            final String destinationAddress = frame.getTextFieldTo().getText();
            final String message = frame.getTextAreaOutgoing().getText();

            if (senderName.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Введите имя отправителя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (destinationAddress.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Введите адрес узла-получателя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Введите текст сообщения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(CorrectIpAdress(destinationAddress)) {
                frame.getTextFieldTo().requestFocus();
                JOptionPane.showMessageDialog(frame,
                        "Введите корректный IP адрес", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final Socket socket = new Socket(destinationAddress,frame.getServerPort());
            final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(senderName);
            out.writeUTF(message);

            socket.close();

            frame.getTextAreaIncoming().append("Получатель (" + destinationAddress + "): " + message + "\n");

            frame.getTextAreaOutgoing().setText("");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Не удалось отправить сообщение: узел-адресат не найден", "Ошибка", JOptionPane.ERROR_MESSAGE);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                    "Не удалось отправить сообщение", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void startServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int port = findFreePort();
                    frame.setServerPort(port);
                    final ServerSocket serverSocket = new ServerSocket(frame.getServerPort());

                    while (!Thread.interrupted()) {
                        final Socket socket = serverSocket.accept();
                        final DataInputStream in = new DataInputStream(socket.getInputStream());

                        final String senderName = in.readUTF();

                        final String message = in.readUTF();

                        socket.close();

                        final String address =
                                ((InetSocketAddress) socket
                                        .getRemoteSocketAddress())
                                        .getAddress()
                                        .getHostAddress();

                        frame.getTextAreaIncoming().append(senderName + " (" + address + "): " + message + "\n");

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(frame,
                            "Ошибка в работе сервера", "Ошибка", JOptionPane.ERROR_MESSAGE);

                }
            }
        }).start();
    }
    public boolean CorrectIpAdress(String ip){
        int counter = 0;
        for (String check: ip.split("\\."))
        {
            counter++;
            for  (int i = 0; i < check.length(); i++){
                int k = (int)check.charAt(i);
                if (k < 48 || k> 57)
                    return true;
            }
            int value = Integer.parseInt(check);
            if (value < 0 || value > 255)
                return  true;
        }
        if (counter != 4)
            return true;
        return false;

    }
    public Integer findFreePort() throws IOException {
        ServerSocket server =new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();
        return port;
    }
}


