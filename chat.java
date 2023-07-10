import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
public class chat {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Escriba su nombre de usuario, java chat [usuario]");
            return;
        }
        
        String username = args[0];
        int port = 50000;
        InetAddress group;
        try {
            group = InetAddress.getByName("239.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        }
        
        try {
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(group);
            
            Thread receiveThread = new Thread(() -> {
                while (true) {
                    try {
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength(),StandardCharsets.ISO_8859_1);
                        System.out.println(message.trim());
                        System.out.print("Escribe tu mensaje: ");
                        System.out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            receiveThread.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in,StandardCharsets.ISO_8859_1));
            System.out.print("Escribe tu mensaje: ");
            System.out.flush();
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                String fullMessage = username + " --> " + line;
                byte[] buffer = fullMessage.getBytes(StandardCharsets.ISO_8859_1);
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, port);
                socket.send(packet);
                
                System.out.flush();
            }
            
            socket.leaveGroup(group);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
