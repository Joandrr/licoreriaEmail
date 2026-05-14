package SMTP;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import Backend.Usuarios.GeneralUsuarioSQLUtils;
import Backend.Utils.GeneralMethods.GeneralMethods;
import Utils.SocketUtils;
import Utils.TecnoUtils;

public class SMTPClient {
    private String server;
    private String receptorUser;
    private String  emisorUser;
    private int port;

    //para usarlo en el parcial
    public SMTPClient(String server, String emisorUser,String receptorUser){
        this.server = server;
        this.receptorUser = receptorUser;
        this.emisorUser = emisorUser;
        this.port = SocketUtils.SMTP_PORT;
    }
    public SMTPClient(){
        this.server = SocketUtils.MAIL_SERVER;
        this.receptorUser = "joandanielrr@gmail.com";
        this.emisorUser = "grupo05sc@tecnoweb.org.bo";
        this.port = SocketUtils.SMTP_PORT;
    }
    public SMTPClient(String emisorUser,
                      String receptorUser){
            this.server = SocketUtils.MAIL_SERVER;
            this.receptorUser = receptorUser;
            this.emisorUser = emisorUser;
            this.port = SocketUtils.SMTP_PORT;
    }

    public void executeSMTPClient() {
        try{
            Socket socket = new Socket(this.getServer(),this.getPort());
            String command = "";
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            if ( SocketUtils.esEntradaValida(socket,input,output) ) {
                System.out.println("Mensaje del servidor: " + input.readLine());
                command = "HELO " + this.getServer() + " \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a HELO: " + input.readLine());

                command = "MAIL FROM: " + this.getEmisorUser() + "\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a Mail From: " + input.readLine());

                command = "RCPT TO: " + this.getReceptorUser() + "\r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a RCPT FROM: " + input.readLine());

