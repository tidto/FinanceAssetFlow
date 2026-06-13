package com.financeasserflow.pfmapp.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.financeasserflow.pfmapp.data.model.AssetCategory;
import com.financeasserflow.pfmapp.data.model.AssetEntity;
import com.financeasserflow.pfmapp.data.model.AssetType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AssetDao_Impl implements AssetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AssetEntity> __insertionAdapterOfAssetEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<AssetEntity> __deletionAdapterOfAssetEntity;

  private final EntityDeletionOrUpdateAdapter<AssetEntity> __updateAdapterOfAssetEntity;

  public AssetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAssetEntity = new EntityInsertionAdapter<AssetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `assets` (`id`,`name`,`assetType`,`category`,`amount`,`principalAmount`,`valuationAmount`,`memo`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AssetEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        final String _tmp = __converters.fromAssetType(entity.getAssetType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        final String _tmp_1 = __converters.fromAssetCategory(entity.getCategory());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_1);
        }
        statement.bindLong(5, entity.getAmount());
        if (entity.getPrincipalAmount() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getPrincipalAmount());
        }
        if (entity.getValuationAmount() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getValuationAmount());
        }
        if (entity.getMemo() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getMemo());
        }
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfAssetEntity = new EntityDeletionOrUpdateAdapter<AssetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `assets` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AssetEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfAssetEntity = new EntityDeletionOrUpdateAdapter<AssetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `assets` SET `id` = ?,`name` = ?,`assetType` = ?,`category` = ?,`amount` = ?,`principalAmount` = ?,`valuationAmount` = ?,`memo` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AssetEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        final String _tmp = __converters.fromAssetType(entity.getAssetType());
        if (_tmp == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, _tmp);
        }
        final String _tmp_1 = __converters.fromAssetCategory(entity.getCategory());
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_1);
        }
        statement.bindLong(5, entity.getAmount());
        if (entity.getPrincipalAmount() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getPrincipalAmount());
        }
        if (entity.getValuationAmount() == null) {
          statement.bindNull(7);
        } else {
          statement.bindLong(7, entity.getValuationAmount());
        }
        if (entity.getMemo() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getMemo());
        }
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final AssetEntity asset, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAssetEntity.insertAndReturnId(asset);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final AssetEntity asset, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAssetEntity.handle(asset);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final AssetEntity asset, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAssetEntity.handle(asset);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AssetEntity>> observeAssets() {
    final String _sql = "SELECT * FROM assets ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"assets"}, new Callable<List<AssetEntity>>() {
      @Override
      @NonNull
      public List<AssetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAssetType = CursorUtil.getColumnIndexOrThrow(_cursor, "assetType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPrincipalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "principalAmount");
          final int _cursorIndexOfValuationAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "valuationAmount");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<AssetEntity> _result = new ArrayList<AssetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AssetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final AssetType _tmpAssetType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAssetType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAssetType);
            }
            _tmpAssetType = __converters.toAssetType(_tmp);
            final AssetCategory _tmpCategory;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCategory);
            }
            _tmpCategory = __converters.toAssetCategory(_tmp_1);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final Long _tmpPrincipalAmount;
            if (_cursor.isNull(_cursorIndexOfPrincipalAmount)) {
              _tmpPrincipalAmount = null;
            } else {
              _tmpPrincipalAmount = _cursor.getLong(_cursorIndexOfPrincipalAmount);
            }
            final Long _tmpValuationAmount;
            if (_cursor.isNull(_cursorIndexOfValuationAmount)) {
              _tmpValuationAmount = null;
            } else {
              _tmpValuationAmount = _cursor.getLong(_cursorIndexOfValuationAmount);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new AssetEntity(_tmpId,_tmpName,_tmpAssetType,_tmpCategory,_tmpAmount,_tmpPrincipalAmount,_tmpValuationAmount,_tmpMemo,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<AssetEntity> observeAsset(final long assetId) {
    final String _sql = "SELECT * FROM assets WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, assetId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"assets"}, new Callable<AssetEntity>() {
      @Override
      @Nullable
      public AssetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAssetType = CursorUtil.getColumnIndexOrThrow(_cursor, "assetType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPrincipalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "principalAmount");
          final int _cursorIndexOfValuationAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "valuationAmount");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final AssetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final AssetType _tmpAssetType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAssetType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAssetType);
            }
            _tmpAssetType = __converters.toAssetType(_tmp);
            final AssetCategory _tmpCategory;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCategory);
            }
            _tmpCategory = __converters.toAssetCategory(_tmp_1);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final Long _tmpPrincipalAmount;
            if (_cursor.isNull(_cursorIndexOfPrincipalAmount)) {
              _tmpPrincipalAmount = null;
            } else {
              _tmpPrincipalAmount = _cursor.getLong(_cursorIndexOfPrincipalAmount);
            }
            final Long _tmpValuationAmount;
            if (_cursor.isNull(_cursorIndexOfValuationAmount)) {
              _tmpValuationAmount = null;
            } else {
              _tmpValuationAmount = _cursor.getLong(_cursorIndexOfValuationAmount);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new AssetEntity(_tmpId,_tmpName,_tmpAssetType,_tmpCategory,_tmpAmount,_tmpPrincipalAmount,_tmpValuationAmount,_tmpMemo,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getAssetOnce(final long assetId,
      final Continuation<? super AssetEntity> $completion) {
    final String _sql = "SELECT * FROM assets WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, assetId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AssetEntity>() {
      @Override
      @Nullable
      public AssetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAssetType = CursorUtil.getColumnIndexOrThrow(_cursor, "assetType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPrincipalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "principalAmount");
          final int _cursorIndexOfValuationAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "valuationAmount");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final AssetEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final AssetType _tmpAssetType;
            final String _tmp;
            if (_cursor.isNull(_cursorIndexOfAssetType)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getString(_cursorIndexOfAssetType);
            }
            _tmpAssetType = __converters.toAssetType(_tmp);
            final AssetCategory _tmpCategory;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfCategory);
            }
            _tmpCategory = __converters.toAssetCategory(_tmp_1);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final Long _tmpPrincipalAmount;
            if (_cursor.isNull(_cursorIndexOfPrincipalAmount)) {
              _tmpPrincipalAmount = null;
            } else {
              _tmpPrincipalAmount = _cursor.getLong(_cursorIndexOfPrincipalAmount);
            }
            final Long _tmpValuationAmount;
            if (_cursor.isNull(_cursorIndexOfValuationAmount)) {
              _tmpValuationAmount = null;
            } else {
              _tmpValuationAmount = _cursor.getLong(_cursorIndexOfValuationAmount);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new AssetEntity(_tmpId,_tmpName,_tmpAssetType,_tmpCategory,_tmpAmount,_tmpPrincipalAmount,_tmpValuationAmount,_tmpMemo,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countAssets(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT COUNT(*) FROM assets";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final Long _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getLong(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AssetEntity>> observeAssetsByType(final AssetType assetType) {
    final String _sql = "SELECT * FROM assets WHERE assetType = ? ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromAssetType(assetType);
    if (_tmp == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, _tmp);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"assets"}, new Callable<List<AssetEntity>>() {
      @Override
      @NonNull
      public List<AssetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAssetType = CursorUtil.getColumnIndexOrThrow(_cursor, "assetType");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfPrincipalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "principalAmount");
          final int _cursorIndexOfValuationAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "valuationAmount");
          final int _cursorIndexOfMemo = CursorUtil.getColumnIndexOrThrow(_cursor, "memo");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<AssetEntity> _result = new ArrayList<AssetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AssetEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final AssetType _tmpAssetType;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfAssetType)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfAssetType);
            }
            _tmpAssetType = __converters.toAssetType(_tmp_1);
            final AssetCategory _tmpCategory;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfCategory)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfCategory);
            }
            _tmpCategory = __converters.toAssetCategory(_tmp_2);
            final long _tmpAmount;
            _tmpAmount = _cursor.getLong(_cursorIndexOfAmount);
            final Long _tmpPrincipalAmount;
            if (_cursor.isNull(_cursorIndexOfPrincipalAmount)) {
              _tmpPrincipalAmount = null;
            } else {
              _tmpPrincipalAmount = _cursor.getLong(_cursorIndexOfPrincipalAmount);
            }
            final Long _tmpValuationAmount;
            if (_cursor.isNull(_cursorIndexOfValuationAmount)) {
              _tmpValuationAmount = null;
            } else {
              _tmpValuationAmount = _cursor.getLong(_cursorIndexOfValuationAmount);
            }
            final String _tmpMemo;
            if (_cursor.isNull(_cursorIndexOfMemo)) {
              _tmpMemo = null;
            } else {
              _tmpMemo = _cursor.getString(_cursorIndexOfMemo);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new AssetEntity(_tmpId,_tmpName,_tmpAssetType,_tmpCategory,_tmpAmount,_tmpPrincipalAmount,_tmpValuationAmount,_tmpMemo,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
