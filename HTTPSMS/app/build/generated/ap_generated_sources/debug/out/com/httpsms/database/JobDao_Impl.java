package com.httpsms.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class JobDao_Impl implements JobDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SmsJob> __insertionAdapterOfSmsJob;

  private final EntityDeletionOrUpdateAdapter<SmsJob> __updateAdapterOfSmsJob;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public JobDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSmsJob = new EntityInsertionAdapter<SmsJob>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `sms_jobs` (`id`,`toNumber`,`text`,`msgType`,`base64Data`,`scheduledTime`,`status`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final SmsJob entity) {
        statement.bindLong(1, entity.id);
        if (entity.toNumber == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.toNumber);
        }
        if (entity.text == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.text);
        }
        if (entity.msgType == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.msgType);
        }
        if (entity.base64Data == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.base64Data);
        }
        statement.bindLong(6, entity.scheduledTime);
        if (entity.status == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.status);
        }
      }
    };
    this.__updateAdapterOfSmsJob = new EntityDeletionOrUpdateAdapter<SmsJob>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `sms_jobs` SET `id` = ?,`toNumber` = ?,`text` = ?,`msgType` = ?,`base64Data` = ?,`scheduledTime` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final SmsJob entity) {
        statement.bindLong(1, entity.id);
        if (entity.toNumber == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.toNumber);
        }
        if (entity.text == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.text);
        }
        if (entity.msgType == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.msgType);
        }
        if (entity.base64Data == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.base64Data);
        }
        statement.bindLong(6, entity.scheduledTime);
        if (entity.status == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.status);
        }
        statement.bindLong(8, entity.id);
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM sms_jobs WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public long insert(final SmsJob job) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfSmsJob.insertAndReturnId(job);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final SmsJob job) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfSmsJob.handle(job);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteById(final long id) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
    int _argIndex = 1;
    _stmt.bindLong(_argIndex, id);
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteById.release(_stmt);
    }
  }

  @Override
  public SmsJob getJobById(final long id) {
    final String _sql = "SELECT * FROM sms_jobs WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfToNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "toNumber");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final int _cursorIndexOfMsgType = CursorUtil.getColumnIndexOrThrow(_cursor, "msgType");
      final int _cursorIndexOfBase64Data = CursorUtil.getColumnIndexOrThrow(_cursor, "base64Data");
      final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final SmsJob _result;
      if (_cursor.moveToFirst()) {
        _result = new SmsJob();
        _result.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfToNumber)) {
          _result.toNumber = null;
        } else {
          _result.toNumber = _cursor.getString(_cursorIndexOfToNumber);
        }
        if (_cursor.isNull(_cursorIndexOfText)) {
          _result.text = null;
        } else {
          _result.text = _cursor.getString(_cursorIndexOfText);
        }
        if (_cursor.isNull(_cursorIndexOfMsgType)) {
          _result.msgType = null;
        } else {
          _result.msgType = _cursor.getString(_cursorIndexOfMsgType);
        }
        if (_cursor.isNull(_cursorIndexOfBase64Data)) {
          _result.base64Data = null;
        } else {
          _result.base64Data = _cursor.getString(_cursorIndexOfBase64Data);
        }
        _result.scheduledTime = _cursor.getLong(_cursorIndexOfScheduledTime);
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _result.status = null;
        } else {
          _result.status = _cursor.getString(_cursorIndexOfStatus);
        }
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<SmsJob> getPendingJobs(final long currentTime) {
    final String _sql = "SELECT * FROM sms_jobs WHERE status = 'pending' AND scheduledTime <= ? ORDER BY scheduledTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, currentTime);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfToNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "toNumber");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final int _cursorIndexOfMsgType = CursorUtil.getColumnIndexOrThrow(_cursor, "msgType");
      final int _cursorIndexOfBase64Data = CursorUtil.getColumnIndexOrThrow(_cursor, "base64Data");
      final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<SmsJob> _result = new ArrayList<SmsJob>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final SmsJob _item;
        _item = new SmsJob();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfToNumber)) {
          _item.toNumber = null;
        } else {
          _item.toNumber = _cursor.getString(_cursorIndexOfToNumber);
        }
        if (_cursor.isNull(_cursorIndexOfText)) {
          _item.text = null;
        } else {
          _item.text = _cursor.getString(_cursorIndexOfText);
        }
        if (_cursor.isNull(_cursorIndexOfMsgType)) {
          _item.msgType = null;
        } else {
          _item.msgType = _cursor.getString(_cursorIndexOfMsgType);
        }
        if (_cursor.isNull(_cursorIndexOfBase64Data)) {
          _item.base64Data = null;
        } else {
          _item.base64Data = _cursor.getString(_cursorIndexOfBase64Data);
        }
        _item.scheduledTime = _cursor.getLong(_cursorIndexOfScheduledTime);
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<SmsJob> getFailedJobs() {
    final String _sql = "SELECT * FROM sms_jobs WHERE status = 'failed' OR status = 'bad message'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfToNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "toNumber");
      final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
      final int _cursorIndexOfMsgType = CursorUtil.getColumnIndexOrThrow(_cursor, "msgType");
      final int _cursorIndexOfBase64Data = CursorUtil.getColumnIndexOrThrow(_cursor, "base64Data");
      final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
      final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
      final List<SmsJob> _result = new ArrayList<SmsJob>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final SmsJob _item;
        _item = new SmsJob();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfToNumber)) {
          _item.toNumber = null;
        } else {
          _item.toNumber = _cursor.getString(_cursorIndexOfToNumber);
        }
        if (_cursor.isNull(_cursorIndexOfText)) {
          _item.text = null;
        } else {
          _item.text = _cursor.getString(_cursorIndexOfText);
        }
        if (_cursor.isNull(_cursorIndexOfMsgType)) {
          _item.msgType = null;
        } else {
          _item.msgType = _cursor.getString(_cursorIndexOfMsgType);
        }
        if (_cursor.isNull(_cursorIndexOfBase64Data)) {
          _item.base64Data = null;
        } else {
          _item.base64Data = _cursor.getString(_cursorIndexOfBase64Data);
        }
        _item.scheduledTime = _cursor.getLong(_cursorIndexOfScheduledTime);
        if (_cursor.isNull(_cursorIndexOfStatus)) {
          _item.status = null;
        } else {
          _item.status = _cursor.getString(_cursorIndexOfStatus);
        }
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