                command = "DATA \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta servidor a DATA: " + input.readLine());

                command = "Subject: DEMO VIA SOCKET'S\r\n";
                command += "\r\n";
                command += "Hola como estas\r\n";
                command += "bien... gracias.\r\n";
                command += ".\r\n";


                System.out.println("comando: "+ command);
                output.writeBytes( command );
                System.out.println("Respuesta servidor a DATA: " + input.readLine());

                command = "QUIT \r\n";
                System.out.println("Comando: " + command);
                output.writeBytes(command);
                System.out.println("Respuesta Servidor: " + input.readLine());
            }
            SocketUtils.closeServices(socket,input,output);
        }catch(Exception e){
            System.out.println("throw - "+ e.getMessage());
        }
    }
    //execute for HTML MEssage

    public void executeSMTPClientHTML() {
        try {
            Socket socket = new Socket(this.getServer(), this.getPort());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());

            if (SocketUtils.esEntradaValida(socket, input, output)) {
                System.out.println("Mensaje del servidor: " + input.readLine());

                // 🔹 HELO
                output.writeBytes("HELO " + this.getServer() + "\r\n");
                System.out.println("Respuesta servidor a HELO: " + input.readLine());

                // 🔹 MAIL FROM
                output.writeBytes("MAIL FROM: " + this.getEmisorUser() + "\r\n");
                System.out.println("Respuesta servidor a MAIL FROM: " + input.readLine());

                // 🔹 RCPT TO
                output.writeBytes("RCPT TO: " + this.getReceptorUser() + "\r\n");
                System.out.println("Respuesta servidor a RCPT TO: " + input.readLine());

                // 🔹 DATA
                output.writeBytes("DATA\r\n");
                System.out.println("Respuesta servidor a DATA: " + input.readLine());

                // 🔹 Encabezados MIME + cuerpo HTML
                String htmlMessage =
                        "MIME-Version: 1.0\r\n" +
                                "Content-Type: text/html; charset=UTF-8\r\n" +
                                "Subject: 📨 Correo HTML desde Java\r\n" +
                                "\r\n" +
                                "<html>\r\n" +
                                "<body style=\"font-family: Arial, sans-serif;\">\r\n" +
                                "  <h2 style=\"color:#4CAF50;\">✅ Usuario creado exitosamente</h2>\r\n" +
                                "  <p><b>Nombre:</b> Evans Balcázar</p>\r\n" +
                                "  <p><b>Email:</b> evans@gmail.com</p>\r\n" +
                                "  <p><b>Teléfono:</b> 76773834</p>\r\n" +
                                "  <p><b>Rol:</b> empleado</p>\r\n" +
                                "  <hr>\r\n" +
                                "  <p>Bienvenido al sistema 🎉</p>\r\n" +
                                "</body>\r\n" +
                                "</html>\r\n" +
                                ".\r\n";

                output.writeBytes(htmlMessage);
                System.out.println("Respuesta servidor a HTML: " + input.readLine());

                // 🔹 QUIT
                output.writeBytes("QUIT\r\n");
                System.out.println("Respuesta servidor a QUIT: " + input.readLine());
            }

            SocketUtils.closeServices(socket, input, output);
        } catch (Exception e) {
            System.out.println("throw - " + e.getMessage());
        }
    }

    public void executeMailFrom(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "MAIL FROM:<" + this.getEmisorUser() + ">\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a Mail From: " + input.readLine());
    }
    public void executeHelo(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "HELO " + this.getServer() + "\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a HELO: " + input.readLine());
    }
    public void executeReceivedTo(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "RCPT TO:<" + this.getReceptorUser() + ">\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        String response = input.readLine();
        System.out.println("Respuesta servidor a RCPT TO: " + response);
        if (response == null || !response.startsWith("250")) {
            throw new IOException("RCPT TO rechazado: " + response);
        }
    }
    public void executeData(BufferedReader input,DataOutputStream output) throws IOException {
        String command = "DATA \r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        String response = input.readLine();
        System.out.println("Respuesta servidor a DATA: " + response);
        if (response == null || !response.startsWith("354")) {
            throw new IOException("DATA rechazado: " + response);
        }
    }
    public void executeOnlySubject(String subject,BufferedReader input,DataOutputStream output) throws IOException {
        String command = "SUBJECT: " + subject + "\r\n";
        command += ".\r\n";
        output.writeBytes(command);
        System.out.println("Respuesta servidor a Data Subject: " + input.readLine());
    }
    public void executeDataSubject(String subject,String context,BufferedReader input,DataOutputStream output) throws IOException {
        StringBuilder command = new StringBuilder();
        command.append("From: <").append(this.getEmisorUser()).append(">\r\n");
        command.append("To: <").append(this.getReceptorUser()).append(">\r\n");
        command.append("Subject: ").append(subject).append("\r\n");
        command.append("\r\n");
        if (context != null) {
            command.append(context).append("\r\n");
        }
        command.append("\r\n.\r\n");
        System.out.println("comando: " + command);
        output.writeBytes(command.toString());
        System.out.println("Respuesta servidor a DATA: " + input.readLine());
    }

    public void executeQuitCommand(BufferedReader input, DataOutputStream output) throws IOException{
        String command = "QUIT\r\n";
        System.out.println("Comando: " + command);
        output.writeBytes(command);
        System.out.println("Respuesta servidor a QUIT: " + input.readLine());
    }
    //antes de usar el metodo requiero instanciar el emisor y receptor
    public void sendDataToServer(String subject,String context){
        Socket socket = null;
        DataOutputStream output = null;
        BufferedReader input = null;
        try{
            socket = new Socket(this.getServer(),this.getPort());
            output = new DataOutputStream(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            if (SocketUtils.esEntradaValida(socket,input,output)) {
                // Leer saludo inicial del servidor (220 ...)
                String greeting = input.readLine();
                System.out.println("Mensaje del servidor: " + greeting);
                this.executeHelo(input,output);
                this.executeMailFrom(input,output);
                this.executeReceivedTo(input,output);
                this.executeData(input,output);
                this.executeDataSubject(subject,context,input,output);
                this.executeQuitCommand(input,output);
            }
        } catch (Exception e) {
            System.out.println("throw - " + e.getMessage());
            try {
                if (input != null && output != null) {
                    this.executeQuitCommand(input, output);
                }
            } catch (Exception ignore) {
            }
        } finally {
            try {
                if (socket != null && input != null && output != null) {
                    SocketUtils.closeServices(socket, input, output);
                }
            } catch (Exception ignore) {
            }
        }
    }








    public static void main(String[] args) {
        // Modo CLI:
        //   java -cp out SMTP.SMTPClient <fromEmail> <toEmail> <subject> [context]
        if (args != null && args.length >= 3) {
            String emisor = args[0];
            String receptor = args[1];
            String subject = args[2];
            String context = (args.length >= 4) ? args[3] : null;
            String server = SocketUtils.MAIL_SERVER;

            TecnoUtils.validarCorreosDeUsuario(emisor, receptor);
            SMTPClient smtpClient = new SMTPClient(server, emisor, receptor);
            smtpClient.sendDataToServer(subject, context);
            return;
        }

        // Demo por defecto (si no pasas args)
        String receptor = "grupo05sc@tecnoweb.org.bo";
        String emisor = "grupo05sc@tecnoweb.org.bo";
        String subject = """
            listarUsuarios["*"]
            """;
        subject = GeneralMethods.parsearSubjectComillaTriple(subject);
        String context = null;
        String server = SocketUtils.MAIL_SERVER;
        TecnoUtils.validarCorreosDeUsuario(emisor, receptor);
        SMTPClient smtpClient = new SMTPClient(server, emisor, receptor);
        smtpClient.sendDataToServer(subject, context);
    }








    public static void executeTask() {
       SMTPClient smtpClient = new SMTPClient();
       smtpClient.executeSMTPClient();
    }


    // metodos que toda clase de JAVA tiene
    @Override
    public String toString() {
        return "SMTP.SMTPClient{" +
                "server='" + server + '\'' +
                ", receptorUser='" + receptorUser + '\'' +
                ", emisorUser='" + emisorUser + '\'' +
                ", port=" + port +
                '}';
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getReceptorUser() {
        return receptorUser;
    }

    public void setReceptorUser(String receptorUser) {
        this.receptorUser = receptorUser;
    }

    public String getEmisorUser() {
        return emisorUser;
    }

    public void setEmisorUser(String emisorUser) {
        this.emisorUser = emisorUser;
    }

    public int getPort(){
        return this.port;
    }

}
