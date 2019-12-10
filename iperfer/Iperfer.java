import java.io.IOException;
import java.net.*;

public class Iperfer
{
    //client mode only variables
    static String hostname;
    static int time;

    //server port or listen port depending on mode
    static int port;

    //finals
    final static int MAX_PORT = 65535;
    final static int MIN_PORT = 1024;

    public static void main(String[] args)
    {
        if(args.length == 7 && args[0].equals("-c")){
           clientMode(args);
        } else if(args.length == 3 && args[0].equals("-s")){
            serverMode(args);
        } else {
            invalidArgsError();
        }
        System.exit(0);
    }

    private static void clientMode(String args[]){
        clientModeCommandLineParser(args);
        runClient();
    }

    private static void serverMode(String args[]){
        serverModeCommandLineParser(args);
        runServer();
    }
    private static void runClient()
    {
        double sent = 0;
        byte[] packet = new byte[1000];
        long startTime = 0;
        long endTime = 0;

        Socket client = new Socket();
        InetSocketAddress host = new InetSocketAddress(hostname, port);

        try {
            client.connect(host);
            startTime = System.currentTimeMillis();
            endTime = startTime + (time * 1000);

            while (System.currentTimeMillis() < endTime)
            {
                client.getOutputStream().write(packet);
                sent++;
            }
            client.close();
        } catch (IOException e) {
            System.out.println("Error IO Exception");
            System.exit(4);
        }

        printResults(startTime, endTime, sent, 'c');
    }

    private static void runServer()
    {
        double received = 0;
        long startTime = 0;
        long endTime = 0;
        byte[] packet = new byte[1000];
        double num = 0;

        try {
            ServerSocket server = new ServerSocket();
            InetSocketAddress host = new InetSocketAddress(port);
            server.bind(host);
            Socket client = server.accept();
            startTime = System.currentTimeMillis();
            while(num > -1)
            {
                num = client.getInputStream().read(packet, 0, 1000);
                received += num/1000;
            }
            endTime = System.currentTimeMillis();

            client.close();
            server.close();
        } catch (IOException e) {
            System.out.println("Error IO Exception");
            System.exit(4);
        }

        printResults(startTime, endTime, received, 's');
    }

    private static void clientModeCommandLineParser(String args[])
    {
        if (args[1].equals("-h")) {
            hostname = args[2];
        } else {
            invalidArgsError();
        }
        if(args[3].equals("-p")){
            try {
                port = Integer.parseInt(args[4]);
                if (port > MAX_PORT || port < MIN_PORT) {
                    portNumberError();
                }
            } catch (NumberFormatException e) {
                invalidArgsError();
            }
        } else {
            invalidArgsError();
        }
        if(args[5].equals("-t")) {
            try {
                time = Integer.parseInt(args[6]);
            } catch (NumberFormatException e) {
                invalidArgsError();
            }
        } else {
            invalidArgsError();
        }
    }

    private static void serverModeCommandLineParser(String args[])
    {
        if(args[1].equals("-p")){
            try {
                port = Integer.parseInt(args[2]);
                if (port > MAX_PORT || port < MIN_PORT) {
                    portNumberError();
                }
            } catch (NumberFormatException e) {
                //in case parse int gets mad
                invalidArgsError();
            }
        } else {
            //throw -p not right error
            invalidArgsError();
        }
    }

    private static void printResults(long startTime, long endTime, double packets, char mode){
      double actualTime = (double)(endTime - startTime) / 1000.0;
      double speed = (8.0*packets/1000.0)/actualTime;
      String speedStr = String.format("%.3f", speed);
      String modeMessage = "sent=";
      if(mode == 's'){
        modeMessage = "received=";
      }
      System.out.println(modeMessage + (int)(packets + 0.01) + " KB rate=" + speedStr + " Mbps");
    }

    /*
     * Occurs when user does not use exact formatting specified in program description.
     * Client: java Iperfer -c -h <server hostname> -p <server port> -t <time>
     * Server: java Iperfer -s -p <listen port>
     */
    private static void invalidArgsError(){
        System.out.println("Error: invalid arguments");
        System.exit(1);
    }

    /*
     * Occurs when port number specified is not in range 1024 <= port <= 65535
     */
    private static void portNumberError(){
        System.out.println("Error: port number must be in the range 1024 to 65535");
        System.exit(1);
    }

}
