package com.httpsms.database;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
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
public final class SmsGroupDao_Impl implements SmsGroupDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SmsGroup> __insertionAdapterOfSmsGroup;

  private final EntityDeletionOrUpdateAdapter<SmsGroup> __deletionAdapterOfSmsGroup;

  public SmsGroupDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSmsGroup = new EntityInsertionAdapter<SmsGroup>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `SmsGroup` (`id`,`name`,`numbers`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final SmsGroup entity) {
        statement.bindLong(1, entity.id);
        if (entity.name == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.name);
        }
        if (entity.numbers == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.numbers);
        }
      }
    };
    this.__deletionAdapterOfSmsGroup = new EntityDeletionOrUpdateAdapter<SmsGroup>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `SmsGroup` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final SmsGroup entity) {
        statement.bindLong(1, entity.id);
      }
    };
  }

  @Override
  public long insert(final SmsGroup group) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      final long _result = __insertionAdapterOfSmsGroup.insertAndReturnId(group);
      __db.setTransactionSuccessful();
      return _result;
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final SmsGroup group) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfSmsGroup.handle(group);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<SmsGroup> getAllGroups() {
    final String _sql = "SELECT * FROM SmsGroup";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfNumbers = CursorUtil.getColumnIndexOrThrow(_cursor, "numbers");
      final List<SmsGroup> _result = new ArrayList<SmsGroup>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final SmsGroup _item;
        _item = new SmsGroup();
        _item.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _item.name = null;
        } else {
          _item.name = _cursor.getString(_cursorIndexOfName);
        }
        if (_cursor.isNull(_cursorIndexOfNumbers)) {
          _item.numbers = null;
        } else {
          _item.numbers = _cursor.getString(_cursorIndexOfNumbers);
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
  public SmsGroup getGroupById(final long id) {
    final String _sql = "SELECT * FROM SmsGroup WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
      final int _cursorIndexOfNumbers = CursorUtil.getColumnIndexOrThrow(_cursor, "numbers");
      final SmsGroup _result;
      if (_cursor.moveToFirst()) {
        _result = new SmsGroup();
        _result.id = _cursor.getLong(_cursorIndexOfId);
        if (_cursor.isNull(_cursorIndexOfName)) {
          _result.name = null;
        } else {
          _result.name = _cursor.getString(_cursorIndexOfName);
        }
        if (_cursor.isNull(_cursorIndexOfNumbers)) {
          _result.numbers = null;
        } else {
          _result.numbers = _cursor.getString(_cursorIndexOfNumbers);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
