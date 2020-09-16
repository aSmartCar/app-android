package com.example.autonomesfahrzeug;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.util.Properties;

public class SshManager {

  public static void downloadFile(String username, String password, String hostname, int port, String src, String des) throws SftpException{
    JSch jsch = new JSch();
    Session session = null;
    try {
      session = jsch.getSession(username, hostname, port);
    } catch (JSchException e) {
      e.printStackTrace();
    }
    session.setPassword(password);


    // Avoid asking for key confirmation
    Properties prop = new Properties();
    prop.put("StrictHostKeyChecking", "no");
    session.setConfig(prop);
    try {
      session.connect();
    } catch (JSchException e) {
      e.printStackTrace();
    }
    // SSH Channel
    ChannelSftp channelssh = null;
    try {
      channelssh = (ChannelSftp) session.openChannel("sftp");
    } catch (JSchException e) {
      e.printStackTrace();
    }


    try {
      channelssh.connect();
    } catch (JSchException e) {
      e.printStackTrace();
    }
    channelssh.get(src, des);
    channelssh.disconnect();
  }

  public static void mkdir(String username, String password, String hostname, int port, String directory) {
    JSch jsch = new JSch();

    Session session = null;
    try {
      session = jsch.getSession(username, hostname, port);
    } catch (JSchException e) {
      e.printStackTrace();
    }
    session.setPassword(password);


    // Avoid asking for key confirmation
    Properties prop = new Properties();
    prop.put("StrictHostKeyChecking", "no");
    session.setConfig(prop);
    try {
      session.connect();
    } catch (JSchException e) {
      e.printStackTrace();
    }
    // SSH Channel
    ChannelSftp channelssh = null;
    try {
      channelssh = (ChannelSftp) session.openChannel("sftp");
    } catch (JSchException e) {
      e.printStackTrace();
    }


    try {
      channelssh.connect();
    } catch (JSchException e) {
      e.printStackTrace();
    }

    try {
      channelssh.mkdir(directory);
    } catch (SftpException e) {
      e.printStackTrace();
    }
    channelssh.disconnect();
    session.disconnect();
  }

  public static void uploadFile(String username, String password, String hostname, int port, String src, String des){
    JSch jsch = new JSch();
    Session session = null;
    try {
      session = jsch.getSession(username, hostname, port);
    } catch (JSchException e) {
      e.printStackTrace();
    }
    session.setPassword(password);


    // Avoid asking for key confirmation
    Properties prop = new Properties();
    prop.put("StrictHostKeyChecking", "no");
    session.setConfig(prop);
    try {
      session.connect();
    } catch (JSchException e) {
      e.printStackTrace();
    }
    // SSH Channel
    //ChannelExec channelssh = null;
    ChannelSftp channelssh = null;
    try {
      channelssh = (ChannelSftp) session.openChannel("sftp");
    } catch (JSchException e) {
      e.printStackTrace();
    }


    try {
      channelssh.connect();
    } catch (JSchException e) {
      e.printStackTrace();
    }

    try {
      channelssh.put(src, des);
    } catch (SftpException e) {
      e.printStackTrace();
    }
    channelssh.disconnect();
    session.disconnect();
  }

  public static boolean checkConnection(String username, String password, String hostname, int port) {
    JSch jsch = new JSch();
    Session session = null;
    try {
      session = jsch.getSession(username, hostname, port);
    } catch (JSchException e) {
      e.printStackTrace();
    }
    session.setPassword(password);


    // Avoid asking for key confirmation
    Properties prop = new Properties();
    prop.put("StrictHostKeyChecking", "no");
    session.setConfig(prop);
    try {
      session.connect();
    } catch (JSchException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
