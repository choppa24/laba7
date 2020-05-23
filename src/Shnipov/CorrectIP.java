package Shnipov;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class CorrectIP {
    public static boolean isCorrectIpAdress(String ip) {
        try {
            return !Inet4Address.getByName(ip).getHostAddress().equals(ip);
        } catch (UnknownHostException ex) {
            return true;
        }
    }
}
   // В том случае, если вас интересует удаленный узел сети, вы можете создать для него объект класса InetAddress с помощью методов getByName
   // getHostAddress() Возвращает строку IP-адреса в текстовом представлении.
   //  UnknownHostException чтобы указать, что IP-адрес хоста не может быть определен.