using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace ChatServer
{
    class ChatServer
    {
        public static Socket listenerSocket;
        public static Dictionary<Socket, string> _clients;
        public static List<string> usernames;
        public static string welcomeMessage;

        //The main function
        public static void Main(String[] args)
        {
            //Networking stuff
            listenerSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            listenerSocket.SetSocketOption(SocketOptionLevel.Socket, SocketOptionName.ReuseAddress, true);
            _clients = new Dictionary<Socket, string>();
            usernames = new List<string>();

            //WELCOME MESSAGE
            welcomeMessage = "Connected to Kevchat!";

            string[] temp;
            
            IPEndPoint ip;
            if (args.Length == 0)
            {
                Console.WriteLine("Please input a server address.");
                string input = Console.ReadLine();
                temp = input.Split(':');

                ip = new IPEndPoint(IPAddress.Parse(temp[0]), int.Parse(temp[1]));
                Console.WriteLine("Attempting to host @" + temp[0] + ":" + temp[1]);
            }
            else
            {
                temp = args[0].Split(':');
                ip = new IPEndPoint(IPAddress.Parse(temp[0]), int.Parse(temp[1]));
                Console.WriteLine("Attempting to host @" + temp[0] + ":" + temp[1]);
            }

            try
            {
                listenerSocket.Bind(ip);

                Thread listenThread = new Thread(ListenThread);

                listenThread.Start();
                Console.WriteLine("Server live!");
            }
            catch (Exception e)
            {
                Console.WriteLine("Hosting Failed. " + e.ToString());
            }

            return;
        }

        static void ListenThread()
        {
            while (true)
            {
                listenerSocket.Listen(0);

                ClientData temp = new ClientData(listenerSocket.Accept());
                Console.WriteLine("New socket opened. Awaititng packets.");
            }
        }

        //disconnect from all active connections
        public static void DisconnectAll()
        {
            List<Socket> temp = new List<Socket>(_clients.Keys);
            foreach (Socket i in temp)
            {
                i.Close();
            }
            listenerSocket.Disconnect(true);
        }

        //receiving data from the clients
        public static void Data_IN(object cSocket)
        {
            Socket clientSocket = (Socket)cSocket;

            byte[] Buffer;
            int readBytes;

            //loop the thread while the connection is open
            while (clientSocket.Connected)
            {
                try
                {
                    Buffer = new byte[2048];

                    readBytes = clientSocket.Receive(Buffer);

                    if (readBytes > 0)
                    {
                        DataManager(Buffer, clientSocket);
                    }

                }
                catch (Exception ex)
                {
                    ex.ToString();
                }
            }
        }

        //handles the types of packets coming in and out
        public static void DataManager(byte[] bytes, Socket sock)
        {
            try
            {
                string newMessage = System.Text.Encoding.Default.GetString(bytes);
                newMessage = newMessage.Split('\0')[0];
                string[] tokens = newMessage.Split();
                int code = int.Parse(tokens[0]);
                string outgoing = "";
                DateTime currentTime = DateTime.Now;
                string timestamp = currentTime.Year + ":" + currentTime.Month + ":" + currentTime.Day + ":" + currentTime.Hour + ":" + currentTime.Minute + ":" + currentTime.Second;
                List <Socket> temp;
                byte[] bytesOut;
            
                Console.WriteLine("Incoming Command: " + newMessage);
                //respond to the packet
                switch (code)
                {

                    //Client Requests a username
                    case 0:
                        if (usernames.Contains(tokens[1].ToLower()))
                        {
                            outgoing = "2 ";
                        }
                        else
                        {
                            //Code 10. Server tells client that user has connected
                            temp = new List<Socket>(_clients.Keys);
                            bytesOut = Encoding.UTF8.GetBytes(10 + " " + tokens[1].ToLower());
                            foreach (Socket i in temp)
                            {
                                if (i != sock)
                                {
                                    i.Send(bytesOut);
                                }
                            }


                            _clients[sock] = tokens[1].ToLower();
                            usernames.Add(tokens[1].ToLower());
                            outgoing = "1 ";
                            foreach (string name in usernames)
                            {
                                outgoing += name + ",";
                            }
                            outgoing += " " + welcomeMessage;
                            Console.WriteLine("New user registered. Username: " + tokens[1]);

                            bytesOut = Encoding.UTF8.GetBytes(outgoing);
                            sock.Send(bytesOut);
                        }
                        break;

                    //Client sends general message
                    case 3:
                        if (newMessage.Length > 2)
                        {
                            
                            outgoing += "5 " + _clients[sock] + " " + timestamp + newMessage.Substring(1);
                            bytesOut = Encoding.UTF8.GetBytes(outgoing);
                            temp = new List<Socket>(_clients.Keys);

                            foreach (Socket i in temp)
                            {
                                if (i != sock)
                                {
                                    i.Send(bytesOut);
                                }
                            }
                        }
                        else
                        {
                            Console.WriteLine("Message received was empty.");
                        }
                        break;

                    //Client sends private message
                    case 4:
                        if (newMessage.Length > 2)
                        {
                            outgoing += "6 "  + _clients[sock]  + " " + timestamp + newMessage.Substring(1);
                            bytesOut = Encoding.UTF8.GetBytes(outgoing);
                            temp = new List<Socket>(_clients.Keys);
                            
                            foreach (Socket i in temp)
                            {
                                if (_clients[i] == tokens[1].ToLower())
                                {
                                    i.Send(bytesOut);
                                }
                            }
                        }
                        else
                        {
                            Console.WriteLine("Private Message Received was empty!");
                        }
                        break;

                    //Client sends disconnect
                    case 7:

                        outgoing += "8";
                        bytesOut = Encoding.UTF8.GetBytes(outgoing);
                        sock.Send(bytesOut);

                        outgoing += "9 " + _clients[sock];
                        bytesOut = Encoding.UTF8.GetBytes(outgoing);
                        temp = new List<Socket>(_clients.Keys);

                        foreach (Socket i in temp)
                        {
                            if (i != sock)
                            {
                                i.Send(bytesOut);
                            }
                            else
                            {
                                Console.WriteLine("Removing user " + _clients[sock] + ": " + usernames.Remove(_clients[sock]));
                                _clients.Remove(sock);
                                sock.Disconnect(true);
                            }
                        }
                        break;

                    default:
                        break;
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
            }

        }

    }

    //data structure for incoming connections
    class ClientData
    {
        public Socket clientSocket;
        public Thread clientThread;
        public string username;

        public ClientData()
        {
            clientThread = new Thread(ChatServer.Data_IN);
            clientThread.Start(clientSocket);
        }

        public ClientData(Socket clientSocket)
        {
            this.clientSocket = clientSocket;
            clientThread = new Thread(ChatServer.Data_IN);
            clientThread.Start(clientSocket);
        }
    }
}
