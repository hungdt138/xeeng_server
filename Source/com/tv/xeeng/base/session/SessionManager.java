package com.tv.xeeng.base.session;

import com.tv.xeeng.base.common.LoggerContext;
import com.tv.xeeng.databaseDriven.UserDB;
import com.tv.xeeng.game.data.UserEntity;
import com.tv.xeeng.game.room.Room;
import com.tv.xeeng.memcached.data.CacheUserInfo;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class SessionManager {

    private final Logger mLog = LoggerContext.getLoggerFactory().getLogger(
            SessionManager.class);
    
    private final ConcurrentHashMap<String, ISession> mSessions;
    private final ConcurrentHashMap<Long, String> mUIDSessions;
    private final ConcurrentHashMap<Long, ISession> prvSessions;
    private final Vector<ISessionListener> mSessionListeners;
    
    private IdGenerator mIdGenerator;
    private int mSessionTimeout = -1;
    public boolean shutDown = false;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public SessionManager(int aSessionTimeout) {
        this.mSessions = new ConcurrentHashMap();
        this.mUIDSessions = new ConcurrentHashMap();

        this.mSessionTimeout = aSessionTimeout;
        this.prvSessions = new ConcurrentHashMap<Long, ISession>();

        mLog.info("mSessionTimeout : " + mSessionTimeout);

        this.mIdGenerator = new IdGenerator();

        this.mSessionListeners = new Vector();
    }

    public ConcurrentHashMap<String, ISession> getmSessions() {
        return mSessions;
    }

    public void sessionCreated(ISession aSession) {
    	
        ((AbstractSession) aSession).setManager(this);
        mLog.debug("Number connection [Add session]" + this.mSessions.size());
        
        String nextId = this.mIdGenerator.generateId();
        ((AbstractSession) aSession).setID(nextId);

        Long nextUID = Long.valueOf(this.mIdGenerator.generateUID());

        aSession.setUID(nextUID);

        aSession.setTimeout(Integer.valueOf(this.mSessionTimeout));
    }

    public void addSession(String aId, ISession aSession) {

        if (aId.equals(aSession.getID())) {
            synchronized (this.mSessions) {
                this.mSessions.put(aId, aSession);

                this.mUIDSessions.put(aSession.getUID(), aId);
            }
        } else {
            ((AbstractSession) aSession).setID(aId);
        }
    }

    void addUIDSession(Long aUid, ISession aSession) {
        if (aUid.longValue() > 0L) {
            Long uid = aSession.getUID().longValue();
            synchronized (this.mSessions) {
                this.mUIDSessions.remove(Long.valueOf(uid));

                this.mUIDSessions.put(aUid, aSession.getID());
            }
        } else {
            synchronized (this.mSessions) {
                this.mUIDSessions.put(aUid, aSession.getID());
            }
        }
    }

    public List<ISession> findAllSession(long aId) {
        List<ISession> values = new ArrayList(this.mSessions.values());
        int sessionSize = values.size();
        List<ISession> results = new ArrayList<ISession>();
        for (int i = 0; i < sessionSize; i++) {
            AbstractSession ses = (AbstractSession) values.get(i);
            long sessUid = (Long) ses.getAttribute("session.user.id");
            if (sessUid == aId) {
                results.add(ses);
            }
        }
        return results;

    }

    public ISession findPrvChatSession(Long aUid) {
        if (prvSessions.contains(aUid)) {
            ISession newSession = prvSessions.get(aUid);
            if (newSession != null) {
                return newSession;
            }
        }

        ISession newSession = findSession(aUid);
        if (newSession != null) {
            prvSessions.put(aUid, newSession);
        }

        return newSession;
    }

    public void removePrvChatSession(Long aUid) {
        prvSessions.remove(aUid);
    }

    public ISession findSession(String aId) {
//		synchronized (this.mSessions) {
        if (this.mSessions.containsKey(aId)) {
            return ((ISession) this.mSessions.get(aId));
        }

        return null;
//		}
    }

    public ISession findSession(Long aUid) {

//		synchronized (this.mSessions) {
        String sessionId = (String) this.mUIDSessions.get(aUid);
        if (sessionId != null) {
            return findSession(sessionId);
        }

//		}
        return null;
    }

    public ISession removeSession(String aId) {
        synchronized (this.mSessions) {
            ISession session = (ISession) this.mSessions.remove(aId);
            if (session != null) {
                this.mUIDSessions.remove(session.getUID());
            }
            return session;
        }
    }

    public ISession removeSession(ISession aSession) {
        synchronized (this.mSessions) {
            if (aSession != null) {
                this.mUIDSessions.remove(aSession.getUID());

                String id = aSession.getID();
                return ((ISession) this.mSessions.remove(id));
            }

            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    public ISession sessionClosed(String aId) {
        ISession session = null;

        synchronized (this.mSessions) {

            session = (ISession) this.mSessions.remove(aId);
        }
        if (session == null) {
            mLog.error("Fuck session omg! " + aId + "  ;  mUIDSessions : "
                    + mUIDSessions.size());

            Enumeration t = mUIDSessions.keys();
            Object foundItem = null;
            while (t.hasMoreElements()) {
                Object l = t.nextElement();
                // System.out.println(l+" : "+mUIDSessions.get(l));
                if (mUIDSessions.get(l).equalsIgnoreCase(aId)) {
                    foundItem = l;
                }
            }
            if (foundItem != null) {
                mUIDSessions.remove(foundItem, aId);
            }
            mLog.error("Fuck session omg After! " + aId
                    + "  ;  mUIDSessions : " + mUIDSessions.size());
        } else {
            this.mUIDSessions.remove(session.getUID());
            notifySessionClosed(session);
        }

        try {
            UserDB userDb = new UserDB();
            if (session.isLoggedIn()) {
                session.setLoggedIn(false);
                userDb.logout(session.getUID(), session.getCollectInfo()
                        .toString());
            }

            mLog.debug("Number connection " + this.mSessions.size());
        } catch (Exception ex) {
        }
        return session;
    }

    public void addSessionListener(ISessionListener aSessionListener) {
        synchronized (this.mSessionListeners) {
            this.mSessionListeners.add(aSessionListener);
        }
    }

    @SuppressWarnings("rawtypes")
    private void notifySessionClosed(ISession aSession) {
        synchronized (this.mSessionListeners) {
            Iterator it = this.mSessionListeners.iterator();
            while (true) {
                if (!(it.hasNext())) {
                    break;
                }
                ISessionListener sessionListener = (ISessionListener) it.next();
                sessionListener.sessionClosed(aSession);
            }
        }
    }

    public void removeUIDSession(long uid) {
        synchronized (this.mUIDSessions) {
            this.mUIDSessions.remove(uid);
        }
    }

    // TODO:  get free user in all zone
    public Vector<UserEntity> dumpFreeUsers(int limitSize) {
        Vector<UserEntity> users = new Vector<UserEntity>();
        Enumeration<Long> keys = null;

        synchronized (this.mUIDSessions) {
            keys = this.mUIDSessions.keys();

        }
        int results = 0;
        while (true) {
            if ((results >= limitSize) || (!keys.hasMoreElements())) {
                break;
            }
            long uid = keys.nextElement();

            try {
                ISession session = findSession(uid);

                if (session != null) {

                    Vector<Room> joinedRoom = session.getJoinedRooms();
                    if (joinedRoom.size() == 0) {
                        CacheUserInfo cacheUser = new CacheUserInfo();
                        UserEntity user = cacheUser.getUserInfo(uid);
                        users.add(user);
                        ++results;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                mLog.error(ex.getMessage(), ex);
            }

        }

        return users;
    }

    public Vector<UserEntity> dumpFreeFriend(int aLength, int aLevel, int currentZone, long minimumMoneyJoin) throws SQLException {
        Vector<UserEntity> userEntities = new Vector<UserEntity>();
        try {
            Enumeration<Long> keys = this.mUIDSessions.keys();
            int results = 0;
            while (true) {
                if ((results >= aLength) || (!keys.hasMoreElements())) {
                    break;
                }
                long uid = keys.nextElement();
                try {
                    ISession session = findSession(uid);

                    if (session == null) {
                        break;
                    }

                    Vector<Room> joinedRoom = session.getJoinedRooms();
                    if ((joinedRoom.isEmpty())
                            && (session.getUID() != 0)
                            && (session.getCurrentZone() <= 0 || session
                            .getCurrentZone() == currentZone)
                            && !session.isRejectInvite()
                            && session.isReplyInvite())                     
	                    {
	                        CacheUserInfo cacheUser = new CacheUserInfo();
	                        UserEntity user = cacheUser.getUserInfo(uid);
	                        if (user.mUid > 0 && user.money >= minimumMoneyJoin) {
	                            userEntities.add(user);
	                            ++results;
	                        }
	                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } finally {
        }

        return userEntities;
    }
}