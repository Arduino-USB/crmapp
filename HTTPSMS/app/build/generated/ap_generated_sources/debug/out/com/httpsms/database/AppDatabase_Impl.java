package com.httpsms.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile JobDao _jobDao;

  private volatile LogDao _logDao;

  private volatile SmsGroupDao _smsGroupDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `sms_jobs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `toNumber` TEXT, `text` TEXT, `msgType` TEXT, `base64Data` TEXT, `scheduledTime` INTEGER NOT NULL, `status` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `message_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `groupId` INTEGER NOT NULL, `message` TEXT, `type` TEXT, `direction` TEXT, `time` INTEGER NOT NULL, `number` TEXT, `base64Data` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `SmsGroup` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `numbers` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '34282a389f3c42db551b722d63606678')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `sms_jobs`");
        db.execSQL("DROP TABLE IF EXISTS `message_logs`");
        db.execSQL("DROP TABLE IF EXISTS `SmsGroup`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsSmsJobs = new HashMap<String, TableInfo.Column>(7);
        _columnsSmsJobs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmsJobs.put("toNumber", new TableInfo.Column("toNumber", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmsJobs.put("text", new TableInfo.Column("text", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmsJobs.put("msgType", new TableInfo.Column("msgType", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmsJobs.put("base64Data", new TableInfo.Column("base64Data", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmsJobs.put("scheduledTime", new TableInfo.Column("scheduledTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmsJobs.put("status", new TableInfo.Column("status", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSmsJobs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSmsJobs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSmsJobs = new TableInfo("sms_jobs", _columnsSmsJobs, _foreignKeysSmsJobs, _indicesSmsJobs);
        final TableInfo _existingSmsJobs = TableInfo.read(db, "sms_jobs");
        if (!_infoSmsJobs.equals(_existingSmsJobs)) {
          return new RoomOpenHelper.ValidationResult(false, "sms_jobs(com.httpsms.database.SmsJob).\n"
                  + " Expected:\n" + _infoSmsJobs + "\n"
                  + " Found:\n" + _existingSmsJobs);
        }
        final HashMap<String, TableInfo.Column> _columnsMessageLogs = new HashMap<String, TableInfo.Column>(8);
        _columnsMessageLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageLogs.put("groupId", new TableInfo.Column("groupId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageLogs.put("message", new TableInfo.Column("message", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageLogs.put("type", new TableInfo.Column("type", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageLogs.put("direction", new TableInfo.Column("direction", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageLogs.put("time", new TableInfo.Column("time", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageLogs.put("number", new TableInfo.Column("number", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsMessageLogs.put("base64Data", new TableInfo.Column("base64Data", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysMessageLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesMessageLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoMessageLogs = new TableInfo("message_logs", _columnsMessageLogs, _foreignKeysMessageLogs, _indicesMessageLogs);
        final TableInfo _existingMessageLogs = TableInfo.read(db, "message_logs");
        if (!_infoMessageLogs.equals(_existingMessageLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "message_logs(com.httpsms.database.MessageLog).\n"
                  + " Expected:\n" + _infoMessageLogs + "\n"
                  + " Found:\n" + _existingMessageLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsSmsGroup = new HashMap<String, TableInfo.Column>(3);
        _columnsSmsGroup.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmsGroup.put("name", new TableInfo.Column("name", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSmsGroup.put("numbers", new TableInfo.Column("numbers", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSmsGroup = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSmsGroup = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSmsGroup = new TableInfo("SmsGroup", _columnsSmsGroup, _foreignKeysSmsGroup, _indicesSmsGroup);
        final TableInfo _existingSmsGroup = TableInfo.read(db, "SmsGroup");
        if (!_infoSmsGroup.equals(_existingSmsGroup)) {
          return new RoomOpenHelper.ValidationResult(false, "SmsGroup(com.httpsms.database.SmsGroup).\n"
                  + " Expected:\n" + _infoSmsGroup + "\n"
                  + " Found:\n" + _existingSmsGroup);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "34282a389f3c42db551b722d63606678", "ace52fbcd8e4aa63a43c1ed203480a89");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "sms_jobs","message_logs","SmsGroup");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `sms_jobs`");
      _db.execSQL("DELETE FROM `message_logs`");
      _db.execSQL("DELETE FROM `SmsGroup`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(JobDao.class, JobDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LogDao.class, LogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SmsGroupDao.class, SmsGroupDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public JobDao jobDao() {
    if (_jobDao != null) {
      return _jobDao;
    } else {
      synchronized(this) {
        if(_jobDao == null) {
          _jobDao = new JobDao_Impl(this);
        }
        return _jobDao;
      }
    }
  }

  @Override
  public LogDao logDao() {
    if (_logDao != null) {
      return _logDao;
    } else {
      synchronized(this) {
        if(_logDao == null) {
          _logDao = new LogDao_Impl(this);
        }
        return _logDao;
      }
    }
  }

  @Override
  public SmsGroupDao groupDao() {
    if (_smsGroupDao != null) {
      return _smsGroupDao;
    } else {
      synchronized(this) {
        if(_smsGroupDao == null) {
          _smsGroupDao = new SmsGroupDao_Impl(this);
        }
        return _smsGroupDao;
      }
    }
  }
}
