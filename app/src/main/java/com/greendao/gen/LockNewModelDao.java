package com.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.android.tacu.db.model.LockNewModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LOCK_NEW_MODEL".
*/
public class LockNewModelDao extends AbstractDao<LockNewModel, Long> {

    public static final String TABLENAME = "LOCK_NEW_MODEL";

    /**
     * Properties of entity LockNewModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property AccountString = new Property(1, String.class, "accountString", false, "ACCOUNT_STRING");
        public final static Property AccountPwd = new Property(2, String.class, "accountPwd", false, "ACCOUNT_PWD");
        public final static Property GestureString = new Property(3, String.class, "gestureString", false, "GESTURE_STRING");
        public final static Property IsFinger = new Property(4, boolean.class, "isFinger", false, "IS_FINGER");
    }


    public LockNewModelDao(DaoConfig config) {
        super(config);
    }
    
    public LockNewModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOCK_NEW_MODEL\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"ACCOUNT_STRING\" TEXT," + // 1: accountString
                "\"ACCOUNT_PWD\" TEXT," + // 2: accountPwd
                "\"GESTURE_STRING\" TEXT," + // 3: gestureString
                "\"IS_FINGER\" INTEGER NOT NULL );"); // 4: isFinger
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOCK_NEW_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LockNewModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String accountString = entity.getAccountString();
        if (accountString != null) {
            stmt.bindString(2, accountString);
        }
 
        String accountPwd = entity.getAccountPwd();
        if (accountPwd != null) {
            stmt.bindString(3, accountPwd);
        }
 
        String gestureString = entity.getGestureString();
        if (gestureString != null) {
            stmt.bindString(4, gestureString);
        }
        stmt.bindLong(5, entity.getIsFinger() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LockNewModel entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String accountString = entity.getAccountString();
        if (accountString != null) {
            stmt.bindString(2, accountString);
        }
 
        String accountPwd = entity.getAccountPwd();
        if (accountPwd != null) {
            stmt.bindString(3, accountPwd);
        }
 
        String gestureString = entity.getGestureString();
        if (gestureString != null) {
            stmt.bindString(4, gestureString);
        }
        stmt.bindLong(5, entity.getIsFinger() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LockNewModel readEntity(Cursor cursor, int offset) {
        LockNewModel entity = new LockNewModel( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // accountString
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // accountPwd
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // gestureString
            cursor.getShort(offset + 4) != 0 // isFinger
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LockNewModel entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setAccountString(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setAccountPwd(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setGestureString(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setIsFinger(cursor.getShort(offset + 4) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LockNewModel entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LockNewModel entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LockNewModel entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
