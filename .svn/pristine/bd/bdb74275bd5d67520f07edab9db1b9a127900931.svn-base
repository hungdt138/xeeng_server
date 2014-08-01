package com.tv.xeeng.service.cardservice;

public class CardServicePortTypeProxy implements CardServicePortType {
  private String _endpoint = null;
  private CardServicePortType cardServicePortType = null;
  
  public CardServicePortTypeProxy() {
    _initCardServicePortTypeProxy();
  }
  
  public CardServicePortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initCardServicePortTypeProxy();
  }
  
  private void _initCardServicePortTypeProxy() {
    try {
      cardServicePortType = (new CardServiceLocator()).getCardServiceHttpSoap11Endpoint();
      if (cardServicePortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)cardServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)cardServicePortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (cardServicePortType != null)
      ((javax.xml.rpc.Stub)cardServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public CardServicePortType getCardServicePortType() {
    if (cardServicePortType == null)
      _initCardServicePortTypeProxy();
    return cardServicePortType;
  }
  
  public java.lang.String cardRefCodeService(java.lang.String patnerId, java.lang.String password, java.lang.Long userId, java.lang.String refCode, java.lang.String userName, java.lang.String cardId, java.lang.String serviceId, java.lang.String cardCode, java.lang.String txnTime, java.lang.String sig) throws java.rmi.RemoteException{
    if (cardServicePortType == null)
      _initCardServicePortTypeProxy();
    return cardServicePortType.cardRefCodeService(patnerId, password, userId, refCode, userName, cardId, serviceId, cardCode, txnTime, sig);
  }
  
  public java.lang.String cardService(java.lang.String patnerId, java.lang.String serviceId, java.lang.String userName, java.lang.String cardId, java.lang.String cardCode, java.lang.String txnTime, java.lang.String sig) throws java.rmi.RemoteException{
    if (cardServicePortType == null)
      _initCardServicePortTypeProxy();
    return cardServicePortType.cardService(patnerId, serviceId, userName, cardId, cardCode, txnTime, sig);
  }
  
  
}