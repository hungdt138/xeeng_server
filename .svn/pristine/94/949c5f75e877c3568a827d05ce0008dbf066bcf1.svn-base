package com.tv.xeeng.game.chat.data;



import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;


import org.slf4j.Logger;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.base.common.ServerException;
import com.tv.xeeng.base.session.ISession;
import com.tv.xeeng.game.data.AIOConstants;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import com.tv.xeeng.protocol.IResponseMessage;
import com.tv.xeeng.server.Server;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
	private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
			ChatRoom.class);

	public final ConcurrentHashMap<Long, ISession> mSessions = new ConcurrentHashMap<Long, ISession>();
        private static final int MAX_MESSAGES = 20;
        private int playing;
	private ChatRoomEntity entity;
        
        private List<ChatEntity> lstChatEntites = new ArrayList<ChatEntity>();
        
        
	public ChatRoom(ChatRoomEntity entity) {
		this.entity = entity;
                
	}
        
        public void addMessageHistory(ChatEntity entity)
        {
            lstChatEntites.add(0, entity);
        }
        
        public String getChatRoomHistory()
        {
            StringBuilder sb = new StringBuilder();
            
            List<ChatEntity> lstUserId = new ArrayList<ChatEntity>();
            
            //add index with content
            int maxSize = MAX_MESSAGES>lstChatEntites.size()?lstChatEntites.size():MAX_MESSAGES;
            for(int i = 0; i< maxSize; i++)
            {
                
                ChatEntity entity = lstChatEntites.get(i);
                int uidSize = lstUserId.size();
                
                int index = uidSize;
                for(int j = 0; j< uidSize; j++)
                {
                    if(lstUserId.get(j).getUserId() == entity.getUserId())
                    {
                        index = j;
                        break;
                    }
                }

                if(index == uidSize || uidSize == 0) //it 'snew uid so we add it
                {

                    lstUserId.add(entity);
                }

                sb.append(index).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.getMessage()).append(AIOConstants.SEPERATOR_BYTE_1);
                sb.append(entity.getDt().getTime()).append(AIOConstants.SEPERATOR_BYTE_2);
            }
            
            if(sb.length()>0)
            {
                sb.deleteCharAt(sb.length() -1);
                sb.append(AIOConstants.SEPERATOR_BYTE_3);
            }
            
            //Add uid and userName
            int userSize = lstUserId.size();
            
            for(int i = 0; i< userSize; i++)
            {
                ChatEntity entity = lstUserId.get(i);
                sb.append(entity.getUserId()).append(AIOConstants.SEPERATOR_BYTE_1);
                 sb.append(entity.getUserName()).append(AIOConstants.SEPERATOR_BYTE_2);
            }
            
            if(userSize>0)
            {
                sb.deleteCharAt(sb.length() -1);
            }
            
            return sb.toString();
        }
        
        

	public void enter(ISession s) {
	   //try to remove old enter chat room
           if(s.getChatRoom()>0)
           {
               ChatRoom oldCR = Server.getChatRoomZone().findChatRoom(s.getChatRoom());
               if(oldCR != null)
               {
                   oldCR.out(s);
               }
           }
           
           //add this session into new room chat
           this.mSessions.put(s.getUID(), s);
               
           s.setChatRoom(this.entity.getChatRoomId());
           playing++;
	}
        
//        public void out(ISession s, ChatRoom oldCR) {
//	   //try to remove old enter chat room
//           
//               
//           if(oldCR != null)
//           {
//               oldCR.out(s);
//           }
//           
//           
//           playing--;
//	}

        public String getOnlinePlayer(ISession requestSession)
        {
            StringBuilder sb = new StringBuilder();
            if(requestSession != null)
            {
                Enumeration<ISession> enumSessions = this.mSessions.elements();
                List<ISession> removeSession = new ArrayList<ISession>();
                CacheUserInfo cacheUser = new CacheUserInfo();
                while(enumSessions.hasMoreElements())
                {
                    ISession session = (ISession) enumSessions.nextElement();
                    if (session != null && !session.isClosed() 
                                        && session.getChatRoom() == entity.getChatRoomId() && session.getUID() >0)	
                    {
                        if(session == requestSession)
                            continue;
                        
                        UserEntity entity = cacheUser.getUserInfo(session.getUID());
                        
                        if(entity != null)
                        {
                        
                             sb.append(entity.mUid).append(AIOConstants.SEPERATOR_BYTE_1);
                             sb.append(entity.mUsername).append(AIOConstants.SEPERATOR_BYTE_1);
                             sb.append(entity.stt == null? "": entity.stt).append(AIOConstants.SEPERATOR_BYTE_1);
                             sb.append(entity.hasBia?"1":"0").append(AIOConstants.SEPERATOR_BYTE_2);
                        }
                        else
                        {
                            mLog.warn("null chat user Entity " + session.getUID());
                            removeSession.add(session);
                        }
                    }
                    else
                    {
//                        if(session == null || session.isClosed() || session.getUID()== 0 || session.)
//                        {
                            removeSession.add(session);
//                        }
                    }
                    
                }
                
                int removeSize = removeSession.size();
                for(int i = 0; i< removeSize; i++)
                {
                    out(removeSession.get(i));   
                }
                        
                if(sb.length()>0)
                {
                    sb.deleteCharAt(sb.length() -1);
                }
                
            }
            
            return sb.toString();
        }
        
        
	public void out(ISession s) {
            if(s != null)
            {
                this.mSessions.remove(s.getUID());

                s.setChatRoom(0);
                if(playing>0)
                    playing--;
            }
	}

	
        
	public void broadcastWithoutSender(IResponseMessage aResMsg, ISession aSender) {
		Enumeration<ISession> enumSessions = null;
                if(aSender == null)
                    return;
//		synchronized (this.mSessions) {
                enumSessions = this.mSessions.elements();
//		}
                List<ISession> removeSession = new ArrayList<ISession>();
                try
                {
                    while (true) {
                            ISession session;
                            while (true) {
                                    if (!(enumSessions.hasMoreElements())) {
                                            return;
                                    }
                                    session = (ISession) enumSessions.nextElement();
                                    if ((session.getUID() != aSender.getUID())) {
                                            break;
                                    }
                            }

                            try {
                                    if (session != null && aResMsg != null && !session.isClosed() 
                                            && session.getChatRoom() == entity.getChatRoomId())	
                                        session.write(aResMsg);
                                    else
                                    {
    //                                    if(session.isClosed() || session == null)
    //                                    {
                                            removeSession.add(session);
    //                                    }
                                    }

                            }catch (ServerException ex) {
                                this.mLog.error(ex.getMessage(), ex);

                            } catch (Exception ex) {
                                    this.mLog.error(ex.getMessage(), ex);

                            }
                    }        
                }
                finally
                {
                    int delSize = removeSession.size();
                    for(int i = 0; i< delSize; i++)
                    {
                        out(removeSession.get(i));   
                    }
                }
		
	}

    /**
     * @return the playing
     */
    public int getPlaying() {
        return playing;
    }

    /**
     * @param playing the playing to set
     */
    public void setPlaying(int playing) {
        
                
        this.playing = playing;
                
    }

    /**
     * @return the entity
     */
    public ChatRoomEntity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(ChatRoomEntity entity) {
        this.entity = entity;
    }
}
