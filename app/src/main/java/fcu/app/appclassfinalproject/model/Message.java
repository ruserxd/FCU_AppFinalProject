package fcu.app.appclassfinalproject.model;

public class Message {

  private int id;
  private int chatroomId;
  private int senderId;
  private String senderName;
  private String content;
  private String createdAt;
  private boolean isSentByMe; // 是否為當前用戶發送

  public Message(int id, int chatroomId, int senderId, String content, String createdAt) {
    this.id = id;
    this.chatroomId = chatroomId;
    this.senderId = senderId;
    this.content = content;
    this.createdAt = createdAt;
  }

  public int getId() {
    return id;
  }

  public int getChatroomId() {
    return chatroomId;
  }

  public int getSenderId() {
    return senderId;
  }

  public String getSenderName() {
    return senderName;
  }

  public void setSenderName(String senderName) {
    this.senderName = senderName;
  }

  public String getContent() {
    return content;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public boolean isSentByMe() {
    return isSentByMe;
  }

  public void setSentByMe(boolean sentByMe) {
    isSentByMe = sentByMe;
  }
}

