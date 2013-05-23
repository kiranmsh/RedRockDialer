package com.redrockdialer;

import java.io.Serializable;
import java.util.Date;

public class CallBean implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String name;
  private String number;
  private String date;
  private String callType;
  private String callDuration;
  private String contactId;
  private String email;
  private String headerText;
  private Date DateforSort;
  private boolean header;
  private boolean fromCallLog;
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCallType() {
    return callType;
  }

  public void setCallType(String callType) {
    this.callType = callType;
  }

  public String getCallDuration() {
    return callDuration;
  }

  public void setCallDuration(String callDuration) {
    this.callDuration = callDuration;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getContactId() {
    return contactId;
  }

  public void setContactId(String contactId) {
    this.contactId = contactId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getDateforSort() {
    return DateforSort;
  }

  public void setDateforSort(Date dateforSort) {
    DateforSort = dateforSort;
  }

  public boolean isHeader() {
    return header;
  }

  public void setHeader(boolean header) {
    this.header = header;
  }

  public String getHeaderText() {
    return headerText;
  }

  public void setHeaderText(String headerText) {
    this.headerText = headerText;
  }

  public boolean isFromCallLog() {
    return fromCallLog;
  }

  public void setFromCallLog(boolean fromCallLog) {
    this.fromCallLog = fromCallLog;
  }


}
