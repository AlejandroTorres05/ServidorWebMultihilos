    import java.io.BufferedReader;
    import java.io.BufferedWriter;
    import java.io.File;
    import java.io.FileInputStream;
    import java.io.IOException;
    import java.io.InputStream;
    import java.io.InputStreamReader;
    import java.io.OutputStream;
    import java.io.OutputStreamWriter;
    import java.net.Socket;
    import java.nio.charset.StandardCharsets;
    import java.util.StringTokenizer;

    public class Handler implements Runnable{

        private final String PATH = "./resourses/";
        private Socket clienSocket;

        public Handler (Socket socket){
            this.clienSocket = socket;
        }


        @Override
        public void run() {
            
            try {

            BufferedWriter bufferedWriter = new BufferedWriter(
                new OutputStreamWriter(clienSocket.getOutputStream()));
            
            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(clienSocket.getInputStream()));

            String requestLine;
            StringTokenizer request;
            String documentName = "";
            int byteNum = 0;
            File document = null;
            FileInputStream inputStream = null;

            while ((requestLine = bufferedReader.readLine()) != null && !requestLine.isEmpty()) {
                    System.out.println(requestLine);
                    request = new StringTokenizer(requestLine);

                    if (request.nextToken().equals("GET")){
                        documentName = request.nextToken();
                        if (documentName.startsWith("/") == true) 
                            documentName = documentName.substring(1);
                        
                        System.out.println(documentName);
                        document = new File(PATH + documentName);
                        if (document.exists()) {
                            inputStream = new FileInputStream(document);
                            byteNum = (int) document.length();
                            byte[] fileInBytes = new byte[byteNum];
                            inputStream.read(fileInBytes);
                        }
                        break;
                    }
            }

            if (document != null && document.exists()){
                bufferedWriter.write("HTTP/1.0 200 OK\r\n");
                if (documentName.endsWith(".jpg"))
                    bufferedWriter.write("Content-Type: image/jpeg\r\n"); 
                if (documentName.endsWith(".gif")) 
                    bufferedWriter.write("Content-Type: image/gif\r\n");
                if (documentName.endsWith(".html"))
                    bufferedWriter.write("Content-Type: text/html\r\n");
                bufferedWriter.write("Content-Length: " + byteNum + "\r\n");
                bufferedWriter.write("\r\n");
                bufferedWriter.flush();
    
                inputStream.close();
                inputStream = new FileInputStream(document);

                enviarBytes(inputStream, clienSocket.getOutputStream());
            } else {
                document = new File(PATH + "404.html");
                inputStream = new FileInputStream(PATH + "404.html");
                byteNum = (int) document.length();
                
                bufferedWriter.write("HTTP/1.0 200 OK\r\n");
                bufferedWriter.write("Content-Type: text/html\r\n");
                bufferedWriter.write("Content-Length: " + byteNum + "\r\n");
                bufferedWriter.write("\r\n");
                bufferedWriter.flush();

                enviarBytes(inputStream, clienSocket.getOutputStream());
                inputStream.close();
            }

            
            bufferedWriter.flush();
            bufferedWriter.close();
            bufferedReader.close();

            clienSocket.close();
                
            } catch (IOException e) {
                System.out.println("Error IO: " + e.getMessage());
                e.printStackTrace();
                
            } catch (Exception e) {
                            e.printStackTrace();
                        }
            
        }
        

        private static void enviarString(String line, OutputStream os) throws Exception {
            os.write(line.getBytes(StandardCharsets.UTF_8));
        }

        private static void enviarBytes(InputStream fis, OutputStream os) throws Exception {
            byte[] buffer = new byte[1024];
            int bytes = 0;

            while ((bytes = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytes);
            }
        }
    }
