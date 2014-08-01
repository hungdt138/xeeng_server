package com.tv.xeeng.base.common;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
@SuppressWarnings("unchecked")
public class Properties
{
  private Map<String, String> mProps;

  @SuppressWarnings("rawtypes")
public Properties()
  {
    this.mProps = new Hashtable();
  }

  public Properties(String aFileName)
    throws ServerException
  {
    
    this(new File(aFileName));
  }

  @SuppressWarnings("rawtypes")
public Properties(File aFile)
    throws ServerException
  {
    try
    {
      this.mProps = new Hashtable();
      if (!(aFile.exists()))
      {
        throw new IOException("File " + aFile.getName() + " is not exist.");
      }
      if (!(aFile.canRead()))
      {
        throw new IOException("File " + aFile.getName() + " must be readable.");
      }

      DocumentBuilderFactory docBuildFac = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuildFac.newDocumentBuilder();
      Document aDoc = docBuilder.parse(aFile);

      loadConfig(aDoc);
    }
    catch (Throwable t) {
      throw new ServerException(t);
    }
  }

  private void loadConfig(Document aDoc)
  {
    Element root = aDoc.getDocumentElement();
    String rootName = root.getNodeName();

    NodeList nodeList = aDoc.getElementsByTagName("*");
    int nodeCount = nodeList.getLength();

    this.mProps.clear();

    for (int i = 0; i < nodeCount; ++i)
    {
      Element element = (Element)nodeList.item(i);

      NodeList tmp = element.getElementsByTagName("*");
      int tmpLength = tmp.getLength();

      if (tmpLength == 0)
      {
        String nameProp = element.getNodeName();
        String value = element.getTextContent();
        Element parentNode = null;
        while (true)
        {
          parentNode = (Element)element.getParentNode();
          if (parentNode.equals(root))
          {
            break;
          }
          nameProp = parentNode.getNodeName() + "." + nameProp;
          element = parentNode;
        }
        nameProp = rootName + "." + nameProp;
        this.mProps.put(nameProp, value);
      }
    }
  }

  public String getString(String aKey)
  {
    String value = (String)this.mProps.get(aKey);
    return value;
  }

  public int getInt(String aKey)
  {
    String value = (String)this.mProps.get(aKey);
    return Integer.decode(value).intValue();
  }

  public boolean getBoolean(String aKey)
  {
    String value = (String)this.mProps.get(aKey);
    return Boolean.parseBoolean(value);
  }
}