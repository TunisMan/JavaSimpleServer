import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;



public class SimpleWebServer


{

    // Port numarasını belirleyin
    private static final int PORT = 80;


    // Kök dizini belirtin, src klasörü bu dizin olabilir
    private static final String ROOT_DIR = "src";


    public static void main(String[] args) throws Exception
    {


        // HttpServer oluştur
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        System.out.println("Sunucu başlatılıyor... Port: " + PORT);



        // RootHandler ile tüm istekleri işle
        server.createContext("/", new RootHandler());
        server.setExecutor(null); // Varsayılan bir executor kullan
        server.start();
        System.out.println("Sunucu çalışıyor!");


    }



    // RootHandler sınıfı gelen istekleri işler
    static class RootHandler implements HttpHandler
    {

        @Override

        public void handle(HttpExchange exchange) throws IOException {
            String filePath = ROOT_DIR + exchange.getRequestURI().getPath();
            File file = new File(filePath);



            // Eğer dosya yoksa 404 döndür
            if (!file.exists())
            {

                String response = "404 (Geçerli Dosyaya erişim sağlanamadı)\n";
                exchange.sendResponseHeaders(404, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;

            }

            // Dosya varsa, MIME türünü belirleyip dosyayı döndür

            String mimeType = Files.probeContentType(file.toPath());
            exchange.sendResponseHeaders(200, file.length());
            OutputStream os = exchange.getResponseBody();
            FileInputStream fs = new FileInputStream(file);

            final byte[] buffer = new byte[0x10000];
            int count = 0;
            while ((count = fs.read(buffer)) >= 0) {
                os.write(buffer, 0, count);
            }
            os.close();
            fs.close();
        }
    }
}