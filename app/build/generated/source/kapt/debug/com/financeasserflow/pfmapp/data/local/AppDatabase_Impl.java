package com.financeasserflow.pfmapp.data.local;

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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile AssetDao _assetDao;

  private volatile AssetHistoryDao _assetHistoryDao;

  private volatile PortfolioTargetDao _portfolioTargetDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `assets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `assetType` TEXT NOT NULL, `category` TEXT NOT NULL, `amount` INTEGER NOT NULL, `principalAmount` INTEGER, `valuationAmount` INTEGER, `memo` TEXT, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assets_name` ON `assets` (`name`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assets_assetType` ON `assets` (`assetType`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_assets_category` ON `assets` (`category`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `asset_histories` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `assetId` INTEGER NOT NULL, `changeType` TEXT NOT NULL, `previousAmount` INTEGER, `newAmount` INTEGER, `memo` TEXT, `createdAt` INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_asset_histories_assetId` ON `asset_histories` (`assetId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_asset_histories_changeType` ON `asset_histories` (`changeType`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `portfolio_targets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `category` TEXT NOT NULL, `targetRatio` REAL NOT NULL, `note` TEXT, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_portfolio_targets_category` ON `portfolio_targets` (`category`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'de09ca514c23bc68fccf8c5dc10df691')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `assets`");
        db.execSQL("DROP TABLE IF EXISTS `asset_histories`");
        db.execSQL("DROP TABLE IF EXISTS `portfolio_targets`");
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
        final HashMap<String, TableInfo.Column> _columnsAssets = new HashMap<String, TableInfo.Column>(10);
        _columnsAssets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("assetType", new TableInfo.Column("assetType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("amount", new TableInfo.Column("amount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("principalAmount", new TableInfo.Column("principalAmount", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("valuationAmount", new TableInfo.Column("valuationAmount", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("memo", new TableInfo.Column("memo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssets.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAssets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAssets = new HashSet<TableInfo.Index>(3);
        _indicesAssets.add(new TableInfo.Index("index_assets_name", false, Arrays.asList("name"), Arrays.asList("ASC")));
        _indicesAssets.add(new TableInfo.Index("index_assets_assetType", false, Arrays.asList("assetType"), Arrays.asList("ASC")));
        _indicesAssets.add(new TableInfo.Index("index_assets_category", false, Arrays.asList("category"), Arrays.asList("ASC")));
        final TableInfo _infoAssets = new TableInfo("assets", _columnsAssets, _foreignKeysAssets, _indicesAssets);
        final TableInfo _existingAssets = TableInfo.read(db, "assets");
        if (!_infoAssets.equals(_existingAssets)) {
          return new RoomOpenHelper.ValidationResult(false, "assets(com.financeasserflow.pfmapp.data.model.AssetEntity).\n"
                  + " Expected:\n" + _infoAssets + "\n"
                  + " Found:\n" + _existingAssets);
        }
        final HashMap<String, TableInfo.Column> _columnsAssetHistories = new HashMap<String, TableInfo.Column>(7);
        _columnsAssetHistories.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssetHistories.put("assetId", new TableInfo.Column("assetId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssetHistories.put("changeType", new TableInfo.Column("changeType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssetHistories.put("previousAmount", new TableInfo.Column("previousAmount", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssetHistories.put("newAmount", new TableInfo.Column("newAmount", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssetHistories.put("memo", new TableInfo.Column("memo", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAssetHistories.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAssetHistories = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAssetHistories = new HashSet<TableInfo.Index>(2);
        _indicesAssetHistories.add(new TableInfo.Index("index_asset_histories_assetId", false, Arrays.asList("assetId"), Arrays.asList("ASC")));
        _indicesAssetHistories.add(new TableInfo.Index("index_asset_histories_changeType", false, Arrays.asList("changeType"), Arrays.asList("ASC")));
        final TableInfo _infoAssetHistories = new TableInfo("asset_histories", _columnsAssetHistories, _foreignKeysAssetHistories, _indicesAssetHistories);
        final TableInfo _existingAssetHistories = TableInfo.read(db, "asset_histories");
        if (!_infoAssetHistories.equals(_existingAssetHistories)) {
          return new RoomOpenHelper.ValidationResult(false, "asset_histories(com.financeasserflow.pfmapp.data.model.AssetHistoryEntity).\n"
                  + " Expected:\n" + _infoAssetHistories + "\n"
                  + " Found:\n" + _existingAssetHistories);
        }
        final HashMap<String, TableInfo.Column> _columnsPortfolioTargets = new HashMap<String, TableInfo.Column>(5);
        _columnsPortfolioTargets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPortfolioTargets.put("category", new TableInfo.Column("category", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPortfolioTargets.put("targetRatio", new TableInfo.Column("targetRatio", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPortfolioTargets.put("note", new TableInfo.Column("note", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPortfolioTargets.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPortfolioTargets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPortfolioTargets = new HashSet<TableInfo.Index>(1);
        _indicesPortfolioTargets.add(new TableInfo.Index("index_portfolio_targets_category", true, Arrays.asList("category"), Arrays.asList("ASC")));
        final TableInfo _infoPortfolioTargets = new TableInfo("portfolio_targets", _columnsPortfolioTargets, _foreignKeysPortfolioTargets, _indicesPortfolioTargets);
        final TableInfo _existingPortfolioTargets = TableInfo.read(db, "portfolio_targets");
        if (!_infoPortfolioTargets.equals(_existingPortfolioTargets)) {
          return new RoomOpenHelper.ValidationResult(false, "portfolio_targets(com.financeasserflow.pfmapp.data.model.PortfolioTargetEntity).\n"
                  + " Expected:\n" + _infoPortfolioTargets + "\n"
                  + " Found:\n" + _existingPortfolioTargets);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "de09ca514c23bc68fccf8c5dc10df691", "c7a4b77c0dccb78e6df9f9a9fc5bc920");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "assets","asset_histories","portfolio_targets");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `assets`");
      _db.execSQL("DELETE FROM `asset_histories`");
      _db.execSQL("DELETE FROM `portfolio_targets`");
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
    _typeConvertersMap.put(AssetDao.class, AssetDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AssetHistoryDao.class, AssetHistoryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PortfolioTargetDao.class, PortfolioTargetDao_Impl.getRequiredConverters());
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
  public AssetDao assetDao() {
    if (_assetDao != null) {
      return _assetDao;
    } else {
      synchronized(this) {
        if(_assetDao == null) {
          _assetDao = new AssetDao_Impl(this);
        }
        return _assetDao;
      }
    }
  }

  @Override
  public AssetHistoryDao assetHistoryDao() {
    if (_assetHistoryDao != null) {
      return _assetHistoryDao;
    } else {
      synchronized(this) {
        if(_assetHistoryDao == null) {
          _assetHistoryDao = new AssetHistoryDao_Impl(this);
        }
        return _assetHistoryDao;
      }
    }
  }

  @Override
  public PortfolioTargetDao portfolioTargetDao() {
    if (_portfolioTargetDao != null) {
      return _portfolioTargetDao;
    } else {
      synchronized(this) {
        if(_portfolioTargetDao == null) {
          _portfolioTargetDao = new PortfolioTargetDao_Impl(this);
        }
        return _portfolioTargetDao;
      }
    }
  }
}
