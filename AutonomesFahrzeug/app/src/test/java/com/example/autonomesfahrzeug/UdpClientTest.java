package com.example.autonomesfahrzeug;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UdpClientTest {

  UdpClient udpClient;
  int appPort;
  int boardPort;
  String ip;

  @Before
  public void setUp() throws Exception {
    appPort = 10000;
    boardPort = 10001;
    ip = "192.168.4.1";
    udpClient = new UdpClient(appPort, boardPort, ip);
  }

  @Test
  public void close() {
    udpClient.close();
    assertEquals(null, udpClient.getUdpSocket());
  }

  @Test
  public void open() {
    udpClient.close();
    assertEquals(null, udpClient.getUdpSocket());
    udpClient.open();
    assertNotEquals(null, udpClient.getUdpSocket());
  }
}