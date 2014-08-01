package com.tv.xeeng.game.room;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.base.session.ISessionListener;

public class ZoneManager implements ISessionListener {
  private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(Zone.class);
  private final String DEFAULT_ZONES_CONFIG = "conf/zones-config.xml";
  private ConcurrentHashMap<Integer, Zone> mZones;
  private IdRoomGenerator mIdGenerator;
  
  public ZoneManager() throws ServerException
  {
    this.mZones = new ConcurrentHashMap();
    this.mIdGenerator = new IdRoomGenerator();
    initZones();
  }

  private void initZones() throws ServerException
  {
    File fConfig;
    try
    {
      fConfig = new File(DEFAULT_ZONES_CONFIG);
      this.mLog.info("[ZONES] From file = " + DEFAULT_ZONES_CONFIG);

      if (!(fConfig.exists()))
      {
        throw new IOException("File " + fConfig.getName() + " is not exist.");
      }
      if (!(fConfig.canRead()))
      {
        throw new IOException("File " + fConfig.getName() + " must be readable.");
      }

      DocumentBuilderFactory docBuildFac = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuildFac.newDocumentBuilder();
      Document aDoc = docBuilder.parse(fConfig);

      Element root = aDoc.getDocumentElement();

      NodeList zoneList = root.getElementsByTagName("zone");
      int numZone = zoneList.getLength();
      int zoneIdx = 0;
      
      while (true) { 
    	 
    	if (zoneIdx >= numZone) break;

        Element zone = (Element)zoneList.item(zoneIdx);
        int zoneId = Integer.parseInt(zone.getAttribute("id"));
        String zoneName = zone.getAttribute("name");
        int roomCapacity = Integer.parseInt(zone.getAttribute("capacity"));
        int playerSize = Integer.parseInt(zone.getAttribute("players"));

        Zone newZone = new Zone();
        newZone.setZoneId(zoneId);
        newZone.setZoneName(zoneName);
        newZone.setRoomCapacity(roomCapacity);
        newZone.setPlayerSize(playerSize);

//        newZone.initRooms();  //Init rooms for this zone
        newZone.initPhong();//tuanda
        
        int joinLimited = -1;
        if (zone.hasAttribute("joinlimited"))
        {
          joinLimited = Integer.parseInt("joinlimited");
          if (joinLimited <= 0)
          {
            joinLimited = -1;
          }
        }
        newZone.setJoinLimited(joinLimited);

        newZone.setIdRoomGenerator(this.mIdGenerator);

        this.mZones.put(Integer.valueOf(zoneId), newZone);

        NodeList roomList = zone.getElementsByTagName("room");
        int numRoom = roomList.getLength();
        for (int roomIdx = 0; roomIdx < numRoom; ++roomIdx)
        {
          Element room = (Element)roomList.item(roomIdx);
          String roomName = room.getAttribute("name");

          Room newRoom = newZone.createRoom(roomName,0,0);

          newRoom.setPermanent();

          newRoom.setName(roomName);
        }
        ++zoneIdx;
      }

    }
    catch (Throwable t)
    {
      throw new ServerException(t);
    }
  }
  public Enumeration<Zone> getZones(){
	  //synchronized (mZones) {
		return this.mZones.elements();
	//}
  }
  
  public Collection<Zone> getZoneList() {
	  return this.mZones.values();
  }
  
  public Phong findPhong(int phongId)
  {
      
      Enumeration<Zone> zones =this.mZones.elements();
      while ((zones != null) && (zones.hasMoreElements())) {
            Zone zone = (Zone) zones.nextElement();
            Phong result = zone.getPhong(phongId);
            if(result!= null)
                return result;

      }
      return null;
  }
  public Zone findZone(int aZoneId)
  {
    return ((Zone)this.mZones.get(Integer.valueOf(aZoneId)));
  }
  
  @Override
  public void sessionClosed(ISession aSession)
  {
	  Vector joinedRooms = aSession.getJoinedRooms();
//    synchronized (joinedRooms)
//    {
      Iterator i$ = joinedRooms.iterator();
      while (true) { 
    	  if (!(i$.hasNext()))
    		  break;
    	  Room aRoom = (Room)i$.next();
    	  if (aRoom != null) {
	          aRoom.left(aSession);
	
	          if ((aRoom.isEmpty()) && (!(aRoom.isPermanent())))
	          {
	            Zone zone = aRoom.getZone();
	            if (zone != null)
	            {
	              zone.deleteRoom(aRoom);
	              this.mLog.debug("[ROOM MANAGER] " + aRoom.getName() + " has been closed!");
	            }
	          }
	      }
      }
   // }
  }

  class IdRoomGenerator
  {
	//private final long INIT_ID_ROOM = 1000L;
	//private static final long MAX_ID = -1001L;
    private final AtomicLong mNextIdRoom;

    public IdRoomGenerator()
    {
      //this.mNextIdRoom = new AtomicLong(System.currentTimeMillis());
        this.mNextIdRoom = new AtomicLong(1000L);
    }

    public synchronized long generateIdRoom()
    {
      synchronized (this.mNextIdRoom)
      {
        long result = this.mNextIdRoom.getAndIncrement();
        if (result >= -1001L)
        {
          result = result - -1001L + 1000L;
          this.mNextIdRoom.set(result + 1L);
        }
        return result;
      }
    }
  }
}