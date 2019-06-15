package Human;

import java.awt.*;
import java.io.ObjectInputStream;
import java.util.*;
import java.net.*;
import java.io.ObjectOutputStream;


public class MusicServer
{
    ArrayList<ObjectOutputStream> clientOutputStreams;

    public static void main(String[] args){
        new MusicServer().go();
    }

    public class ClientHandler implements Runnable{
        ObjectInputStream in;
        Socket clientSocket;

        public ClientHandler(Socket socket){
            try{
                clientSocket = socket;
                // поток для отправки на клиент объектов
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch(Exception ex) { ex.printStackTrace(); }
        }

        @Override
        public void run() {
            Object o2 = null;
            Object o1 = null;
            try{
                while((o1=in.readObject()) != null){
                    o2 = in.readObject();
                    System.out.println("Прочитано два объекта");
                    tellEveryone(o1 , o2);
                }
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    public void go(){
        clientOutputStreams = new ArrayList<ObjectOutputStream>();

        try{
            ServerSocket serverSock = new ServerSocket(4242);

            while(true){
                Socket clientSocket = serverSock.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                // Выходные потоки для записи во клиенты
                clientOutputStreams.add(out);

                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch(Exception ex) {ex.printStackTrace();}
    }
    public void tellEveryone(Object one, Object two)
    {
       Iterator it = clientOutputStreams.iterator();
       while(it.hasNext()){
           try{
               ObjectOutputStream out = (ObjectOutputStream) it.next();
               out.writeObject(one);
               out.writeObject(two);
           } catch(Exception ex) { ex.printStackTrace();}
       }
    }
}