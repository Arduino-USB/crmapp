package com.httpsms.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
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
public final class LogDao_Impl implements LogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MessageLog> __insertionAdapterOfMessageLog;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLogsByNumber;

  public LogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMessageLog = new EntityInsertionAdapter<MessageLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `message_logs` (`id`,`groupId`,`message`,`type`,`direction`,`time`,`number`,`base64Data`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final MessageLog entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.groupId);
        if (entity.message == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.message);
        }
        if (entity.type == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.type);
        }
        if (entity.direction == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.direction);
        }
        statement.bindLong(6, entity.time);
        if (entity.number == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.number);
        }
        if (entity.base64Data == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.base64Data);
        }
      }
    };
    this.__preparedStmtOfDeleteLogsByNumber = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM message_logs WHERE number = ?";
        return _query;
      }
    };
  }

  @Override
  public void insert(final MessageLog log) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfMessageLog.insert(log);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteLogsByNumber(final String number) {
    __db.assertNotSuspendingTransaction();
    final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLogsByNumber.acquire();
    int _argIndex = 1;
    if (number == null) {
      _stmt.bindNull(_argIndex);
    } else {
      _stmt.bindString(_argIndex, number);
    }
    try {
      __db.beginTransaction();
      try {
        _stmt.executeUpdateDelete();
        __db.setTransactionSuccessful();
      } finally {
        __db.endTransaction();
      }
    } finally {
      __preparedStmtOfDeleteLogsByNumber.release(_stmt);
    }
  }

  @Override
  public List<MessageLog> getLogsByNumberPaged(final String number, final int limit,
      final int offset) {
    final String _sql = "SELECT * FROM message_logs WHERE number = ? ORDER BY time DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (number == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, number);
    }
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 3;
    _statement.bindLong(_argIndex, offset);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
      final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final int _cursorIndexOfNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "number");
      final int _cursorIndexOfBase64Data = CursorUtil.getColumnIndexOrThrow(_cursor, "base64Data");
      final List<MessageLog> _result = new ArrayList<MessageLog>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final MessageLog _item;
        _item = new MessageLog();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        _item.groupId = _cursor.getLong(_cursorIndexOfGroupId);
        if (_cursor.isNull(_cursorIndexOfMessage)) {
          _item.message = null;
        } else {
          _item.message = _cursor.getString(_cursorIndexOfMessage);
        }
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        if (_cursor.isNull(_cursorIndexOfDirection)) {
          _item.direction = null;
        } else {
          _item.direction = _cursor.getString(_cursorIndexOfDirection);
        }
        _item.time = _cursor.getLong(_cursorIndexOfTime);
        if (_cursor.isNull(_cursorIndexOfNumber)) {
          _item.number = null;
        } else {
          _item.number = _cursor.getString(_cursorIndexOfNumber);
        }
        if (_cursor.isNull(_cursorIndexOfBase64Data)) {
          _item.base64Data = null;
        } else {
          _item.base64Data = _cursor.getString(_cursorIndexOfBase64Data);
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
  public List<MessageLog> getAllLogsPaged(final int limit, final int offset) {
    final String _sql = "SELECT * FROM message_logs ORDER BY time DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 2;
    _statement.bindLong(_argIndex, offset);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
      final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final int _cursorIndexOfNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "number");
      final int _cursorIndexOfBase64Data = CursorUtil.getColumnIndexOrThrow(_cursor, "base64Data");
      final List<MessageLog> _result = new ArrayList<MessageLog>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final MessageLog _item;
        _item = new MessageLog();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        _item.groupId = _cursor.getLong(_cursorIndexOfGroupId);
        if (_cursor.isNull(_cursorIndexOfMessage)) {
          _item.message = null;
        } else {
          _item.message = _cursor.getString(_cursorIndexOfMessage);
        }
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        if (_cursor.isNull(_cursorIndexOfDirection)) {
          _item.direction = null;
        } else {
          _item.direction = _cursor.getString(_cursorIndexOfDirection);
        }
        _item.time = _cursor.getLong(_cursorIndexOfTime);
        if (_cursor.isNull(_cursorIndexOfNumber)) {
          _item.number = null;
        } else {
          _item.number = _cursor.getString(_cursorIndexOfNumber);
        }
        if (_cursor.isNull(_cursorIndexOfBase64Data)) {
          _item.base64Data = null;
        } else {
          _item.base64Data = _cursor.getString(_cursorIndexOfBase64Data);
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
  public List<MessageLog> getLogsByGroupIdPaged(final long groupId, final int limit,
      final int offset) {
    final String _sql = "SELECT * FROM message_logs WHERE groupId = ? ORDER BY time DESC LIMIT ? OFFSET ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, groupId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    _argIndex = 3;
    _statement.bindLong(_argIndex, offset);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfGroupId = CursorUtil.getColumnIndexOrThrow(_cursor, "groupId");
      final int _cursorIndexOfMessage = CursorUtil.getColumnIndexOrThrow(_cursor, "message");
      final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
      final int _cursorIndexOfDirection = CursorUtil.getColumnIndexOrThrow(_cursor, "direction");
      final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
      final int _cursorIndexOfNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "number");
      final int _cursorIndexOfBase64Data = CursorUtil.getColumnIndexOrThrow(_cursor, "base64Data");
      final List<MessageLog> _result = new ArrayList<MessageLog>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final MessageLog _item;
        _item = new MessageLog();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        _item.groupId = _cursor.getLong(_cursorIndexOfGroupId);
        if (_cursor.isNull(_cursorIndexOfMessage)) {
          _item.message = null;
        } else {
          _item.message = _cursor.getString(_cursorIndexOfMessage);
        }
        if (_cursor.isNull(_cursorIndexOfType)) {
          _item.type = null;
        } else {
          _item.type = _cursor.getString(_cursorIndexOfType);
        }
        if (_cursor.isNull(_cursorIndexOfDirection)) {
          _item.direction = null;
        } else {
          _item.direction = _cursor.getString(_cursorIndexOfDirection);
        }
        _item.time = _cursor.getLong(_cursorIndexOfTime);
        if (_cursor.isNull(_cursorIndexOfNumber)) {
          _item.number = null;
        } else {
          _item.number = _cursor.getString(_cursorIndexOfNumber);
        }
        if (_cursor.isNull(_cursorIndexOfBase64Data)) {
          _item.base64Data = null;
        } else {
          _item.base64Data = _cursor.getString(_cursorIndexOfBase64Data);
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
