package com.example.autonomesfahrzeug;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Niklas Röske
 */
public class UdpClient {

  private DatagramSocket udpSocket;
  private InetAddress serverAddr;
  private int boardPort;
  private int appPort;
  private Logger logger;
  private int counter;

  /**
   * Constructor for a UdpClient
   * @param appPort local socket which the client should use
   * @param boardPort socket off the host which should get the message
   * @param ip host ip address
   */
  public UdpClient(int appPort, int boardPort, String ip) {
    try {
      this.appPort = appPort;
      this.udpSocket = new DatagramSocket(appPort);
      serverAddr = InetAddress.getByName(ip);
      //udpSocket.setBroadcast(true);
      this.boardPort = boardPort;
    } catch (SocketException | UnknownHostException e) {
      e.printStackTrace();
    }
  }

  public void setLogger(Logger logger) {
    this.logger = logger;
    counter = 1;
  }

  /**
   * Checks the connection to the host
   * @return boolean. status of the connection
   */
  public boolean checkConnection(){
    byte[] buf = new byte[1000];
    DatagramPacket dp = new DatagramPacket(buf, buf.length);
    try {
      udpSocket.setSoTimeout(100);   // set the timeout in millisecounds.
      try {
        udpSocket.receive(dp);
        long t4 = System.nanoTime();
        if (ActivityManualMode.sessionRunning) {
          long t1 = -1;
          long t2 = -1;
          long t3 = -1;
          if (logger != null) {
            String s = new String(buf, 0, dp.getLength());
            JSONObject jsonObject = JsonFile.stringToJSONObject(s);
            if (jsonObject.has("t1")) {
              t1 = jsonObject.getLong("t1");
            }
            if (jsonObject.has("t2")) {
              t2 = jsonObject.getLong("t2");
            }
            if (jsonObject.has("t3")) {
              t3 = jsonObject.getLong("t3");
            }
            System.out.println("t1: " +  t1 + "t2: " + t2 + "t3: " + t3 + "t4: " + t4);
            logger.addLog(new String[] {"" + counter, "" + t1, "" + t2, "" + t3, "" + t4});
            counter++;
          }
        }

        //System.out.println(s);
      } catch (SocketTimeoutException e) {
        return false;
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      } catch (JSONException e) {
        e.printStackTrace();
      }
    } catch (SocketException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Sends a String of a json object to the boardPort socket on the host side.
   * @param json JSONObject
   */
  public void send(JSONObject json)  {
    byte[] buf = json.toString().getBytes();
    DatagramPacket packet = new DatagramPacket(buf, buf.length, serverAddr, boardPort);
    try {
      udpSocket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      e.printStackTrace();
    }
  }

  /**
   * Closes the local UDP Socket.
   */
  public void close() {
    if (udpSocket != null) {
      udpSocket.close();
      udpSocket = null;
    }
  }

  /**
   * Opens the local Udp Socket
   */
  public void open() {
    if (udpSocket == null) {
      try {
        this.udpSocket = new DatagramSocket(appPort);
        udpSocket.setBroadcast(true);
      } catch (SocketException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   *
   * @return local udp Socket
   */
  public DatagramSocket getUdpSocket() {
    return udpSocket;
  }
}
