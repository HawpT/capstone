using Assets._scripts;
using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class InputController : MonoBehaviour {

    public Text output;
    private bool IsConnected;
    private Client client;
    private bool holdForDisconnect;
    private InputField consoleInput;
    private InputField.SubmitEvent se;
    private List<string> onlineUsers;

    // Use this for initialization
    void Start() {
        client = new Client();
        onlineUsers = new List<string>();
        consoleInput = GameObject.Find("InputField").GetComponent<InputField>();
        IsConnected = false;

        se = new InputField.SubmitEvent();
        consoleInput.onEndEdit = se;
        se.AddListener(NewInput);
    }

    // Update is called once per frame
    void Update() {
        if (IsConnected)
        {
            while (Client.messages.Count > 0)
            {
                WriteOnScreen(Client.messages.Dequeue());
            }
        }

        if (Input.GetKeyUp("enter"))
        {
            consoleInput.ActivateInputField();
        }

        if (output.text.Length > 2000)
            output.text = output.text.Substring(1500);
    }

    void OnApplicationQuit()
    {
        Client.SendCommand(7 + " ");
        Client.master.Disconnect(true);
    }

    void NewInput(string input)
    {
        //tokenize our input string
        string[] tokens = input.Split();
        //empty the input bar
        consoleInput.text = "";

        //PARSE INPUT
        switch (tokens[0])
        {
            //connect to the server
            case "connect":
                if (tokens.Length == 2)
                {
                    string response = Client.StartClient("127.0.0.1", "8000", tokens[1]);
                    output.text += "\n>>" + "Attempting to connect @default 127.0.0.1:8000";
                    output.text += "\n>>" + response;
                    IsConnected = true;
                }
                else if (tokens.Length == 3)
                {
                    string[] temp = tokens[1].Split(':');
                    if (temp[1].Length > 0 && temp[0].Length > 0)
                    {
                        string response = Client.StartClient(temp[0], temp[1], tokens[2]);
                        output.text += "\n>>" + "Attempting to connect @" + temp[0] + ":" + temp[1];
                        output.text += "\n>>" + response;
                        IsConnected = true;
                    }
                    else
                    {
                        output.text += "\n>>" + "Invalid input. Usage: connect <IPaddress>:<port> <username>";
                    }
                }
                else
                {
                    output.text += "\n>>" + "Invalid input. Usage: connect <IPaddress>:<port> <username>";
                }
                break;
            
            //send a private message
            case "/w":
                if (IsConnected)
                {
                    if (tokens.Length >= 3)
                    {
                        if (onlineUsers.Contains(tokens[1])) {
                            string temp = "";
                            for(int i = 2; i < tokens.Length; i++)
                            {
                                temp += tokens[i] + " ";
                            }
                            Client.SendCommand("4 " + tokens[1] + " " + temp);
                        }
                        else
                        {
                            output.text += "\n>>" + "No user with that name exists.";
                        }
                    }
                    else
                    {
                        output.text += "\n>>" + "Invalid input. Usage: /w <username> <message>";
                    }
                }
                else
                {
                    output.text += "\n>>" + "Please connect before sending messages.";
                }
                break;

            case "disconnect":
                if (IsConnected)
                {
                    Client.SendCommand("7");
                    holdForDisconnect = true;
                }
                else
                {
                    output.text += "\n>>" + "Please connect before attempting a disconnect.";
                }
                break;

            default:
                //DEFAULT send general message to server
                if (IsConnected)
                {
                    Client.SendCommand("3 " + input);
                    output.text += "\nYou >>" + input;
                }
                //SPECIAL CASE if our username is DENIED, then send a new one, and loop until we can get one
                else if (tokens.Length == 1)
                {
                    Client.SendCommand("0 " + tokens[0]);
                }
                else
                {
                    output.text += "\n>>" + "Please connect before sending messages.";
                }
                break;
        }
    }

    public void WriteOnScreen(string message)
    {
        string[] tokens = message.Split();
        int code = int.Parse(tokens[0]);
        string temp = "";
        string[] time;

        switch (code)
        {
            //Server has accepted our username
            case 1:
                IsConnected = true;
                output.text += "\n >>" + "Successfully connected to server.";
                
                //create a list of users
                string[] users = tokens[1].Split(',');
                foreach(string name in users)
                {
                    onlineUsers.Add(name);
                }
                temp = " Server Welcome: ";
                
                //print the rest of the message
                for (int i = 2; i < tokens.Length; i++)
                {
                    temp += tokens[i] + " ";
                }
                output.text += "\n " + temp;
                break;

            //Server denied our user name request
            case 2:
                output.text += "\n >>" + "Username is taken. Enter a new username";
                break;
            
            //Server sent a general message
            case 5:
                //grab time stamp YYYY:MM:DD:HH:MM:SS
                time = tokens[2].Split(':');
                temp = "";
                for (int i = 3; i < tokens.Length; i++)
                {
                    temp += tokens[i] + " ";
                }
                output.text += "\n (" + time[3] + ":" + time[4] + ":" + time[5] + ") " + tokens[1] + ": " + temp;
                break;
            
            //Server sent a private message
            case 6:
                //grab time stamp YYYY:MM:DD:HH:MM:SS
                time = tokens[2].Split(':');
                temp = "";
                for (int i = 3; i < tokens.Length; i++)
                {
                    temp += tokens[i] + " ";
                }
                output.text += "\n (" + time[3] + ":" + time[4] + ":" + time[5] + " (Private)" + tokens[1] + ": " + temp;
                break;

            //Server says goodbye to client
            case 8:
                IsConnected = false;
                Client.Disconnect();
                output.text += "\n " + "Server said goodbye! Disconnecting.";
                break;

            //Server says a user has disconnected
            case 9:
                onlineUsers.Remove(tokens[1]);
                output.text += "\n >>" + tokens[1] + " has disconnected from server.";
                break;

            //Server says a user has connected
            case 10:
                onlineUsers.Add(tokens[1]);
                output.text += "\n >>" + tokens[1] + " has connected to server.";
                break;
        }
    }
}
