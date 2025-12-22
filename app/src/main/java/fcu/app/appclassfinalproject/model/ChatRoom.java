package fcu.app.appclassfinalproject.model;

import java.util.List;

public class ChatRoom {

  private int id;
  private String name;
  private String type; // "private" 或 "group"
  private String createdAt;
  private int createdBy;
  private List<String> memberNames; // 成員名稱列表
  private String lastMessage; // 最後一條訊息
  private String lastMessageTime; // 最後訊息時間

  public ChatRoom(int id, String name, String type, String createdAt, int createdBy) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.createdAt = createdAt;
    this.createdBy = createdBy;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public int getCreatedBy() {
    return createdBy;
  }

  public List<String> getMemberNames() {
    return memberNames;
  }

  public void setMemberNames(List<String> memberNames) {
    this.memberNames = memberNames;
  }

  public String getLastMessage() {
    return lastMessage;
  }

  public void setLastMessage(String lastMessage) {
    this.lastMessage = lastMessage;
  }

  public String getLastMessageTime() {
    return lastMessageTime;
  }

  public void setLastMessageTime(String lastMessageTime) {
    this.lastMessageTime = lastMessageTime;
  }
}

